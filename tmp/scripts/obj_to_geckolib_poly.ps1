$ErrorActionPreference = "Stop"

$OBJ_PATH = "D:/InteliJ IDEA mods/1.20.1/RTS Reign of Nether Plus/tmp/vindicator_obj/tinker.obj"
$OUT_PATH = "D:/InteliJ IDEA mods/1.20.1/RTS Reign of Nether Plus/tmp/converted/vindicator_armored.geo.json"

$TARGET_HEIGHT = 24.0
$TEX_W = 64
$TEX_H = 64

function Round6([double]$v) {
  return [math]::Round($v, 6)
}

$verts = New-Object "System.Collections.Generic.List[object]"
$faces = New-Object "System.Collections.Generic.List[object]"

foreach ($rawLine in Get-Content -Path $OBJ_PATH -Encoding UTF8) {
  if ($null -eq $rawLine) { continue }
  $line = $rawLine.Trim()
  if ($line.Length -eq 0 -or $line.StartsWith("#")) { continue }

  if ($line.StartsWith("v ")) {
    $parts = $line -split "\s+"
    if ($parts.Length -ge 4) {
      $verts.Add(@([double]$parts[1], [double]$parts[2], [double]$parts[3]))
    }
    continue
  }

  if ($line.StartsWith("f ")) {
    $parts = ($line -split "\s+")[1..(($line -split "\s+").Length - 1)]
    $idxs = New-Object "System.Collections.Generic.List[int]"
    foreach ($p in $parts) {
      if ([string]::IsNullOrWhiteSpace($p)) { continue }
      $vIdxRaw = ($p -split "/")[0]
      if ([string]::IsNullOrWhiteSpace($vIdxRaw)) { continue }
      $i = [int]$vIdxRaw
      if ($i -lt 0) {
        $i = $verts.Count + $i + 1
      }
      $idxs.Add($i - 1)
    }
    if ($idxs.Count -ge 3) {
      for ($t = 1; $t -lt $idxs.Count - 1; $t++) {
        $faces.Add(@($idxs[0], $idxs[$t], $idxs[$t + 1]))
      }
    }
  }
}

if ($verts.Count -eq 0 -or $faces.Count -eq 0) {
  throw "OBJ has no vertices/faces"
}

$minX = [double]::PositiveInfinity
$minY = [double]::PositiveInfinity
$minZ = [double]::PositiveInfinity
$maxX = [double]::NegativeInfinity
$maxY = [double]::NegativeInfinity
$maxZ = [double]::NegativeInfinity

foreach ($v in $verts) {
  $x = [double]$v[0]
  $y = [double]$v[1]
  $z = [double]$v[2]
  if ($x -lt $minX) { $minX = $x }
  if ($x -gt $maxX) { $maxX = $x }
  if ($y -lt $minY) { $minY = $y }
  if ($y -gt $maxY) { $maxY = $y }
  if ($z -lt $minZ) { $minZ = $z }
  if ($z -gt $maxZ) { $maxZ = $z }
}

$spanY = [math]::Max($maxY - $minY, 1e-6)
$spanX = [math]::Max($maxX - $minX, 1e-6)
$spanZ = [math]::Max($maxZ - $minZ, 1e-6)
$scale = $TARGET_HEIGHT / $spanY
$centerX = ($minX + $maxX) * 0.5
$centerZ = ($minZ + $maxZ) * 0.5

$positions = New-Object "System.Collections.Generic.List[object]"
foreach ($v in $verts) {
  $positions.Add(@(
      (Round6 (([double]$v[0] - $centerX) * $scale)),
      (Round6 (([double]$v[1] - $minY) * $scale)),
      (Round6 (([double]$v[2] - $centerZ) * $scale))
    ))
}

$uvs = New-Object "System.Collections.Generic.List[object]"
foreach ($v in $verts) {
  $u = (([double]$v[0] - $minX) / $spanX) * $TEX_W
  $vv = (([double]$v[2] - $minZ) / $spanZ) * $TEX_H
  $uvs.Add(@((Round6 $u), (Round6 $vv)))
}

$normals = New-Object "System.Collections.Generic.List[object]"
$normalIndex = @{}
$polys = New-Object "System.Collections.Generic.List[object]"

function Get-NormalIndex([int]$a, [int]$b, [int]$c) {
  $ax = [double]$positions[$a][0]
  $ay = [double]$positions[$a][1]
  $az = [double]$positions[$a][2]
  $bx = [double]$positions[$b][0]
  $by = [double]$positions[$b][1]
  $bz = [double]$positions[$b][2]
  $cx = [double]$positions[$c][0]
  $cy = [double]$positions[$c][1]
  $cz = [double]$positions[$c][2]

  $ux = $bx - $ax
  $uy = $by - $ay
  $uz = $bz - $az
  $vx = $cx - $ax
  $vy = $cy - $ay
  $vz = $cz - $az

  $nx = $uy * $vz - $uz * $vy
  $ny = $uz * $vx - $ux * $vz
  $nz = $ux * $vy - $uy * $vx

  $len = [math]::Sqrt($nx * $nx + $ny * $ny + $nz * $nz)
  if ($len -lt 1e-9) {
    $nx = 0.0
    $ny = 1.0
    $nz = 0.0
  } else {
    $nx /= $len
    $ny /= $len
    $nz /= $len
  }

  $kx = Round6 $nx
  $ky = Round6 $ny
  $kz = Round6 $nz
  $key = "$kx,$ky,$kz"

  if ($normalIndex.ContainsKey($key)) {
    return [int]$normalIndex[$key]
  }

  $idx = $normals.Count
  $normalIndex[$key] = $idx
  $normals.Add(@($kx, $ky, $kz))
  return $idx
}

foreach ($f in $faces) {
  $a = [int]$f[0]
  $b = [int]$f[1]
  $c = [int]$f[2]
  $n = Get-NormalIndex -a $a -b $b -c $c
  $polys.Add(@(
      @($a, $n, $a),
      @($b, $n, $b),
      @($c, $n, $c)
    ))
}

$visibleWidth = Round6 ((($maxX - $minX) * $scale) / 16.0)
$visibleHeight = Round6 ((($maxY - $minY) * $scale) / 16.0)
$visibleOffsetY = Round6 ($visibleHeight * 0.5)

$geo = [ordered]@{
  format_version      = "1.12.0"
  "minecraft:geometry" = @(
    [ordered]@{
      description = [ordered]@{
        identifier            = "geometry.vindicator_armored"
        texture_width         = $TEX_W
        texture_height        = $TEX_H
        visible_bounds_width  = $visibleWidth
        visible_bounds_height = $visibleHeight
        visible_bounds_offset = @(0, $visibleOffsetY, 0)
      }
      bones       = @(
        [ordered]@{
          name      = "root"
          pivot     = @(0, 0, 0)
          poly_mesh = [ordered]@{
            normalized_uvs = $false
            positions      = $positions
            normals        = $normals
            uvs            = $uvs
            polys          = $polys
          }
        }
      )
    }
  )
}

$outDir = Split-Path -Path $OUT_PATH -Parent
if (-not (Test-Path -Path $outDir)) {
  New-Item -Path $outDir -ItemType Directory -Force | Out-Null
}

$json = $geo | ConvertTo-Json -Depth 100 -Compress
[System.IO.File]::WriteAllText($OUT_PATH, $json, [System.Text.UTF8Encoding]::new($false))

Write-Host "WROTE $OUT_PATH"
Write-Host "VERTICES $($verts.Count) TRIS $($faces.Count) NORMALS $($normals.Count)"
Write-Host "SCALE $scale"
