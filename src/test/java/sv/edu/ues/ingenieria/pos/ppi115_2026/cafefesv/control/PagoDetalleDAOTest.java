package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Pago;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.PagoDetalle;

/**
 *
 * @author valen
 */
public class PagoDetalleDAOTest extends AbstractDAOTestBase<PagoDetalle, PagoDetalleDAO> {

    private static final BigDecimal FALTAN_DIEZ = new BigDecimal("10.00");

    @Override
    protected PagoDetalleDAO crearDAO() {
        return new PagoDetalleDAO();
    }

    @Override
    protected PagoDetalle crearEntidad() {
        return new PagoDetalle();
    }

    // ===================== validarPagoEditable =====================

    @Test
    void validarPagoEditable_pagoCompletado_devuelveError() {
        assertNotNull(dao.validarPagoEditable(pago(PagoDAO.COMPLETADO)));
    }

    @Test
    void validarPagoEditable_pagoPendiente_devuelveNull() {
        assertNull(dao.validarPagoEditable(pago(PagoDAO.PENDIENTE)));
    }

    // ===================== validarDetalle =====================

    @Test
    void validarDetalle_sinPago_devuelveError() {
        assertNotNull(dao.validarDetalle(detalle(null, PagoDetalleDAO.EFECTIVO, "5.00", null), FALTAN_DIEZ));
    }

    @Test
    void validarDetalle_pagoQueNoEstaPendiente_devuelveError() {
        assertNotNull(dao.validarDetalle(detalle(pago(PagoDAO.COMPLETADO), PagoDetalleDAO.EFECTIVO, "5.00", null), FALTAN_DIEZ));
    }

    @Test
    void validarDetalle_tipoDePagoQueNoExiste_devuelveError() {
        assertNotNull(dao.validarDetalle(detalle(pago(PagoDAO.PENDIENTE), "BITCOIN", "5.00", null), FALTAN_DIEZ));
    }

    @Test
    void validarDetalle_montoCero_devuelveError() {
        assertNotNull(dao.validarDetalle(detalle(pago(PagoDAO.PENDIENTE), PagoDetalleDAO.EFECTIVO, "0", null), FALTAN_DIEZ));
    }

    @Test
    void validarDetalle_tarjetaSinReferencia_devuelveError() {
        assertNotNull(dao.validarDetalle(detalle(pago(PagoDAO.PENDIENTE), PagoDetalleDAO.TARJETA, "5.00", null), FALTAN_DIEZ));
    }

    @Test
    void validarDetalle_montoMayorALoQueFalta_devuelveError() {
        assertNotNull(dao.validarDetalle(detalle(pago(PagoDAO.PENDIENTE), PagoDetalleDAO.EFECTIVO, "15.00", null), FALTAN_DIEZ));
    }

    @Test
    void validarDetalle_efectivoDentroDelSaldo_devuelveNull() {
        assertNull(dao.validarDetalle(detalle(pago(PagoDAO.PENDIENTE), PagoDetalleDAO.EFECTIVO, "4.00", null), FALTAN_DIEZ));
    }

    @Test
    void validarDetalle_tarjetaConReferencia_devuelveNull() {
        assertNull(dao.validarDetalle(detalle(pago(PagoDAO.PENDIENTE), PagoDetalleDAO.TARJETA, "10.00", "AUT-12345"), FALTAN_DIEZ));
    }

    // ===================== ayudantes =====================

    private Pago pago(String estado) {
        Pago pago = new Pago(UUID.randomUUID());
        pago.setEstado(estado);
        pago.setIdFactura(new Factura(UUID.randomUUID()));
        return pago;
    }

    private PagoDetalle detalle(Pago pago, String tipoPago, String monto, String referencia) {
        PagoDetalle detalle = new PagoDetalle(UUID.randomUUID());
        detalle.setIdPago(pago);
        detalle.setTipoPago(tipoPago);
        detalle.setMonto(new BigDecimal(monto));
        detalle.setReferenciaExterna(referencia);
        return detalle;
    }
}