# Guía de Código — vetnova_catalogo (puerto 8082)

---

## ¿Qué hace este microservicio?

Maneja el **catálogo de productos y servicios** de la veterinaria.
- Productos: alimentos, medicamentos, accesorios (cosas físicas con stock)
- Servicios: consultas, vacunaciones, cirugías (cosas que se realizan)
- Categorías: agrupan productos o servicios
- Ofertas: descuentos por porcentaje sobre productos activos

No maneja stock (eso es vetnova_inventario). Solo define qué existe y a qué precio.

---

## Estructura de carpetas

```
vetnova_catalogo/src/main/java/cl/vetnova/catalogo/
│
├── controller/
│   ├── CategoriaController.java        → endpoints /api/v1/categorias
│   ├── ProductoController.java         → endpoints /api/v1/productos
│   ├── ServicioController.java         → endpoints /api/v1/servicios
│   ├── OfertaController.java           → endpoints /api/v1/ofertas
│   └── CatalogoBuscadorController.java → endpoints /api/v1/catalogo (búsquedas)
│
├── service/
│   ├── CategoriaService.java
│   ├── ProductoService.java
│   ├── ServicioService.java
│   ├── OfertaService.java
│   └── CatalogoBuscadorService.java
│
├── repository/
│   ├── CategoriaRepository.java
│   ├── ProductoRepository.java
│   ├── ServicioRepository.java
│   └── OfertaRepository.java
│
├── model/
│   ├── Categoria.java
│   ├── Producto.java
│   ├── Servicio.java
│   └── Oferta.java
│
├── dto/
│   ├── CategoriaRequest.java   → record
│   ├── ProductoRequest.java    → class con getters
│   ├── ProductoResponse.java   → class con getters
│   ├── ServicioRequest.java    → record
│   ├── OfertaRequest.java      → record
│   └── ErrorResponse.java      → respuesta de error estándar
│
└── exception/
    ├── BusinessRuleException.java      → 400
    ├── ConflictException.java          → 409
    ├── ResourceNotFoundException.java  → 404
    └── GlobalExceptionHandler.java     → captura todos los errores
```

---

## Patrón CSR — cómo fluye una petición

Toda petición sigue este camino:

```
Postman → Controller → Service → Repository → Base de datos H2
                                               ↓
Postman ← Controller ← Service ← Repository ←
```

Ejemplo con `POST /api/v1/productos`:

1. `ProductoController.crear()` recibe el JSON
2. Llama a `ProductoService.crear(request)`
3. El service valida las reglas de negocio
4. Llama a `ProductoRepository.save(producto)`
5. H2 persiste en `./data/catalogodb`
6. El controller devuelve `201 Created` con el objeto guardado

---

## Las entidades (lo que se guarda en BD)

### Categoria
```java
@Entity
@Table(name = "categorias")
public class Categoria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", length = 150)
    private String nombre;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "tipo", length = 50)
    private String tipo;  // "PRODUCTO" o "SERVICIO"
}
```

### Producto
```java
@Entity
@Table(name = "productos")
public class Producto {
    private Long id;
    private String nombre;        // único (case-insensitive)
    private String descripcion;
    private Double precio;        // > 0 obligatorio
    private Boolean activo;       // true por defecto — soft delete
    private Long categoriaId;     // referencia lógica (sin @ManyToOne)
    private String imagenUrl;     // debe ser https://...
    private LocalDate fechaActualizacion; // se auto-actualiza
}
```

**Por qué `Long categoriaId` en vez de `@ManyToOne Categoria categoria`?**

Patrón de microservicios: cada entidad es dueña solo de su tabla.
Con `Long categoriaId` evitamos JOINs automáticos de JPA.
La integridad la valida el service manualmente con `categoriaRepository.existsById()`.

### Servicio
```java
@Entity
@Table(name = "servicios")
public class Servicio {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer duracionMinutos; // en minutos, > 0
    private Boolean activo;
    private Long categoriaId;
}
```

### Oferta
```java
@Entity
@Table(name = "ofertas")
public class Oferta {
    private Long id;
    private Long productoId;      // producto al que aplica
    private Double descuento;     // porcentaje: 1 a 100
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean activa;
}
```

---

## Los DTOs — qué envías en el body

Los DTOs son las clases que reciben el JSON de Postman. 
Hay dos estilos en este MS:

### record (inmutable, sin setters)
```java
// CategoriaRequest.java
public record CategoriaRequest(

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    String nombre,

    String descripcion,

    @NotBlank(message = "El tipo es obligatorio (PRODUCTO o SERVICIO)")
    String tipo
) {}
```

Uso en Postman:
```json
{
  "nombre": "Alimentos",
  "descripcion": "Alimentos para mascotas",
  "tipo": "PRODUCTO"
}
```

### class con getters (mutable)
```java
// ProductoRequest.java
public class ProductoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    private Double precio;

    private Long categoriaId;
    private String imagenUrl;

    // getters y setters...
}
```

Uso en Postman:
```json
{
  "nombre": "Purina Pro Plan 15kg",
  "descripcion": "Alimento premium para perros adultos",
  "precio": 45990.0,
  "categoriaId": 1,
  "imagenUrl": "https://purina.cl/proplan.jpg"
}
```

---

## Los repositorios — cómo se consulta la BD

Spring Data JPA genera el SQL automáticamente si el nombre del método sigue la convención:

```java
// ProductoRepository.java
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // SQL generado: SELECT * FROM productos WHERE UPPER(nombre) = UPPER(?)
    boolean existsByNombreIgnoreCase(String nombre);

    // SQL: SELECT * FROM productos WHERE categoria_id = ? AND activo = true
    List<Producto> findByCategoriaIdAndActivoTrue(Long categoriaId);

    // SQL: SELECT * FROM productos WHERE activo = true AND UPPER(nombre) LIKE %?%
    List<Producto> findByActivoTrueAndNombreContainingIgnoreCase(String nombre);

    // SQL: SELECT * FROM productos WHERE activo = true AND precio BETWEEN ? AND ?
    List<Producto> findByActivoTrueAndPrecioBetween(Double min, Double max);

    // SQL: SELECT * FROM productos WHERE activo = true
    List<Producto> findByActivoTrue();

    // SQL: SELECT COUNT(*) > 0 FROM productos WHERE categoria_id = ?
    boolean existsByCategoriaId(Long categoriaId);
}
```

---

## Los services — dónde vive la lógica de negocio

### ProductoService.crear() — flujo completo anotado

```java
public ProductoResponse crear(ProductoRequest request) {

    // 1. Validar que el nombre no sea null ni vacío
    if (request.getNombre() == null || request.getNombre().isBlank()) {
        throw new BusinessRuleException("El nombre del producto es obligatorio");
    }

    // 2. Validar unicidad del nombre (case-insensitive)
    if (productoRepository.existsByNombreIgnoreCase(request.getNombre())) {
        throw new ConflictException("Ya existe un producto con ese nombre");
    }

    // 3. Validar precio positivo
    if (request.getPrecio() == null || request.getPrecio() <= 0) {
        throw new BusinessRuleException("El precio debe ser mayor a cero");
    }

    // 4. Validar que la categoría existe en BD
    if (request.getCategoriaId() != null &&
        !categoriaRepository.existsById(request.getCategoriaId())) {
        throw new ResourceNotFoundException("Categoría no encontrada");
    }

    // 5. Validar formato URL de imagen (si viene)
    if (request.getImagenUrl() != null && !request.getImagenUrl().isBlank()) {
        if (!request.getImagenUrl().matches("^https?://[^\\s]+$")) {
            throw new BusinessRuleException("La URL de imagen no es válida");
        }
    }

    // 6. Construir la entidad y guardar
    Producto producto = new Producto();
    producto.setNombre(request.getNombre());
    producto.setDescripcion(request.getDescripcion());
    producto.setPrecio(request.getPrecio());
    producto.setCategoriaId(request.getCategoriaId());
    producto.setImagenUrl(request.getImagenUrl());
    producto.setActivo(true);                         // siempre activo al crear
    producto.setFechaActualizacion(LocalDate.now());  // fecha de hoy

    log.info("event=crear_producto_catalogo nombre={}", producto.getNombre());
    Producto guardado = productoRepository.save(producto);

    // 7. Convertir a DTO de respuesta
    return toResponse(guardado);
}
```

### OfertaService.crear() — la más compleja (detecta solapamiento)

```java
public Oferta crear(Oferta oferta) {

    // 1. Validar que el productoId no sea null
    if (oferta.getProductoId() == null) {
        throw new BusinessRuleException("El productoId es obligatorio");
    }

    // 2. Verificar que el producto existe
    Producto producto = productoRepository.findById(oferta.getProductoId())
        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

    // 3. El producto debe estar activo
    if (!Boolean.TRUE.equals(producto.getActivo())) {
        throw new BusinessRuleException("No se puede crear oferta para producto inactivo");
    }

    // 4. Descuento entre 1 y 100
    if (oferta.getDescuento() == null || oferta.getDescuento() <= 0
            || oferta.getDescuento() > 100) {
        throw new BusinessRuleException("El descuento debe ser entre 1 y 100");
    }

    // 5. Fecha inicio no puede ser en el pasado
    if (oferta.getFechaInicio() != null &&
        oferta.getFechaInicio().isBefore(LocalDate.now())) {
        throw new BusinessRuleException("La fecha de inicio no puede ser en el pasado");
    }

    // 6. Fecha fin >= fecha inicio
    if (oferta.getFechaInicio() != null && oferta.getFechaFin() != null &&
        oferta.getFechaFin().isBefore(oferta.getFechaInicio())) {
        throw new BusinessRuleException("La fecha de fin debe ser mayor o igual a la de inicio");
    }

    // 7. Detectar solapamiento con otras ofertas activas del mismo producto
    List<Oferta> activas = ofertaRepository.findByProductoIdAndActivaTrue(oferta.getProductoId());
    for (Oferta existente : activas) {
        // Dos rangos [A, B] y [C, D] se solapan si: A <= D && C <= B
        boolean solapa = !oferta.getFechaInicio().isAfter(existente.getFechaFin())
                      && !existente.getFechaInicio().isAfter(oferta.getFechaFin());
        if (solapa) {
            throw new ConflictException("Ya existe una oferta activa en ese periodo para este producto");
        }
    }

    // 8. Guardar
    if (oferta.getActiva() == null) oferta.setActiva(true);
    return ofertaRepository.save(oferta);
}
```

### CatalogoBuscadorService — el buscador (solo lectura)

```java
// Sucursales permitidas — hardcodeadas en el service
private static final Set<String> SUCURSALES = Set.of("CHILLAN", "LOS_ANGELES", "TALCA", "SANTIAGO");

public List<Producto> buscarPorNombre(String nombre) {
    if (nombre == null)    throw new BusinessRuleException("El nombre de búsqueda es obligatorio");
    if (nombre.isBlank())  throw new BusinessRuleException("El nombre de búsqueda no puede estar vacío");
    return productoRepository.findByActivoTrueAndNombreContainingIgnoreCase(nombre);
}

public List<Producto> listarDisponibles(String sucursal) {
    if (sucursal == null)                    throw new BusinessRuleException("La sucursal es obligatoria");
    if (!SUCURSALES.contains(sucursal))      throw new ResourceNotFoundException("Sucursal no encontrada");
    return productoRepository.findByActivoTrue(); // el filtro por sucursal es solo de validación
}

public Object getDetalle(Long itemId, String tipo) {
    // Acepta "producto" o "servicio" (case-insensitive)
    if (!"producto".equalsIgnoreCase(tipo) && !"servicio".equalsIgnoreCase(tipo)) {
        throw new BusinessRuleException("Tipo no válido. Valores permitidos: producto, servicio");
    }
    if ("servicio".equalsIgnoreCase(tipo)) {
        return servicioRepository.findById(itemId)
            .filter(s -> Boolean.TRUE.equals(s.getActivo()))
            .orElseThrow(() -> new ResourceNotFoundException("Ítem no encontrado"));
    }
    return productoRepository.findById(itemId)
        .filter(p -> Boolean.TRUE.equals(p.getActivo()))
        .orElseThrow(() -> new ResourceNotFoundException("Ítem no encontrado"));
}
```

---

## Los controllers — los endpoints HTTP

### ProductoController — ejemplo anotado

```java
@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // POST /api/v1/productos
    // @Valid activa las validaciones del DTO antes de llegar al service
    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(request));
    }

    // GET /api/v1/productos
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar() {
        return ResponseEntity.ok(productoService.listar());
    }

    // GET /api/v1/productos/5
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    // PUT /api/v1/productos/5/activar
    @PutMapping("/{id}/activar")
    public ResponseEntity<ProductoResponse> activar(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.activar(id));
    }

    // PUT /api/v1/productos/5/desactivar
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<ProductoResponse> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.desactivar(id));
    }

    // PUT /api/v1/productos/5/precio?nuevoPrecio=49990
    // El nuevo precio viene como query param, NO en el body
    @PutMapping("/{id}/precio")
    public ResponseEntity<ProductoResponse> actualizarPrecio(
            @PathVariable Long id,
            @RequestParam Double nuevoPrecio) {
        return ResponseEntity.ok(productoService.actualizarPrecio(id, nuevoPrecio));
    }

    // DELETE /api/v1/productos/5
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
```

---

## Manejo de errores — GlobalExceptionHandler

Este archivo captura TODAS las excepciones y las convierte en JSON estructurado:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 — recurso no encontrado
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("event=resource_not_found path={} message={}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(false, ex.getMessage(), request.getRequestURI(), 404, null));
    }

    // 400 — regla de negocio violada
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(
            BusinessRuleException ex, HttpServletRequest request) {
        log.warn("event=business_rule path={} message={}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(false, ex.getMessage(), request.getRequestURI(), 400, null));
    }

    // 409 — conflicto (duplicado, solapamiento)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            ConflictException ex, HttpServletRequest request) {
        log.warn("event=conflict path={} message={}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(false, ex.getMessage(), request.getRequestURI(), 409, null));
    }

    // 400 — validaciones de @Valid fallaron
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e ->
            errores.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(false, "Datos inválidos", request.getRequestURI(), 400, errores));
    }
}
```

**Respuesta de error estándar (siempre el mismo formato):**
```json
{
  "success": false,
  "message": "Ya existe un producto con ese nombre",
  "path": "/api/v1/productos",
  "status": 409,
  "errors": {},
  "timestamp": "2025-06-27T10:30:45"
}
```

---

## Flujos completos de ejemplo

### Flujo 1 — Crear categoría y producto exitosamente

```
1. POST /api/v1/categorias
   Body: { "nombre": "Alimentos", "descripcion": "...", "tipo": "PRODUCTO" }
   
   CategoriaController.crear()
   → @Valid valida @NotBlank
   → CategoriaService.crear()
     → nombre no null ✓
     → existsByNombreIgnoreCase("Alimentos") = false ✓
     → tipo == "PRODUCTO" ✓
     → categoriaRepository.save()
   ← 201 { "id": 1, "nombre": "Alimentos", "tipo": "PRODUCTO" }

2. POST /api/v1/productos
   Body: { "nombre": "Purina 15kg", "precio": 45990, "categoriaId": 1 }
   
   ProductoController.crear()
   → @Valid valida @NotBlank y @Positive
   → ProductoService.crear()
     → nombre no blank ✓
     → existsByNombreIgnoreCase("Purina 15kg") = false ✓
     → precio > 0 ✓
     → categoriaRepository.existsById(1) = true ✓
     → producto.setActivo(true), setFechaActualizacion(hoy)
     → productoRepository.save()
   ← 201 { "id": 1, "nombre": "Purina 15kg", "activo": true, "fechaActualizacion": "2025-06-27" }
```

### Flujo 2 — Intentar crear producto con nombre duplicado

```
POST /api/v1/productos
Body: { "nombre": "Purina 15kg", "precio": 49990, "categoriaId": 1 }

ProductoService.crear()
→ existsByNombreIgnoreCase("Purina 15kg") = true
→ throw new ConflictException("Ya existe un producto con ese nombre")

GlobalExceptionHandler.handleConflict()
← 409 { "success": false, "message": "Ya existe un producto con ese nombre", "status": 409 }
```

### Flujo 3 — Bajar precio de un producto

```
PUT /api/v1/productos/1/precio?nuevoPrecio=39990

ProductoController.actualizarPrecio(1, 39990)
→ ProductoService.actualizarPrecio(1, 39990)
  → precio > 0 ✓
  → productoRepository.findById(1) → Producto encontrado
  → producto.setPrecio(39990)
  → producto.setFechaActualizacion(hoy)
  → productoRepository.save()
← 200 { "id": 1, "precio": 39990, "fechaActualizacion": "2025-06-27" }
```

### Flujo 4 — Eliminar categoría con productos

```
DELETE /api/v1/categorias/1

CategoriaService.eliminar(1)
→ categoriaRepository.existsById(1) = true ✓
→ productoRepository.existsByCategoriaId(1) = true ← hay productos!
→ throw new BusinessRuleException("No se puede eliminar la categoría porque tiene productos asociados")

← 400 { "success": false, "message": "No se puede eliminar la categoría porque tiene productos asociados" }
```

### Flujo 5 — Buscar en catálogo

```
GET /api/v1/catalogo/disponibles?sucursal=CHILLAN

CatalogoBuscadorController.listarDisponibles("CHILLAN")
→ CatalogoBuscadorService.listarDisponibles("CHILLAN")
  → sucursal != null ✓
  → SUCURSALES.contains("CHILLAN") = true ✓
  → productoRepository.findByActivoTrue()
← 200 [ lista de todos los productos activos ]

GET /api/v1/catalogo/disponibles?sucursal=VALPARAISO
→ SUCURSALES.contains("VALPARAISO") = false
→ throw new ResourceNotFoundException("Sucursal no encontrada")
← 404 { "message": "Sucursal no encontrada" }
```

---

## Tabla resumen de todos los endpoints

| Método | URL | Service | Respuesta |
|--------|-----|---------|-----------|
| POST | `/api/v1/categorias` | CategoriaService.crear() | 201 Categoria |
| GET | `/api/v1/categorias` | CategoriaService.listar() | 200 List |
| DELETE | `/api/v1/categorias/{id}` | CategoriaService.eliminar() | 204 |
| POST | `/api/v1/productos` | ProductoService.crear() | 201 ProductoResponse |
| GET | `/api/v1/productos` | ProductoService.listar() | 200 List |
| GET | `/api/v1/productos/{id}` | ProductoService.obtenerPorId() | 200 ProductoResponse |
| PUT | `/api/v1/productos/{id}/activar` | ProductoService.activar() | 200 ProductoResponse |
| PUT | `/api/v1/productos/{id}/desactivar` | ProductoService.desactivar() | 200 ProductoResponse |
| PUT | `/api/v1/productos/{id}/precio?nuevoPrecio=X` | ProductoService.actualizarPrecio() | 200 ProductoResponse |
| DELETE | `/api/v1/productos/{id}` | ProductoService.eliminar() | 204 |
| POST | `/api/v1/servicios` | ServicioService.crear() | 201 Servicio |
| GET | `/api/v1/servicios` | ServicioService.listar() | 200 List |
| PUT | `/api/v1/servicios/{id}/activar` | ServicioService.activar() | 200 Servicio |
| PUT | `/api/v1/servicios/{id}/desactivar` | ServicioService.desactivar() | 200 Servicio |
| PUT | `/api/v1/servicios/{id}/precio?nuevoPrecio=X` | ServicioService.actualizarPrecio() | 200 Servicio |
| DELETE | `/api/v1/servicios/{id}` | ServicioService.eliminar() | 204 |
| POST | `/api/v1/ofertas` | OfertaService.crear() | 201 Oferta |
| GET | `/api/v1/ofertas` | OfertaService.listar() | 200 List |
| PUT | `/api/v1/ofertas/{id}/activar` | OfertaService.activar() | 200 Oferta |
| PUT | `/api/v1/ofertas/{id}/desactivar` | OfertaService.desactivar() | 200 Oferta |
| DELETE | `/api/v1/ofertas/{id}` | OfertaService.eliminar() | 204 |
| GET | `/api/v1/catalogo/buscar?nombre=X` | CatalogoBuscadorService.buscarPorNombre() | 200 List |
| GET | `/api/v1/catalogo/buscar/categoria?categoriaId=X` | CatalogoBuscadorService.filtrarPorCategoria() | 200 List |
| GET | `/api/v1/catalogo/buscar/rango?min=X&max=Y` | CatalogoBuscadorService.filtrarPorRango() | 200 List |
| GET | `/api/v1/catalogo/disponibles?sucursal=X` | CatalogoBuscadorService.listarDisponibles() | 200 List |
| GET | `/api/v1/catalogo/detalle?itemId=X&tipo=Y` | CatalogoBuscadorService.getDetalle() | 200 Object |

---

## Tabla de errores posibles

| Situación | Excepción lanzada | HTTP |
|-----------|-------------------|------|
| Nombre de producto/servicio/categoría ya existe | ConflictException | 409 |
| Categoría no encontrada al crear producto | ResourceNotFoundException | 404 |
| Producto no encontrado | ResourceNotFoundException | 404 |
| Precio <= 0 | BusinessRuleException | 400 |
| Nombre vacío o null | BusinessRuleException | 400 |
| Tipo de categoría inválido | BusinessRuleException | 400 |
| URL de imagen con formato inválido | BusinessRuleException | 400 |
| Sucursal inválida en búsqueda | ResourceNotFoundException | 404 |
| Oferta con fechas solapadas | ConflictException | 409 |
| Oferta sobre producto inactivo | BusinessRuleException | 400 |
| Descuento fuera de rango | BusinessRuleException | 400 |
| Eliminar categoría con productos | BusinessRuleException | 400 |
| Validación @Valid falla | MethodArgumentNotValidException | 400 |

---

## Preguntas típicas de defensa

**¿Por qué `Long categoriaId` en vez de `@ManyToOne`?**
> Patrón de microservicios — cada MS es dueño de sus tablas. La integridad se valida explícitamente con `categoriaRepository.existsById()` en el service.

**¿Cómo se hace el soft delete de productos?**
> No se elimina de la BD. `PUT /api/v1/productos/{id}/desactivar` pone `activo = false`. Las búsquedas en CatalogoBuscadorService usan `findByActivoTrue()` para excluirlos automáticamente.

**¿Dónde se valida que no haya ofertas duplicadas?**
> En `OfertaService.crear()` — consulta las ofertas activas del mismo producto y detecta solapamiento de fechas con la condición: `!fechaInicio.isAfter(otraFin) && !otraInicio.isAfter(fechaFin)`.

**¿Qué pasa si mando un body con campo inválido?**
> `@Valid` en el controller activa las anotaciones del DTO antes de llegar al service. El `GlobalExceptionHandler` captura el `MethodArgumentNotValidException` y devuelve un `400` con el campo exacto que falló.

**¿Por qué el precio se actualiza con query param en vez de body?**
> Es una operación parcial sobre un solo campo. No tiene sentido mandar todo el objeto para cambiar solo el precio. El query param `?nuevoPrecio=X` es más explícito y simple.
