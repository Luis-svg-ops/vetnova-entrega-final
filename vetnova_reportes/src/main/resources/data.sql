-- Reporte base de ejemplo (sucursal 1)
INSERT INTO reportes (tipo, sucursal, desde, hasta, generado_por, generado_en, estado)
SELECT 'VENTA', 1, DATEADD('DAY', -30, CURRENT_DATE), CURRENT_DATE, 1, CURRENT_TIMESTAMP, 'GENERADO'
WHERE NOT EXISTS (SELECT 1 FROM reportes WHERE tipo = 'VENTA' AND sucursal = 1);
