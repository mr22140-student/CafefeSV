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
public class EmpleadoRolResourceTest extends AbstractResourceTestBase<EmpleadoRol, EmpleadoRolResource, EmpleadoRolDAO> {

    protected EmpleadoRolDAO crearDao() { return mock(EmpleadoRolDAO.class); }

    protected EmpleadoRolResource crearRecurso(EmpleadoRolDAO dao) {
        EmpleadoRolResource r = new EmpleadoRolResource();
        r.empleadoRolDAO = dao;
        return r;
    }

    protected EmpleadoRol entidad(UUID id) { return new EmpleadoRol(id); }

    @Test
    void crear_sinActivo_quedaActivo() {
        assertEquals(Boolean.TRUE, ((EmpleadoRol) crear("{}").getEntity()).getActivo());
    }

    @Test
    void crear_conEmpleadoQueNoExiste_devuelve422() {
        Response respuesta = crear("{\"idEmpleado\":\"" + UUID.randomUUID() + "\"}");
        assertEquals("idEmpleado", respuesta.getHeaderString("Wrong-Parameter"));
        verify(dao, never()).save(any());
    }

    @Test
    void crear_conRolQueNoExiste_devuelve422() {
        assertEquals("idRol", crear("{" + relacion("idRol") + "}").getHeaderString("Wrong-Parameter"));
    }

    @Test
    void crear_conRolInactivo_devuelve422() {
        existeEnLaBase(Rol.class, new Rol(UUID.randomUUID()));
        when(dao.validarRolElegido(any())).thenReturn("El rol está inactivo.");
        assertError422(crear("{" + relacion("idRol") + "}"), "El rol está inactivo.");
    }

    @Test
    void crear_siYaTieneUnRolActivo_devuelve422() {
        when(dao.validarAsignacion(any())).thenReturn("El empleado ya tiene un rol activo.");
        assertError422(crear("{}"), "El empleado ya tiene un rol activo.");
    }

    @Test
    void actualizar_conElMismoRol_noRevisaSiElRolEstaActivo() {
        Rol rol = existeEnLaBase(Rol.class, new Rol(UUID.randomUUID()));
        EmpleadoRol guardado = existente();
        guardado.setIdRol(rol);

        assertEquals(200, actualizar(guardado, "{" + relacion("idRol") + "}").getStatus());
        verify(dao, never()).validarRolElegido(any());
    }

    @Test
    void actualizar_aOtroRolInactivo_devuelve422() {
        existeEnLaBase(Rol.class, new Rol(UUID.randomUUID()));
        EmpleadoRol guardado = existente();
        guardado.setIdRol(new Rol(UUID.randomUUID()));
        when(dao.validarRolElegido(any())).thenReturn("El rol está inactivo.");

        assertError422(actualizar(guardado, "{" + relacion("idRol") + "}"), "El rol está inactivo.");
    }

    @Test
    void eliminar_siTieneOrdenes_devuelve422() {
        when(dao.validarEliminar(any())).thenReturn("La asignación tiene órdenes.");
        assertError422(eliminar(existente()), "La asignación tiene órdenes.");
    }

    @Test
    void porEmpleado_siExiste_buscaSusRolesPorSuId() {
        Empleado empleado = existeEnLaBase(Empleado.class, new Empleado(UUID.randomUUID()));
        List<EmpleadoRol> lista = List.of(new EmpleadoRol(UUID.randomUUID()));
        when(dao.findByEmpleado(empleado.getIdEmpleado())).thenReturn(lista);
        assertSame(lista, resource.porEmpleado(empleado.getIdEmpleado().toString()).getEntity());
    }

    @Test
    void porEmpleado_siNoExiste_devuelve404() {
        assertEquals(404, resource.porEmpleado(UUID.randomUUID().toString()).getStatus());
    }
}
