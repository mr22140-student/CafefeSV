package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verifyNoInteractions;
import java.util.UUID;

import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Pago;

/**
 *
 * @author valen
 */
class PagoDAOTest extends AbstractDAOTestBase<Pago, PagoDAO> {

    @Override
    protected PagoDAO crearDAO() {
        return new PagoDAO();
    }

    @Override
    protected Pago crearEntidad() {
        return new Pago();
    }

    @Test
    void sumaMontos_sinDetalles_devuelveCero() {
        consultaDecimalQueDevuelve(null);
        assertEquals(BigDecimal.ZERO, dao.sumaMontos(new Pago()));
    }

    @Test
    void totalFactura_sinProductos_devuelveCero() {
        consultaDecimalQueDevuelve(null);
        assertEquals(BigDecimal.ZERO, dao.totalFactura(new Factura()));
    }

    @Test
    void existePagoActivo_sinPagos_devuelveFalse() {
        @SuppressWarnings("unchecked")
        TypedQuery<Long> consulta = mock(TypedQuery.class, RETURNS_SELF);
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(consulta);
        when(consulta.getSingleResult()).thenReturn(0L);

        assertFalse(dao.existePagoActivo(new Factura(), null));
    }
    // ===================== calcularSaldo =====================

    @Test
    void calcularSaldo_esElTotalMenosLoPagado() {
        consultasDecimalesQueDevuelven(new BigDecimal("10.00"), new BigDecimal("4.00"));

        assertEquals(new BigDecimal("6.00"), dao.calcularSaldo(pago(PagoDAO.PENDIENTE), BigDecimal.ZERO));
    }

    @Test
    void calcularSaldo_noCuentaDosVecesElMontoQueSeEstaModificando() {
        consultasDecimalesQueDevuelven(new BigDecimal("10.00"), new BigDecimal("4.00"));

        assertEquals(new BigDecimal("10.00"), dao.calcularSaldo(pago(PagoDAO.PENDIENTE), new BigDecimal("4.00")));
    }

    @Test
    void calcularSaldo_sinPago_esCero() {
        assertEquals(BigDecimal.ZERO, dao.calcularSaldo(null, BigDecimal.ZERO));
    }

    // ===================== validarPago =====================
    @Test
    void validarPago_sinFactura_devuelveError() {
        Pago pago = pago(PagoDAO.PENDIENTE);
        pago.setIdFactura(null);

        assertNotNull(dao.validarPago(pago));
    }

    @Test
    void validarPago_conEstadoQueNoExiste_devuelveError() {
        assertNotNull(dao.validarPago(pago("PERDIDO")));
    }

    @Test
    void validarPago_facturaConOtroPagoActivo_devuelveError() {
        simularConteo(1);
        assertNotNull(dao.validarPago(pago(PagoDAO.PENDIENTE)));
    }

    @Test
    void validarPago_facturaSinOtroPago_devuelveNull() {
        simularConteo(0);
        assertNull(dao.validarPago(pago(PagoDAO.PENDIENTE)));
    }

    @Test
    void validarPago_anulado_noBuscaOtrosPagos() {
        assertNull(dao.validarPago(pago(PagoDAO.ANULADO)));
        verifyNoInteractions(em);
    }

    // ===================== validarCambioDeEstado =====================
    @Test
    void validarCambioDeEstado_pagoAnulado_devuelveError() {
        assertNotNull(dao.validarCambioDeEstado(PagoDAO.ANULADO, PagoDAO.PENDIENTE));
    }

    @Test
    void validarCambioDeEstado_deCompletadoAPendiente_devuelveError() {
        assertNotNull(dao.validarCambioDeEstado(PagoDAO.COMPLETADO, PagoDAO.PENDIENTE));
    }

    @Test
    void validarCambioDeEstado_deCompletadoAAnulado_devuelveNull() {
        assertNull(dao.validarCambioDeEstado(PagoDAO.COMPLETADO, PagoDAO.ANULADO));
    }

    // ===================== validarCompletar =====================
    @Test
    void validarCompletar_siNoPasaACompletado_noRevisaNada() {
        assertNull(dao.validarCompletar(pago(PagoDAO.PENDIENTE), PagoDAO.PENDIENTE));
        verifyNoInteractions(em);
    }

    @Test
    void validarCompletar_facturaSinProductos_devuelveError() {
        consultasDecimalesQueDevuelven(BigDecimal.ZERO, BigDecimal.ZERO);
        assertNotNull(dao.validarCompletar(pago(PagoDAO.COMPLETADO), PagoDAO.PENDIENTE));
    }

    @Test
    void validarCompletar_sinCubrirElTotal_devuelveError() {
        consultasDecimalesQueDevuelven(new BigDecimal("10.00"), new BigDecimal("5.00"));
        assertNotNull(dao.validarCompletar(pago(PagoDAO.COMPLETADO), PagoDAO.PENDIENTE));
    }

    @Test
    void validarCompletar_cubriendoElTotal_devuelveNull() {
        consultasDecimalesQueDevuelven(new BigDecimal("10.00"), new BigDecimal("10.00"));
        assertNull(dao.validarCompletar(pago(PagoDAO.COMPLETADO), PagoDAO.PENDIENTE));
    }

    // ===================== validarEliminar =====================
    @Test
    void validarEliminar_pagoCompletado_devuelveError() {
        assertNotNull(dao.validarEliminar(pago(PagoDAO.COMPLETADO), PagoDAO.COMPLETADO));
    }

    @Test
    void validarEliminar_conMontosRegistrados_devuelveError() {
        simularConteo(2);
        assertNotNull(dao.validarEliminar(pago(PagoDAO.PENDIENTE), PagoDAO.PENDIENTE));
    }

    @Test
    void validarEliminar_pendienteSinMontos_devuelveNull() {
        simularConteo(0);
        assertNull(dao.validarEliminar(pago(PagoDAO.PENDIENTE), PagoDAO.PENDIENTE));
    }

    // ===================== calcularEstadoFactura =====================
    @Test
    void calcularEstadoFactura_alCompletar_quedaPagada() {
        assertEquals(FacturaDAO.PAGADA, dao.calcularEstadoFactura(PagoDAO.PENDIENTE, PagoDAO.COMPLETADO));
    }

    @Test
    void calcularEstadoFactura_alAnularUnCompletado_regresaAPendiente() {
        assertEquals(FacturaDAO.PENDIENTE, dao.calcularEstadoFactura(PagoDAO.COMPLETADO, PagoDAO.ANULADO));
    }

    @Test
    void calcularEstadoFactura_sinCambioImportante_noCambia() {
        assertNull(dao.calcularEstadoFactura(PagoDAO.PENDIENTE, PagoDAO.ANULADO));
    }

    // ===================== ayudantes =====================
    private Pago pago(String estado) {
        Pago pago = new Pago(UUID.randomUUID());
        pago.setEstado(estado);
        pago.setIdFactura(new Factura(UUID.randomUUID()));
        return pago;
    }

    // Las sumas se piden en este orden: primero el total de la factura y luego lo pagado.
    private void consultasDecimalesQueDevuelven(BigDecimal totalFactura, BigDecimal pagado) {
        @SuppressWarnings("unchecked")
        TypedQuery<BigDecimal> consulta = mock(TypedQuery.class, RETURNS_SELF);
        when(em.createQuery(anyString(), eq(BigDecimal.class))).thenReturn(consulta);
        when(consulta.getSingleResult()).thenReturn(totalFactura, pagado);
    }

    private void consultaDecimalQueDevuelve(BigDecimal valor) {
        @SuppressWarnings("unchecked")
        TypedQuery<BigDecimal> consulta = mock(TypedQuery.class, RETURNS_SELF);
        when(em.createQuery(anyString(), eq(BigDecimal.class))).thenReturn(consulta);
        when(consulta.getSingleResult()).thenReturn(valor);
    }
}
