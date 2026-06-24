package cl.vetnova.inventario.dto;

import cl.vetnova.inventario.model.TransferenciaStock;

public class CancelacionResponse {

    private TransferenciaStock transferencia;
    private String mensaje;

    public CancelacionResponse() {
    }

    public CancelacionResponse(TransferenciaStock transferencia, String mensaje) {
        this.transferencia = transferencia;
        this.mensaje = mensaje;
    }

    public TransferenciaStock getTransferencia() { return transferencia; }
    public void setTransferencia(TransferenciaStock transferencia) { this.transferencia = transferencia; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
