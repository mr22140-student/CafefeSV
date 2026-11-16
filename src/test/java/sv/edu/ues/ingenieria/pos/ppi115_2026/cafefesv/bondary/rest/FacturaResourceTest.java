package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.*;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.*;

/** @author valen */
public class FacturaResourceTest extends AbstractResourceTestBase<Factura, FacturaResource, FacturaDAO> {

    protected FacturaDAO crearDao() { return mock(FacturaDAO.class); }

    protected FacturaResource crearRecurso(FacturaDAO dao) {
        FacturaResource r = new FacturaResource();
        r.facturaDAO = dao;
        return r;
    }

    protected Factura entidad(UUID id) { return new Factura(id); }

    @Test
    void crear_siempreEmpiezaPendienteConFecha() {
        Factura creada = (Factura) crear("{\"estado\":\"PAGADA\"}").getEntity();
        assertEquals(FacturaDAO.PENDIENTE, creada.getEstado());
        assertNotNull(creada.getFechaFacturacion());
    }

    @Test
    void crear_sinCliente_quedaComoConsumidorFinal() {
        assertEquals("Consumidor final", ((Factura) crear("{\"cliente\":\"   \"}").getEntity()).getCliente());
    }

    @Test
    void crear_conCliente_leQuitaLosEspacios() {
        assertEquals("Ana", ((Factura) crear("{\"cliente\":\"  Ana \"}").getEntity()).getCliente());
    }

    @Test
    void crear_conCajaQueNoExiste_devuelve422() {
        assertEquals("idCaja", crear("{" + relacion("idCaja") + "}").getHeaderString("Wrong-Parameter"));
    }

    @Test
    void crear_conDatosInvalidos_devuelve422() {
        when(dao.validarFactura(any())).thenReturn("La caja está inactiva.");
        assertError422(crear("{}"), "La caja está inactiva.");
    }

    @Test
    void actualizar_unaFacturaPagada_devuelve422() {
        Factura guardada = existente();
        guardada.setEstado("PAGADA");
        when(dao.validarModificable("PAGADA")).thenReturn("Una factura pagada no se puede modificar.");
        assertError422(actualizar(guardada, "{}"), "Una factura pagada no se puede modificar.");
        verify(dao, never()).update(any());
    }

    @Test
    void eliminar_siTienePagos_devuelve422() {
        when(dao.validarEliminar(any())).thenReturn("La factura tiene pagos.");
        assertError422(eliminar(existente()), "La factura tiene pagos.");
    }
}
