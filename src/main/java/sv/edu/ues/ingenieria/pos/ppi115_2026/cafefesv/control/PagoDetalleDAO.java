/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Pago;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.PagoDetalle;

/**
 *
 * @author daniel
 */
public interface PagoDetalleDAO extends GenericDAO<PagoDetalle, UUID> {

    List<PagoDetalle> findByMonto(BigDecimal monto);

    List<PagoDetalle> findByTipoPago(String tipoPago);

    List<PagoDetalle> findByObservaciones(String observaciones);

    List<PagoDetalle> findByReferenciaExterna(String referenciaExterna);

    List<PagoDetalle> findByPago(Pago pago);
}