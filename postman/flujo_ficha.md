# Flujo vetnova_ficha — Puerto 8087

**Base URL:** `http://localhost:8087`

---

## ¿Qué hace este microservicio?

Registra y consulta toda la historia clínica de las mascotas.
La información médica **nunca se puede editar ni borrar** (inmutabilidad legal).

---

## Flujo completo en orden

```
1. Registrar mascota
2. Crear ficha clínica para esa mascota
3. Registrar evoluciones / vacunas / procedimientos / recetas / certificados
4. Consultar historial completo
```

---

## Endpoints — Mascotas

| Método | URL | Qué hace | Respuesta |
|--------|-----|----------|-----------|
| `POST` | `/api/v1/mascotas` | Registra una mascota nueva | 201 |
| `GET` | `/api/v1/mascotas` | Lista todas las mascotas con nombre del dueño | 200 |
| `GET` | `/api/v1/mascotas/{id}` | Obtiene una mascota por id | 200 |
| `PUT` | `/api/v1/mascotas/{id}` | Actualiza datos de la mascota | 200 |
| `DELETE` | `/api/v1/mascotas/{id}` | Desactiva la mascota (soft delete) | 200 |

**Body POST / PUT:**
```json
{
  "nombre": "Max",
  "especie": "Perro",
  "raza": "Labrador",
  "fechaNacimiento": "2020-03-15",
  "clienteId": 1
}
```

---

## Endpoints — Ficha Clínica

| Método | URL | Qué hace | Respuesta |
|--------|-----|----------|-----------|
| `POST` | `/api/v1/fichas` | Crea la ficha clínica de una mascota | 201 |
| `GET` | `/api/v1/fichas` | Lista todas las fichas | 200 |
| `GET` | `/api/v1/fichas/{id}` | Obtiene ficha por id | 200 |
| `GET` | `/api/v1/fichas?mascotaId=1` | Obtiene la ficha de una mascota específica | 200 |
| `DELETE` | `/api/v1/fichas/{id}` | **BLOQUEADO** — lanza error 400 (inmutable) | 400 |

**Body POST:**
```json
{
  "mascotaId": 1,
  "observacionesGenerales": "Mascota en buen estado general"
}
```

---

## Endpoints — Evoluciones (visitas al veterinario)

| Método | URL | Qué hace | Respuesta |
|--------|-----|----------|-----------|
| `POST` | `/api/v1/evoluciones` | Registra una evolución clínica | 201 |
| `GET` | `/api/v1/evoluciones` | Lista todas las evoluciones | 200 |
| `GET` | `/api/v1/evoluciones?fichaId=1` | Evoluciones de una ficha específica | 200 |
| `PUT` | `/api/v1/evoluciones/{id}` | **BLOQUEADO** — inmutable | 400 |
| `DELETE` | `/api/v1/evoluciones/{id}` | **BLOQUEADO** — inmutable | 400 |

**Body POST:**
```json
{
  "fichaId": 1,
  "veterinarioId": 3,
  "descripcion": "Revisión general. Animal sano.",
  "diagnostico": "Sin patologías",
  "tratamiento": "Ninguno"
}
```

---

## Endpoints — Vacunas

| Método | URL | Qué hace | Respuesta |
|--------|-----|----------|-----------|
| `POST` | `/api/v1/vacunas` | Registra una vacuna aplicada | 201 |
| `GET` | `/api/v1/vacunas` | Lista todas las vacunas | 200 |
| `GET` | `/api/v1/vacunas?fichaId=1` | Vacunas de una ficha específica | 200 |
| `PUT` | `/api/v1/vacunas/{id}` | **BLOQUEADO** — inmutable | 400 |
| `DELETE` | `/api/v1/vacunas/{id}` | **BLOQUEADO** — inmutable | 400 |

**Body POST:**
```json
{
  "fichaId": 1,
  "nombreVacuna": "Antirrábica",
  "fechaAplicacion": "2025-06-27",
  "proximaAplicacion": "2026-06-27",
  "veterinarioId": 3
}
```

---

## Endpoints — Procedimientos

| Método | URL | Qué hace | Respuesta |
|--------|-----|----------|-----------|
| `POST` | `/api/v1/procedimientos` | Registra un procedimiento realizado | 201 |
| `GET` | `/api/v1/procedimientos` | Lista todos los procedimientos | 200 |
| `GET` | `/api/v1/procedimientos?fichaId=1` | Procedimientos de una ficha | 200 |
| `PUT` | `/api/v1/procedimientos/{id}` | **BLOQUEADO** — inmutable | 400 |
| `DELETE` | `/api/v1/procedimientos/{id}` | **BLOQUEADO** — inmutable | 400 |

**Body POST:**
```json
{
  "fichaId": 1,
  "nombreProcedimiento": "Esterilización",
  "descripcion": "Procedimiento realizado sin complicaciones",
  "veterinarioId": 3
}
```

---

## Endpoints — Recetas

| Método | URL | Qué hace | Respuesta |
|--------|-----|----------|-----------|
| `POST` | `/api/v1/recetas` | Emite una receta médica | 201 |
| `GET` | `/api/v1/recetas` | Lista todas las recetas | 200 |
| `GET` | `/api/v1/recetas?fichaId=1` | Recetas de una ficha | 200 |
| `PUT` | `/api/v1/recetas/{id}` | **BLOQUEADO** — inmutable | 400 |
| `DELETE` | `/api/v1/recetas/{id}` | **BLOQUEADO** — inmutable | 400 |

**Body POST:**
```json
{
  "fichaId": 1,
  "veterinarioId": 3,
  "medicamento": "Amoxicilina 250mg",
  "dosis": "1 comprimido cada 12 horas",
  "duracion": "7 días"
}
```

---

## Endpoints — Certificados

| Método | URL | Qué hace | Respuesta |
|--------|-----|----------|-----------|
| `POST` | `/api/v1/certificados` | Emite un certificado veterinario | 201 |
| `GET` | `/api/v1/certificados` | Lista todos los certificados | 200 |
| `GET` | `/api/v1/certificados?fichaId=1` | Certificados de una ficha | 200 |
| `PUT` | `/api/v1/certificados/{id}` | **BLOQUEADO** — inmutable | 400 |
| `DELETE` | `/api/v1/certificados/{id}` | **BLOQUEADO** — inmutable | 400 |

**Body POST:**
```json
{
  "fichaId": 1,
  "veterinarioId": 3,
  "tipo": "Salud",
  "descripcion": "Mascota en óptimas condiciones para viaje"
}
```

---

## Regla clave — Inmutabilidad

Ficha, Evolución, Vacuna, Procedimiento, Receta y Certificado:

- `POST` → permitido (crear)
- `GET` → permitido (consultar)
- `PUT` → **lanza 400** RegistroInmutableException
- `DELETE` → **lanza 400** RegistroInmutableException

Esto es intencional: los registros médicos son evidencia legal y no deben alterarse.

---

## Resumen del flujo en Postman

```
POST /api/v1/mascotas          → crea Max (id=1)
POST /api/v1/fichas            → crea ficha de Max (id=1)
POST /api/v1/evoluciones       → registra visita de Max
POST /api/v1/vacunas           → registra vacuna de Max
POST /api/v1/recetas           → emite receta para Max
GET  /api/v1/fichas?mascotaId=1 → ver ficha completa de Max
GET  /api/v1/evoluciones?fichaId=1 → ver todas las visitas
GET  /api/v1/vacunas?fichaId=1     → ver todas las vacunas
```
