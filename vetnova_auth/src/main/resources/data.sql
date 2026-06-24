MERGE INTO roles_permisos (id, nombre_rol, descripcion, activo) KEY(id) VALUES (1, 'ADMIN_SISTEMA', 'Administrador general del sistema', true);
MERGE INTO roles_permisos (id, nombre_rol, descripcion, activo) KEY(id) VALUES (2, 'CLIENTE', 'Cliente web/app', true);
MERGE INTO roles_permisos (id, nombre_rol, descripcion, activo) KEY(id) VALUES (3, 'RECEPCIONISTA', 'Recepción y caja', true);
MERGE INTO roles_permisos (id, nombre_rol, descripcion, activo) KEY(id) VALUES (4, 'VETERINARIO', 'Profesional veterinario', true);
MERGE INTO roles_permisos (id, nombre_rol, descripcion, activo) KEY(id) VALUES (5, 'ADMIN_SUCURSAL', 'Administrador de sucursal', true);
MERGE INTO roles_permisos (id, nombre_rol, descripcion, activo) KEY(id) VALUES (6, 'BODEGUERO', 'Bodega y logística', true);

MERGE INTO rol_permiso_items (rol_id, permiso) KEY(rol_id, permiso) VALUES (1, 'USUARIOS_GESTIONAR');
MERGE INTO rol_permiso_items (rol_id, permiso) KEY(rol_id, permiso) VALUES (1, 'ROLES_GESTIONAR');
MERGE INTO rol_permiso_items (rol_id, permiso) KEY(rol_id, permiso) VALUES (1, 'SISTEMA_MONITOREAR');
MERGE INTO rol_permiso_items (rol_id, permiso) KEY(rol_id, permiso) VALUES (2, 'CUENTA_PROPIA');
MERGE INTO rol_permiso_items (rol_id, permiso) KEY(rol_id, permiso) VALUES (2, 'TICKETS_CREAR');
MERGE INTO rol_permiso_items (rol_id, permiso) KEY(rol_id, permiso) VALUES (3, 'TICKETS_GESTIONAR');
MERGE INTO rol_permiso_items (rol_id, permiso) KEY(rol_id, permiso) VALUES (3, 'ORDENES_EXAMEN_CREAR');
MERGE INTO rol_permiso_items (rol_id, permiso) KEY(rol_id, permiso) VALUES (4, 'ORDENES_EXAMEN_SOLICITAR');
MERGE INTO rol_permiso_items (rol_id, permiso) KEY(rol_id, permiso) VALUES (4, 'RESULTADOS_CONSULTAR');

-- Passwords con DelegatingPasswordEncoder: Admin1234, Cliente1234, Recep1234, Vet1234
MERGE INTO usuarios (id, nombre, email, telefono, password_hash, activo, fecha_creacion, rol_id) KEY(id) VALUES (1, 'Admin VetNova', 'admin@vetnova.cl', '+56911111111', '{noop}Admin1234', true, CURRENT_TIMESTAMP, 1);
MERGE INTO usuarios (id, nombre, email, telefono, password_hash, activo, fecha_creacion, rol_id) KEY(id) VALUES (2, 'Cliente Demo', 'cliente@vetnova.cl', '+56922222222', '{noop}Cliente1234', true, CURRENT_TIMESTAMP, 2);
MERGE INTO usuarios (id, nombre, email, telefono, password_hash, activo, fecha_creacion, rol_id) KEY(id) VALUES (3, 'Recepcionista Demo', 'recepcion@vetnova.cl', '+56933333333', '{noop}Recep1234', true, CURRENT_TIMESTAMP, 3);
MERGE INTO usuarios (id, nombre, email, telefono, password_hash, activo, fecha_creacion, rol_id) KEY(id) VALUES (4, 'Veterinario Demo', 'vet@vetnova.cl', '+56944444444', '{noop}Vet1234', true, CURRENT_TIMESTAMP, 4);
