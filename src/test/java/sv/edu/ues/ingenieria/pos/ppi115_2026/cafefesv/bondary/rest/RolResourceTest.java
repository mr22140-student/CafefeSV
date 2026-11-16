package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.RolDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Rol;

/**
 * Rol, y los casos de AbstractResource que no estan en la base comun.
 *
 * @author valen
 */
public class RolResourceTest extends AbstractResourceTestBase<Rol, RolResource, RolDAO> {

    protected RolDAO crearDao() { return mock(RolDAO.class); }

    protected RolResource crearRecurso(RolDAO dao) {
        RolResource r = new RolResource();
        r.rolDAO = dao;
        return r;
    }

    protected Rol entidad(UUID id) { return new Rol(id); }

    // ===================== AbstractResource =====================
    @Test
    void findRange_pideDelInicioAlFinNoLaCantidad() {
        resource.findRange(20, 10);
        verify(dao).findRange(20, 30);
    }

    @Test
    void findRange_conPaginaInvalida_devuelve422SinConsultar() {
        assertEquals(422, resource.findRange(-1, 50).getStatus());
        assertEquals(422, resource.findRange(0, 0).getStatus());
        assertEquals(422, resource.findRange(0, 101).getStatus());
        assertEquals("first,max", resource.findRange(0, 0).getHeaderString("Wrong-Parameter"));
        verify(dao, never()).findRange(anyInt(), anyInt());
    }

    @Test
    void findRange_siLaBaseFalla_devuelve500ConAppError() {
        when(dao.findRange(0, 50)).thenThrow(new IllegalStateException("sin base"));
        Response respuesta = resource.findRange(0, 50);
        assertEquals(500, respuesta.getStatus());
        assertNotNull(respuesta.getHeaderString("App-Error"));
    }

    @Test
    void crear_devuelveLaUbicacionDelNuevoRegistro() {
        Response respuesta = crear("{\"nombre\":\"Cajero\"}");
        Rol creado = (Rol) respuesta.getEntity();
        assertEquals("http://localhost/recurso/" + creado.getIdRol(), respuesta.getLocation().toString());
    }

    @Test
    void crear_conIdQueYaExiste_devuelve422SinGuardar() {
        Rol guardado = existente();
        assertEquals(422, crear("{\"idRol\":\"" + guardado.getIdRol() + "\"}").getStatus());
        verify(dao, never()).save(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void crear_queNoCumpleLasAnotaciones_devuelve422ConElCampo() {
        ConstraintViolation<Object> violacion = mock(ConstraintViolation.class);
        Path campo = mock(Path.class);
        when(campo.toString()).thenReturn("nombre");
        when(violacion.getPropertyPath()).thenReturn(campo);
        when(violacion.getMessage()).thenReturn("el tamaño debe estar entre 0 y 155");
        Validator validator = mock(Validator.class);
        when(validator.validate(any())).thenReturn(Set.of(violacion));
        resource.validator = validator;

        Response respuesta = crear("{\"nombre\":\"x\"}");

        assertEquals(422, respuesta.getStatus());
        assertEquals("nombre", respuesta.getHeaderString("Wrong-Parameter"));
        verify(dao, never()).save(any());
    }

    @Test
    void crear_siLaBaseRechazaPorIntegridad_devuelve422() {
        doThrow(new RuntimeException(new SQLException("duplicado", "23505"))).when(dao).save(any());
        Response respuesta = crear("{\"nombre\":\"Cajero\"}");
        assertEquals(422, respuesta.getStatus());
        assertTrue(respuesta.getEntity().toString().contains("ya existe"));
    }

    @Test
    void actualizar_usaElIdDeLaUrlNoElDelCuerpo() {
        Rol guardado = existente();
        Response respuesta = actualizar(guardado, "{\"idRol\":\"" + UUID.randomUUID() + "\",\"nombre\":\"Barista Senior\"}");
        assertEquals(guardado.getIdRol(), ((Rol) respuesta.getEntity()).getIdRol());
    }

    // ===================== Rol =====================
    @Test
    void crear_sinActivo_quedaActivo() {
        assertEquals(Boolean.TRUE, ((Rol) crear("{\"nombre\":\"Cajero\"}").getEntity()).getActivo());
    }

    @Test
    void actualizar_siTieneEmpleados_noSePuedeInactivar() {
        when(dao.validarInactivar(any())).thenReturn("El rol tiene empleados asignados.");
        assertError422(actualizar(existente(), "{\"activo\":false}"), "El rol tiene empleados asignados.");
        verify(dao, never()).update(any());
    }

    @Test
    void eliminar_siTieneEmpleados_devuelve422() {
        when(dao.validarEliminar(any())).thenReturn("El rol tiene empleados asignados.");
        assertError422(eliminar(existente()), "El rol tiene empleados asignados.");
        verify(dao, never()).delete(any());
    }
}
