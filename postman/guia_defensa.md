# Guía de Defensa Técnica — VetNova
**Duración: 15 minutos | MS propios: catálogo (8082), ficha (8087), agenda (8086)**

---

## 1. FLUJO DE DEMOSTRACIÓN (minuto a minuto)

### Minuto 0–2 — Contexto general
- Abrir el README o el diagrama de arquitectura
- Decir: *"VetNova tiene 13 microservicios Spring Boot, cada uno con su propia base de datos H2. Yo me encargué de catálogo, ficha clínica y agenda."*
- Mostrar el mapa de puertos brevemente

### Minuto 2–5 — Catálogo (8082)
- Abrir `http://localhost:8082/swagger-ui/index.html`
- Mostrar los controllers: categorías, productos, servicios, ofertas, buscador
- Ejecutar en vivo: `GET /api/v1/catalogo/disponibles?sucursal=CHILLAN`
- Explicar: *"El catálogo es autónomo, no llama a ningún otro MS"*
- Mostrar que activar/desactivar no borra, solo cambia estado

### Minuto 5–9 — Ficha Clínica (8087)
- Abrir `http://localhost:8087/swagger-ui/index.html`
- Mostrar mascotas y fichas
- Ejecutar: `POST /api/v1/mascotas` → luego `POST /api/v1/fichas`
- Intentar `DELETE /api/v1/evoluciones/1` → mostrar que da error 400
- Explicar: *"Los registros médicos son inmutables por diseño — no se pueden borrar ni editar"*
- Mostrar soft delete: `DELETE /api/v1/mascotas/1` pone activo=false

### Minuto 9–13 — Agenda (8086)
- Abrir `http://localhost:8086/swagger-ui/index.html`
- Mostrar la máquina de estados: crear cita → confirmar → iniciar → completar
- Ejecutar `POST /api/v1/citas` y explicar las validaciones internas
- Mostrar en el código `CitaService` la llamada a AuthClient y FichaClient
- Explicar: *"Agenda llama a auth para verificar que el cliente existe, y a ficha para verificar la mascota. Si alguno no existe, la cita no se crea"*

### Minuto 13–15 — Preguntas / cierre
- Tener abierto el código de `CitaService` en VS Code
- Tener abierto Postman con la colección lista como respaldo

---

## 2. PREGUNTAS POR MICROSERVICIO

### vetnova_catalogo

**P1: ¿Por qué usaste activar/desactivar en vez de eliminar un producto?**
> Porque si elimino físicamente un producto que ya fue vendido, pierdo el historial. Con activo=false el producto desaparece del catálogo pero los registros de ventas anteriores siguen intactos. Es un patrón de borrado lógico.

**P2 (trampa): Si quiero actualizar el precio de un producto, ¿qué mando en el body?**
> Nada en el body. El precio se actualiza con un query param en la URL: `PUT /api/v1/productos/1?nuevoPrecio=9990`. Así el endpoint queda más limpio para una operación simple.

**P3 (código en vivo): ¿Cómo validas que la categoría existe antes de crear un producto?**
> En `ProductoService`, antes de guardar llamo a `categoriaRepository.findById(categoriaId)` y si no encuentra lanza `ResourceNotFoundException`. No llamo a otro microservicio porque catálogo tiene su propia tabla de categorías — es autónomo.

---

### vetnova_ficha

**P1: ¿Por qué no se pueden editar las evoluciones?**
> Porque son registros médicos legales. Si un veterinario comete un error, debe agregar una nueva evolución corrigiendo, nunca editar la anterior. En el código, el método PUT lanza `RegistroInmutableException` con HTTP 400 siempre.

**P2 (trampa): ¿Qué pasa si hago DELETE a una mascota?**
> No se borra físicamente. El servicio pone `activo = false` en la base de datos. Esto preserva todas las fichas, evoluciones y vacunas asociadas. La mascota "desaparece" del listado pero sigue en la BD.

**P3 (código en vivo): ¿Puede una mascota tener dos fichas clínicas?**
> No. En `FichaClinicaService`, al crear verifica si ya existe una ficha para esa mascota con `fichaRepository.findByMascotaId()`. Si encuentra una, lanza `ConflictException` con HTTP 409. Es una relación 1 a 1.

---

### vetnova_agenda

**P1: Explica la máquina de estados de las citas.**
> Una cita parte como PENDIENTE cuando se crea. El recepcionista la confirma → CONFIRMADA. El veterinario inicia la atención → EN_CURSO. Al terminar → COMPLETADA. En cualquier punto antes de COMPLETADA puede cancelarse → CANCELADA. Cada transición verifica el estado actual; si no corresponde, lanza excepción.

**P2 (trampa): Si el microservicio de auth está caído, ¿se puede crear una cita?**
> Depende. Si auth está caído completamente, no se puede verificar que el cliente existe, entonces la cita NO se crea. Pero si auth responde pero falla al obtener el nombre del cliente, la cita SÍ se crea y `nombreCliente` queda como null. Eso es degradación suave — funciona pero con datos incompletos.

**P3 (código en vivo): ¿Cómo evitas que dos citas del mismo veterinario se solapen?**
> En `CitaService`, antes de guardar consulto todas las citas del veterinario en ese día y verifico si el nuevo horario se superpone con alguna existente. Comparo el inicio y fin de cada cita: si el nuevo inicio cae dentro del rango de otra, lanzo una excepción indicando solapamiento.

---

## 3. PREGUNTAS DE ARQUITECTURA GENERAL

**P: ¿Qué es un Bounded Context?**
> Es el límite de responsabilidad de un microservicio. Por ejemplo, "Catálogo" solo sabe de productos, servicios y categorías. No sabe nada de ventas ni de clientes. Cada MS tiene su propia base de datos y sus propias reglas de negocio. Si catálogo y ventas necesitan el mismo dato, cada uno lo guarda por separado.

**P: ¿Por qué 13 microservicios y no uno solo?**
> Con un solo sistema (monolito), si falla el módulo de reportes se cae todo. Con microservicios, si reportes falla, las citas y ventas siguen funcionando. Además cada MS se puede escalar por separado — si hay muchas ventas, escalo solo ventas sin tocar los demás.

**P: ¿Qué es degradación suave vs dura?**
> Dura: si falla, la operación se cancela completamente. Ejemplo: si auth no puede verificar que el cliente existe, la cita no se crea.
> Suave: si falla, la operación continúa pero con datos incompletos. Ejemplo: si falla obtener el nombre del cliente, la cita igual se crea pero `nombreCliente` queda null.

**P: ¿Por qué usaron WebClient y no RestTemplate?**
> Usamos WebClient porque es el estándar actual de Spring para comunicación entre microservicios. RestTemplate está siendo deprecado. WebClient maneja mejor los errores remotos y nos permite implementar degradación suave con try/catch de forma más limpia — si la llamada a otro MS falla, podemos capturar la excepción y decidir si cortamos la operación (validación dura) o continuamos con datos nulos (degradación suave). En el código, `AuthClient` y `FichaClient` usan `WebClient` con `.block()` para esperar la respuesta de forma síncrona.

**P: ¿Qué hace el Gateway?**
> Es el punto de entrada único al sistema. En vez de que el cliente llame directo a `localhost:8087`, llama a `localhost:8080/ficha/...` y el gateway redirige al MS correcto. También puede manejar autenticación, rate limiting y balanceo de carga centralizados.

---

## 4. PREGUNTAS DE TESTS

**P: ¿Qué es un mock?**
> Un mock es un objeto falso que simula el comportamiento de uno real. Por ejemplo, en los tests de `CitaService` no quiero llamar de verdad a la BD ni a otros MS — creo un mock del repositorio que devuelve datos fijos. Así el test es rápido y no depende de nada externo.

**P: ¿Qué es Given/When/Then?**
> Es la estructura de un test:
> - **Given** (dado): preparo el escenario, defino qué devuelven los mocks
> - **When** (cuando): ejecuto el método que quiero probar
> - **Then** (entonces): verifico que el resultado sea el esperado
>
> Ejemplo: Given que el cliente existe / When creo una cita / Then el estado es PENDIENTE.

**P: Explica un test que tengas.**
> En `CitaServiceTest`, el test `crearCita_exitosa` hace: Given → mockeamos el repo de citas para que `save()` devuelva una cita de prueba, y AuthClient para que diga que el cliente existe. When → llamamos a `citaService.crear(cita)`. Then → verificamos que la cita retornada tiene estado PENDIENTE y que `save()` fue llamado exactamente una vez.

**P (en vivo): Escribe un test para verificar que no se puede crear una cita en el pasado.**
```java
@Test
void crearCita_conFechaPasada_lanzaExcepcion() {
    // Given
    Cita cita = new Cita();
    cita.setFechaHora(LocalDateTime.now().minusDays(1)); // fecha pasada

    // When / Then
    assertThrows(IllegalArgumentException.class, () -> citaService.crear(cita));
}
```

---

## 5. PREGUNTAS DE SWAGGER Y YAML

**P: ¿Qué es OpenAPI?**
> Es un estándar para documentar APIs REST. Defines los endpoints, parámetros y respuestas en un archivo YAML/JSON. Swagger UI lee ese archivo y genera la interfaz visual interactiva. springdoc-openapi genera ese archivo automáticamente desde las anotaciones de Spring.

**P: ¿Cómo lees el YAML del Gateway?**
> El Gateway tiene un `application.yml` con rutas. Cada ruta tiene: `id` (nombre), `uri` (a dónde redirige), `predicates` (qué URLs captura) y `filters` (qué transforma). Por ejemplo: si llega `/api/v1/citas/**`, el predicado lo captura y lo redirige a `http://localhost:8086`.

**P: ¿Qué significa el `*` rojo en Swagger?**
> Que ese campo es obligatorio. Swagger lo detecta automáticamente desde las anotaciones `@NotNull` o `@NotBlank` en el DTO. Si mandas el body sin ese campo, el servidor retorna 400 Bad Request con el mensaje de validación.

**P: ¿Qué es `application/json` en Swagger?**
> Es el Content-Type — le dice al servidor que el body viene en formato JSON. Spring lo maneja automáticamente con `@RequestBody`. Si mandas XML o texto plano, el endpoint no lo acepta.

---

## 6. MODIFICACIONES EN VIVO MÁS PROBABLES

### Agregar una validación
**Escenario: "Agrega que el nombre de la mascota no puede tener números"**
```java
// En MascotaService, método crear():
if (mascota.getNombre().matches(".*\\d.*")) {
    throw new IllegalArgumentException("El nombre no puede contener números");
}
```

### Cambiar un código HTTP
**Escenario: "El POST de categorías debería retornar 201, no 200"**
```java
// En CategoriaController:
return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
// Antes era: return ResponseEntity.ok(service.crear(request));
```

### Agregar un log
**Escenario: "Agrega un log cuando se crea una cita"**
```java
// Al inicio de la clase CitaService:
private static final Logger log = LoggerFactory.getLogger(CitaService.class);

// En el método crear(), antes del return:
log.info("Cita creada para clienteId={} mascotaId={}", cita.getClienteId(), cita.getMascotaId());
```

### Agregar un endpoint nuevo
**Escenario: "Agrega un endpoint para buscar mascotas por nombre"**
```java
// En MascotaController:
@GetMapping("/buscar")
public ResponseEntity<List<Mascota>> buscarPorNombre(@RequestParam String nombre) {
    return ResponseEntity.ok(mascotaService.buscarPorNombre(nombre));
}

// En MascotaService:
public List<Mascota> buscarPorNombre(String nombre) {
    return mascotaRepository.findByNombreContainingIgnoreCase(nombre);
}
```

---

## TIPS FINALES

- Si no sabes algo: *"Eso no lo implementé directamente pero lo que hace es..."* — no te quedes en blanco
- Siempre apunta al código cuando expliques: abre el archivo, señala la línea
- Si el profe mata un MS: clic en Run del main de ese MS en VS Code
- La respuesta sobre degradación suave/dura te la van a preguntar seguro — es el patrón más visible de tu trabajo
- Si preguntan por algo de otro MS que no es tuyo: *"Ese MS lo implementó mi compañero, pero entiendo que funciona así..."*
