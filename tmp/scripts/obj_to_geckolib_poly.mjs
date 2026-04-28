import fs from "fs";
import path from "path";

const OBJ_PATH =
  "D:/InteliJ IDEA mods/1.20.1/RTS Reign of Nether Plus/tmp/vindicator_obj/tinker.obj";
const OUT_PATH =
  "D:/InteliJ IDEA mods/1.20.1/RTS Reign of Nether Plus/tmp/converted/vindicator_armored.geo.json";

const TARGET_HEIGHT = 24.0;
const TEX_W = 64;
const TEX_H = 64;

const text = fs.readFileSync(OBJ_PATH, "utf8");
const lines = text.split(/\r?\n/);

const verts = [];
const faces = [];

for (const rawLine of lines) {
  const line = rawLine.trim();
  if (!line || line.startsWith("#")) continue;

  if (line.startsWith("v ")) {
    const parts = line.split(/\s+/);
    if (parts.length >= 4) {
      verts.push([Number(parts[1]), Number(parts[2]), Number(parts[3])]);
    }
    continue;
  }

  if (line.startsWith("f ")) {
    const parts = line.split(/\s+/).slice(1);
    const idxs = [];

    for (const p of parts) {
      const vIdxRaw = p.split("/")[0];
      if (!vIdxRaw) continue;
      let i = Number(vIdxRaw);
      if (i < 0) i = verts.length + i + 1;
      idxs.push(i - 1);
    }

    if (idxs.length >= 3) {
      for (let t = 1; t < idxs.length - 1; t += 1) {
        faces.push([idxs[0], idxs[t], idxs[t + 1]]);
      }
    }
  }
}

if (verts.length === 0 || faces.length === 0) {
  throw new Error("OBJ has no vertices/faces");
}

let minX = Infinity;
let minY = Infinity;
let minZ = Infinity;
let maxX = -Infinity;
let maxY = -Infinity;
let maxZ = -Infinity;

for (const [x, y, z] of verts) {
  if (x < minX) minX = x;
  if (x > maxX) maxX = x;
  if (y < minY) minY = y;
  if (y > maxY) maxY = y;
  if (z < minZ) minZ = z;
  if (z > maxZ) maxZ = z;
}

const spanY = Math.max(maxY - minY, 1e-6);
const spanX = Math.max(maxX - minX, 1e-6);
const spanZ = Math.max(maxZ - minZ, 1e-6);
const scale = TARGET_HEIGHT / spanY;
const centerX = (minX + maxX) * 0.5;
const centerZ = (minZ + maxZ) * 0.5;

const round6 = (v) => Math.round(v * 1e6) / 1e6;
const normKey = (x, y, z) => `${round6(x)},${round6(y)},${round6(z)}`;

const positions = verts.map(([x, y, z]) => [
  round6((x - centerX) * scale),
  round6((y - minY) * scale),
  round6((z - centerZ) * scale),
]);

const uvs = verts.map(([x, _y, z]) => [
  round6(((x - minX) / spanX) * TEX_W),
  round6(((z - minZ) / spanZ) * TEX_H),
]);

const normals = [];
const normalIndex = new Map();
const polys = [];

const getNormalIndex = (a, b, c) => {
  const [ax, ay, az] = positions[a];
  const [bx, by, bz] = positions[b];
  const [cx, cy, cz] = positions[c];

  const ux = bx - ax;
  const uy = by - ay;
  const uz = bz - az;
  const vx = cx - ax;
  const vy = cy - ay;
  const vz = cz - az;

  let nx = uy * vz - uz * vy;
  let ny = uz * vx - ux * vz;
  let nz = ux * vy - uy * vx;

  const len = Math.sqrt(nx * nx + ny * ny + nz * nz);
  if (len < 1e-9) {
    nx = 0;
    ny = 1;
    nz = 0;
  } else {
    nx /= len;
    ny /= len;
    nz /= len;
  }

  const key = normKey(nx, ny, nz);
  if (normalIndex.has(key)) {
    return normalIndex.get(key);
  }

  const idx = normals.length;
  normalIndex.set(key, idx);
  normals.push([round6(nx), round6(ny), round6(nz)]);
  return idx;
};

for (const [a, b, c] of faces) {
  const n = getNormalIndex(a, b, c);
  polys.push([
    [a, n, a],
    [b, n, b],
    [c, n, c],
  ]);
}

const geo = {
  format_version: "1.12.0",
  "minecraft:geometry": [
    {
      description: {
        identifier: "geometry.vindicator_armored",
        texture_width: TEX_W,
        texture_height: TEX_H,
        visible_bounds_width: round6(((maxX - minX) * scale) / 16.0),
        visible_bounds_height: round6(((maxY - minY) * scale) / 16.0),
        visible_bounds_offset: [
          0,
          round6((((maxY - minY) * scale) / 16.0) * 0.5),
          0,
        ],
      },
      bones: [
        {
          name: "root",
          pivot: [0, 0, 0],
          poly_mesh: {
            normalized_uvs: false,
            positions,
            normals,
            uvs,
            polys,
          },
        },
      ],
    },
  ],
};

fs.mkdirSync(path.dirname(OUT_PATH), { recursive: true });
fs.writeFileSync(OUT_PATH, JSON.stringify(geo), "utf8");

console.log("WROTE", OUT_PATH);
console.log("VERTICES", verts.length, "TRIS", faces.length, "NORMALS", normals.length);
console.log("SCALE", scale);
