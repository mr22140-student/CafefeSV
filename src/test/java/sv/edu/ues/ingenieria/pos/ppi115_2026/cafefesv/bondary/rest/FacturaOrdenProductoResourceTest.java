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
public class FacturaOrdenProductoResourceTest extends AbstractResourceTestBase<FacturaOrdenProducto, FacturaOrdenProductoResource, FacturaOrdenProductoDAO> {

    protected FacturaOrdenProductoDAO crearDao() { return mock(FacturaOrdenProductoDAO.class); }

    protected FacturaOrdenProductoResource crearRecurso(FacturaOrdenProductoDAO dao) {
        FacturaOrdenProductoResource r = new FacturaOrdenProductoResource();
        r.facturaOrdenProductoDAO = dao;
        return r;
    }

    protected FacturaOrdenProducto entidad(UUID id) { return new FacturaOrdenProducto(id); }

    @Test
    void crear_sinPrecio_tomaElPrecioDelProductoDeLaOrden() {
        OrdenProducto pedido = new OrdenProducto(UUID.randomUUID());
        pedido.setPrecio(new BigDecimal("3.50"));
        existeEnLaBase(OrdenProducto.class, pedido);

        FacturaOrdenProducto creado = (FacturaOrdenProducto) crear("{" + relacion("idOrdenProducto") + "}").getEntity();

        assertEquals(new BigDecimal("3.50"), creado.getPrecio());
    }

    @Test
    void crear_conFacturaQueNoExiste_devuelve422() {
        assertEquals("idFactura", crear("{" + relacion("idFactura") + "}").getHeaderString("Wrong-Parameter"));
    }

    @Test
    void crear_conDetalleInvalido_devuelve422() {
        when(dao.validarDetalle(any())).thenReturn("El producto ya está en otra factura.");
        assertError422(crear("{}"), "El producto ya está en otra factura.");
    }

    @Test
    void actualizar_deUnaFacturaPagada_devuelve422() {
        when(dao.validarFacturaEditable(any())).thenReturn("La factura ya está pagada.");
        assertError422(actualizar(existente(), "{}"), "La factura ya está pagada.");
    }

    @Test
    void eliminar_deUnaFacturaPagada_devuelve422() {
        when(dao.validarFacturaEditable(any())).thenReturn("La factura ya está pagada.");
        assertError422(eliminar(existente()), "La factura ya está pagada.");
    }

    @Test
    void porFactura_siExiste_devuelveSusProductos() {
        Factura factura = existeEnLaBase(Factura.class, new Factura(UUID.randomUUID()));
        List<FacturaOrdenProducto> lista = List.of(new FacturaOrdenProducto(UUID.randomUUID()));
        when(dao.findByFactura(factura)).thenReturn(lista);
        assertSame(lista, resource.porFactura(factura.getIdFactura().toString()).getEntity());
    }

    @Test
    void porFactura_siNoExiste_devuelve404() {
        assertEquals(404, resource.porFactura(UUID.randomUUID().toString()).getStatus());
    }

    @Test
    void disponibles_devuelveLosProductosSinFacturar() {
        List<OrdenProducto> lista = List.of(new OrdenProducto(UUID.randomUUID()));
        when(dao.findOrdenProductoDisponibles()).thenReturn(lista);
        assertSame(lista, resource.disponibles().getEntity());
    }

    @Test
    void disponibles_siLaBaseFalla_devuelve500() {
        when(dao.findOrdenProductoDisponibles()).thenThrow(new IllegalStateException("sin base"));
        assertEquals(500, resource.disponibles().getStatus());
    }
}
