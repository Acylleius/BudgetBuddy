$ErrorActionPreference = "Stop"

$root = Resolve-Path (Join-Path $PSScriptRoot "..")
$webDir = Join-Path $root "web"
$distDir = Join-Path $root "dist\web"

$requiredFiles = @(
    "index.html",
    "features\auth\login.html",
    "features\auth\auth.js",
    "shared\js\api.js",
    "shared\styles\variables.css",
    "shared\styles\components.css",
    "shared\styles\layout.css"
)

foreach ($file in $requiredFiles) {
    $path = Join-Path $webDir $file
    if (-not (Test-Path $path)) {
        throw "Missing required frontend file: web\$file"
    }
}

if (Test-Path $distDir) {
    Remove-Item -LiteralPath $distDir -Recurse -Force
}

New-Item -ItemType Directory -Force -Path $distDir | Out-Null
Copy-Item -Path (Join-Path $webDir "*") -Destination $distDir -Recurse -Force

Write-Host "Frontend production build completed successfully."
Write-Host "Output directory: dist\web"
