package cl.vetnova.ventas.service;

import org.springframework.stereotype.Component;

import cl.vetnova.ventas.model.Pago;

/**
 * Implementación por defecto de la pasarela: autoriza todo pago con monto positivo.
 */
@Component
public class PasarelaPagoSimulada implements PasarelaPago {

    @Override
    public boolean autorizar(Pago pago) {
        return pago.getMonto() != null && pago.getMonto() > 0;
    }
}
