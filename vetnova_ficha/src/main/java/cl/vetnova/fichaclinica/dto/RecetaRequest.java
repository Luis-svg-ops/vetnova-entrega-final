package cl.vetnova.fichaclinica.dto;

import java.sql.Date;
import java.util.List;

public class RecetaRequest {

    private Long fichaId;
    private Long veterinarioId;
    private List<MedicamentoRequest> medicamentos;
    private Date fechaVencimiento;

    public Long getFichaId() { return fichaId; }
    public void setFichaId(Long fichaId) { this.fichaId = fichaId; }
    public Long getVeterinarioId() { return veterinarioId; }
    public void setVeterinarioId(Long veterinarioId) { this.veterinarioId = veterinarioId; }
    public List<MedicamentoRequest> getMedicamentos() { return medicamentos; }
    public void setMedicamentos(List<MedicamentoRequest> medicamentos) { this.medicamentos = medicamentos; }
    public Date getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(Date fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
}
