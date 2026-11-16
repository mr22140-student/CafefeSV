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
public class PagoResourceTest extends AbstractResourceTestBase<Pago, PagoResource, PagoDAO> {

    private FacturaDAO facturaDAO;

    protected PagoDAO crearDao() { return mock(PagoDAO.class); }

    protected PagoResource crearRecurso(PagoDAO dao) {
        facturaDAO = mock(FacturaDAO.class);
        PagoResource r = new PagoResource();
        r.pagoDAO = dao;
        r.facturaDAO = facturaDAO;
        return r;
    }

    protected Pago entidad(UUID id) { return new Pago(id); }

    @Test
    void crear_siempreEmpiezaPendiente() {
        assertEquals(PagoDAO.PENDIENTE, ((Pago) crear("{\"estado\":\"COMPLETADO\"}").getEntity()).getEstado());
    }

    @Test
    void crear_conFacturaQueNoExiste_devuelve422() {
        assertEquals("idFactura", crear("{" + relacion("idFactura") + "}").getHeaderString("Wrong-Parameter"));
    }

    @Test
    void crear_conPagoInvalido_devuelve422() {
        when(dao.validarPago(any())).thenReturn("La factura ya tiene un pago.");
        assertError422(crear("{}"), "La factura ya tiene un pago.");
    }

    @Test
    void actualizar_conCambioDeEstadoNoPermitido_devuelve422() {
        Pago guardado = existente();
        guardado.setEstado("ANULADO");
        when(dao.validarCambioDeEstado(eq("ANULADO"), any())).thenReturn("Un pago anulado no cambia de estado.");
        assertError422(actualizar(guardado, "{\"estado\":\"COMPLETADO\"}"), "Un pago anulado no cambia de estado.");
    }

    @Test
    void actualizar_completarSinCubrirElTotal_devuelve422() {
        when(dao.validarCompletar(any(), any())).thenReturn("Los pagos no cubren el total.");
        assertError422(actualizar(existente(), "{\"estado\":\"COMPLETADO\"}"), "Los pagos no cubren el total.");
        verify(dao, never()).update(any());
    }

    @Test
    void actualizar_alCompletar_cambiaElEstadoDeLaFactura() {
        Factura factura = existeEnLaBase(Factura.class, new Factura(UUID.randomUUID()));
        Factura facturaGuardada = new Factura(factura.getIdFactura());
        when(facturaDAO.findById(factura.getIdFactura())).thenReturn(facturaGuardada);
        when(dao.calcularEstadoFactura(any(), eq("COMPLETADO"))).thenReturn("PAGADA");

        assertEquals(200, actualizar(existente(), "{\"estado\":\"COMPLETADO\"," + relacion("idFactura") + "}").getStatus());

        assertEquals("PAGADA", facturaGuardada.getEstado());
        verify(facturaDAO).update(facturaGuardada);
    }

    @Test
    void actualizar_sinCambioParaLaFactura_noLaToca() {
        actualizar(existente(), "{}");
        verify(facturaDAO, never()).update(any());
    }

    @Test
    void eliminar_unPagoCompletado_devuelve422() {
        Pago guardado = existente();
        guardado.setEstado("COMPLETADO");
        when(dao.validarEliminar(guardado, "COMPLETADO")).thenReturn("Un pago completado no se puede eliminar.");
        assertError422(eliminar(guardado), "Un pago completado no se puede eliminar.");
    }
}
