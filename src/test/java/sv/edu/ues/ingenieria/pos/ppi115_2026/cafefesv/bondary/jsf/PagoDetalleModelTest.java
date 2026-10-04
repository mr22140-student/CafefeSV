package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
     * Por defecto: la factura cuesta $10 y no se ha pagado nada.
     */
    @BeforeEach
    void totalesPorDefecto() {
        when(pagoDAO.totalFactura(any())).thenReturn(new BigDecimal("10.00"));
        when(pagoDAO.sumaMontos(any())).thenReturn(BigDecimal.ZERO);
    }

    @Test
    void noAgregaMontosAUnPagoQueNoEstaPendiente() {
        prepararDetalle(pago(PagoModel.COMPLETADO), PagoDetalleModel.EFECTIVO, "5.00", null);

        model.btnCrearHandler();

        verify(pagoDetalleDAO, never()).save(any());
        assertEquals("El pago está COMPLETADO; solo se pueden cambiar montos de pagos PENDIENTES.",
                ultimoMensaje().getSummary());
    }

    @Test
    void noGuardaUnMontoDeCero() {
        prepararDetalle(pago(PagoModel.PENDIENTE), PagoDetalleModel.EFECTIVO, "0", null);

        model.btnCrearHandler();

        verify(pagoDetalleDAO, never()).save(any());
        assertEquals("El monto debe ser mayor a cero.", ultimoMensaje().getSummary());
    }

    @Test
    void conTarjeta_exigeReferencia() {
        prepararDetalle(pago(PagoModel.PENDIENTE), PagoDetalleModel.TARJETA, "5.00", "  ");

        model.btnCrearHandler();

        verify(pagoDetalleDAO, never()).save(any());
        assertEquals("Para TARJETA debe indicar la referencia (número de autorización o transferencia).",
                ultimoMensaje().getSummary());
    }

    @Test
    void noPermitePagarMasDeLoQueFalta() {
        prepararDetalle(pago(PagoModel.PENDIENTE), PagoDetalleModel.EFECTIVO, "15.00", null);

        model.btnCrearHandler();

        verify(pagoDetalleDAO, never()).save(any());
        assertTrue(ultimoMensaje().getSummary().startsWith("El monto supera lo que falta por pagar"));
    }

    @Test
    void guardaUnMontoValidoEnEfectivo() {
        PagoDetalle detalle = prepararDetalle(pago(PagoModel.PENDIENTE), PagoDetalleModel.EFECTIVO, "4.00", null);

        model.btnCrearHandler();

        verify(pagoDetalleDAO).save(detalle);
    }

    @Test
    void avisaCuandoSeCubreElTotalDeLaFactura() {
        Pago pago = pago(PagoModel.PENDIENTE);
        // 1.ª consulta (validar): no se ha pagado nada. 2.ª (después de guardar): ya se pagaron $10
        when(pagoDAO.sumaMontos(pago)).thenReturn(BigDecimal.ZERO, new BigDecimal("10.00"));
        prepararDetalle(pago, PagoDetalleModel.EFECTIVO, "10.00", null);

        model.btnCrearHandler();

        assertTrue(ultimoMensaje().getSummary().startsWith("Ya se cubrió el total de la factura"));
    }

    private Pago pago(String estado) {
        Pago p = new Pago(UUID.randomUUID());
        p.setEstado(estado);
        p.setIdFactura(new Factura(UUID.randomUUID()));
        return p;
    }

    private PagoDetalle prepararDetalle(Pago pago, String tipo, String monto, String referencia) {
        model.btnNuevoHandler();
        PagoDetalle d = model.getRegistro();
        d.setIdPago(pago);
        d.setTipoPago(tipo);
        d.setMonto(new BigDecimal(monto));
        d.setReferenciaExterna(referencia);
        return d;
    }

}
