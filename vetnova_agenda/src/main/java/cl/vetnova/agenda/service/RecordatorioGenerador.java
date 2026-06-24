package cl.vetnova.agenda.service;

import cl.vetnova.agenda.model.Cita;

/**
 * Generación y cancelación automática de recordatorios disparada desde el ciclo
 * de vida de una {@link Cita}. Se expone como interfaz para que
 * {@link CitaService} dependa de una abstracción (y no de la clase concreta).
 */
public interface RecordatorioGenerador {

    void generarParaCita(Cita cita);

    void cancelarPorCita(Long citaId);
}
