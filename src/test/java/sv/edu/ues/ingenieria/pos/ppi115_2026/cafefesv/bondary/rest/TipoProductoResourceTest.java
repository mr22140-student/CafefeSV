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
public class TipoProductoResourceTest extends AbstractResourceTestBase<TipoProducto, TipoProductoResource, TipoProductoDAO> {

    protected TipoProductoDAO crearDao() { return mock(TipoProductoDAO.class); }

    protected TipoProductoResource crearRecurso(TipoProductoDAO dao) {
        TipoProductoResource r = new TipoProductoResource();
        r.tipoProductoDAO = dao;
        return r;
    }

    protected TipoProducto entidad(UUID id) { return new TipoProducto(id); }

    @Test
    void crear_sinActivo_quedaActivo() {
        assertEquals(Boolean.TRUE, ((TipoProducto) crear("{\"nombre\":\"Postres\"}").getEntity()).getActivo());
    }

    @Test
    void actualizar_siNoSePuedeInactivar_devuelve422() {
        when(dao.validarInactivar(any())).thenReturn("Tiene productos activos.");
        assertError422(actualizar(existente(), "{\"activo\":false}"), "Tiene productos activos.");
        verify(dao, never()).update(any());
    }

    @Test
    void eliminar_siTieneProductos_devuelve422() {
        when(dao.validarEliminar(any())).thenReturn("Tiene productos.");
        assertError422(eliminar(existente()), "Tiene productos.");
        verify(dao, never()).delete(any());
    }
}
