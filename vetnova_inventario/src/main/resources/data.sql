-- Productos iniciales para pruebas
INSERT INTO productos (sku, nombre, descripcion, precio, activo, fecha_creacion)
SELECT 'ALI-001', 'Alimento perro adulto 15kg', 'Saco de alimento premium para perro adulto', 35990, true, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE sku = 'ALI-001');

INSERT INTO productos (sku, nombre, descripcion, precio, activo, fecha_creacion)
SELECT 'ALI-002', 'Alimento gato adulto 10kg', 'Saco de alimento premium para gato adulto', 28990, true, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE sku = 'ALI-002');

INSERT INTO productos (sku, nombre, descripcion, precio, activo, fecha_creacion)
SELECT 'MED-001', 'Antiparasitario canino', 'Comprimido antiparasitario dosis mensual', 12990, true, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE sku = 'MED-001');

-- Stock inicial por sucursal (1=Chillán, 2=Los Ángeles, 3=Talca)
INSERT INTO stock_sucursal (producto_id, id_sucursal, cantidad, stock_minimo)
SELECT p.id, 1, 50, 5 FROM productos p WHERE p.sku = 'ALI-001'
AND NOT EXISTS (SELECT 1 FROM stock_sucursal s WHERE s.producto_id = p.id AND s.id_sucursal = 1);

INSERT INTO stock_sucursal (producto_id, id_sucursal, cantidad, stock_minimo)
SELECT p.id, 2, 30, 5 FROM productos p WHERE p.sku = 'ALI-001'
AND NOT EXISTS (SELECT 1 FROM stock_sucursal s WHERE s.producto_id = p.id AND s.id_sucursal = 2);

INSERT INTO stock_sucursal (producto_id, id_sucursal, cantidad, stock_minimo)
SELECT p.id, 1, 40, 5 FROM productos p WHERE p.sku = 'ALI-002'
AND NOT EXISTS (SELECT 1 FROM stock_sucursal s WHERE s.producto_id = p.id AND s.id_sucursal = 1);

INSERT INTO stock_sucursal (producto_id, id_sucursal, cantidad, stock_minimo)
SELECT p.id, 1, 8, 5 FROM productos p WHERE p.sku = 'MED-001'
AND NOT EXISTS (SELECT 1 FROM stock_sucursal s WHERE s.producto_id = p.id AND s.id_sucursal = 1);
