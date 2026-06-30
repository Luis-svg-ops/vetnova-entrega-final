# Flujo vetnova_catalogo — Puerto 8082

**Base URL:** `http://localhost:8082`

---

## ¿Qué hace este microservicio?

Define el catálogo de **productos** (cosas físicas: alimentos, medicamentos) y **servicios** (cosas que se realizan: consultas, cirugías) de la veterinaria.
También maneja **categorías** para organizar productos/servicios, y **ofertas** (descuentos).
No maneja stock — eso es vetnova_inventario.

---

## Flujo completo en orden

```
1. Crear categoría
2. Crear productos/servicios en esa categoría
3. Buscar en el catálogo
4. Crear ofertas sobre productos activos
5. Activar / desactivar / cambiar precio
```

---

## Endpoints — Categorías

| Método | URL | Qué hace | Respuesta |
|--------|-----|----------|-----------|
| `POST` | `/api/v1/categorias` | Crea una categoría nueva | 201 |
| `GET` | `/api/v1/categorias` | Lista todas las categorías | 200 |
| `DELETE` | `/api/v1/categorias/{id}` | Elimina categoría (falla si tiene productos) | 204 / 400 |

**Body POST:**
```json
{
  "nombre": "Alimentos",
  "descripcion": "Alimentos para mascotas",
  "tipo": "PRODUCTO"
}
```
> `tipo` solo acepta `"PRODUCTO"` o `"SERVICIO"`

---

## Endpoints — Productos

| Método | URL | Qué hace | Respuesta |
|--------|-----|----------|-----------|
| `POST` | `/api/v1/productos` | Crea un producto | 201 |
| `GET` | `/api/v1/productos` | Lista todos los productos | 200 |
| `GET` | `/api/v1/productos/{id}` | Obtiene producto por id | 200 |
| `PUT` | `/api/v1/productos/{id}/activar` | Activa un producto desactivado | 200 |
| `PUT` | `/api/v1/productos/{id}/desactivar` | Desactiva un producto (soft delete) | 200 |
| `PUT` | `/api/v1/productos/{id}/precio?nuevoPrecio=X` | Cambia el precio | 200 |
| `DELETE` | `/api/v1/productos/{id}` | Elimina permanentemente | 204 |

**Body POST:**
```json
{
  "nombre": "Purina Pro Plan 15kg",
  "descripcion": "Alimento premium para perros adultos",
  "precio": 45990.0,
  "categoriaId": 1,
  "imagenUrl": "https://purina.cl/proplan.jpg"
}
```

> `precio` en el PUT va como query param, no en body:
> `PUT /api/v1/productos/1/precio?nuevoPrecio=39990`

---

## Endpoints — Servicios

| Método | URL | Qué hace | Respuesta |
|--------|-----|----------|-----------|
| `POST` | `/api/v1/servicios` | Crea un servicio | 201 |
| `GET` | `/api/v1/servicios` | Lista todos los servicios | 200 |
| `PUT` | `/api/v1/servicios/{id}/activar` | Activa un servicio | 200 |
| `PUT` | `/api/v1/servicios/{id}/desactivar` | Desactiva un servicio | 200 |
| `PUT` | `/api/v1/servicios/{id}/precio?nuevoPrecio=X` | Cambia el precio | 200 |
| `DELETE` | `/api/v1/servicios/{id}` | Elimina permanentemente | 204 |

**Body POST:**
```json
{
  "nombre": "Consulta General",
  "descripcion": "Revisión veterinaria completa",
  "precio": 25000.0,
  "duracionMinutos": 30,
  "categoriaId": 2
}
```

---

## Endpoints — Ofertas

| Método | URL | Qué hace | Respuesta |
|--------|-----|----------|-----------|
| `POST` | `/api/v1/ofertas` | Crea una oferta con descuento | 201 |
| `GET` | `/api/v1/ofertas` | Lista todas las ofertas | 200 |
| `PUT` | `/api/v1/ofertas/{id}/activar` | Activa una oferta | 200 |
| `PUT` | `/api/v1/ofertas/{id}/desactivar` | Desactiva una oferta | 200 |
| `DELETE` | `/api/v1/ofertas/{id}` | Elimina una oferta | 204 |

**Body POST:**
```json
{
  "productoId": 1,
  "descuento": 20.0,
  "fechaInicio": "2025-07-01",
  "fechaFin": "2025-07-31"
}
```
> `descuento` es porcentaje (1-100). El producto debe estar activo.

---

## Endpoints — Buscador de Catálogo

| Método | URL | Qué hace | Respuesta |
|--------|-----|----------|-----------|
| `GET` | `/api/v1/catalogo/buscar?nombre=Purina` | Busca productos activos por nombre | 200 |
| `GET` | `/api/v1/catalogo/buscar/categoria?categoriaId=1` | Filtra por categoría | 200 |
| `GET` | `/api/v1/catalogo/buscar/rango?min=10000&max=50000` | Filtra por rango de precio | 200 |
| `GET` | `/api/v1/catalogo/disponibles?sucursal=CHILLAN` | Lista productos disponibles en sucursal | 200 |
| `GET` | `/api/v1/catalogo/detalle?itemId=1&tipo=producto` | Detalle de producto o servicio | 200 |

> Sucursales válidas: `CHILLAN`, `LOS_ANGELES`, `TALCA`, `SANTIAGO`
> `tipo` acepta: `producto` o `servicio`

---

## Resumen del flujo en Postman

```
POST /api/v1/categorias                              → crea "Alimentos" (id=1)
POST /api/v1/categorias                              → crea "Consultas" tipo SERVICIO (id=2)
POST /api/v1/productos                               → crea Purina 15kg en categoría 1 (id=1)
POST /api/v1/servicios                               → crea "Consulta General" en categoría 2 (id=1)
POST /api/v1/ofertas                                 → 20% descuento en Purina, julio
GET  /api/v1/catalogo/disponibles?sucursal=CHILLAN   → ver todo el catálogo
GET  /api/v1/catalogo/buscar?nombre=Purina           → buscar Purina
PUT  /api/v1/productos/1/precio?nuevoPrecio=39990    → bajar el precio
PUT  /api/v1/productos/1/desactivar                  → desactivar (soft delete)
```
