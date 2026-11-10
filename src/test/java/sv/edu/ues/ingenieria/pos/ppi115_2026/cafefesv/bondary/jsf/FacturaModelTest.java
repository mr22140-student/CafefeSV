package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.CajaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoRolDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.FacturaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caja;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;

/**
 *
 * @author valen
 */
public class FacturaModelTest extends ModelTestBase {

    @InjectMocks
    private FacturaModel model;

    @Mock
    private FacturaDAO facturaDAO;
    @Mock
    private CajaDAO cajaDAO;
    @Mock
    private EmpleadoRolDAO empleadoRolDAO;
    @Mock
    private EmpleadoDAO empleadoDAO;

    @Test
    void nuevaFactura_traeValoresPorDefecto() {
        model.btnNuevoHandler();
        Factura factura = model.getRegistro();

        assertEquals(FacturaDAO.PENDIENTE, factura.getEstado());
        assertEquals("Consumidor final", factura.getCliente());
        assertNotNull(factura.getFechaFacturacion());
    }

    @Test
    void noGuardaSiElDaoRechazaLaFactura() {
        model.btnNuevoHandler();
        Factura factura = model.getRegistro();
        when(facturaDAO.validarFactura(factura)).thenReturn("Debe seleccionar la caja.");

        model.btnCrearHandler();

        verify(facturaDAO, never()).save(any());
        assertEquals("Factura no válida", ultimoMensaje().getSummary());
    }

    @Test
    void conClienteVacio_guardaComoConsumidorFinal() {
        model.btnNuevoHandler();
        Factura factura = model.getRegistro();
        factura.setIdCaja(new Caja(UUID.randomUUID()));
        factura.setIdEmpleadoRol(new EmpleadoRol(UUID.randomUUID()));
        factura.setCliente("   ");

        model.btnCrearHandler();

        verify(facturaDAO).save(factura);
        assertEquals("Consumidor final", factura.getCliente());
    }

    @Test
    void noModificaSiElDaoDiceQueEstaAnulada() {
        Factura factura = new Factura(UUID.randomUUID());
        factura.setEstado(FacturaDAO.ANULADA);
        when(facturaDAO.validarModificable(FacturaDAO.ANULADA)).thenReturn("No se puede modificar una factura ANULADA.");
        model.seleccionarRegistro(factura);

        model.btnModificarHandler();

        verify(facturaDAO, never()).update(any());
        assertEquals("No se puede modificar", ultimoMensaje().getSummary());
    }

    @Test
    void noEliminaSiElDaoLaRechaza() {
        Factura factura = new Factura(UUID.randomUUID());
        when(facturaDAO.validarEliminar(factura)).thenReturn("La factura tiene productos o pagos.");
        model.seleccionarRegistro(factura);

        model.btnEliminarHandler();

        verify(facturaDAO, never()).delete(any());
        assertEquals("No se puede eliminar", ultimoMensaje().getSummary());
    }

    @Test
    void alIniciar_lasCajasDelComboLasTraeElDao() {
        Caja cajaPrincipal = new Caja(UUID.randomUUID());
        when(cajaDAO.findByActivo(Boolean.TRUE)).thenReturn(List.of(cajaPrincipal));

        model.init();

        assertEquals(List.of(cajaPrincipal), model.getListaCaja());
    }

    @Test
    void colorSegunElEstado() {
        assertEquals("success", model.severidadEstado(FacturaDAO.PAGADA));
        assertEquals("danger", model.severidadEstado(FacturaDAO.ANULADA));
        assertEquals("warning", model.severidadEstado(FacturaDAO.PENDIENTE));
    }
}