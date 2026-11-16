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
public class ProductoCaracteristicaResourceTest extends AbstractResourceTestBase<ProductoCaracteristica, ProductoCaracteristicaResource, ProductoCaracteristicaDAO> {

    protected ProductoCaracteristicaDAO crearDao() { return mock(ProductoCaracteristicaDAO.class); }

    protected ProductoCaracteristicaResource crearRecurso(ProductoCaracteristicaDAO dao) {
        ProductoCaracteristicaResource r = new ProductoCaracteristicaResource();
        r.productoCaracteristicaDAO = dao;
        return r;
    }

    protected ProductoCaracteristica entidad(UUID id) { return new ProductoCaracteristica(id); }

    @Test
    void crear_conProductoQueNoExiste_devuelve422() {
        Response respuesta = crear("{" + relacion("idProducto") + "}");
        assertEquals("idProducto", respuesta.getHeaderString("Wrong-Parameter"));
    }

    @Test
    void crear_conCaracteristicaQueNoExiste_devuelve422() {
        existeEnLaBase(Producto.class, new Producto(UUID.randomUUID()));
        Response respuesta = crear("{" + relacion("idProducto") + "," + relacion("idCaracteristica") + "}");
        assertEquals("idCaracteristica", respuesta.getHeaderString("Wrong-Parameter"));
    }

    @Test
    void crear_conValorInvalido_devuelveEseError() {
        when(dao.validarValor(any())).thenReturn("El valor no cumple el formato.");
        assertError422(crear("{\"valor\":\"x\"}"), "El valor no cumple el formato.");
        verify(dao, never()).save(any());
    }

    @Test
    void actualizar_duplicado_devuelve422() {
        when(dao.validarDuplicado(any())).thenReturn("El producto ya tiene esa característica.");
        assertError422(actualizar(existente(), "{}"), "El producto ya tiene esa característica.");
    }

    @Test
    void porProducto_siExiste_devuelveSusCaracteristicas() {
        Producto producto = existeEnLaBase(Producto.class, new Producto(UUID.randomUUID()));
        List<ProductoCaracteristica> lista = List.of(new ProductoCaracteristica(UUID.randomUUID()));
        when(dao.findByProducto(producto)).thenReturn(lista);
        assertSame(lista, resource.porProducto(producto.getIdProducto().toString()).getEntity());
    }

    @Test
    void porProducto_siNoExiste_devuelve404() {
        assertEquals(404, resource.porProducto(UUID.randomUUID().toString()).getStatus());
    }
}
