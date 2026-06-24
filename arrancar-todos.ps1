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

foreach ($servicio in $servicios) {
    Write-Host "Iniciando $servicio..."
    Start-Process powershell -ArgumentList @(
        "-NoExit",
        "-Command",
        "cd '$PWD\$servicio'; .\mvnw.cmd spring-boot:run"
    )
    Start-Sleep -Seconds 3
}

Write-Host "Iniciando Gateway..."
Start-Process powershell -ArgumentList @(
    "-NoExit",
    "-Command",
    "cd '$PWD\getawayspring-profeAlejandro'; .\mvnw.cmd spring-boot:run"
)

Write-Host "Todo iniciado."