-- Rango de folios inicial para boletas y facturas (sucursal 1)
INSERT INTO folios (tipo_documento, folio_desde, folio_hasta, folio_actual, folios_restantes, activo, sucursal, umbral)
SELECT 'BOLETA', 1, 100, 1, 100, true, 1, 10
WHERE NOT EXISTS (SELECT 1 FROM folios WHERE tipo_documento = 'BOLETA' AND sucursal = 1);

INSERT INTO folios (tipo_documento, folio_desde, folio_hasta, folio_actual, folios_restantes, activo, sucursal, umbral)
SELECT 'FACTURA', 1, 50, 1, 50, true, 1, 10
WHERE NOT EXISTS (SELECT 1 FROM folios WHERE tipo_documento = 'FACTURA' AND sucursal = 1);
