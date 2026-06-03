package com.universidad.tareas.repository;

import com.universidad.tareas.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de acceso a datos para la entidad Tarea.
 * Extiende JpaRepository para operaciones CRUD estándar.
 *
 * @author Andres Felipe Jimenez Ramirez
 */
@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    /**
     * Consulta derivada: filtra tareas por estado de completitud.
     * Probada con @DataJpaTest en TareaRepositoryTest.
     */
    List<Tarea> findByCompletada(boolean completada);
}
