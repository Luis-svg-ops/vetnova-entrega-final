-- Canales de notificación de ejemplo para el usuario 1
INSERT INTO canal_notificacion (usuario_id, tipo, destino, activo)
SELECT 1, 'EMAIL', 'usuario1@vetnova.cl', true
WHERE NOT EXISTS (SELECT 1 FROM canal_notificacion WHERE usuario_id = 1 AND tipo = 'EMAIL');

INSERT INTO canal_notificacion (usuario_id, tipo, destino, activo)
SELECT 1, 'SMS', '+56912345678', true
WHERE NOT EXISTS (SELECT 1 FROM canal_notificacion WHERE usuario_id = 1 AND tipo = 'SMS');
