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
public class PagoDetalleResourceTest extends AbstractResourceTestBase<PagoDetalle, PagoDetalleResource, PagoDetalleDAO> {

    private PagoDAO pagoDAO;

    protected PagoDetalleDAO crearDao() { return mock(PagoDetalleDAO.class); }

    protected PagoDetalleResource crearRecurso(PagoDetalleDAO dao) {
        pagoDAO = mock(PagoDAO.class);
        PagoDetalleResource r = new PagoDetalleResource();
        r.pagoDetalleDAO = dao;
        r.pagoDAO = pagoDAO;
        return r;
    }

    protected PagoDetalle entidad(UUID id) { return new PagoDetalle(id); }

    @Test
    void crear_sinTipo_esEfectivo() {
        assertEquals(PagoDetalleDAO.EFECTIVO, ((PagoDetalle) crear("{}").getEntity()).getTipoPago());
    }

    @Test
    void crear_conReferenciaVacia_laDejaNull() {
        assertEquals(null, ((PagoDetalle) crear("{\"referenciaExterna\":\"   \"}").getEntity()).getReferenciaExterna());
    }

    @Test
    void crear_conReferencia_leQuitaLosEspacios() {
        assertEquals("ABC123", ((PagoDetalle) crear("{\"referenciaExterna\":\" ABC123 \"}").getEntity()).getReferenciaExterna());
    }

    @Test
    void crear_validaElMontoContraElSaldoDelPago() {
        Pago pago = existeEnLaBase(Pago.class, new Pago(UUID.randomUUID()));
        when(pagoDAO.calcularSaldo(pago, BigDecimal.ZERO)).thenReturn(new BigDecimal("2.00"));
        when(dao.validarDetalle(any(), eq(new BigDecimal("2.00")))).thenReturn("El monto supera el saldo.");

        assertError422(crear("{\"monto\":5," + relacion("idPago") + "}"), "El monto supera el saldo.");
    }

    @Test
    void actualizar_deUnPagoCompletado_devuelve422() {
        when(dao.validarPagoEditable(any())).thenReturn("El pago ya está completado.");
        assertError422(actualizar(existente(), "{}"), "El pago ya está completado.");
    }

    @Test
    void eliminar_deUnPagoCompletado_devuelve422() {
        when(dao.validarPagoEditable(any())).thenReturn("El pago ya está completado.");
        assertError422(eliminar(existente()), "El pago ya está completado.");
    }

    @Test
    void porPago_siExiste_devuelveSusDetalles() {
        Pago pago = existeEnLaBase(Pago.class, new Pago(UUID.randomUUID()));
        List<PagoDetalle> lista = List.of(new PagoDetalle(UUID.randomUUID()));
        when(dao.findByPago(pago)).thenReturn(lista);
        assertSame(lista, resource.porPago(pago.getIdPago().toString()).getEntity());
    }

    @Test
    void porPago_siNoExiste_devuelve404() {
        assertEquals(404, resource.porPago(UUID.randomUUID().toString()).getStatus());
    }
}
