package cl.vetnova.inventario.service;

import cl.vetnova.inventario.model.Inventario;
import cl.vetnova.inventario.model.MovimientoStock;
import cl.vetnova.inventario.model.TipoMovimiento;

/**
 * Operación de stock compartida entre {@link InventarioService} (endpoints
 * entrada/salida) y {@link MovimientoStockService} (endpoint /movimientos).
 * Centraliza la mutación de stock, el registro del MovimientoStock y el
 * disparo de alertas de stock crítico.
 */
public interface OperacionStock {

    MovimientoStock aplicarMovimiento(Inventario inventario, TipoMovimiento tipo, Integer cantidad,
                                      String motivo, String responsable);
}
