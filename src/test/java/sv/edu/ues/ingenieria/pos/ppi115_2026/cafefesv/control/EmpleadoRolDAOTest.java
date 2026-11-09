package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.util.List;

import jakarta.persistence.TypedQuery;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Rol;

/**
 *
 * @author valen
 */
class EmpleadoRolDAOTest extends AbstractDAOTestBase<EmpleadoRol, EmpleadoRolDAO> {

    @Override
    protected EmpleadoRolDAO crearDAO() {
        return new EmpleadoRolDAO();
    }

    @Override
    protected EmpleadoRol crearEntidad() {
        return new EmpleadoRol();
    }

    @Test
    void existeAsignacion_sinCoincidencias_devuelveFalse() {
        @SuppressWarnings("unchecked")
        TypedQuery<Long> consulta = mock(TypedQuery.class, RETURNS_SELF);
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(consulta);
        when(consulta.getSingleResult()).thenReturn(0L);

        assertFalse(dao.existeAsignacion(UUID.randomUUID(), new Rol(), null));
    }
    // ===================== validarRolElegido =====================

    @Test
    void validarRolElegido_sinRol_devuelveError() {
        assertNotNull(dao.validarRolElegido(null));
    }

    @Test
    void validarRolElegido_inactivo_devuelveError() {
        Rol barista = rol("Barista", false);
        when(em.find(Rol.class, barista.getIdRol())).thenReturn(barista);

        assertNotNull(dao.validarRolElegido(barista));
    }

    @Test
    void validarRolElegido_activo_devuelveNull() {
        Rol cajero = rol("Cajero", true);
        when(em.find(Rol.class, cajero.getIdRol())).thenReturn(cajero);

        assertNull(dao.validarRolElegido(cajero));
    }

    // ===================== validarSinRolAsignado =====================
    @Test
    void validarSinRolAsignado_empleadoConRol_devuelveError() {
        UUID idEmpleado = UUID.randomUUID();
        simularConsulta(EmpleadoRol.class, List.of(asignacion(idEmpleado, rol("Cajero", true))));

        assertNotNull(dao.validarSinRolAsignado(idEmpleado));
    }

    @Test
    void validarSinRolAsignado_empleadoSinRol_devuelveNull() {
        simularConsulta(EmpleadoRol.class, List.of());

        assertNull(dao.validarSinRolAsignado(UUID.randomUUID()));
    }

    // ===================== validarCambioDeRol =====================
    @Test
    void validarCambioDeRol_sinAsignacionActual_devuelveError() {
        assertNotNull(dao.validarCambioDeRol(null, rol("Cajero", true)));
    }

    @Test
    void validarCambioDeRol_alMismoRol_devuelveError() {
        Rol cajero = rol("Cajero", true);
        assertNotNull(dao.validarCambioDeRol(asignacion(UUID.randomUUID(), cajero), cajero));
    }

    @Test
    void validarCambioDeRol_aOtroRol_devuelveNull() {
        assertNull(dao.validarCambioDeRol(asignacion(UUID.randomUUID(), rol("Cajero", true)), rol("Camarero", true)));
    }

    // ===================== validarAsignacion =====================
    @Test
    void validarAsignacion_sinEmpleado_devuelveError() {
        assertNotNull(dao.validarAsignacion(asignacion(null, rol("Cajero", true))));
    }

    @Test
    void validarAsignacion_sinRol_devuelveError() {
        assertNotNull(dao.validarAsignacion(asignacion(UUID.randomUUID(), null)));
    }

    @Test
    void validarAsignacion_repetida_devuelveError() {
        simularConteo(1);
        assertNotNull(dao.validarAsignacion(asignacion(UUID.randomUUID(), rol("Cajero", true))));
    }

    @Test
    void validarAsignacion_nueva_devuelveNull() {
        simularConteo(0);
        assertNull(dao.validarAsignacion(asignacion(UUID.randomUUID(), rol("Cajero", true))));
    }

    // ===================== validarEliminar =====================
    @Test
    void validarEliminar_conOrdenesOFacturas_devuelveError() {
        simularConteo(1);
        assertNotNull(dao.validarEliminar(asignacion(UUID.randomUUID(), rol("Camarero", true))));
    }

    @Test
    void validarEliminar_sinHistorial_devuelveNull() {
        simularConteo(0);
        assertNull(dao.validarEliminar(asignacion(UUID.randomUUID(), rol("Camarero", true))));
    }

    // ===================== ayudantes =====================
    private Rol rol(String nombre, boolean activo) {
        Rol rol = new Rol(UUID.randomUUID());
        rol.setNombre(nombre);
        rol.setActivo(activo);
        return rol;
    }

    private EmpleadoRol asignacion(UUID idEmpleado, Rol rol) {
        EmpleadoRol asignacion = new EmpleadoRol(UUID.randomUUID());
        asignacion.setIdEmpleado(idEmpleado);
        asignacion.setIdRol(rol);
        asignacion.setActivo(true);
        return asignacion;
    }
}
