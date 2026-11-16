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
public class ProductoTipoProductoResourceTest extends AbstractResourceTestBase<ProductoTipoProducto, ProductoTipoProductoResource, ProductoTipoProductoDAO> {

    protected ProductoTipoProductoDAO crearDao() { return mock(ProductoTipoProductoDAO.class); }

    protected ProductoTipoProductoResource crearRecurso(ProductoTipoProductoDAO dao) {
        ProductoTipoProductoResource r = new ProductoTipoProductoResource();
        r.productoTipoProductoDAO = dao;
        return r;
    }

    protected ProductoTipoProducto entidad(UUID id) { return new ProductoTipoProducto(id); }

    @Test
    void crear_sinFecha_tomaLaFechaActual() {
        assertNotNull(((ProductoTipoProducto) crear("{}").getEntity()).getFechaCreacion());
    }

    @Test
    void crear_conTipoQueNoExiste_devuelve422() {
        existeEnLaBase(Producto.class, new Producto(UUID.randomUUID()));
        Response respuesta = crear("{" + relacion("idTipoProducto") + ",\"producto\":{\"idProducto\":\"" + UUID.randomUUID() + "\"}}");
        assertEquals("idTipoProducto", respuesta.getHeaderString("Wrong-Parameter"));
    }

    @Test
    void crear_conTipoInactivo_devuelve422() {
        when(dao.validarTipo(any())).thenReturn("La categoría está inactiva.");
        assertError422(crear("{}"), "La categoría está inactiva.");
    }

    @Test
    void actualizar_duplicado_devuelve422() {
        when(dao.validarDuplicado(any())).thenReturn("El producto ya está en esa categoría.");
        assertError422(actualizar(existente(), "{}"), "El producto ya está en esa categoría.");
    }

    @Test
    void porProducto_siExiste_devuelveSusCategorias() {
        Producto producto = existeEnLaBase(Producto.class, new Producto(UUID.randomUUID()));
        List<ProductoTipoProducto> lista = List.of(new ProductoTipoProducto(UUID.randomUUID()));
        when(dao.findByProducto(producto)).thenReturn(lista);
        assertSame(lista, resource.porProducto(producto.getIdProducto().toString()).getEntity());
    }

    @Test
    void porTipoProducto_siExiste_devuelveSusProductos() {
        TipoProducto tipo = existeEnLaBase(TipoProducto.class, new TipoProducto(UUID.randomUUID()));
        List<ProductoTipoProducto> lista = List.of(new ProductoTipoProducto(UUID.randomUUID()));
        when(dao.findByTipoProducto(tipo)).thenReturn(lista);
        assertSame(lista, resource.porTipoProducto(tipo.getIdTipoProducto().toString()).getEntity());
    }

    @Test
    void porTipoProducto_siNoExiste_devuelve404() {
        assertEquals(404, resource.porTipoProducto(UUID.randomUUID().toString()).getStatus());
    }
}
