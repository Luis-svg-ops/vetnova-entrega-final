# VetNova — Guía de Demostración Postman

> **Sucursales válidas:** `CHILLAN` · `LOS_ANGELES` · `TALCA`  
> **IDs:** guardar el `id` de cada respuesta para usarlo en los pasos siguientes.  
> **Sin token requerido** en ningún MS salvo vetnova_auth.

---

## 1. vetnova_auth — puerto 8081

### Paso 1 — Registrar usuario cliente
**POST** `http://localhost:8081/api/auth/register`
```json
{
  "nombre": "María González",
  "email": "maria@gmail.com",
  "telefono": "+56912345678",
  "password": "Pass1234!",
  "rol": "CLIENTE"
}
```
**Verificar:** `201` · campo `email` presente

---

### Paso 2 — Iniciar sesión
**POST** `http://localhost:8081/api/auth/login`
```json
{
  "email": "maria@gmail.com",
  "password": "Pass1234!"
}
```
**Verificar:** `200` · campo `token` presente

---

### Paso 3 — Registrar cliente en el sistema
**POST** `http://localhost:8081/api/v1/clientes`
```json
{
  "usuarioId": 1,
  "rut": "12345678-9",
  "nombre": "María",
  "apellido": "González",
  "email": "maria@gmail.com",
  "telefono": "+56912345678",
  "direccion": "Av. O'Higgins 123, Chillán"
}
```
**Verificar:** `201` · campo `id` → guardar como `clienteId`

---

### Paso 4 — Crear usuario interno (veterinario)
**POST** `http://localhost:8081/api/usuarios`
```json
{
  "nombre": "Dr. Carlos Ruiz",
  "email": "carlos.ruiz@vetnova.cl",
  "telefono": "+56911111111",
  "password": "Admin1234!",
  "nombreRol": "VETERINARIO"
}
```
**Verificar:** `201` · campo `id` → guardar como `veterinarioId`

---

### Paso 5 — Crear rol con permisos
**POST** `http://localhost:8081/api/roles`
```json
{
  "nombreRol": "RECEPCIONISTA",
  "descripcion": "Gestión de citas y clientes",
  "permisos": ["CREAR_CITA", "CANCELAR_CITA"]
}
```
**Verificar:** `201` · campo `id` → guardar como `rolId`

---

### Paso 6 — Asignar permiso adicional
**PUT** `http://localhost:8081/api/roles/{rolId}/permisos`
```json
{
  "permiso": "VER_HISTORIAL"
}
```
**Verificar:** `200`

---

### Paso 7 — Desactivar y reactivar cuenta
**PATCH** `http://localhost:8081/api/usuarios/{veterinarioId}/desactivar`  
**Verificar:** `200`

**PATCH** `http://localhost:8081/api/usuarios/{veterinarioId}/activar`  
**Verificar:** `200`

---

### ❌ Error — Email duplicado
**POST** `http://localhost:8081/api/auth/register`
```json
{
  "nombre": "Copia",
  "email": "maria@gmail.com",
  "telefono": "+56999999999",
  "password": "Pass1234!",
  "rol": "CLIENTE"
}
```
**Verificar:** `400` o `409` · email ya registrado

---

## 2. vetnova_ficha — puerto 8087

### Paso 1 — Registrar mascota
**POST** `http://localhost:8087/api/v1/mascotas`
```json
{
  "clienteId": 1,
  "nombre": "Firulais",
  "especie": "Perro",
  "raza": "Labrador",
  "sexo": "Macho",
  "fechaNacimiento": "2020-03-15",
  "peso": 28.5,
  "microchip": "985112345678901",
  "activo": true
}
```
**Verificar:** `201` · campo `id` → guardar como `mascotaId` · `activo` = `true`

---

### Paso 2 — Consultar mascota (con nombre cliente enriquecido)
**GET** `http://localhost:8087/api/v1/mascotas/{mascotaId}`  
**Verificar:** `200` · campo `nombreCliente` presente (puede ser `null` si auth no responde)

---

### Paso 3 — Crear ficha clínica
**POST** `http://localhost:8087/api/v1/fichas`
```json
{
  "mascotaId": 1,
  "observacionesGenerales": "Paciente sano, vacunas al día"
}
```
**Verificar:** `201` · campo `id` → guardar como `fichaId`

---

### Paso 4 — Registrar evolución (inmutable)
**POST** `http://localhost:8087/api/v1/evoluciones`
```json
{
  "fichaId": 1,
  "veterinarioId": 1,
  "citaId": 1,
  "anamnesis": "Dueño reporta decaimiento y fiebre desde ayer",
  "examenFisico": "Temperatura 39.5°C, mucosas rosadas, linfonodos normales",
  "diagnostico": "Síndrome febril agudo",
  "tratamiento": "Dipirona 500mg IM, reposo y abundante agua",
  "observaciones": "Control en 48 horas si no mejora"
}
```
**Verificar:** `201` · campo `id` → guardar como `evolucionId`

---

### Paso 5 — Registrar receta (inmutable)
**POST** `http://localhost:8087/api/v1/recetas`
```json
{
  "fichaId": 1,
  "veterinarioId": 1,
  "medicamentos": [
    {
      "nombre": "Amoxicilina 500mg",
      "dosis": "1 comprimido",
      "frecuencia": "Cada 8 horas",
      "duracionDias": 7
    }
  ],
  "fechaVencimiento": "2025-07-27"
}
```
**Verificar:** `201`

---

### Paso 6 — Registrar vacuna (inmutable)
**POST** `http://localhost:8087/api/v1/vacunas`
```json
{
  "fichaId": 1,
  "veterinarioId": 1,
  "nombre": "Antirrábica",
  "fechaAplicacion": "2025-06-27",
  "proximaFecha": "2026-06-27"
}
```
**Verificar:** `201`

---

### Paso 7 — Emitir certificado (inmutable)
**POST** `http://localhost:8087/api/v1/certificados`
```json
{
  "fichaId": 1,
  "veterinarioId": 1,
  "tipo": "SALUD",
  "descripcion": "El paciente Firulais se encuentra en buen estado de salud general."
}
```
**Verificar:** `201`

---

### Paso 8 — Registrar procedimiento
**POST** `http://localhost:8087/api/v1/procedimientos`
```json
{
  "fichaId": 1,
  "veterinarioId": 1,
  "nombre": "Limpieza dental",
  "tipo": "PROFILAXIS",
  "descripcion": "Limpieza dental con ultrasonido bajo anestesia local",
  "fecha": "2025-06-27",
  "resultado": "Exitoso, sin complicaciones"
}
```
**Verificar:** `201`

---

### ❌ Error — Intentar editar evolución (inmutabilidad)
**PUT** `http://localhost:8087/api/v1/evoluciones/{evolucionId}`
```json
{
  "diagnostico": "Intentando editar"
}
```
**Verificar:** `422` · registro inmutable

---

### ♻️ Soft delete — Eliminar mascota
**DELETE** `http://localhost:8087/api/v1/mascotas/{mascotaId}`  
**Verificar:** `200` · campo `activo` = `false` (no se elimina de la BD)

---

## 3. vetnova_agenda — puerto 8086

### Paso 1 — Crear box
**POST** `http://localhost:8086/api/v1/boxes`
```json
{
  "nombre": "Box 1",
  "sucursal": "CHILLAN"
}
```
**Verificar:** `201` · campo `id` → guardar como `boxId` · `disponible` = `true`

---

### Paso 2 — Registrar disponibilidad veterinario
**POST** `http://localhost:8086/api/v1/disponibilidad`
```json
{
  "veterinarioId": 1,
  "sucursal": "CHILLAN",
  "diaSemana": "LUNES",
  "horaInicio": "08:00",
  "horaFin": "17:00"
}
```
**Verificar:** `201`

---

### Paso 3 — Agendar cita
**POST** `http://localhost:8086/api/v1/citas`
```json
{
  "clienteId": 1,
  "mascotaId": 1,
  "veterinarioId": 1,
  "servicioId": 1,
  "boxId": 1,
  "sucursal": "CHILLAN",
  "fechaHora": "2030-08-15T10:00:00",
  "duracionMinutos": 30,
  "canal": "WEB"
}
```
**Verificar:** `201` · `estado` = `"pendiente"` · campo `id` → guardar como `citaId`

---

### Paso 4 — Consultar agenda del día (enriquecida)
**GET** `http://localhost:8086/api/v1/citas/agenda`  
**Verificar:** `200` · campos `nombreCliente`, `nombreMascota`, `nombreVeterinario` presentes

---

### Paso 5 — Reprogramar cita
**PUT** `http://localhost:8086/api/v1/citas/{citaId}`
```json
{
  "fechaHora": "2030-09-01T11:00:00",
  "duracionMinutos": 45
}
```
**Verificar:** `200` · `fechaHora` actualizado

---

### Paso 6 — Confirmar cita
**PUT** `http://localhost:8086/api/v1/citas/{citaId}/confirmar`  
**Verificar:** `200` · `estado` = `"confirmada"`

---

### Paso 7 — Iniciar atención
**PUT** `http://localhost:8086/api/v1/citas/{citaId}/iniciar`  
**Verificar:** `200` · `estado` = `"en curso"`

---

### Paso 8 — Completar atención
**PUT** `http://localhost:8086/api/v1/citas/{citaId}/completar`  
**Verificar:** `200` · `estado` = `"completada"`

---

### Paso 9 — Crear recordatorio
**POST** `http://localhost:8086/api/v1/recordatorios`
```json
{
  "citaId": 1,
  "tipo": "EMAIL"
}
```
**Verificar:** `201`

---

### ❌ Error — Fecha en el pasado
**POST** `http://localhost:8086/api/v1/citas`
```json
{
  "clienteId": 1,
  "veterinarioId": 1,
  "servicioId": 1,
  "sucursal": "CHILLAN",
  "fechaHora": "2020-01-01T10:00:00",
  "duracionMinutos": 30,
  "canal": "WEB"
}
```
**Verificar:** `400` · `"La fecha y hora deben ser futuras"`

---

### ❌ Error — Sucursal inválida
**POST** `http://localhost:8086/api/v1/citas`
```json
{
  "clienteId": 1,
  "veterinarioId": 1,
  "servicioId": 1,
  "sucursal": "VALPARAISO",
  "fechaHora": "2030-08-15T10:00:00",
  "duracionMinutos": 30,
  "canal": "WEB"
}
```
**Verificar:** `404` · `"Sucursal no encontrada"`

---

## 4. vetnova_catalogo — puerto 8082

### Paso 1 — Crear categoría
**POST** `http://localhost:8082/api/v1/categorias`
```json
{
  "nombre": "Alimentos",
  "descripcion": "Alimentos y suplementos para mascotas",
  "tipo": "PRODUCTO"
}
```
**Verificar:** `201` · campo `id` → guardar como `categoriaId`

---

### Paso 2 — Crear producto
**POST** `http://localhost:8082/api/v1/productos`
```json
{
  "nombre": "Purina Pro Plan 15kg",
  "descripcion": "Alimento premium para perros adultos",
  "precio": 45990.0,
  "categoriaId": 1
}
```
**Verificar:** `201` · campo `id` → guardar como `productoId`

---

### Paso 3 — Crear servicio veterinario
**POST** `http://localhost:8082/api/v1/servicios`
```json
{
  "nombre": "Consulta General",
  "descripcion": "Consulta médica veterinaria estándar",
  "precio": 25000.0,
  "duracionMinutos": 30,
  "activo": true,
  "categoriaId": 1
}
```
**Verificar:** `201` · campo `id` → guardar como `servicioId`

---

### Paso 4 — Buscar productos disponibles por sucursal
**GET** `http://localhost:8082/api/v1/catalogo/disponibles?sucursal=CHILLAN`  
**Verificar:** `200` · lista de productos activos

---

### Paso 5 — Buscar por nombre
**GET** `http://localhost:8082/api/v1/catalogo/buscar?nombre=Purina`  
**Verificar:** `200` · lista con productos que contienen "Purina"

---

### Paso 6 — Crear oferta
**POST** `http://localhost:8082/api/v1/ofertas`
```json
{
  "productoId": 1,
  "descuento": 15.0,
  "fechaInicio": "2025-07-01",
  "fechaFin": "2025-07-31",
  "activa": true
}
```
**Verificar:** `201`

---

### Paso 7 — Desactivar y reactivar producto
**PUT** `http://localhost:8082/api/v1/productos/{productoId}/desactivar`  
**Verificar:** `200` · `activo` = `false`

**PUT** `http://localhost:8082/api/v1/productos/{productoId}/activar`  
**Verificar:** `200` · `activo` = `true`

---

### ❌ Error — Sucursal inválida
**GET** `http://localhost:8082/api/v1/catalogo/disponibles?sucursal=INVALIDA`  
**Verificar:** `404` · `"Sucursal no encontrada"`

---

## 5. vetnova_inventario — puerto 8083

### Paso 1 — Registrar producto en inventario
**POST** `http://localhost:8083/api/v1/inventario/productos`
```json
{
  "sku": "PPP15KG",
  "nombre": "Purina Pro Plan 15kg",
  "descripcion": "Alimento premium para perros adultos",
  "precio": 45990.0
}
```
**Verificar:** `201` · campo `id` → guardar como `productoInvId`

---

### Paso 2 — Registrar proveedor
**POST** `http://localhost:8083/api/v1/proveedors`
```json
{
  "nombre": "Distribuidora PetFood Chile",
  "rut": "76543210-2",
  "email": "contacto@petfood.cl",
  "telefono": "+56223456789",
  "direccion": "Av. Industrial 456, Santiago"
}
```
**Verificar:** `201` · campo `id` → guardar como `proveedorId`

---

### Paso 3 — Asociar producto al proveedor
**POST** `http://localhost:8083/api/v1/proveedors/{proveedorId}/productos`
```json
{
  "productoId": 1
}
```
**Verificar:** `200` o `201`

---

### Paso 4 — Abrir inventario en sucursal
**POST** `http://localhost:8083/api/v1/inventarios`
```json
{
  "productoId": 1,
  "sucursal": "CHILLAN",
  "stockDisponible": 0,
  "stockMinimo": 5
}
```
**Verificar:** `201` · campo `id` → guardar como `inventarioId` · `stockDisponible` = `0`

---

### Paso 5 — Registrar entrada de stock
**POST** `http://localhost:8083/api/v1/inventarios/{inventarioId}/entrada`
```json
{
  "cantidad": 20,
  "responsable": "Juan Bodeguero"
}
```
**Verificar:** `200` · `stockDisponible` = `20`

---

### Paso 6 — Registrar salida (bajar stock a crítico)
**POST** `http://localhost:8083/api/v1/inventarios/{inventarioId}/salida`
```json
{
  "cantidad": 18,
  "motivo": "Ventas del mes"
}
```
**Verificar:** `200` · `stockDisponible` = `2` (bajo el mínimo de 5)

---

### Paso 7 — Ver alertas de stock crítico
**GET** `http://localhost:8083/api/v1/alertastocks`  
**Verificar:** `200` · alerta automática para el producto con stock < 5

---

### Paso 8 — Hacer pedido al proveedor
**POST** `http://localhost:8083/api/v1/pedidoproveedors`
```json
{
  "proveedorId": 1,
  "sucursal": "CHILLAN",
  "responsable": "Ana Administradora",
  "detalles": [
    {
      "productoId": 1,
      "cantidad": 50,
      "precioUnitario": 35000.0
    }
  ]
}
```
**Verificar:** `201` · campo `id` → guardar como `pedidoId`

---

### Paso 9 — Solicitud de reposición entre sucursales
**POST** `http://localhost:8083/api/v1/solicitudreposicions`
```json
{
  "inventarioId": 1,
  "cantidadSolicitada": 10,
  "motivo": "Stock crítico en sucursal Chillán",
  "solicitadoPor": 1
}
```
**Verificar:** `201` · campo `id` → guardar como `solicitudId`

---

### Paso 10 — Aprobar solicitud
**PUT** `http://localhost:8083/api/v1/solicitudreposicions/{solicitudId}/aprobar`  
**Verificar:** `200`

---

### Paso 11 — Transferencia física de stock
**POST** `http://localhost:8083/api/v1/transferenciastocks`
```json
{
  "sucursalOrigen": "LOS_ANGELES",
  "sucursalDestino": "TALCA",
  "productoId": 1,
  "cantidad": 10
}
```
**Verificar:** `201`

---

### ❌ Error — Salida con stock insuficiente
**POST** `http://localhost:8083/api/v1/inventarios/{inventarioId}/salida`
```json
{
  "cantidad": 9999,
  "motivo": "Prueba de error"
}
```
**Verificar:** `400` o `422` · stock insuficiente

---

## 6. vetnova_ventas — puerto 8084

### Paso 1 — Crear carrito
**POST** `http://localhost:8084/api/v1/carritos`
```json
{
  "clienteId": 1
}
```
**Verificar:** `201` · campo `id` → guardar como `carritoId`

---

### Paso 2 — Agregar producto al carrito
**POST** `http://localhost:8084/api/v1/carritos/{carritoId}/items`
```json
{
  "itemId": 1,
  "tipo": "PRODUCTO",
  "nombre": "Purina Pro Plan 15kg",
  "cantidad": 2,
  "precio": 45990.0
}
```
**Verificar:** `201`

---

### Paso 3 — Ver total del carrito
**GET** `http://localhost:8084/api/v1/carritos/{carritoId}/total`  
**Verificar:** `200` · campo `total` = `91980`

---

### Paso 4 — Crear orden de venta
**POST** `http://localhost:8084/api/v1/ordenes`
```json
{
  "clienteId": 1,
  "sucursal": "CHILLAN",
  "detalles": [
    {
      "productoId": 1,
      "nombreProducto": "Purina Pro Plan 15kg",
      "cantidad": 2,
      "precioUnitario": 45990.0
    }
  ]
}
```
**Verificar:** `201` · campo `id` → guardar como `ordenId` · `estado` = `"PENDIENTE"`

---

### Paso 5 — Confirmar orden
**PUT** `http://localhost:8084/api/v1/ordenes/{ordenId}/confirmar`  
**Verificar:** `200` · `estado` = `"CONFIRMADA"`

---

### Paso 6 — Registrar pago
**POST** `http://localhost:8084/api/v1/pagos`
```json
{
  "ordenId": 1,
  "metodo": "DEBITO",
  "monto": 91980.0,
  "referencia": "TXN-2025-001"
}
```
**Verificar:** `201` · campo `id` → guardar como `pagoId`

---

### Paso 7 — Procesar pago
**PUT** `http://localhost:8084/api/v1/pagos/{pagoId}/procesar`  
**Verificar:** `200`

---

### ❌ Error — Cancelar orden ya confirmada
**PUT** `http://localhost:8084/api/v1/ordenes/{ordenId}/cancelar`  
**Verificar:** `400` o `422`

---

## 7. vetnova_facturacion — puerto 8090

### Paso 1 — Emitir boleta
**POST** `http://localhost:8090/documentos`
```json
{
  "ordenId": 1,
  "clienteId": 1,
  "tipo": "BOLETA",
  "neto": 77294.0,
  "total": 91980.0,
  "rutEmisor": "76000001-1",
  "rutReceptor": "12345678-9",
  "sucursal": "CHILLAN"
}
```
**Verificar:** `201` · campo `id` → guardar como `documentoId`

---

### Paso 2 — Emitir factura
**POST** `http://localhost:8090/documentos`
```json
{
  "ordenId": 1,
  "clienteId": 1,
  "tipo": "FACTURA",
  "neto": 77294.0,
  "total": 91980.0,
  "rutEmisor": "76000001-1",
  "rutReceptor": "76543210-2",
  "sucursal": "CHILLAN"
}
```
**Verificar:** `201`

---

### Paso 3 — Consultar documentos
**GET** `http://localhost:8090/documentos`  
**Verificar:** `200` · lista con los documentos creados

---

### Paso 4 — Anular documento
**PUT** `http://localhost:8090/documentos/{documentoId}/anular`
```json
{
  "motivo": "Error en monto ingresado"
}
```
**Verificar:** `200`

---

### Paso 5 — Registrar folio SII
**POST** `http://localhost:8090/folios`
```json
{
  "sucursal": "CHILLAN",
  "tipoDocumento": "BOLETA",
  "folioDesde": 1001,
  "folioHasta": 2000
}
```
**Verificar:** `201`

---

### ❌ Error — Orden inexistente
**POST** `http://localhost:8090/documentos`
```json
{
  "ordenId": 99999,
  "clienteId": 1,
  "tipo": "BOLETA",
  "neto": 1000.0,
  "total": 1190.0,
  "rutEmisor": "76000001-1",
  "rutReceptor": "12345678-9",
  "sucursal": "CHILLAN"
}
```
**Verificar:** `404` · orden no encontrada

---

## 8. vetnova_envio — puerto 8085

### Paso 1 — Crear envío
**POST** `http://localhost:8085/api/v1/envios`
```json
{
  "ordenId": 1,
  "tipoEnvio": "DOMICILIO",
  "idSucursalOrigen": "CHILLAN",
  "direccionEntrega": "Av. O'Higgins 123, Chillán"
}
```
**Verificar:** `201` · campo `id` → guardar como `envioId`

---

### Paso 2 — Actualizar estado del envío
**PUT** `http://localhost:8085/api/v1/envios/{envioId}/estado`
```json
{
  "estado": "EN_RUTA",
  "observacion": "Paquete en camino al domicilio"
}
```
**Verificar:** `200` · `estado` = `"EN_RUTA"`

---

### Paso 3 — Tracking del envío
**GET** `http://localhost:8085/api/v1/envios/{envioId}/tracking`  
**Verificar:** `200` · historial de estados

---

### Paso 4 — Transferencia de stock entre sucursales
**POST** `http://localhost:8085/api/v1/transferencias`
```json
{
  "idProducto": 1,
  "idSucursalOrigen": "LOS_ANGELES",
  "idSucursalDestino": "TALCA",
  "cantidad": 10,
  "observacion": "Reposición urgente por stock crítico"
}
```
**Verificar:** `201`

---

### Paso 5 — Marcar como entregado
**PUT** `http://localhost:8085/api/v1/envios/{envioId}/estado`
```json
{
  "estado": "ENTREGADO",
  "observacion": "Entregado al cliente en domicilio"
}
```
**Verificar:** `200` · `estado` = `"ENTREGADO"`

---

### ❌ Error — Orden inexistente
**POST** `http://localhost:8085/api/v1/envios`
```json
{
  "ordenId": 99999,
  "tipoEnvio": "DOMICILIO",
  "idSucursalOrigen": "CHILLAN",
  "direccionEntrega": "Calle Falsa 123"
}
```
**Verificar:** `404` · orden no encontrada (dura vs ventas)

---

## 9. vetnova_laboratorio — puerto 8089

### Paso 1 — Crear tipo de examen
**POST** `http://localhost:8089/tipos-examen`
```json
{
  "nombre": "Hemograma Completo",
  "descripcion": "Análisis completo de sangre",
  "tiempoEstimadoHoras": 24,
  "requiereMuestra": true,
  "instrucciones": "Ayuno de 8 horas previo a la extracción"
}
```
**Verificar:** `201` · campo `id` → guardar como `tipoExamenId`

---

### Paso 2 — Crear orden de examen
**POST** `http://localhost:8089/ordenes-examen`
```json
{
  "mascotaId": 1,
  "veterinarioId": 1,
  "tipoExamenId": 1,
  "descripcion": "Control post-operatorio, revisar glóbulos blancos",
  "fechaProgramada": "2030-08-16T09:00:00"
}
```
**Verificar:** `201` · campo `id` → guardar como `ordenExamenId`

---

### Paso 3 — Programar orden
**PUT** `http://localhost:8089/ordenes-examen/{ordenExamenId}/programar`
```json
{
  "fechaProgramada": "2030-08-16T09:00:00"
}
```
**Verificar:** `200`

---

### Paso 4 — Registrar muestra
**POST** `http://localhost:8089/muestras`
```json
{
  "ordenExamenId": 1,
  "tipo": "SANGRE",
  "codigoMuestra": "MUE-2025-001",
  "descripcion": "Muestra de sangre venosa extraída en consulta"
}
```
**Verificar:** `201` · campo `id` → guardar como `muestraId`

---

### Paso 5 — Recepcionar muestra
**PUT** `http://localhost:8089/muestras/{muestraId}/recepcion`
```json
{
  "responsableId": 2
}
```
**Verificar:** `200`

---

### Paso 6 — Registrar procesamiento
**POST** `http://localhost:8089/procesamientos`
```json
{
  "muestraId": 1,
  "tecnicoId": 2,
  "metodologia": "Análisis hematológico automatizado Sysmex XN-1000",
  "observaciones": "Muestra en buen estado"
}
```
**Verificar:** `201` · campo `id` → guardar como `procesamientoId`

---

### Paso 7 — Completar procesamiento
**PUT** `http://localhost:8089/procesamientos/{procesamientoId}/completar`
```json
{
  "fechaFin": "2030-08-16T14:00:00",
  "observaciones": "Procesamiento completado sin incidencias"
}
```
**Verificar:** `200`

---

### Paso 8 — Registrar resultado
**POST** `http://localhost:8089/resultados-examen`
```json
{
  "ordenExamenId": 1,
  "muestraId": 1,
  "tecnicoId": 2,
  "resultado": "Hemograma normal. Glóbulos blancos 7.200/μL dentro del rango.",
  "observaciones": "Sin anomalías detectadas"
}
```
**Verificar:** `201` · campo `id` → guardar como `resultadoId`

---

### Paso 9 — Publicar resultado
**PUT** `http://localhost:8089/resultados-examen/{resultadoId}/publicar`  
**Verificar:** `200`

---

### ❌ Error — Mascota inexistente
**POST** `http://localhost:8089/ordenes-examen`
```json
{
  "mascotaId": 99999,
  "veterinarioId": 1,
  "tipoExamenId": 1,
  "descripcion": "Prueba de error"
}
```
**Verificar:** `404` · `"Mascota no encontrada"` (dura vs vetnova_ficha)

---

## 10. vetnova_notificaciones — puerto 8092

### Paso 1 — Crear plantilla
**POST** `http://localhost:8092/plantillas`
```json
{
  "nombre": "RECORDATORIO_CITA",
  "contenido": "Estimado {{nombre}}, le recordamos su cita el {{fecha}} a las {{hora}}.",
  "tipo": "EMAIL"
}
```
**Verificar:** `201` · campo `id` → guardar como `plantillaId`

---

### Paso 2 — Crear notificación manual
**POST** `http://localhost:8092/notificaciones`
```json
{
  "usuarioId": 1,
  "tipo": "RECORDATORIO",
  "mensaje": "Su mascota Firulais tiene cita el lunes 15 de septiembre a las 10:00",
  "canal": "EMAIL"
}
```
**Verificar:** `201` · campo `id` → guardar como `notificacionId` · `leida` = `false`

---

### Paso 3 — Contar no leídas
**GET** `http://localhost:8092/notificaciones/no-leidas/count`  
**Verificar:** `200` · campo `count` > `0`

---

### Paso 4 — Marcar como leída
**PUT** `http://localhost:8092/notificaciones/{notificacionId}/leer`  
**Verificar:** `200` · `leida` = `true`

---

### Paso 5 — Reenviar notificación
**POST** `http://localhost:8092/notificaciones/{notificacionId}/reenviar`  
**Verificar:** `200`

---

### ❌ Error — Notificación inexistente
**PUT** `http://localhost:8092/notificaciones/99999/leer`  
**Verificar:** `404`

---

## 11. vetnova_reportes — puerto 8091

### Paso 1 — Dashboard general
**GET** `http://localhost:8091/dashboard`  
**Verificar:** `200`

---

### Paso 2 — Reporte de atenciones
**POST** `http://localhost:8091/reportes-atencion`
```json
{
  "tipo": "ATENCION",
  "sucursal": "CHILLAN",
  "desde": "2025-01-01",
  "hasta": "2025-12-31",
  "generadoPor": 1
}
```
**Verificar:** `201` · campo `id` → guardar como `reporteId`

---

### Paso 3 — Reporte de ventas
**POST** `http://localhost:8091/reportes-venta`
```json
{
  "tipo": "VENTA",
  "sucursal": "CHILLAN",
  "desde": "2025-01-01",
  "hasta": "2025-12-31",
  "generadoPor": 1
}
```
**Verificar:** `201`

---

### Paso 4 — Reporte de stock
**POST** `http://localhost:8091/reportes-stock`
```json
{
  "tipo": "STOCK",
  "sucursal": "CHILLAN",
  "desde": "2025-01-01",
  "hasta": "2025-12-31",
  "generadoPor": 1
}
```
**Verificar:** `201`

---

### Paso 5 — Exportar reporte
**GET** `http://localhost:8091/reportes/{reporteId}/exportar`  
**Verificar:** `200`

---

### Paso 6 — Monitorear microservicio
**POST** `http://localhost:8091/monitor`
```json
{
  "microservicio": "vetnova_agenda",
  "estado": "UP",
  "mensaje": "Servicio operativo"
}
```
**Verificar:** `201`

---

### Paso 7 — Registrar incidente
**POST** `http://localhost:8091/incidentes`
```json
{
  "microservicio": "vetnova_laboratorio",
  "descripcion": "Latencia elevada — tiempo de respuesta > 2s",
  "severidad": "MEDIA"
}
```
**Verificar:** `201` · campo `id` → guardar como `incidenteId`

---

### Paso 8 — Crear respaldo
**POST** `http://localhost:8091/respaldos`
```json
{
  "descripcion": "Respaldo pre-defensa",
  "tipo": "COMPLETO"
}
```
**Verificar:** `201` · campo `id` → guardar como `respaldoId`

---

### Paso 9 — Restaurar respaldo
**POST** `http://localhost:8091/respaldos/{respaldoId}/restaurar`  
**Verificar:** `200`

---

### ❌ Error — Rango de fechas inválido
**POST** `http://localhost:8091/reportes-atencion`
```json
{
  "tipo": "ATENCION",
  "sucursal": "CHILLAN",
  "desde": "2025-12-31",
  "hasta": "2025-01-01",
  "generadoPor": 1
}
```
**Verificar:** `400`

---

## 12. vetnova_soporte — puerto 8088

### Paso 1 — Crear categoría de ticket
**POST** `http://localhost:8088/categorias-ticket`
```json
{
  "nombre": "Problemas técnicos",
  "descripcion": "Errores o fallas en el sistema",
  "areaPorDefecto": "TI",
  "prioridadDefault": "MEDIA"
}
```
**Verificar:** `201` · campo `id` → guardar como `categoriaTicketId`

---

### Paso 2 — Crear ticket de soporte
**POST** `http://localhost:8088/tickets`
```json
{
  "clienteId": 1,
  "motivo": "No puedo ver mis citas agendadas",
  "descripcion": "Al ingresar al portal las citas no aparecen en la lista.",
  "sucursalId": "CHILLAN"
}
```
**Verificar:** `201` · campo `id` → guardar como `ticketId` · `estado` = `"ABIERTO"`

---

### Paso 3 — Clasificar ticket
**PUT** `http://localhost:8088/tickets/{ticketId}/clasificar`
```json
{
  "categoriaId": 1,
  "prioridad": "MEDIA"
}
```
**Verificar:** `200`

---

### Paso 4 — Agregar respuesta al ticket
**POST** `http://localhost:8088/tickets/{ticketId}/respuestas`
```json
{
  "autorId": 2,
  "contenido": "Hemos revisado su caso. Limpie caché del navegador e intente nuevamente.",
  "visible": true
}
```
**Verificar:** `201`

---

### Paso 5 — Derivar ticket
**PUT** `http://localhost:8088/tickets/{ticketId}/derivar`
```json
{
  "responsableId": 3
}
```
**Verificar:** `200`

---

### Paso 6 — Cerrar ticket
**PUT** `http://localhost:8088/tickets/{ticketId}/cerrar`
```json
{
  "resolucion": "Problema resuelto limpiando caché del navegador."
}
```
**Verificar:** `200` · `estado` = `"CERRADO"`

---

### Paso 7 — Dejar valoración
**POST** `http://localhost:8088/valoraciones`
```json
{
  "ticketId": 1,
  "puntuacion": 5,
  "comentario": "Problema resuelto rápidamente, excelente atención."
}
```
**Verificar:** `201`

---

### Paso 8 — Consultar promedio de valoraciones
**GET** `http://localhost:8088/valoraciones/promedio`  
**Verificar:** `200` · campo `promedio` = `5.0`

---

### ❌ Error — Cerrar ticket sin respuestas
**POST** `http://localhost:8088/tickets`
```json
{
  "clienteId": 1,
  "motivo": "Ticket sin responder",
  "descripcion": "Prueba de error al cerrar sin respuesta.",
  "sucursalId": "CHILLAN"
}
```
Luego cerrar inmediatamente:  
**PUT** `http://localhost:8088/tickets/{nuevoTicketId}/cerrar`
```json
{
  "resolucion": "Cierre sin respuesta"
}
```
**Verificar:** `400` · `"El ticket debe tener al menos una respuesta"`

---

### ❌ Error — Cliente inexistente
**POST** `http://localhost:8088/tickets`
```json
{
  "clienteId": 99999,
  "motivo": "Prueba",
  "descripcion": "Error esperado",
  "sucursalId": "CHILLAN"
}
```
**Verificar:** `404` · usuario no encontrado (dura vs vetnova_auth)

---

## Resumen de validaciones especiales

| Tipo | MS | Endpoint | Esperado |
|------|----|----------|----------|
| Inmutabilidad | `vetnova_ficha` | `PUT /api/v1/evoluciones/{id}` | `422` |
| Inmutabilidad | `vetnova_ficha` | `DELETE /api/v1/recetas/{id}` | `422` |
| Inmutabilidad | `vetnova_ficha` | `PUT /api/v1/vacunas/{id}` | `422` |
| Inmutabilidad | `vetnova_ficha` | `DELETE /api/v1/certificados/{id}` | `422` |
| Soft delete | `vetnova_ficha` | `DELETE /api/v1/mascotas/{id}` | `200` + `activo: false` |
| Degradación suave | `vetnova_agenda` | `GET /api/v1/citas` con auth caído | `200` + `nombreCliente: null` |
| Degradación dura | `vetnova_laboratorio` | `POST /ordenes-examen` con ficha caída | `404` bloquea |
| Stock crítico | `vetnova_inventario` | Bajar stock < mínimo → `GET /alertastocks` | alerta automática |
