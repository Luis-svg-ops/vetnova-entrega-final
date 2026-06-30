# Flujo General — Conexiones entre Microservicios VetNova

---

## Mapa de puertos

| Microservicio | Puerto | Rol |
|---|---|---|
| Gateway | 8080 | Punto de entrada único (todas las requests pasan por aquí) |
| vetnova_auth | 8081 | Login, usuarios, roles y tokens |
| vetnova_catalogo | 8082 | Productos, servicios, categorías, ofertas |
| vetnova_agenda | 8086 | Citas, boxes, disponibilidad veterinaria |
| vetnova_ficha | 8087 | Fichas clínicas, mascotas, historial médico |
| vetnova_inventario | — | Stock de productos |
| vetnova_ventas | — | Ventas y cobros |
| vetnova_facturacion | — | Facturas y boletas |
| vetnova_envio | — | Despachos entre sucursales |
| vetnova_laboratorio | — | Exámenes de laboratorio |
| vetnova_notificaciones | — | Emails y SMS |
| vetnova_soporte | — | Tickets de soporte |
| vetnova_reportes | — | Dashboards y reportes |

---

## Conexiones entre los 3 microservicios del alumno

```
┌─────────────────────────────────────────────────────────────┐
│                    vetnova_auth (8081)                       │
│              Login, usuarios, verificar cliente              │
└──────────────────────────┬──────────────────────────────────┘
                           │  GET /api/v1/usuarios/{id}
                           │  GET /api/usuarios/{id}/existe
                           │  (RestTemplate)
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                   vetnova_agenda (8086)                      │
│              Citas, boxes, disponibilidad                    │
└──────────────────────────┬──────────────────────────────────┘
                           │  GET /api/v1/mascotas/{id}
                           │  (RestTemplate)
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                   vetnova_ficha (8087)                       │
│              Mascotas, fichas, historial médico              │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                  vetnova_catalogo (8082)                     │
│              Productos, servicios, categorías                │
│                  (independiente, no llama a otros)           │
└─────────────────────────────────────────────────────────────┘
```

---

## Detalle de cada conexión

### 1. agenda → auth (verificar y obtener nombre del cliente)

Cuando se crea una cita, agenda llama a auth con dos propósitos:

**Verificar que el cliente existe:**
```
GET http://localhost:8081/api/usuarios/{clienteId}/existe
→ Respuesta: { "existe": true }
→ Si false o error → lanza ResourceNotFoundException("Cliente no encontrado")
```

**Obtener el nombre del cliente (para mostrarlo en GET /citas):**
```
GET http://localhost:8081/api/usuarios/{clienteId}
→ Respuesta: { "nombre": "Luis", "apellido": "Hernández", ... }
→ Retorna: "Luis Hernández"
→ Si falla → retorna null (degradación suave, no corta la cita)
```

**Esto significa:**
- Si auth está caído → la cita igual se crea, pero `nombreCliente` aparece como `null`
- Si el clienteId no existe en auth → la cita NO se crea (error 404)

---

### 2. agenda → ficha (verificar y obtener nombre de la mascota)

Cuando se crea una cita, agenda llama a ficha:

**Verificar que la mascota existe:**
```
GET http://localhost:8087/api/v1/mascotas/{mascotaId}
→ Si falla → lanza ResourceNotFoundException("Mascota no encontrada en el sistema")
```

**Obtener el nombre de la mascota (para mostrarlo en GET /citas):**
```
GET http://localhost:8087/api/v1/mascotas/{mascotaId}
→ Respuesta: { "nombre": "Max", "especie": "Perro", ... }
→ Retorna: "Max"
→ Si falla → retorna null (degradación suave)
```

---

### 3. catalogo — sin conexiones externas

catalogo es completamente autónomo. No llama a ningún otro microservicio.
Valida integridad interna (ej: categoría existe antes de crear producto) dentro de su propia BD.

---

## Flujo completo de punta a punta — "Agendar una cita"

```
PASO 1 — Login (vetnova_auth, puerto 8081)
POST http://localhost:8081/api/auth/login
Body: { "email": "recepcionista@vetnova.cl", "password": "1234" }
→ Responde: { "token": "uuid-token", "rol": "RECEPCIONISTA" }

PASO 2 — Verificar catálogo de servicios (vetnova_catalogo, puerto 8082)
GET http://localhost:8082/api/v1/catalogo/disponibles?sucursal=CHILLAN
→ Ve qué servicios están disponibles (ej: id=1 "Consulta General")

PASO 3 — Registrar mascota si no existe (vetnova_ficha, puerto 8087)
POST http://localhost:8087/api/v1/mascotas
Body: { "nombre": "Max", "especie": "Perro", "clienteId": 1 }
→ Responde: { "id": 1, "nombre": "Max" }

PASO 4 — Crear ficha clínica de la mascota (vetnova_ficha, puerto 8087)
POST http://localhost:8087/api/v1/fichas
Body: { "mascotaId": 1, "observacionesGenerales": "Primera visita" }
→ Responde: { "id": 1, "mascotaId": 1 }

PASO 5 — Configurar box (vetnova_agenda, puerto 8086)
POST http://localhost:8086/api/v1/boxes
Body: { "nombre": "Box 1", "sucursal": "CHILLAN" }
→ Responde: { "id": 1, "estado": "DISPONIBLE" }

PASO 6 — Agendar la cita (vetnova_agenda, puerto 8086)
POST http://localhost:8086/api/v1/citas
Body: {
  "clienteId": 1,
  "mascotaId": 1,
  "veterinarioId": 3,
  "servicioId": 1,
  "boxId": 1,
  "sucursal": "CHILLAN",
  "fechaHora": "2025-07-15T10:00:00",
  "duracionMinutos": 30,
  "canal": "PRESENCIAL"
}
→ Internamente llama a auth(8081) para verificar cliente
→ Internamente llama a ficha(8087) para verificar mascota
→ Responde: { "id": 1, "estado": "PENDIENTE" }

PASO 7 — Confirmar la cita (vetnova_agenda, puerto 8086)
PUT http://localhost:8086/api/v1/citas/1/confirmar
→ Responde: { "id": 1, "estado": "CONFIRMADA" }

PASO 8 — Ver agenda del día (vetnova_agenda, puerto 8086)
GET http://localhost:8086/api/v1/citas/agenda
→ Lista citas de hoy con nombres de cliente, mascota y veterinario

PASO 9 — Iniciar atención (vetnova_agenda, puerto 8086)
PUT http://localhost:8086/api/v1/citas/1/iniciar
→ Responde: { "id": 1, "estado": "EN_CURSO" }

PASO 10 — Registrar evolución (vetnova_ficha, puerto 8087)
POST http://localhost:8087/api/v1/evoluciones
Body: { "fichaId": 1, "veterinarioId": 3, "diagnostico": "Sano", "tratamiento": "Ninguno" }
→ Queda registrado en la ficha clínica permanentemente

PASO 11 — Completar cita (vetnova_agenda, puerto 8086)
PUT http://localhost:8086/api/v1/citas/1/completar
→ Responde: { "id": 1, "estado": "COMPLETADA" }

PASO 12 — Liberar el box (vetnova_agenda, puerto 8086)
PUT http://localhost:8086/api/v1/boxes/1/liberar
→ Responde: { "id": 1, "estado": "DISPONIBLE" }
```

---

## Flujo de reprogramación de cita

```
SITUACIÓN: el cliente llama para cambiar su cita del 15 al 16 de julio

GET  http://localhost:8086/api/v1/citas/1       → verificar estado actual
PUT  http://localhost:8086/api/v1/citas/1        → reprogramar
Body: { "fechaHora": "2025-07-16T11:00:00", "duracionMinutos": 30 }

REGLAS QUE VERIFICA INTERNAMENTE:
  ✓ La cita existe
  ✓ No está COMPLETADA ni CANCELADA
  ✓ La nueva fecha es futura
  ✓ El veterinario no tiene otra cita en ese horario (sin solapamiento)
```

---

## Qué pasa si un microservicio está caído

| Microservicio caído | Impacto en los demás |
|---|---|
| auth (8081) | agenda NO puede crear citas (no verifica cliente). Los GET de citas muestran `nombreCliente: null` |
| ficha (8087) | agenda NO puede crear citas (no verifica mascota). Los GET muestran `nombreMascota: null` |
| catalogo (8082) | No afecta a agenda ni ficha. Es independiente |
| agenda (8086) | No afecta a catalogo ni ficha. Es independiente |

---

## Resumen visual de quién llama a quién

```
auth   (8081)  ←── agenda (8086) llama para verificar cliente y obtener nombre
ficha  (8087)  ←── agenda (8086) llama para verificar mascota y obtener nombre
catalogo (8082) — no recibe llamadas de otros MS, no llama a otros MS
```

Solo hay comunicación en una dirección: **agenda → auth** y **agenda → ficha**.
El resto de los microservicios son independientes entre sí para sus operaciones básicas.
