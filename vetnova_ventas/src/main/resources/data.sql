-- Orden de prueba inicial (cliente 2, sucursal CHILLAN)
INSERT INTO ordenes (cliente_id, sucursal, estado, subtotal, impuestos, total, fecha_creacion)
SELECT 2, 'CHILLAN', 'CONFIRMADA', 35990, 6838.1, 42828.1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM ordenes WHERE id = 1);

-- Detalle de la orden
INSERT INTO detalle_orden (orden_id, producto_id, nombre_producto, tipo_item, cantidad, precio_unitario, subtotal)
SELECT o.id, 1, 'Alimento perro adulto 15kg', 'PRODUCTO', 1, 35990, 35990
FROM ordenes o WHERE o.estado = 'CONFIRMADA'
AND NOT EXISTS (SELECT 1 FROM detalle_orden WHERE orden_id = o.id);

-- Pago registrado para la orden
INSERT INTO pagos (orden_id, metodo, monto, estado, referencia, fecha)
SELECT o.id, 'DEBITO', 42828.1, 'APROBADO', 'TRX-0001', CURRENT_TIMESTAMP
FROM ordenes o WHERE o.estado = 'CONFIRMADA'
AND NOT EXISTS (SELECT 1 FROM pagos WHERE orden_id = o.id);
