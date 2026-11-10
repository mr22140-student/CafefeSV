package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.PagoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.PagoDetalleDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Pago;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.PagoDetalle;

/**
 *
 * @author valen
 */
public class PagoDetalleModelTest extends ModelTestBase {

    @InjectMocks
    private PagoDetalleModel model;

    @Mock
    private PagoDetalleDAO pagoDetalleDAO;
    @Mock
    private PagoDAO pagoDAO;

    /**
     * Por defecto: faltan $10 por pagar.
     */
    @BeforeEach
    void saldoPorDefecto() {
        when(pagoDAO.calcularSaldo(any(), any())).thenReturn(new BigDecimal("10.00"));
    }

    @Test
    void noGuardaSiElDaoRechazaElMonto() {
        PagoDetalle detalle = prepararDetalle(pago(PagoDAO.PENDIENTE), PagoDetalleDAO.EFECTIVO, "15.00", null);
        when(pagoDetalleDAO.validarDetalle(eq(detalle), any())).thenReturn("El monto supera lo que falta por pagar ($10.00).");

        model.btnCrearHandler();

        verify(pagoDetalleDAO, never()).save(any());
        assertEquals("Monto no válido", ultimoMensaje().getSummary());
    }

    @Test
    void lePasaAlDaoLoQueFaltaPorPagar() {
        PagoDetalle detalle = prepararDetalle(pago(PagoDAO.PENDIENTE), PagoDetalleDAO.EFECTIVO, "4.00", null);

        model.btnCrearHandler();

        verify(pagoDetalleDAO).validarDetalle(detalle, new BigDecimal("10.00"));
        verify(pagoDetalleDAO).save(detalle);
    }

    @Test
    void limpiaLaReferenciaAntesDeValidar() {
        PagoDetalle detalle = prepararDetalle(pago(PagoDAO.PENDIENTE), PagoDetalleDAO.TARJETA, "5.00", "   ");

        model.btnCrearHandler();

        assertNull(detalle.getReferenciaExterna());
    }

    @Test
    void noModificaSiElPagoOriginalYaNoSePuedeCambiar() {
        Pago completado = pago(PagoDAO.COMPLETADO);
        PagoDetalle detalle = new PagoDetalle(UUID.randomUUID());
        detalle.setIdPago(completado);
        when(pagoDetalleDAO.validarPagoEditable(completado))
                .thenReturn("El pago está COMPLETADO; solo se pueden cambiar montos de pagos PENDIENTES.");
        model.seleccionarRegistro(detalle);

        model.btnModificarHandler();

        verify(pagoDetalleDAO, never()).update(any());
        assertEquals("No se puede modificar", ultimoMensaje().getSummary());
    }

    @Test
    void avisaCuandoSeCubreElTotalDeLaFactura() {
        Pago pago = pago(PagoDAO.PENDIENTE);
        // 1.ª vez (validar): faltan $10. 2.ª (después de guardar): ya no falta nada
        when(pagoDAO.calcularSaldo(eq(pago), any())).thenReturn(new BigDecimal("10.00"), BigDecimal.ZERO);
        prepararDetalle(pago, PagoDetalleDAO.EFECTIVO, "10.00", null);

        model.btnCrearHandler();

        assertTrue(ultimoMensaje().getSummary().startsWith("Ya se cubrió el total de la factura"));
    }

    @Test
    void alElegirElPago_proponeComoMontoLoQueFalta() {
        model.btnNuevoHandler();
        model.getRegistro().setIdPago(pago(PagoDAO.PENDIENTE));

        model.pagoSeleccionadoHandler();

        assertEquals(new BigDecimal("10.00"), model.getRegistro().getMonto());
    }

    private Pago pago(String estado) {
        Pago pago = new Pago(UUID.randomUUID());
        pago.setEstado(estado);
        pago.setIdFactura(new Factura(UUID.randomUUID()));
        return pago;
    }

    private PagoDetalle prepararDetalle(Pago pago, String tipoPago, String monto, String referencia) {
        model.btnNuevoHandler();
        PagoDetalle detalle = model.getRegistro();
        detalle.setIdPago(pago);
        detalle.setTipoPago(tipoPago);
        detalle.setMonto(new BigDecimal(monto));
        detalle.setReferenciaExterna(referencia);
        return detalle;
    }
}