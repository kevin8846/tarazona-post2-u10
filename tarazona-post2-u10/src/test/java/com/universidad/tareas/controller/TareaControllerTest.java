package com.universidad.tareas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universidad.tareas.exception.TareaNotFoundException;
import com.universidad.tareas.model.Tarea;
import com.universidad.tareas.service.TareaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de capa web usando @WebMvcTest.
 * Solo carga el contexto del controlador (sin BD).
 * TareaService es un @MockBean (Mockito gestionado por Spring).
 * Patrón de nomenclatura: metodo_condicion_resultadoEsperado
 *
 * @author Andres Felipe Jimenez Ramirez
 */
@WebMvcTest(TareaController.class)
class TareaControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TareaService service;

    @Autowired
    ObjectMapper objectMapper;

    // ── GET /api/tareas/{id} ──────────────────────────────────────────────────

    @Test
    @DisplayName("GET /{id}: tarea existe → retorna 200 con datos JSON")
    void get_tareaExiste_retorna200ConJson() throws Exception {
        Tarea tarea = new Tarea();
        tarea.setId(1L);
        tarea.setTitulo("Test tarea");

        when(service.buscarPorId(1L)).thenReturn(tarea);

        mockMvc.perform(get("/api/tareas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Test tarea"));
    }

    @Test
    @DisplayName("GET /{id}: tarea no existe → retorna 404 Not Found")
    void get_tareaNoExiste_retorna404() throws Exception {
        when(service.buscarPorId(99L))
                .thenThrow(new TareaNotFoundException("Tarea no encontrada con ID: 99"));

        mockMvc.perform(get("/api/tareas/99"))
                .andExpect(status().isNotFound());
    }

    // ── GET /api/tareas ───────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /: lista todas las tareas → retorna 200 con array JSON")
    void getAll_tareasExistentes_retorna200ConLista() throws Exception {
        Tarea t1 = new Tarea("Tarea A", null);
        Tarea t2 = new Tarea("Tarea B", null);

        when(service.listarTodas()).thenReturn(List.of(t1, t2));

        mockMvc.perform(get("/api/tareas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titulo").value("Tarea A"))
                .andExpect(jsonPath("$[1].titulo").value("Tarea B"));
    }

    // ── POST /api/tareas ──────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /: tarea válida → retorna 201 Created con la tarea")
    void post_tareaValida_retorna201() throws Exception {
        Tarea nueva = new Tarea();
        nueva.setTitulo("Nueva tarea");
        nueva.setDescripcion("Descripción de prueba");

        Tarea guardada = new Tarea();
        guardada.setId(1L);
        guardada.setTitulo("Nueva tarea");

        when(service.crear(any(Tarea.class))).thenReturn(guardada);

        mockMvc.perform(post("/api/tareas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Nueva tarea"));
    }

    @Test
    @DisplayName("POST /: título vacío → retorna 400 Bad Request")
    void post_tituloVacio_retorna400() throws Exception {
        Tarea invalida = new Tarea();
        invalida.setTitulo("  ");

        when(service.crear(any(Tarea.class)))
                .thenThrow(new IllegalArgumentException("El título no puede estar vacío"));

        mockMvc.perform(post("/api/tareas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalida)))
                .andExpect(status().isBadRequest());
    }

    // ── PATCH /api/tareas/{id}/completar ─────────────────────────────────────

    @Test
    @DisplayName("PATCH /{id}/completar: tarea existente → retorna 200 con completada=true")
    void patch_completar_tareaExistente_retorna200() throws Exception {
        Tarea completada = new Tarea();
        completada.setId(1L);
        completada.setTitulo("Tarea completada");
        completada.setCompletada(true);

        when(service.completar(1L)).thenReturn(completada);

        mockMvc.perform(patch("/api/tareas/1/completar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completada").value(true));
    }

    // ── DELETE /api/tareas/{id} ───────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /{id}: tarea existente → retorna 204 No Content")
    void delete_tareaExistente_retorna204() throws Exception {
        mockMvc.perform(delete("/api/tareas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /{id}: tarea no existe → retorna 404 Not Found")
    void delete_tareaNoExiste_retorna404() throws Exception {
        org.mockito.Mockito.doThrow(new TareaNotFoundException(55L))
                .when(service).eliminar(55L);

        mockMvc.perform(delete("/api/tareas/55"))
                .andExpect(status().isNotFound());
    }
}
