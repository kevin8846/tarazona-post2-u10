package com.universidad.tareas.controller;

import com.universidad.tareas.exception.TareaNotFoundException;
import com.universidad.tareas.model.Tarea;
import com.universidad.tareas.service.TareaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la entidad Tarea.
 * Probado con @WebMvcTest en TareaControllerTest.
 *
 * @author Andres Felipe Jimenez Ramirez
 */
@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    private final TareaService service;

    public TareaController(TareaService service) {
        this.service = service;
    }

    /** GET /api/tareas → lista todas las tareas */
    @GetMapping
    public List<Tarea> listar() {
        return service.listarTodas();
    }

    /** GET /api/tareas/{id} → busca tarea por ID, 404 si no existe */
    @GetMapping("/{id}")
    public ResponseEntity<Tarea> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /** POST /api/tareas → crea una tarea nueva, 201 Created */
    @PostMapping
    public ResponseEntity<Tarea> crear(@Valid @RequestBody Tarea tarea) {
        Tarea creada = service.crear(tarea);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    /** PATCH /api/tareas/{id}/completar → marca la tarea como completada */
    @PatchMapping("/{id}/completar")
    public ResponseEntity<Tarea> completar(@PathVariable Long id) {
        return ResponseEntity.ok(service.completar(id));
    }

    /** DELETE /api/tareas/{id} → elimina la tarea, 204 No Content */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /** Manejador global: TareaNotFoundException → 404 Not Found */
    @ExceptionHandler(TareaNotFoundException.class)
    public ResponseEntity<String> handleNotFound(TareaNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /** Manejador: IllegalArgumentException → 400 Bad Request */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
