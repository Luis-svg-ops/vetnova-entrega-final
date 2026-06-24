# Microservicio: Reportes - VetNova

## Descripción
Microservicio de generación y gestión de reportes para el sistema VetNova. Gestiona tres tipos de reportes: Atención, Venta y Stock.

## Configuración Spring Boot
- **Spring Boot:** 3.3.5
- **Java:** 17
- **Puerto:** 8091
- **Base de datos:** H2 (archivo) → `./data/reportesdb`

## Estructura del proyecto

```
vetnova-reportes-service/
└── src/main/java/cl/vetnova/reportes/
    ├── ReportesApplication.java
    ├── controller/   → ReporteController, ReporteAtencionController, ReporteVentaController, ReporteStockController
    ├── service/      → ReporteService, ReporteAtencionService, ReporteVentaService, ReporteStockService
    ├── repository/   → ReporteRepository, ReporteAtencionRepository, ReporteVentaRepository, ReporteStockRepository
    ├── model/        → Reporte, ReporteAtencion, ReporteVenta, ReporteStock
    ├── dto/          → ErrorResponse
    └── exception/    → ResourceNotFoundException, GlobalExceptionHandler
```

## Pasos para ejecutar

No requiere instalar base de datos: usa H2 en archivo y se crea sola al levantar.

1. **Ejecutar el proyecto:**
```bash
./mvnw spring-boot:run
```

## Endpoints disponibles

### Reporte (base)
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/v1/reportes` | Listar todos |
| GET | `/api/v1/reportes/{id}` | Obtener por ID |
| GET | `/api/v1/reportes/sucursal/{sucursal}` | Filtrar por sucursal |
| GET | `/api/v1/reportes/tipo/{tipo}` | Filtrar por tipo |
| POST | `/api/v1/reportes` | Crear reporte |
| PUT | `/api/v1/reportes/{id}` | Actualizar reporte |
| DELETE | `/api/v1/reportes/{id}` | Eliminar reporte |

### ReporteAtencion
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/v1/reportes-atencion` | Listar todos |
| GET | `/api/v1/reportes-atencion/{id}` | Obtener por ID |
| GET | `/api/v1/reportes-atencion/reporte/{reporteId}` | Por ID de reporte |
| POST | `/api/v1/reportes-atencion` | Crear |
| PUT | `/api/v1/reportes-atencion/{id}` | Actualizar |
| DELETE | `/api/v1/reportes-atencion/{id}` | Eliminar |

### ReporteVenta
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/v1/reportes-venta` | Listar todos |
| GET | `/api/v1/reportes-venta/{id}` | Obtener por ID |
| GET | `/api/v1/reportes-venta/reporte/{reporteId}` | Por ID de reporte |
| POST | `/api/v1/reportes-venta` | Crear |
| PUT | `/api/v1/reportes-venta/{id}` | Actualizar |
| DELETE | `/api/v1/reportes-venta/{id}` | Eliminar |

### ReporteStock
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/v1/reportes-stock` | Listar todos |
| GET | `/api/v1/reportes-stock/{id}` | Obtener por ID |
| GET | `/api/v1/reportes-stock/reporte/{reporteId}` | Por ID de reporte |
| POST | `/api/v1/reportes-stock` | Crear |
| PUT | `/api/v1/reportes-stock/{id}` | Actualizar |
| DELETE | `/api/v1/reportes-stock/{id}` | Eliminar |

## Swagger UI
Disponible en: http://localhost:8091/swagger-ui.html
