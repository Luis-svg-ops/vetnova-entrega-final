-- Fichas clínicas iniciales para pruebas
INSERT INTO fichas_clinicas (mascota_id, fecha_creacion, observaciones_generales)
SELECT 1, CURRENT_DATE, 'Paciente canino sano, control al día'
WHERE NOT EXISTS (SELECT 1 FROM fichas_clinicas WHERE mascota_id = 1);

INSERT INTO fichas_clinicas (mascota_id, fecha_creacion, observaciones_generales)
SELECT 2, CURRENT_DATE, 'Paciente felino con tratamiento dermatológico'
WHERE NOT EXISTS (SELECT 1 FROM fichas_clinicas WHERE mascota_id = 2);
