$java = 'C:\Program Files\Eclipse Adoptium\jdk-25.0.2.10-hotspot\bin\java.exe'

$raiz = $PSScriptRoot
if (-not $raiz) { $raiz = (Get-Location).Path }

$logs = "$raiz\logs"
if (-not (Test-Path $logs)) { New-Item -ItemType Directory -Path $logs | Out-Null }

$servicios = @(
    "vetnova_auth",
    "vetnova_catalogo",
    "vetnova_inventario",
    "vetnova_ventas",
    "vetnova_envio",
    "vetnova_ficha",
    "vetnova_agenda",
    "vetnova_facturacion",
    "vetnova_notificaciones",
    "vetnova_laboratorio",
    "vetnova_reportes",
    "vetnova_soporte"
)

foreach ($servicio in $servicios) {
    $jar = Get-ChildItem "$raiz\$servicio\target\*.jar" -ErrorAction SilentlyContinue |
           Where-Object { $_.Name -notlike "*.jar.original" } |
           Select-Object -First 1

    if (-not $jar) {
        Write-Host "  AVISO: no hay JAR para $servicio" -ForegroundColor Yellow
        continue
    }

    $logFile = "$logs\$servicio.log"
    Start-Process -FilePath $java `
                  -ArgumentList "-jar `"$($jar.FullName)`"" `
                  -WindowStyle Hidden `
                  -RedirectStandardOutput $logFile `
                  -RedirectStandardError "$logs\$servicio-err.log"

    Write-Host "OK $servicio" -ForegroundColor Green
    Start-Sleep -Seconds 5
}

Write-Host ""
Write-Host "Todos los MS corriendo en segundo plano." -ForegroundColor Green
Write-Host "Logs en: $logs" -ForegroundColor Cyan
