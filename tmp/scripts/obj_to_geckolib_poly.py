import json
import math
from pathlib import Path

OBJ_PATH = Path(r"D:/InteliJ IDEA mods/1.20.1/RTS Reign of Nether Plus/tmp/vindicator_obj/tinker.obj")
OUT_PATH = Path(r"D:/InteliJ IDEA mods/1.20.1/RTS Reign of Nether Plus/tmp/converted/vindicator_armored.geo.json")

TARGET_HEIGHT = 24.0  # minecraft units
TEX_W = 64
TEX_H = 64

verts = []
faces = []

with OBJ_PATH.open('r', encoding='utf-8', errors='ignore') as f:
    for line in f:
        if not line:
            continue
        line = line.strip()
        if not line or line.startswith('#'):
            continue
        if line.startswith('v '):
            parts = line.split()
            if len(parts) >= 4:
                x = float(parts[1])
                y = float(parts[2])
                z = float(parts[3])
                verts.append((x, y, z))
        elif line.startswith('f '):
            parts = line.split()[1:]
            idxs = []
            for p in parts:
                # supports v, v/vt, v/vt/vn, v//vn
                v_idx = p.split('/')[0]
                if not v_idx:
                    continue
                i = int(v_idx)
                if i < 0:
                    i = len(verts) + i + 1
                idxs.append(i - 1)
            if len(idxs) >= 3:
                # triangulate ngons via fan
                for t in range(1, len(idxs) - 1):
                    faces.append((idxs[0], idxs[t], idxs[t + 1]))

if not verts or not faces:
    raise RuntimeError('OBJ has no vertices/faces')

min_x = min(v[0] for v in verts)
max_x = max(v[0] for v in verts)
min_y = min(v[1] for v in verts)
max_y = max(v[1] for v in verts)
min_z = min(v[2] for v in verts)
max_z = max(v[2] for v in verts)

span_y = max(max_y - min_y, 1e-6)
scale = TARGET_HEIGHT / span_y

center_x = (min_x + max_x) * 0.5
center_z = (min_z + max_z) * 0.5

def tx(v):
    return round((v[0] - center_x) * scale, 6)

def ty(v):
    return round((v[1] - min_y) * scale, 6)

def tz(v):
    return round((v[2] - center_z) * scale, 6)

# Precompute transformed vertices for indexed position list
positions = [[tx(v), ty(v), tz(v)] for v in verts]

# UVs from x/z projection to texture space
span_x = max(max_x - min_x, 1e-6)
span_z = max(max_z - min_z, 1e-6)
uvs = []
for v in verts:
    u = ((v[0] - min_x) / span_x) * TEX_W
    vv = ((v[2] - min_z) / span_z) * TEX_H
    uvs.append([round(u, 6), round(vv, 6)])

normals = []
normal_index = {}

def norm_key(nx, ny, nz):
    return (round(nx, 6), round(ny, 6), round(nz, 6))

def get_normal_index(a, b, c):
    ax, ay, az = positions[a]
    bx, by, bz = positions[b]
    cx, cy, cz = positions[c]
    ux, uy, uz = (bx - ax, by - ay, bz - az)
    vx, vy, vz = (cx - ax, cy - ay, cz - az)
    nx = uy * vz - uz * vy
    ny = uz * vx - ux * vz
    nz = ux * vy - uy * vx
    ln = math.sqrt(nx * nx + ny * ny + nz * nz)
    if ln < 1e-9:
        nx, ny, nz = 0.0, 1.0, 0.0
    else:
        nx /= ln
        ny /= ln
        nz /= ln
    k = norm_key(nx, ny, nz)
    idx = normal_index.get(k)
    if idx is None:
        idx = len(normals)
        normal_index[k] = idx
        normals.append([k[0], k[1], k[2]])
    return idx

polys = []
for a, b, c in faces:
    n_idx = get_normal_index(a, b, c)
    polys.append([
        [a, n_idx, a],
        [b, n_idx, b],
        [c, n_idx, c],
    ])

geo = {
    "format_version": "1.12.0",
    "minecraft:geometry": [
        {
            "description": {
                "identifier": "geometry.vindicator_armored",
                "texture_width": TEX_W,
                "texture_height": TEX_H,
                "visible_bounds_width": round((max_x - min_x) * scale / 16.0, 6),
                "visible_bounds_height": round((max_y - min_y) * scale / 16.0, 6),
                "visible_bounds_offset": [0, round(((max_y - min_y) * scale / 16.0) * 0.5, 6), 0]
            },
            "bones": [
                {
                    "name": "root",
                    "pivot": [0, 0, 0],
                    "poly_mesh": {
                        "normalized_uvs": False,
                        "positions": positions,
                        "normals": normals,
                        "uvs": uvs,
                        "polys": polys
                    }
                }
            ]
        }
    ]
}

OUT_PATH.parent.mkdir(parents=True, exist_ok=True)
with OUT_PATH.open('w', encoding='utf-8') as f:
    json.dump(geo, f, ensure_ascii=False)

print('WROTE', OUT_PATH)
print('VERTICES', len(verts), 'TRIS', len(faces), 'NORMALS', len(normals))
print('SCALE', scale)
