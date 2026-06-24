-- Boxes de atención por sucursal
INSERT INTO boxes (nombre, sucursal, disponible, tipo)
SELECT 'Box 1', 'Chillán', true, 'CONSULTA'
WHERE NOT EXISTS (SELECT 1 FROM boxes WHERE nombre = 'Box 1' AND sucursal = 'Chillán');

INSERT INTO boxes (nombre, sucursal, disponible, tipo)
SELECT 'Box 2', 'Chillán', true, 'PROCEDIMIENTO'
WHERE NOT EXISTS (SELECT 1 FROM boxes WHERE nombre = 'Box 2' AND sucursal = 'Chillán');

INSERT INTO boxes (nombre, sucursal, disponible, tipo)
SELECT 'Box 1', 'Los Ángeles', true, 'CONSULTA'
WHERE NOT EXISTS (SELECT 1 FROM boxes WHERE nombre = 'Box 1' AND sucursal = 'Los Ángeles');
