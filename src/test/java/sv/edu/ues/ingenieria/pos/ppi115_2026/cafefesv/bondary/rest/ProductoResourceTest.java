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
public class ProductoResourceTest extends AbstractResourceTestBase<Producto, ProductoResource, ProductoDAO> {

    protected ProductoDAO crearDao() { return mock(ProductoDAO.class); }

    protected ProductoResource crearRecurso(ProductoDAO dao) {
        ProductoResource r = new ProductoResource();
        r.productoDAO = dao;
        return r;
    }

    protected Producto entidad(UUID id) { return new Producto(id); }

    @Test
    void crear_sinActivo_quedaActivo() {
        assertEquals(Boolean.TRUE, ((Producto) crear("{\"nombre\":\"Latte\"}").getEntity()).getActivo());
    }

    @Test
    void crear_conPrecioInvalido_devuelve422SinGuardar() {
        when(dao.validarPrecio(any())).thenReturn("El precio debe ser mayor que 0.");
        assertError422(crear("{\"precioSugerido\":-1}"), "El precio debe ser mayor que 0.");
        verify(dao, never()).save(any());
    }

    @Test
    void actualizar_conPrecioInvalido_devuelve422SinModificar() {
        when(dao.validarPrecio(any())).thenReturn("El precio debe ser mayor que 0.");
        assertError422(actualizar(existente(), "{\"precioSugerido\":0}"), "El precio debe ser mayor que 0.");
        verify(dao, never()).update(any());
    }

    @Test
    void eliminar_siEstaEnOrdenes_devuelve422() {
        when(dao.validarEliminar(any())).thenReturn("El producto está en órdenes.");
        assertError422(eliminar(existente()), "El producto está en órdenes.");
    }
}
