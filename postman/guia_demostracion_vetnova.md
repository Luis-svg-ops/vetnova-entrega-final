# VetNova — Guía de Demostración Postman

> **Sucursales válidas:** `CHILLAN` · `LOS_ANGELES` · `TALCA`  
> **IDs:** guardar el `id` de cada respuesta para usarlo en los pasos siguientes.  
> **Sin token requerido** en ningún MS salvo vetnova_auth.

---

## 1. vetnova_auth — puerto 8081

### Paso 1 — Registrar usuario (cliente web)
**POST** `http://localhost:8081/api/auth/register`
```json
{
  "nombre": "María",
  "apellido": "González",
  "email": "maria@gmail.com",
  "password": "Pass1234!"
}
```
**Verificar:** `201` · campo `email` = `"maria@gmail.com"`

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
  "nombre": "María",
  "apellido": "González",
  "email": "maria@gmail.com",
  "telefono": "+56912345678",
  "direccion": "Av. O'Higgins 123, Chillán"
}
```
**Verificar:** `201` · campo `id` → guardar como `clienteId`

---

### Paso 4 — Crear usuario interno (recepcionista)
**POST** `http://localhost:8081/api/usuarios`
```json
{
  "nombre": "Ana",
  "apellido": "Soto",
  "email": "ana.soto@vetnova.cl",
  "password": "Admin1234!",
  "rolId": 1
}
```
**Verificar:** `201` · campo `id` → guardar como `veterinarioId` (simula veterinario)

---

### Paso 5 — Crear rol y asignar permiso
**POST** `http://localhost:8081/api/roles`
```json
{
  "nombre": "RECEPCIONISTA",
  "descripcion": "Gestión de citas y clientes"
}
```
**Verificar:** `201` · campo `id` → guardar como `rolId`

**PUT** `http://localhost:8081/api/roles/{rolId}/permisos`
```json
{
  "permiso": "CREAR_CITA"
}
```
**Verificar:** `200`

---

### Paso 6 — Desactivar y reactivar cuenta
**PATCH** `http://localhost:8081/api/usuarios/{id}/desactivar`  
**Verificar:** `200`

**PATCH** `http://localhost:8081/api/usuarios/{id}/activar`  
**Verificar:** `200`

---

### ❌ Error — Email duplicado
**POST** `http://localhost:8081/api/auth/register`
```json
{
  "nombre": "Otro",
  "apellido": "Usuario",
  "email": "maria@gmail.com",
  "password": "Pass1234!"
}
```
**Verificar:** `400` o `409` · mensaje de email ya registrado

---

## 2. vetnova_ficha — puerto 8087

### Paso 1 — Registrar mascota
**POST** `http://localhost:8087/api/v1/mascotas`
```json
{
  "nombre": "Firulais",
  "especie": "Perro",
  "raza": "Labrador",
  "fechaNacimiento": "2020-03-15",
  "clienteId": 1
}
```
**Verificar:** `201` · campo `id` → guardar como `mascotaId` · campo `activo` = `true`

---

### Paso 2 — Consultar mascota (enriquecida con nombre cliente)
**GET** `http://localhost:8087/api/v1/mascotas/{mascotaId}`  
**Verificar:** `200` · campo `nombreCliente` presente (puede ser `null` si auth no responde)

---

### Paso 3 — Crear ficha clínica
**POST** `http://localhost:8087/api/v1/fichas`
```json
{
  "mascotaId": 1
}
```
**Verificar:** `201` · campo `id` → guardar como `fichaId`

---

### Paso 4 — Registrar evolución (inmutable)
**POST** `http://localhost:8087/api/v1/evoluciones`
```json
{
  "fichaId": 1,
  "mascotaId": 1,
  "descripcion": "Paciente presenta fiebre 39.5°C. Se administra antitérmico.",
  "citaId": 1
}
```
**Verificar:** `201` · campo `id` → guardar como `evolucionId`

---

### Paso 5 — Registrar receta (inmutable)
**POST** `http://localhost:8087/api/v1/recetas`
```json
{
  "fichaId": 1,
  "mascotaId": 1,
  "medicamento": "Amoxicilina 500mg",
  "dosis": "1 comprimido cada 8 horas por 7 días",
  "veterinarioId": 1
}
```
**Verificar:** `201`

---

### Paso 6 — Registrar vacuna (inmutable)
**POST** `http://localhost:8087/api/v1/vacunas`
```json
{
  "fichaId": 1,
  "mascotaId": 1,
  "nombre": "Antirrábica",
  "fechaAplicacion": "2025-06-27",
  "proximaFecha": "2026-06-27",
  "veterinarioId": 1
}
```
**Verificar:** `201`

---

### Paso 7 — Emitir certificado (inmutable)
**POST** `http://localhost:8087/api/v1/certificados`
```json
{
  "fichaId": 1,
  "mascotaId": 1,
  "tipo": "SALUD",
  "descripcion": "El paciente Firulais se encuentra en buen estado de salud.",
  "veterinarioId": 1
}
```
**Verificar:** `201`

---

### ❌ Error — Intentar editar evolución (inmutabilidad)
**PUT** `http://localhost:8087/api/v1/evoluciones/{evolucionId}`
```json
{
  "descripcion": "Intentando editar"
}
```
**Verificar:** `422` · mensaje indica registro inmutable

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
**Verificar:** `201` · campo `id` → guardar como `boxId` · campo `disponible` = `true`

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
**Verificar:** `201` · campo `estado` = `"pendiente"` · campo `id` → guardar como `citaId`

---

### Paso 4 — Consultar agenda del día (enriquecida)
**GET** `http://localhost:8086/api/v1/citas/agenda`  
**Verificar:** `200` · lista con campos `nombreCliente`, `nombreMascota`, `nombreVeterinario`

---

### Paso 5 — Reprogramar cita
**PUT** `http://localhost:8086/api/v1/citas/{citaId}`
```json
{
  "fechaHora": "2030-09-01T11:00:00",
  "duracionMinutos": 45
}
```
**Verificar:** `200` · campo `fechaHora` actualizado

---

### Paso 6 — Confirmar cita
**PUT** `http://localhost:8086/api/v1/citas/{citaId}/confirmar`  
**Verificar:** `200` · campo `estado` = `"confirmada"`

---

### Paso 7 — Iniciar atención
**PUT** `http://localhost:8086/api/v1/citas/{citaId}/iniciar`  
**Verificar:** `200` · campo `estado` = `"en curso"`

---

### Paso 8 — Completar atención
**PUT** `http://localhost:8086/api/v1/citas/{citaId}/completar`  
**Verificar:** `200` · campo `estado` = `"completada"`

---

### Paso 9 — Crear recordatorio
**POST** `http://localhost:8086/api/v1/recordatorios`
```json
{
  "citaId": 1,
  "clienteId": 1,
  "mensaje": "Recordatorio: cita mañana a las 10:00 en sucursal Chillán",
  "fechaEnvio": "2030-09-14T09:00:00"
}
```
**Verificar:** `201`

---

### ❌ Error — Cita con fecha pasada
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
**Verificar:** `400` · mensaje `"La fecha y hora deben ser futuras"`

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
**Verificar:** `404` · mensaje `"Sucursal no encontrada"`

---

### ❌ Error — Cancelar cita ya completada
**PUT** `http://localhost:8086/api/v1/citas/{citaId}/cancelar`
```json
{
  "motivoCancelacion": "Error de prueba"
}
```
**Verificar:** `400` · mensaje `"No se puede cancelar cita completada"`

---

## 4. vetnova_catalogo — puerto 8082

### Paso 1 — Crear categoría
**POST** `http://localhost:8082/api/v1/categorias`
```json
{
  "nombre": "Alimentos",
  "descripcion": "Alimentos y suplementos para mascotas"
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
  "sku": "PPP15KG",
  "categoriaId": 1,
  "activo": true
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
  "activo": true
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

### Paso 6 — Desactivar producto
**PUT** `http://localhost:8082/api/v1/productos/{productoId}/desactivar`  
**Verificar:** `200` · campo `activo` = `false`

**PUT** `http://localhost:8082/api/v1/productos/{productoId}/activar`  
**Verificar:** `200` · campo `activo` = `true`

---

### ❌ Error — Sucursal inválida en búsqueda
**GET** `http://localhost:8082/api/v1/catalogo/disponibles?sucursal=INVALIDA`  
**Verificar:** `404` · mensaje `"Sucursal no encontrada"`

---

### ❌ Error — Nombre vacío en búsqueda
**GET** `http://localhost:8082/api/v1/catalogo/buscar?nombre=`  
**Verificar:** `400` · mensaje de nombre obligatorio

---

## 5. vetnova_inventario — puerto 8083

### Paso 1 — Registrar proveedor
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

### Paso 2 — Asociar producto al proveedor
**POST** `http://localhost:8083/api/v1/proveedors/{proveedorId}/productos`
```json
{
  "productoId": 1,
  "precioProveedor": 35000.0
}
```
**Verificar:** `201` o `200`

---

### Paso 3 — Abrir producto en inventario (sucursal)
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

### Paso 4 — Registrar entrada de stock
**POST** `http://localhost:8083/api/v1/inventarios/{inventarioId}/entrada`
```json
{
  "cantidad": 20,
  "referencia": "Pedido inicial proveedor"
}
```
**Verificar:** `200` · campo `stockDisponible` = `20`

---

### Paso 5 — Consultar inventario
**GET** `http://localhost:8083/api/v1/inventarios?sucursal=CHILLAN`  
**Verificar:** `200` · lista con `stockDisponible: 20`

---

### Paso 6 — Registrar salidas (bajar stock hasta crítico)
**POST** `http://localhost:8083/api/v1/inventarios/{inventarioId}/salida`
```json
{
  "cantidad": 3,
  "referencia": "Venta directa"
}
```
**Verificar:** `200` · `stockDisponible` = `17`  
*Repetir hasta bajar a 2 (por debajo del mínimo 5)*

---

### Paso 7 — Ver alertas de stock crítico
**GET** `http://localhost:8083/api/v1/alertastocks`  
**Verificar:** `200` · aparece alerta automática para el producto con stock < mínimo

---

### Paso 8 — Hacer pedido al proveedor
**POST** `http://localhost:8083/api/v1/pedidoproveedors`
```json
{
  "proveedorId": 1,
  "sucursal": "CHILLAN",
  "detalles": [
    {
      "productoId": 1,
      "cantidad": 50,
      "precioUnitario": 35000
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
  "sucursalOrigen": "LOS_ANGELES",
  "sucursalDestino": "TALCA",
  "productoId": 1,
  "cantidadSolicitada": 10,
  "solicitanteId": 1
}
```
**Verificar:** `201` · campo `id` → guardar como `solicitudId` · `estado` = `"PENDIENTE"`

---

### Paso 10 — Aprobar solicitud (Admin Sucursal)
**PUT** `http://localhost:8083/api/v1/solicitudreposicions/{solicitudId}/aprobar`  
**Verificar:** `200` · `estado` = `"APROBADA"`

---

### Paso 11 — Transferencia física de stock
**POST** `http://localhost:8083/api/v1/transferenciastocks`
```json
{
  "sucursalOrigen": "LOS_ANGELES",
  "sucursalDestino": "TALCA",
  "productoId": 1,
  "cantidad": 10,
  "solicitudId": 1
}
```
**Verificar:** `201`

---

### ❌ Error — Salida con stock insuficiente
**POST** `http://localhost:8083/api/v1/inventarios/{inventarioId}/salida`
```json
{
  "cantidad": 9999,
  "referencia": "Error de prueba"
}
```
**Verificar:** `400` o `422` · mensaje de stock insuficiente

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
  "productoId": 1,
  "cantidad": 2
}
```
**Verificar:** `201` · ítem agregado

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
      "cantidad": 2,
      "precioUnitario": 45990
    }
  ]
}
```
**Verificar:** `201` · campo `id` → guardar como `ordenId` · `estado` = `"PENDIENTE"`

---

### Paso 5 — Confirmar orden
**PUT** `http://localhost:8084/api/v1/ordenes/{ordenId}/confirmar`  
**Verificar:** `200` · campo `estado` = `"CONFIRMADA"`

---

### Paso 6 — Registrar pago
**POST** `http://localhost:8084/api/v1/pagos`
```json
{
  "ordenId": 1,
  "monto": 91980,
  "metodoPago": "TARJETA_CREDITO"
}
```
**Verificar:** `201` · campo `id` → guardar como `pagoId`

---

### Paso 7 — Procesar pago
**PUT** `http://localhost:8084/api/v1/pagos/{pagoId}/procesar`  
**Verificar:** `200` · campo `estado` = `"PROCESADO"` o `"CONFIRMADO"`

---

### ❌ Error — Cancelar orden ya confirmada
**PUT** `http://localhost:8084/api/v1/ordenes/{ordenId}/cancelar`  
**Verificar:** `400` o `422` · mensaje de orden no cancelable en ese estado

---

## 7. vetnova_facturacion — puerto 8090

### Paso 1 — Emitir boleta
**POST** `http://localhost:8090/documentos`
```json
{
  "tipo": "BOLETA",
  "ordenId": 1,
  "clienteId": 1
}
```
**Verificar:** `201` · campo `id` → guardar como `documentoId` · campo `tipo` = `"BOLETA"`

---

### Paso 2 — Emitir factura
**POST** `http://localhost:8090/documentos`
```json
{
  "tipo": "FACTURA",
  "ordenId": 1,
  "clienteId": 1
}
```
**Verificar:** `201`

---

### Paso 3 — Consultar documentos emitidos
**GET** `http://localhost:8090/documentos`  
**Verificar:** `200` · lista con los documentos creados

---

### Paso 4 — Anular documento
**PUT** `http://localhost:8090/documentos/{documentoId}/anular`  
**Verificar:** `200` · campo de estado indica anulado

---

### Paso 5 — Ver reporte tributario
**POST** `http://localhost:8090/reportes-tributarios`
```json
{
  "mes": 6,
  "anio": 2025
}
```
**Verificar:** `201`

---

### ❌ Error — Documento con orden inexistente
**POST** `http://localhost:8090/documentos`
```json
{
  "tipo": "BOLETA",
  "ordenId": 99999,
  "clienteId": 1
}
```
**Verificar:** `404` o `400` · orden no encontrada

---

## 8. vetnova_envio — puerto 8085

### Paso 1 — Crear envío
**POST** `http://localhost:8085/api/v1/envios`
```json
{
  "ordenId": 1,
  "direccionDestino": "Av. O'Higgins 123, Chillán",
  "sucursalOrigen": "CHILLAN",
  "clienteId": 1
}
```
**Verificar:** `201` · campo `id` → guardar como `envioId`

---

### Paso 2 — Actualizar estado del envío
**PUT** `http://localhost:8085/api/v1/envios/{envioId}/estado`
```json
{
  "estado": "EN_TRANSITO"
}
```
**Verificar:** `200` · campo `estado` = `"EN_TRANSITO"`

---

### Paso 3 — Tracking del envío
**GET** `http://localhost:8085/api/v1/envios/{envioId}/tracking`  
**Verificar:** `200` · historial de estados del envío

---

### Paso 4 — Transferencia de stock entre sucursales (envío físico)
**POST** `http://localhost:8085/api/v1/transferencias`
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

### Paso 5 — Marcar como entregado
**PUT** `http://localhost:8085/api/v1/envios/{envioId}/entrega`  
**Verificar:** `200` · campo `estado` = `"ENTREGADO"`

---

### ❌ Error — Envío con orden inexistente
**POST** `http://localhost:8085/api/v1/envios`
```json
{
  "ordenId": 99999,
  "direccionDestino": "Calle Falsa 123",
  "sucursalOrigen": "CHILLAN",
  "clienteId": 1
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
  "tiempoEstimadoHoras": 24
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
  "observaciones": "Control post-operatorio, revisar glóbulos blancos"
}
```
**Verificar:** `201` · campo `id` → guardar como `ordenExamenId`

---

### Paso 3 — Programar orden
**PUT** `http://localhost:8089/ordenes-examen/{ordenExamenId}/programar`  
**Verificar:** `200`

---

### Paso 4 — Registrar muestra
**POST** `http://localhost:8089/muestras`
```json
{
  "ordenExamenId": 1,
  "tipo": "SANGRE",
  "descripcion": "Muestra de sangre venosa extraída en consulta"
}
```
**Verificar:** `201` · campo `id` → guardar como `muestraId`

---

### Paso 5 — Recepcionar muestra en laboratorio
**PUT** `http://localhost:8089/muestras/{muestraId}/recepcion`  
**Verificar:** `200`

---

### Paso 6 — Registrar procesamiento
**POST** `http://localhost:8089/procesamientos`
```json
{
  "muestraId": 1,
  "tecnicoId": 2,
  "procedimiento": "Análisis hematológico automatizado"
}
```
**Verificar:** `201` · campo `id` → guardar como `procesamientoId`

---

### Paso 7 — Completar procesamiento
**PUT** `http://localhost:8089/procesamientos/{procesamientoId}/completar`  
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

### ❌ Error — Orden con mascota inactiva o inexistente
**POST** `http://localhost:8089/ordenes-examen`
```json
{
  "mascotaId": 99999,
  "veterinarioId": 1,
  "tipoExamenId": 1,
  "observaciones": "Prueba de error"
}
```
**Verificar:** `404` · `"Mascota no encontrada"` (dura vs vetnova_ficha)

---

## 10. vetnova_notificaciones — puerto 8092

### Paso 1 — Crear notificación manual
**POST** `http://localhost:8092/notificaciones`
```json
{
  "usuarioId": 1,
  "tipo": "RECORDATORIO",
  "mensaje": "Su mascota Firulais tiene cita el lunes 15 de septiembre a las 10:00",
  "canal": "EMAIL"
}
```
**Verificar:** `201` · campo `id` → guardar como `notificacionId` · campo `leida` = `false`

---

### Paso 2 — Listar notificaciones
**GET** `http://localhost:8092/notificaciones`  
**Verificar:** `200` · lista con al menos 1 notificación

---

### Paso 3 — Contar no leídas
**GET** `http://localhost:8092/notificaciones/no-leidas/count`  
**Verificar:** `200` · campo `count` > `0`

---

### Paso 4 — Marcar como leída
**PUT** `http://localhost:8092/notificaciones/{notificacionId}/leer`  
**Verificar:** `200` · campo `leida` = `true`

---

### Paso 5 — Reenviar notificación
**POST** `http://localhost:8092/notificaciones/{notificacionId}/reenviar`  
**Verificar:** `200`

---

### Paso 6 — Crear plantilla de mensaje
**POST** `http://localhost:8092/plantillas`
```json
{
  "nombre": "RECORDATORIO_CITA",
  "contenido": "Estimado {{nombre}}, le recordamos su cita el {{fecha}} a las {{hora}}.",
  "tipo": "EMAIL"
}
```
**Verificar:** `201`

---

### ❌ Error — Notificación a usuario inexistente
**POST** `http://localhost:8092/notificaciones`
```json
{
  "usuarioId": 99999,
  "tipo": "ALERTA",
  "mensaje": "Prueba de error",
  "canal": "EMAIL"
}
```
**Verificar:** `404` o `400`

---

## 11. vetnova_reportes — puerto 8091

### Paso 1 — Dashboard general
**GET** `http://localhost:8091/dashboard`  
**Verificar:** `200` · datos generales del sistema

---

### Paso 2 — Reporte de atenciones por sucursal
**POST** `http://localhost:8091/reportes-atencion`
```json
{
  "sucursal": "CHILLAN",
  "fechaInicio": "2025-01-01",
  "fechaFin": "2025-12-31"
}
```
**Verificar:** `201` · campo `id` → guardar como `reporteId`

---

### Paso 3 — Reporte de ventas
**POST** `http://localhost:8091/reportes-venta`
```json
{
  "sucursal": "CHILLAN",
  "fechaInicio": "2025-01-01",
  "fechaFin": "2025-12-31"
}
```
**Verificar:** `201`

---

### Paso 4 — Reporte de stock
**POST** `http://localhost:8091/reportes-stock`
```json
{
  "sucursal": "CHILLAN",
  "fechaInicio": "2025-01-01",
  "fechaFin": "2025-12-31"
}
```
**Verificar:** `201`

---

### Paso 5 — Exportar reporte
**GET** `http://localhost:8091/reportes/{reporteId}/exportar`  
**Verificar:** `200` · archivo o contenido exportable

---

### Paso 6 — Registrar monitoreo de microservicio
**POST** `http://localhost:8091/monitor`
```json
{
  "microservicio": "vetnova_agenda",
  "estado": "UP",
  "mensaje": "Servicio operativo — 138 tests pasando"
}
```
**Verificar:** `201`

---

### Paso 7 — Ver historial de monitoreo
**GET** `http://localhost:8091/monitor/vetnova_agenda/historial`  
**Verificar:** `200` · historial de estados del servicio

---

### Paso 8 — Registrar incidente
**POST** `http://localhost:8091/incidentes`
```json
{
  "microservicio": "vetnova_laboratorio",
  "descripcion": "Tiempo de respuesta elevado — latencia > 2s",
  "severidad": "MEDIA"
}
```
**Verificar:** `201` · campo `id` → guardar como `incidenteId`

---

### Paso 9 — Crear respaldo
**POST** `http://localhost:8091/respaldos`
```json
{
  "descripcion": "Respaldo pre-defensa",
  "tipo": "COMPLETO"
}
```
**Verificar:** `201` · campo `id` → guardar como `respaldoId`

---

### Paso 10 — Restaurar respaldo
**POST** `http://localhost:8091/respaldos/{respaldoId}/restaurar`  
**Verificar:** `200`

---

### ❌ Error — Reporte con rango de fechas inválido
**POST** `http://localhost:8091/reportes-atencion`
```json
{
  "sucursal": "CHILLAN",
  "fechaInicio": "2025-12-31",
  "fechaFin": "2025-01-01"
}
```
**Verificar:** `400` · fechaInicio no puede ser mayor que fechaFin

---

## 12. vetnova_soporte — puerto 8088

### Paso 1 — Crear categoría de ticket
**POST** `http://localhost:8088/categorias-ticket`
```json
{
  "nombre": "Problemas técnicos",
  "descripcion": "Errores o fallas en el sistema"
}
```
**Verificar:** `201` · campo `id` → guardar como `categoriaTicketId`

---

### Paso 2 — Crear ticket de soporte
**POST** `http://localhost:8088/tickets`
```json
{
  "titulo": "No puedo ver mis citas agendadas",
  "descripcion": "Al ingresar al portal las citas no aparecen en la lista.",
  "categoriaId": 1,
  "clienteId": 1
}
```
**Verificar:** `201` · campo `id` → guardar como `ticketId` · `estado` = `"ABIERTO"`

---

### Paso 3 — Agregar respuesta al ticket
**POST** `http://localhost:8088/tickets/{ticketId}/respuestas`
```json
{
  "mensaje": "Hemos revisado su caso. Limpie caché del navegador e intente nuevamente.",
  "operadorId": 2
}
```
**Verificar:** `201`

---

### Paso 4 — Clasificar ticket
**PUT** `http://localhost:8088/tickets/{ticketId}/clasificar`
```json
{
  "categoriaId": 1,
  "prioridad": "MEDIA"
}
```
**Verificar:** `200`

---

### Paso 5 — Derivar ticket
**PUT** `http://localhost:8088/tickets/{ticketId}/derivar`
```json
{
  "operadorDestinoId": 3,
  "motivo": "Requiere revisión técnica especializada"
}
```
**Verificar:** `200`

---

### Paso 6 — Cerrar ticket
**PUT** `http://localhost:8088/tickets/{ticketId}/cerrar`  
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
**Verificar:** `201` · campo `id` → guardar como `valoracionId`

---

### Paso 8 — Consultar promedio de valoraciones
**GET** `http://localhost:8088/valoraciones/promedio`  
**Verificar:** `200` · campo `promedio` = `5.0`

---

### ❌ Error — Cerrar ticket sin respuestas
**POST** `http://localhost:8088/tickets`
```json
{
  "titulo": "Ticket sin responder",
  "descripcion": "Prueba de error al cerrar sin respuesta.",
  "categoriaId": 1,
  "clienteId": 1
}
```
Luego intentar cerrar inmediatamente:  
**PUT** `http://localhost:8088/tickets/{nuevoTicketId}/cerrar`  
**Verificar:** `400` · mensaje `"El ticket debe tener al menos una respuesta"`

---

### ❌ Error — Ticket con usuario inexistente
**POST** `http://localhost:8088/tickets`
```json
{
  "titulo": "Prueba",
  "descripcion": "Error esperado",
  "categoriaId": 1,
  "clienteId": 99999
}
```
**Verificar:** `404` · usuario no encontrado (dura vs vetnova_auth)

---

## Resumen de validaciones especiales

| Tipo | MS | Qué probar | Esperado |
|------|----|-----------|----------|
| Inmutabilidad | `vetnova_ficha` | `PUT /api/v1/evoluciones/{id}` | `422` |
| Inmutabilidad | `vetnova_ficha` | `DELETE /api/v1/recetas/{id}` | `422` |
| Inmutabilidad | `vetnova_ficha` | `PUT /api/v1/vacunas/{id}` | `422` |
| Inmutabilidad | `vetnova_ficha` | `DELETE /api/v1/certificados/{id}` | `422` |
| Soft delete | `vetnova_ficha` | `DELETE /api/v1/mascotas/{id}` | `200` + `activo: false` |
| Degradación suave | `vetnova_agenda` | `GET /api/v1/citas` (auth caído) | `200` + `nombreCliente: null` |
| Degradación dura | `vetnova_laboratorio` | `POST /ordenes-examen` (ficha caída) | `404` bloquea creación |
| Stock crítico | `vetnova_inventario` | Bajar stock < mínimo → `GET /alertastocks` | alerta automática |
