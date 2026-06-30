# Flujo vetnova_agenda — Puerto 8086

**Base URL:** `http://localhost:8086`

---

## ¿Qué hace este microservicio?

Maneja la **agenda de citas veterinarias**.
- Citas: el núcleo (agendar, confirmar, iniciar, completar, cancelar, reprogramar)
- Boxes: salas físicas de atención (disponible / reservado)
- Bloques de agenda: horarios bloqueados del veterinario
- Disponibilidad profesional: horarios habituales de trabajo
- Historial de agenda: log de cambios de estado
- Recordatorios: avisos automáticos de citas

**Conexiones con otros MS:**
- Llama a **vetnova_auth (8081)** para verificar que el cliente existe y obtener su nombre
- Llama a **vetnova_ficha (8087)** para verificar que la mascota existe y obtener su nombre

---

## Flujo completo en orden

```
1. Configurar boxes y disponibilidad del veterinario
2. Crear una cita
3. Confirmar → Iniciar → Completar (o Cancelar)
4. Consultar agenda del día
5. Reprogramar si es necesario
```

---

## Endpoints — Boxes (salas de atención)

| Método | URL | Qué hace | Respuesta |
|--------|-----|----------|-----------|
| `POST` | `/api/v1/boxes` | Crea un box nuevo | 201 |
| `GET` | `/api/v1/boxes` | Lista todos los boxes | 200 |
| `PUT` | `/api/v1/boxes/{id}/reservar` | Marca el box como RESERVADO | 200 |
| `PUT` | `/api/v1/boxes/{id}/liberar` | Marca el box como DISPONIBLE | 200 |
| `DELETE` | `/api/v1/boxes/{id}` | Elimina un box | 204 |

**Body POST:**
```json
{
  "nombre": "Box 1",
  "sucursal": "CHILLAN"
}
```
> Sucursales válidas: `CHILLAN`, `LOS_ANGELES`, `TALCA`, `SANTIAGO`

---

## Endpoints — Disponibilidad Profesional

| Método | URL | Qué hace | Respuesta |
|--------|-----|----------|-----------|
| `POST` | `/api/v1/disponibilidad` | Registra horario de un veterinario | 201 |
| `GET` | `/api/v1/disponibilidad` | Lista toda la disponibilidad | 200 |
| `GET` | `/api/v1/disponibilidad/{id}` | Obtiene un registro específico | 200 |
| `PUT` | `/api/v1/disponibilidad/{id}` | Actualiza el horario | 200 |
| `PUT` | `/api/v1/disponibilidad/{id}/activar` | Activa la disponibilidad | 200 |
| `PUT` | `/api/v1/disponibilidad/{id}/desactivar` | Desactiva la disponibilidad | 200 |
| `DELETE` | `/api/v1/disponibilidad/{id}` | Elimina el registro | 200 |

**Body POST:**
```json
{
  "veterinarioId": 3,
  "diaSemana": "LUNES",
  "horaInicio": "09:00",
  "horaFin": "18:00",
  "sucursal": "CHILLAN"
}
```

---

## Endpoints — Bloques de Agenda (horarios bloqueados)

| Método | URL | Qué hace | Respuesta |
|--------|-----|----------|-----------|
| `POST` | `/api/v1/bloques` | Bloquea un horario del veterinario | 201 |
| `GET` | `/api/v1/bloques` | Lista todos los bloques | 200 |
| `GET` | `/api/v1/bloques/{id}` | Obtiene un bloque por id | 200 |
| `DELETE` | `/api/v1/bloques/{id}` | Elimina un bloque | 200 |

**Body POST:**
```json
{
  "veterinarioId": 3,
  "fechaHoraInicio": "2025-07-15T12:00:00",
  "fechaHoraFin": "2025-07-15T13:00:00",
  "motivo": "Almuerzo"
}
```

---

## Endpoints — Citas (el núcleo del microservicio)

| Método | URL | Qué hace | Respuesta |
|--------|-----|----------|-----------|
| `POST` | `/api/v1/citas` | Agenda una cita nueva | 201 |
| `GET` | `/api/v1/citas` | Lista todas las citas con nombres | 200 |
| `GET` | `/api/v1/citas/agenda` | Citas de hoy (agenda del día) | 200 |
| `GET` | `/api/v1/citas/{id}` | Obtiene cita con nombres de cliente/mascota/vet | 200 |
| `PUT` | `/api/v1/citas/{id}` | **Reprograma** la cita (nueva fecha/hora) | 200 |
| `PUT` | `/api/v1/citas/{id}/confirmar` | Cambia estado a CONFIRMADA | 200 |
| `PUT` | `/api/v1/citas/{id}/iniciar` | Cambia estado a EN_CURSO | 200 |
| `PUT` | `/api/v1/citas/{id}/completar` | Cambia estado a COMPLETADA | 200 |
| `PUT` | `/api/v1/citas/{id}/cancelar` | Cambia estado a CANCELADA | 200 |

**Body POST (crear cita):**
```json
{
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
```

**Body PUT reprogramar:**
```json
{
  "fechaHora": "2025-07-16T11:00:00",
  "duracionMinutos": 45
}
```

**Body PUT cancelar:**
```json
{
  "motivoCancelacion": "Cliente no puede asistir"
}
```

> Los estados siguen este orden:
> `PENDIENTE → CONFIRMADA → EN_CURSO → COMPLETADA`
> En cualquier punto antes de COMPLETADA se puede `CANCELADA`

> No se puede reprogramar una cita COMPLETADA o CANCELADA.

---

## Endpoints — Historial de Agenda

| Método | URL | Qué hace | Respuesta |
|--------|-----|----------|-----------|
| `POST` | `/api/v1/historial` | Registra un evento de agenda | 201 |
| `GET` | `/api/v1/historial` | Lista todos los eventos | 200 |
| `DELETE` | `/api/v1/historial/{id}` | Elimina un evento del historial | 204 |

---

## Endpoints — Recordatorios

| Método | URL | Qué hace | Respuesta |
|--------|-----|----------|-----------|
| `POST` | `/api/v1/recordatorios` | Crea un recordatorio para una cita | 201 |
| `GET` | `/api/v1/recordatorios` | Lista todos los recordatorios | 200 |
| `GET` | `/api/v1/recordatorios/{id}` | Obtiene un recordatorio por id | 200 |
| `PUT` | `/api/v1/recordatorios/{id}/reenviar` | Reenvía el recordatorio | 200 |
| `PUT` | `/api/v1/recordatorios/{id}` | **BLOQUEADO** — inmutable | 400 |
| `DELETE` | `/api/v1/recordatorios/{id}` | **BLOQUEADO** — inmutable | 400 |

**Body POST:**
```json
{
  "citaId": 1,
  "tipo": "EMAIL",
  "mensaje": "Recuerde su cita el 15 de julio a las 10:00"
}
```

---

## Resumen del flujo en Postman

```
POST /api/v1/boxes                       → crea "Box 1" en CHILLAN (id=1)
POST /api/v1/disponibilidad              → vet 3 disponible lunes 9-18 en CHILLAN
POST /api/v1/citas                       → agenda cita (estado: PENDIENTE)
GET  /api/v1/citas/{id}                  → ver cita con nombres de cliente y mascota
PUT  /api/v1/citas/{id}/confirmar        → estado: CONFIRMADA
POST /api/v1/recordatorios               → crea recordatorio para el cliente
PUT  /api/v1/boxes/1/reservar            → box queda RESERVADO
PUT  /api/v1/citas/{id}/iniciar          → estado: EN_CURSO
PUT  /api/v1/citas/{id}/completar        → estado: COMPLETADA
PUT  /api/v1/boxes/1/liberar             → box vuelve a DISPONIBLE
GET  /api/v1/citas/agenda                → ver agenda de hoy
```
