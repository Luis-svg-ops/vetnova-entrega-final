package cl.vetnova.ventas.service;

import cl.vetnova.ventas.model.Pago;

/**
 * Abstracción de la pasarela de pagos. Se expone como interfaz para que
 * {@link PagoService} dependa de la abstracción (y se pueda simular en pruebas).
 */
public interface PasarelaPago {

    boolean autorizar(Pago pago);
}
