-- Categorías base del catálogo
INSERT INTO categorias (nombre, descripcion, tipo)
SELECT 'Alimentos', 'Alimentos para mascotas', 'PRODUCTO'
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Alimentos');

INSERT INTO categorias (nombre, descripcion, tipo)
SELECT 'Medicamentos', 'Medicamentos veterinarios', 'PRODUCTO'
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Medicamentos');

INSERT INTO categorias (nombre, descripcion, tipo)
SELECT 'Atención clínica', 'Servicios de atención veterinaria', 'SERVICIO'
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Atención clínica');

-- Productos publicados en el catálogo
INSERT INTO productos (nombre, descripcion, precio, activo, categoria_id, imagen_url, fecha_actualizacion)
SELECT 'Alimento perro adulto 15kg', 'Saco de alimento premium', 35990, true, 1, NULL, CURRENT_DATE
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Alimento perro adulto 15kg');

INSERT INTO productos (nombre, descripcion, precio, activo, categoria_id, imagen_url, fecha_actualizacion)
SELECT 'Antiparasitario canino', 'Comprimido dosis mensual', 12990, true, 2, NULL, CURRENT_DATE
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Antiparasitario canino');

-- Servicios de la clínica
INSERT INTO servicios (nombre, descripcion, precio, duracion_minutos, activo, categoria_id)
SELECT 'Consulta general', 'Consulta veterinaria general', 25000, 30, true, 3
WHERE NOT EXISTS (SELECT 1 FROM servicios WHERE nombre = 'Consulta general');

INSERT INTO servicios (nombre, descripcion, precio, duracion_minutos, activo, categoria_id)
SELECT 'Vacunación', 'Aplicación de vacuna con control', 18000, 20, true, 3
WHERE NOT EXISTS (SELECT 1 FROM servicios WHERE nombre = 'Vacunación');
