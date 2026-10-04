package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        Factura f = model.getRegistro();

        assertEquals(FacturaModel.PENDIENTE, f.getEstado());
        assertEquals("Consumidor final", f.getCliente());
        assertNotNull(f.getFechaFacturacion());
    }

    @Test
    void noGuardaSinCaja() {
        model.btnNuevoHandler();
        model.getRegistro().setIdEmpleadoRol(new EmpleadoRol(UUID.randomUUID()));

        model.btnCrearHandler();

        verify(facturaDAO, never()).save(any());
        assertEquals("Debe seleccionar la caja.", ultimoMensaje().getSummary());
    }

    @Test
    void noGuardaSinCajero() {
        model.btnNuevoHandler();
        model.getRegistro().setIdCaja(new Caja(UUID.randomUUID()));

        model.btnCrearHandler();

        verify(facturaDAO, never()).save(any());
        assertEquals("Debe seleccionar el cajero.", ultimoMensaje().getSummary());
    }

    @Test
    void conClienteVacio_guardaComoConsumidorFinal() {
        model.btnNuevoHandler();
        Factura f = model.getRegistro();
        f.setIdCaja(new Caja(UUID.randomUUID()));
        f.setIdEmpleadoRol(new EmpleadoRol(UUID.randomUUID()));
        f.setCliente("   ");

        model.btnCrearHandler();

        verify(facturaDAO).save(f);
        assertEquals("Consumidor final", f.getCliente());
    }

    @Test
    void noModificaUnaFacturaAnulada() {
        Factura f = new Factura(UUID.randomUUID());
        f.setEstado(FacturaModel.ANULADA);
        model.seleccionarRegistro(f);

        model.btnModificarHandler();

        verify(facturaDAO, never()).update(any());
        assertEquals("No se puede modificar una factura ANULADA.", ultimoMensaje().getSummary());
    }

    @Test
    void noEliminaUnaFacturaConProductos() {
        Factura f = new Factura(UUID.randomUUID());
        when(facturaDAO.countProductos(f)).thenReturn(1L);
        model.seleccionarRegistro(f);

        model.btnEliminarHandler();

        verify(facturaDAO, never()).delete(any());
    }

    @Test
    void colorSegunElEstado() {
        assertEquals("success", model.severidadEstado(FacturaModel.PAGADA));
        assertEquals("danger", model.severidadEstado(FacturaModel.ANULADA));
        assertEquals("warning", model.severidadEstado(FacturaModel.PENDIENTE));
    }

}
