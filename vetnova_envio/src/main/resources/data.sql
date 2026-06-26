-- Rutas de despacho entre sucursales
INSERT INTO ruta_despachos (sucursal_origen, sucursal_destino, distancia_km, tiempo_estimado_min, activa)
SELECT 'CHILLAN', 'LOS_ANGELES', 100, 90, true
WHERE NOT EXISTS (SELECT 1 FROM ruta_despachos WHERE sucursal_origen = 'CHILLAN' AND sucursal_destino = 'LOS_ANGELES');

INSERT INTO ruta_despachos (sucursal_origen, sucursal_destino, distancia_km, tiempo_estimado_min, activa)
SELECT 'CHILLAN', 'TALCA', 150, 120, true
WHERE NOT EXISTS (SELECT 1 FROM ruta_despachos WHERE sucursal_origen = 'CHILLAN' AND sucursal_destino = 'TALCA');

INSERT INTO ruta_despachos (sucursal_origen, sucursal_destino, distancia_km, tiempo_estimado_min, activa)
SELECT 'LOS_ANGELES', 'TALCA', 200, 180, true
WHERE NOT EXISTS (SELECT 1 FROM ruta_despachos WHERE sucursal_origen = 'LOS_ANGELES' AND sucursal_destino = 'TALCA');

-- Envío de prueba asociado a la orden 1
INSERT INTO envios (numero_guia, orden_id, tipo_envio, id_sucursal_origen, direccion_entrega, estado_actual, fecha_creacion)
SELECT 'GD-0001', 1, 'DOMICILIO', 'CHILLAN', 'Av. Libertad 123, Chillán', 'PREPARANDO', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM envios WHERE numero_guia = 'GD-0001');
