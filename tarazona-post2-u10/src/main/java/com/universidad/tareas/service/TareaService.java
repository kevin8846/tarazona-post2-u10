package com.universidad.tareas.service;

import com.universidad.tareas.exception.TareaNotFoundException;
import com.universidad.tareas.model.Tarea;
import com.universidad.tareas.repository.TareaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio con la lógica de negocio para Tarea.
 * Clase bajo prueba en TareaServiceTest usando Mockito.
 *
 * @author Andres Felipe Jimenez Ramirez
 */
@Service
public class TareaService {

    private final TareaRepository repo;

    public TareaService(TareaRepository repo) {
        this.repo = repo;
    }

    /**
     * Crea una nueva tarea.
     * Lanza IllegalArgumentException si el título es nulo o vacío.
     */
    @Transactional
    public Tarea crear(Tarea tarea) {
        if (tarea.getTitulo() == null || tarea.getTitulo().isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        return repo.save(tarea);
    }

    /**
     * Busca una tarea por ID.
     * Lanza TareaNotFoundException si no existe.
     */
    public Tarea buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new TareaNotFoundException(id));
    }

    /**
     * Marca una tarea como completada.
     */
    @Transactional
    public Tarea completar(Long id) {
        Tarea tarea = buscarPorId(id);
        tarea.setCompletada(true);
        return repo.save(tarea);
    }

    /**
     * Lista todas las tareas.
     */
    public List<Tarea> listarTodas() {
        return repo.findAll();
    }

    /**
     * Lista tareas filtradas por estado de completitud.
     */
    public List<Tarea> listarPorEstado(boolean completada) {
        return repo.findByCompletada(completada);
    }

    /**
     * Elimina una tarea por ID.
     * Lanza TareaNotFoundException si no existe.
     */
    @Transactional
    public void eliminar(Long id) {
        buscarPorId(id); // valida existencia
        repo.deleteById(id);
    }
}
