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
public class DescuentoProductoResourceTest extends AbstractResourceTestBase<DescuentoProducto, DescuentoProductoResource, DescuentoProductoDAO> {

    protected DescuentoProductoDAO crearDao() { return mock(DescuentoProductoDAO.class); }

    protected DescuentoProductoResource crearRecurso(DescuentoProductoDAO dao) {
        DescuentoProductoResource r = new DescuentoProductoResource();
        r.descuentoProductoDAO = dao;
        return r;
    }

    protected DescuentoProducto entidad(UUID id) { return new DescuentoProducto(id); }

    @Test
    void crear_sinFechaDesde_empiezaHoy() {
        assertNotNull(((DescuentoProducto) crear("{}").getEntity()).getFechaDesde());
    }

    @Test
    void crear_conProductoQueNoExiste_devuelve422() {
        existeEnLaBase(Descuento.class, new Descuento(UUID.randomUUID()));
        Response respuesta = crear("{" + relacion("idDescuento") + "," + relacion("idProducto") + "}");
        assertEquals("idProducto", respuesta.getHeaderString("Wrong-Parameter"));
    }

    @Test
    void crear_devuelveElPrimerErrorEnOrden() {
        when(dao.validarValor(any())).thenReturn("El valor supera el máximo.");
        when(dao.validarDuplicado(any())).thenReturn("Ya tiene ese descuento.");
        assertError422(crear("{}"), "El valor supera el máximo.");
    }

    @Test
    void actualizar_duplicado_devuelve422() {
        when(dao.validarDuplicado(any())).thenReturn("Ya tiene ese descuento.");
        assertError422(actualizar(existente(), "{}"), "Ya tiene ese descuento.");
    }

    @Test
    void porDescuento_siExiste_devuelveSusProductos() {
        Descuento descuento = existeEnLaBase(Descuento.class, new Descuento(UUID.randomUUID()));
        List<DescuentoProducto> lista = List.of(new DescuentoProducto(UUID.randomUUID()));
        when(dao.findByDescuento(descuento)).thenReturn(lista);
        assertSame(lista, resource.porDescuento(descuento.getIdDescuento().toString()).getEntity());
    }

    @Test
    void porProducto_siNoExiste_devuelve404() {
        assertEquals(404, resource.porProducto(UUID.randomUUID().toString()).getStatus());
    }
}
