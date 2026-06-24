package cl.vetnova.auth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.vetnova.auth.dto.ActualizarClienteRequest;
import cl.vetnova.auth.dto.CrearClienteRequest;
import cl.vetnova.auth.model.Cliente;
import cl.vetnova.auth.service.ClienteService;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<Cliente>> listar() {
        return ResponseEntity.ok(clienteService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Cliente> crear(@RequestBody CrearClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable Long id, @RequestBody ActualizarClienteRequest request) {
        return ResponseEntity.ok(clienteService.actualizarDatos(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Cliente> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.desactivar(id));
    }
}
