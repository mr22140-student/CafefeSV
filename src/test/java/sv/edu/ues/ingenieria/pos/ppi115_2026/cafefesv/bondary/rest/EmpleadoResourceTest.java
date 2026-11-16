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
public class EmpleadoResourceTest extends AbstractResourceTestBase<Empleado, EmpleadoResource, EmpleadoDAO> {

    protected EmpleadoDAO crearDao() { return mock(EmpleadoDAO.class); }

    protected EmpleadoResource crearRecurso(EmpleadoDAO dao) {
        EmpleadoResource r = new EmpleadoResource();
        r.empleadoDAO = dao;
        return r;
    }

    protected Empleado entidad(UUID id) { return new Empleado(id); }

    @Test
    void crear_sinActivo_quedaActivo() {
        assertEquals(Boolean.TRUE, ((Empleado) crear("{\"nombre\":\"Laura\"}").getEntity()).getActivo());
    }

    @Test
    void eliminar_borraTambienSusRoles() {
        Empleado guardado = existente();
        assertEquals(204, eliminar(guardado).getStatus());
        verify(dao).eliminarConRoles(guardado);
        verify(dao, never()).delete(any());
    }

    @Test
    void eliminar_siTieneFacturas_devuelve422SinBorrar() {
        when(dao.validarEliminar(any())).thenReturn("El empleado tiene facturas.");
        assertError422(eliminar(existente()), "El empleado tiene facturas.");
        verify(dao, never()).eliminarConRoles(any());
    }
}
