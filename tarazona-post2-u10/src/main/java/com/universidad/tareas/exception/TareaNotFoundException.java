package com.universidad.tareas.exception;

/**
 * Excepción lanzada cuando una Tarea no es encontrada por su ID.
 * Usada en TareaService para comunicar el error al controlador.
 */
public class TareaNotFoundException extends RuntimeException {
    public TareaNotFoundException(Long id) {
        super("Tarea no encontrada con ID: " + id);
    }
    public TareaNotFoundException(String message) {
        super(message);
    }
}
