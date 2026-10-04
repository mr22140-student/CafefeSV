package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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

    @Mock private PagoDAO pagoDAO;
    @Mock private FacturaDAO facturaDAO;

    @Test
    void nuevoPago_empiezaPendiente() {
        model.btnNuevoHandler();

        assertEquals(PagoModel.PENDIENTE, model.getRegistro().getEstado());
    }

    @Test
    void noGuardaSinFactura() {
        model.btnNuevoHandler();

        model.btnCrearHandler();

        verify(pagoDAO, never()).save(any());
        assertEquals("Debe seleccionar la factura.", ultimoMensaje().getSummary());
    }

    @Test
    void noGuardaSiLaFacturaYaTieneUnPagoActivo() {
        when(pagoDAO.existePagoActivo(any(), any())).thenReturn(true);
        model.btnNuevoHandler();
        model.getRegistro().setIdFactura(factura());

        model.btnCrearHandler();

        verify(pagoDAO, never()).save(any());
        assertEquals("Esa factura ya tiene un pago registrado que no está anulado.", ultimoMensaje().getSummary());
    }

    @Test
    void guardaUnPagoValido() {
        when(pagoDAO.existePagoActivo(any(), any())).thenReturn(false);
        model.btnNuevoHandler();
        Pago pago = model.getRegistro();
        pago.setIdFactura(factura());

        model.btnCrearHandler();

        verify(pagoDAO).save(pago);
    }

    @Test
    void noModificaUnPagoAnulado() {
        model.seleccionarRegistro(pago(PagoModel.ANULADO, factura()));

        model.btnModificarHandler();

        verify(pagoDAO, never()).update(any());
        assertEquals("No se puede modificar un pago ANULADO.", ultimoMensaje().getSummary());
    }

    @Test
    void noCompletaSiNoSeHaPagadoElTotal() {
        Pago p = pago(PagoModel.PENDIENTE, factura());
        model.seleccionarRegistro(p);
        p.setEstado(PagoModel.COMPLETADO);
        when(pagoDAO.totalFactura(p.getIdFactura())).thenReturn(new BigDecimal("10.00"));
        when(pagoDAO.sumaMontos(p)).thenReturn(new BigDecimal("5.00"));

        model.btnModificarHandler();

        verify(pagoDAO, never()).update(any());
        assertTrue(ultimoMensaje().getSummary().startsWith("No se puede completar"));
    }

    @Test
    void alCompletarElPago_laFacturaQuedaPagada() {
        Factura f = factura();
        Pago p = pago(PagoModel.PENDIENTE, f);
        model.seleccionarRegistro(p);
        p.setEstado(PagoModel.COMPLETADO);
        when(pagoDAO.totalFactura(f)).thenReturn(new BigDecimal("10.00"));
        when(pagoDAO.sumaMontos(p)).thenReturn(new BigDecimal("10.00"));

        model.btnModificarHandler();

        verify(pagoDAO).update(p);
        verify(facturaDAO).update(f);
        assertEquals(FacturaModel.PAGADA, f.getEstado());
    }

    @Test
    void noEliminaUnPagoConMontosRegistrados() {
        Pago p = pago(PagoModel.PENDIENTE, factura());
        when(pagoDAO.countDetalles(p)).thenReturn(2L);
        model.seleccionarRegistro(p);

        model.btnEliminarHandler();

        verify(pagoDAO, never()).delete(any());
    }

    private Factura factura() {
        Factura f = new Factura(UUID.randomUUID());
        f.setEstado(FacturaModel.PENDIENTE);
        return f;
    }

    private Pago pago(String estado, Factura f) {
        Pago p = new Pago(UUID.randomUUID());
        p.setEstado(estado);
        p.setIdFactura(f);
        return p;
    }
    
}
