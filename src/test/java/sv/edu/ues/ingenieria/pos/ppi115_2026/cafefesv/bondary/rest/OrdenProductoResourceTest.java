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
public class OrdenProductoResourceTest extends AbstractResourceTestBase<OrdenProducto, OrdenProductoResource, OrdenProductoDAO> {

    protected OrdenProductoDAO crearDao() { return mock(OrdenProductoDAO.class); }

    protected OrdenProductoResource crearRecurso(OrdenProductoDAO dao) {
        OrdenProductoResource r = new OrdenProductoResource();
        r.ordenProductoDAO = dao;
        return r;
    }

    protected OrdenProducto entidad(UUID id) { return new OrdenProducto(id); }

    private Producto latte() {
        Producto latte = new Producto(UUID.randomUUID());
        latte.setPrecioSugerido(new BigDecimal("5.00"));
        return existeEnLaBase(Producto.class, latte);
    }

    @Test
    void crear_sinPrecio_tomaElPrecioSugeridoDelProducto() {
        latte();
        OrdenProducto creado = (OrdenProducto) crear("{" + relacion("idProducto") + "}").getEntity();
        assertEquals(new BigDecimal("5.00"), creado.getPrecio());
    }

    @Test
    void crear_conPrecio_respetaElQueVino() {
        latte();
        OrdenProducto creado = (OrdenProducto) crear("{\"precio\":4.25," + relacion("idProducto") + "}").getEntity();
        assertEquals(new BigDecimal("4.25"), creado.getPrecio());
    }

    @Test
    void crear_conOrdenQueNoExiste_devuelve422() {
        assertEquals("idOrden", crear("{" + relacion("idOrden") + "}").getHeaderString("Wrong-Parameter"));
    }

    @Test
    void crear_conProductoInactivo_devuelve422() {
        when(dao.validarProductoElegido(any())).thenReturn("El producto no está activo.");
        assertError422(crear("{}"), "El producto no está activo.");
        verify(dao, never()).save(any());
    }

    @Test
    void actualizar_siYaEstaFacturado_devuelve422() {
        when(dao.validarNoFacturado(any())).thenReturn("El producto ya está facturado.");
        assertError422(actualizar(existente(), "{}"), "El producto ya está facturado.");
    }

    @Test
    void eliminar_siYaEstaFacturado_devuelve422() {
        when(dao.validarNoFacturado(any())).thenReturn("El producto ya está facturado.");
        assertError422(eliminar(existente()), "El producto ya está facturado.");
        verify(dao, never()).delete(any());
    }

    @Test
    void porOrden_siExiste_devuelveSusProductos() {
        Orden orden = existeEnLaBase(Orden.class, new Orden(UUID.randomUUID()));
        List<OrdenProducto> lista = List.of(new OrdenProducto(UUID.randomUUID()));
        when(dao.findByOrden(orden)).thenReturn(lista);
        assertSame(lista, resource.porOrden(orden.getIdOrden().toString()).getEntity());
    }

    @Test
    void porOrden_siNoExiste_devuelve404() {
        assertEquals(404, resource.porOrden(UUID.randomUUID().toString()).getStatus());
    }
}
