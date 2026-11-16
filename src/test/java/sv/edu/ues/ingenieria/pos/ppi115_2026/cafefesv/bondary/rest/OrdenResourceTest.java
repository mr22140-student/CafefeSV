package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.OrdenDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Orden;

/**
 * Orden y su estado de preparacion. El DAO es real (spy) para usar sus reglas
 * del estado; lo que toca la base se simula.
 *
 * @author valen
 */
public class OrdenResourceTest extends AbstractResourceTestBase<Orden, OrdenResource, OrdenDAO> {

    protected OrdenDAO crearDao() {
        OrdenDAO dao = spy(new OrdenDAO());
        doReturn(null).when(dao).findById(any());
        doReturn(null).when(dao).buscarReferencia(any(), any());
        doReturn(null).when(dao).validarOrden(any());
        doReturn(null).when(dao).validarEliminar(any());
        return dao;
    }

    protected OrdenResource crearRecurso(OrdenDAO dao) {
        OrdenResource r = new OrdenResource();
        r.ordenDAO = dao;
        return r;
    }

    protected Orden entidad(UUID id) { return new Orden(id); }

    // ===================== Orden =====================
    @Test
    void crear_sinFecha_tomaLaFechaActual() {
        assertNotNull(((Orden) crear("{}").getEntity()).getFechaCreacion());
    }

    @Test
    void crear_sinCamarero_devuelve422() {
        doCallRealMethod().when(dao).validarOrden(any());
        assertError422(crear("{}"), "Solo un empleado con rol camarero puede tomar la orden.");
        verify(dao, never()).save(any());
    }

    @Test
    void crear_conAsignacionQueNoExiste_devuelve422() {
        assertEquals("idEmpleadoRol", crear("{" + relacion("idEmpleadoRol") + "}").getHeaderString("Wrong-Parameter"));
    }

    @Test
    void eliminar_conProductos_devuelve422() {
        doReturn("La orden tiene productos.").when(dao).validarEliminar(any());
        assertError422(eliminar(existente()), "La orden tiene productos.");
    }

    @Test
    void pendientes_devuelveLasOrdenesSinPagar() {
        List<Orden> lista = List.of(new Orden(UUID.randomUUID()));
        doReturn(lista).when(dao).findPendientes();
        assertSame(lista, resource.pendientes().getEntity());
    }

    @Test
    void pendientes_siLaBaseFalla_devuelve500() {
        doThrow(new IllegalStateException("sin base")).when(dao).findPendientes();
        assertEquals(500, resource.pendientes().getStatus());
    }

    // ===================== GET estado =====================
    @Test
    void estado_deUnaOrdenNueva_devuelveRecibida() {
        Orden orden = existente();
        Response respuesta = resource.estado(id(orden));
        assertEquals(200, respuesta.getStatus());
        assertEquals(Map.of("idOrden", id(orden), "estado", "RECIBIDA"), respuesta.getEntity());
    }

    @Test
    void estado_deUnaOrdenQueNoExiste_devuelve404() {
        assertEquals(404, resource.estado(UUID.randomUUID().toString()).getStatus());
    }

    @Test
    void estado_conIdInvalido_devuelve422() {
        assertEquals(422, resource.estado("orden-1").getStatus());
    }

    // ===================== PUT estado =====================
    @Test
    void cambiarEstado_aUnoSiguiente_loGuarda() {
        Orden orden = existente();
        Response respuesta = resource.cambiarEstado(id(orden), Map.of("estado", "PREPARANDO"));
        assertEquals(200, respuesta.getStatus());
        assertEquals("PREPARANDO", dao.buscarEstado(orden.getIdOrden()));
    }

    @Test
    void cambiarEstado_aUnoQueNoExiste_devuelve422SinCambiarlo() {
        Orden orden = existente();
        Response respuesta = resource.cambiarEstado(id(orden), Map.of("estado", "COCINANDO"));
        assertEquals("estado", respuesta.getHeaderString("Wrong-Parameter"));
        assertEquals("RECIBIDA", dao.buscarEstado(orden.getIdOrden()));
    }

    @Test
    void cambiarEstado_sinCuerpo_devuelve422() {
        assertEquals(422, resource.cambiarEstado(id(existente()), null).getStatus());
    }

    @Test
    void cambiarEstado_aUnoAnterior_devuelve422() {
        Orden orden = existente();
        resource.cambiarEstado(id(orden), Map.of("estado", "ENTREGADA"));
        assertEquals(422, resource.cambiarEstado(id(orden), Map.of("estado", "PREPARANDO")).getStatus());
        assertEquals("ENTREGADA", dao.buscarEstado(orden.getIdOrden()));
    }

    @Test
    void cambiarEstado_deUnaOrdenQueNoExiste_devuelve404() {
        assertEquals(404, resource.cambiarEstado(UUID.randomUUID().toString(), Map.of("estado", "PREPARANDO")).getStatus());
    }
}
