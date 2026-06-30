package cl.vetnova.agenda.service;

import cl.vetnova.agenda.model.Cita;

// Contrato para generar y cancelar recordatorios desde el ciclo de vida de una cita.
// CitaService depende de esta interfaz (no de RecordatorioService directamente) para facilitar el testing.
public interface RecordatorioGenerador {

    // Genera un recordatorio EMAIL automáticamente al crear una cita
    void generarParaCita(Cita cita);

    // Cancela todos los recordatorios no enviados cuando se cancela una cita
    void cancelarPorCita(Long citaId);
}
