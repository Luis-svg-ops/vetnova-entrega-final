# Ejecuta las pruebas unitarias (mvn test) de los 12 microservicios.
# Uso: abrir PowerShell en la raiz del proyecto y ejecutar:  .\probar-todos.ps1
# Requiere Maven (mvn) y JDK 17+ en el PATH.

$raiz = $PSScriptRoot
if (-not $raiz) { $raiz = Get-Location }

$servicios = @(
    "vetnova_auth",
    "vetnova_catalogo",
    "vetnova_inventario",
    "vetnova_ventas",
    "vetnova_envio",
    "vetnova_agenda",
    "vetnova_ficha",
    "vetnova_soporte",
    "vetnova_laboratorio",
    "vetnova_facturacion",
    "vetnova_reportes",
    "vetnova_notificaciones"
)

$resultados = @()

foreach ($servicio in $servicios) {
    $carpeta = Join-Path $raiz $servicio
    Write-Host ""
    Write-Host "==============================================" -ForegroundColor Cyan
    Write-Host " Probando: $servicio" -ForegroundColor Cyan
    Write-Host "==============================================" -ForegroundColor Cyan

    if (-not (Test-Path $carpeta)) {
        Write-Host "AVISO: la carpeta $servicio no existe en $raiz" -ForegroundColor Yellow
        $resultados += [PSCustomObject]@{ Servicio = $servicio; Estado = "NO EXISTE" }
        continue
    }
    if (-not (Test-Path (Join-Path $carpeta "pom.xml"))) {
        Write-Host "AVISO: $servicio no tiene pom.xml" -ForegroundColor Yellow
        $resultados += [PSCustomObject]@{ Servicio = $servicio; Estado = "SIN POM" }
        continue
    }

    Push-Location $carpeta
    try {
        mvn test -q
        if ($LASTEXITCODE -eq 0) {
            Write-Host "OK: $servicio" -ForegroundColor Green
            $resultados += [PSCustomObject]@{ Servicio = $servicio; Estado = "OK" }
        } else {
            Write-Host "FAIL: $servicio" -ForegroundColor Red
            $resultados += [PSCustomObject]@{ Servicio = $servicio; Estado = "FAIL" }
        }
    }
    finally {
        Pop-Location
    }
}

Write-Host ""
Write-Host "================= RESUMEN =================" -ForegroundColor Cyan
$resultados | Format-Table -AutoSize

$fallidos = ($resultados | Where-Object { $_.Estado -ne "OK" }).Count
if ($fallidos -eq 0) {
    Write-Host "Los 12 microservicios pasaron sus pruebas unitarias." -ForegroundColor Green
} else {
    Write-Host "$fallidos servicio(s) con problemas. Revisar arriba." -ForegroundColor Red
}
