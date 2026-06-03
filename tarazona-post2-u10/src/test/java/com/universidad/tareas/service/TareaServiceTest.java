package com.universidad.tareas.service;

import com.universidad.tareas.exception.TareaNotFoundException;
import com.universidad.tareas.model.Tarea;
import com.universidad.tareas.repository.TareaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de TareaService usando Mockito.
 * El repositorio es un mock: no se toca la base de datos.
 * Patrón de nomenclatura: metodo_condicion_resultadoEsperado
 *
 * @author Andres Felipe Jimenez Ramirez
 */
@ExtendWith(MockitoExtension.class)
class TareaServiceTest {

    @Mock
    TareaRepository repo;

    @InjectMocks
    TareaService service;

    // ── crear() ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("crear: título válido → guarda y retorna la tarea")
    void crear_conTituloValido_guardaYRetorna() {
        Tarea tarea = new Tarea();
        tarea.setTitulo("Estudiar JUnit");

        when(repo.save(any(Tarea.class))).thenReturn(tarea);

        Tarea resultado = service.crear(tarea);

        assertThat(resultado.getTitulo()).isEqualTo("Estudiar JUnit");
        verify(repo).save(tarea);
    }

    @Test
    @DisplayName("crear: título vacío → lanza IllegalArgumentException y NO guarda")
    void crear_conTituloVacio_lanzaIllegalArgumentException() {
        Tarea tarea = new Tarea();
        tarea.setTitulo("   ");

        assertThrows(IllegalArgumentException.class, () -> service.crear(tarea));

        // Verificar que el repositorio NUNCA fue llamado
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("crear: título nulo → lanza IllegalArgumentException")
    void crear_conTituloNulo_lanzaIllegalArgumentException() {
        Tarea tarea = new Tarea();
        tarea.setTitulo(null);

        assertThrows(IllegalArgumentException.class, () -> service.crear(tarea));
        verify(repo, never()).save(any());
    }

    // ── buscarPorId() ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("buscarPorId: ID existente → retorna la tarea correcta")
    void buscarPorId_idExistente_retornaTarea() {
        Tarea tarea = new Tarea();
        tarea.setId(1L);
        tarea.setTitulo("Tarea existente");

        when(repo.findById(1L)).thenReturn(Optional.of(tarea));

        Tarea resultado = service.buscarPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getTitulo()).isEqualTo("Tarea existente");
        verify(repo).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId: ID no existente → lanza TareaNotFoundException")
    void buscarPorId_idNoExistente_lanzaTareaNotFoundException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TareaNotFoundException.class, () -> service.buscarPorId(99L));
        verify(repo).findById(99L);
    }

    // ── completar() ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("completar: tarea pendiente → queda marcada como completada")
    void completar_tareaPendiente_marcaComoCompletada() {
        Tarea tarea = new Tarea();
        tarea.setId(1L);
        tarea.setTitulo("Pendiente");
        tarea.setCompletada(false);

        when(repo.findById(1L)).thenReturn(Optional.of(tarea));
        when(repo.save(any(Tarea.class))).thenAnswer(inv -> inv.getArgument(0));

        Tarea resultado = service.completar(1L);

        assertThat(resultado.isCompletada()).isTrue();
        verify(repo).save(tarea);
    }

    @Test
    @DisplayName("completar: ID inexistente → lanza TareaNotFoundException")
    void completar_idInexistente_lanzaTareaNotFoundException() {
        when(repo.findById(50L)).thenReturn(Optional.empty());

        assertThrows(TareaNotFoundException.class, () -> service.completar(50L));
        verify(repo, never()).save(any());
    }

    // ── listarPorEstado() ─────────────────────────────────────────────────────

    @Test
    @DisplayName("listarPorEstado: completada=false → retorna lista de pendientes")
    void listarPorEstado_completadaFalse_retornaListaPendientes() {
        Tarea t1 = new Tarea("Tarea 1", null);
        Tarea t2 = new Tarea("Tarea 2", null);

        when(repo.findByCompletada(false)).thenReturn(List.of(t1, t2));

        List<Tarea> resultado = service.listarPorEstado(false);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting("titulo")
                .containsExactlyInAnyOrder("Tarea 1", "Tarea 2");
        verify(repo).findByCompletada(false);
    }

    // ── eliminar() ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminar: ID existente → llama deleteById exactamente una vez")
    void eliminar_idExistente_llamaDeleteById() {
        Tarea tarea = new Tarea();
        tarea.setId(2L);
        tarea.setTitulo("A eliminar");

        when(repo.findById(2L)).thenReturn(Optional.of(tarea));
        doNothing().when(repo).deleteById(2L);

        service.eliminar(2L);

        verify(repo, times(1)).deleteById(2L);
    }

    @Test
    @DisplayName("eliminar: ID inexistente → lanza TareaNotFoundException sin eliminar")
    void eliminar_idInexistente_lanzaTareaNotFoundException() {
        when(repo.findById(77L)).thenReturn(Optional.empty());

        assertThrows(TareaNotFoundException.class, () -> service.eliminar(77L));
        verify(repo, never()).deleteById(any());
    }
}
