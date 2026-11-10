package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.FacturaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.PagoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Pago;

/**
 *
 * @author valen
 */
public class PagoModelTest extends ModelTestBase {

    @InjectMocks
    private PagoModel model;

    @Mock
    private PagoDAO pagoDAO;
    @Mock
    private FacturaDAO facturaDAO;

    @Test
    void nuevoPago_empiezaPendiente() {
        model.btnNuevoHandler();

        assertEquals(PagoDAO.PENDIENTE, model.getRegistro().getEstado());
    }

    @Test
    void noGuardaSiElDaoRechazaElPago() {
        model.btnNuevoHandler();
        Pago pago = model.getRegistro();
        when(pagoDAO.validarPago(pago)).thenReturn("Debe seleccionar la factura.");

        model.btnCrearHandler();

        verify(pagoDAO, never()).save(any());
        assertEquals("Pago no válido", ultimoMensaje().getSummary());
    }

    @Test
    void guardaSiElDaoNoEncuentraErrores() {
        model.btnNuevoHandler();
        Pago pago = model.getRegistro();
        pago.setIdFactura(factura());

        model.btnCrearHandler();

        verify(pagoDAO).save(pago);
    }

    @Test
    void noModificaSiElDaoRechazaElCambioDeEstado() {
        Pago anulado = pago(PagoDAO.ANULADO, factura());
        when(pagoDAO.validarCambioDeEstado(PagoDAO.ANULADO, PagoDAO.ANULADO)).thenReturn("No se puede modificar un pago ANULADO.");
        model.seleccionarRegistro(anulado);

        model.btnModificarHandler();

        verify(pagoDAO, never()).update(any());
        assertEquals("No se puede modificar", ultimoMensaje().getSummary());
    }

    @Test
    void noCompletaSiElDaoDiceQueNoSeCubreElTotal() {
        Pago pago = pago(PagoDAO.PENDIENTE, factura());
        model.seleccionarRegistro(pago);
        pago.setEstado(PagoDAO.COMPLETADO);
        when(pagoDAO.validarCompletar(pago, PagoDAO.PENDIENTE)).thenReturn("No se puede completar: se han pagado $5.00 de $10.00.");

        model.btnModificarHandler();

        verify(pagoDAO, never()).update(any());
        assertEquals("No se puede completar", ultimoMensaje().getSummary());
    }

    @Test
    void alCompletarElPago_guardaLaFacturaConElEstadoQueCalculaElDao() {
        Factura factura = factura();
        Pago pago = pago(PagoDAO.PENDIENTE, factura);
        model.seleccionarRegistro(pago);
        pago.setEstado(PagoDAO.COMPLETADO);
        when(pagoDAO.calcularEstadoFactura(PagoDAO.PENDIENTE, PagoDAO.COMPLETADO)).thenReturn(FacturaDAO.PAGADA);

        model.btnModificarHandler();

        verify(pagoDAO).update(pago);
        verify(facturaDAO).update(factura);
        assertEquals(FacturaDAO.PAGADA, factura.getEstado());
    }

    @Test
    void siElDaoDiceQueLaFacturaNoCambia_noLaActualiza() {
        Pago pago = pago(PagoDAO.PENDIENTE, factura());
        model.seleccionarRegistro(pago);

        model.btnModificarHandler();

        verify(pagoDAO).update(pago);
        verify(facturaDAO, never()).update(any());
    }

    @Test
    void noEliminaSiElDaoLoRechaza() {
        Pago pago = pago(PagoDAO.PENDIENTE, factura());
        when(pagoDAO.validarEliminar(pago, PagoDAO.PENDIENTE)).thenReturn("El pago tiene montos registrados.");
        model.seleccionarRegistro(pago);

        model.btnEliminarHandler();

        verify(pagoDAO, never()).delete(any());
        assertEquals("No se puede eliminar", ultimoMensaje().getSummary());
    }

    @Test
    void alIniciar_lasFacturasPendientesLasTraeElDao() {
        Factura pendiente = factura();
        when(facturaDAO.buscarPendientes()).thenReturn(List.of(pendiente));

        model.init();

        assertEquals(List.of(pendiente), model.getListaFacturaPendiente());
    }

    private Factura factura() {
        Factura factura = new Factura(UUID.randomUUID());
        factura.setEstado(FacturaDAO.PENDIENTE);
        return factura;
    }

    private Pago pago(String estado, Factura factura) {
        Pago pago = new Pago(UUID.randomUUID());
        pago.setEstado(estado);
        pago.setIdFactura(factura);
        return pago;
    }
}