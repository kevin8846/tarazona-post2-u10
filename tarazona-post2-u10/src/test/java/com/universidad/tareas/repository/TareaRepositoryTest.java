package com.universidad.tareas.repository;

import com.universidad.tareas.model.Tarea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de la capa de repositorio con @DataJpaTest.
 * Usa H2 en memoria — los cambios se revierten automáticamente entre tests.
 * No carga el contexto completo de Spring Boot.
 * Patrón de nomenclatura: metodo_condicion_resultadoEsperado
 *
 * @author Andres Felipe Jimenez Ramirez
 */
@DataJpaTest
class TareaRepositoryTest {

    @Autowired
    TareaRepository repo;

    @Autowired
    TestEntityManager em;

    @BeforeEach
    void setUp() {
        // Insertar datos de prueba antes de cada test
        Tarea pendiente = new Tarea();
        pendiente.setTitulo("Pendiente");
        pendiente.setCompletada(false);
        em.persistAndFlush(pendiente);

        Tarea completada = new Tarea();
        completada.setTitulo("Completada");
        completada.setCompletada(true);
        em.persistAndFlush(completada);
    }

    // ── findByCompletada() ────────────────────────────────────────────────────

    @Test
    @DisplayName("findByCompletada(false): retorna solo las tareas pendientes")
    void findByCompletada_estadoFalse_retornaUnaTareaPendiente() {
        List<Tarea> pendientes = repo.findByCompletada(false);

        assertThat(pendientes).hasSize(1)
                .extracting("titulo")
                .containsExactly("Pendiente");
    }

    @Test
    @DisplayName("findByCompletada(true): retorna solo las tareas completadas")
    void findByCompletada_estadoTrue_retornaUnaTareaCompletada() {
        List<Tarea> completadas = repo.findByCompletada(true);

        assertThat(completadas).hasSize(1)
                .extracting("titulo")
                .containsExactly("Completada");
    }

    // ── save() ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("save: tarea nueva → persiste y genera ID autoincrementado")
    void save_tareaNueva_persisteConId() {
        Tarea nueva = new Tarea();
        nueva.setTitulo("Nueva tarea JPA");
        nueva.setCompletada(false);

        Tarea guardada = repo.save(nueva);

        assertThat(guardada.getId()).isNotNull().isPositive();
        assertThat(guardada.getTitulo()).isEqualTo("Nueva tarea JPA");
    }

    // ── findById() ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById: ID existente → retorna la tarea correcta")
    void findById_idExistente_retornaOptionalConTarea() {
        Tarea tarea = new Tarea();
        tarea.setTitulo("Buscar por ID");
        Tarea persistida = em.persistAndFlush(tarea);

        Optional<Tarea> resultado = repo.findById(persistida.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getTitulo()).isEqualTo("Buscar por ID");
    }

    @Test
    @DisplayName("findById: ID inexistente → retorna Optional vacío")
    void findById_idInexistente_retornaOptionalVacio() {
        Optional<Tarea> resultado = repo.findById(9999L);

        assertThat(resultado).isEmpty();
    }

    // ── deleteById() ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteById: ID existente → la tarea ya no existe en BD")
    void deleteById_idExistente_eliminaTarea() {
        Tarea tarea = new Tarea();
        tarea.setTitulo("A eliminar");
        Tarea persistida = em.persistAndFlush(tarea);
        Long id = persistida.getId();

        repo.deleteById(id);
        em.flush();

        assertThat(repo.findById(id)).isEmpty();
    }

    // ── findAll() ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findAll: con datos en BD → retorna todas las tareas")
    void findAll_conDatos_retornaTodasLasTareas() {
        List<Tarea> todas = repo.findAll();

        // setUp inserta 2 tareas por cada test
        assertThat(todas).hasSizeGreaterThanOrEqualTo(2);
    }
}
