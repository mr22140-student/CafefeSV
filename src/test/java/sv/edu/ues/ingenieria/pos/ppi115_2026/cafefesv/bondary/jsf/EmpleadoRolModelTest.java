package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoRolDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.RolDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Empleado;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Rol;

/**
 *
 * @author valen
 */
public class EmpleadoRolModelTest extends ModelTestBase {

    @InjectMocks
    private EmpleadoRolModel model;

    @Mock
    private EmpleadoRolDAO empleadoRolDAO;
    @Mock
    private EmpleadoDAO empleadoDAO;
    @Mock
    private RolDAO rolDAO;

    @Test
    void noGuardaSiElDaoRechazaLaAsignacion() {
        EmpleadoRol asignacion = asignacion(UUID.randomUUID(), rol("Cajero", true));
        when(empleadoRolDAO.validarAsignacion(asignacion)).thenReturn("Ese empleado ya tiene asignado el rol Cajero.");
        model.setRegistro(asignacion);

        model.btnCrearHandler();

        verify(empleadoRolDAO, never()).save(any());
        assertEquals("Asignación no válida", ultimoMensaje().getSummary());
    }

    @Test
    void guardaSiElDaoNoEncuentraErrores() {
        EmpleadoRol asignacion = asignacion(UUID.randomUUID(), rol("Cajero", true));
        model.setRegistro(asignacion);

        model.btnCrearHandler();

        verify(empleadoRolDAO).save(asignacion);
    }

    @Test
    void noEliminaSiElDaoLaRechaza() {
        EmpleadoRol asignacion = asignacion(UUID.randomUUID(), rol("Camarero", true));
        when(empleadoRolDAO.validarEliminar(asignacion)).thenReturn("Esta asignación ya tiene órdenes o facturas.");
        model.seleccionarRegistro(asignacion);

        model.btnEliminarHandler();

        verify(empleadoRolDAO, never()).delete(any());
        assertEquals("No se puede eliminar", ultimoMensaje().getSummary());
    }

    @Test
    void alIniciar_soloListaEmpleadosActivosYLosRolesActivosDelDao() {
        Empleado ana = empleado("Ana", true);
        Empleado beto = empleado("Beto", false);
        Rol cajero = rol("Cajero", true);
        when(empleadoDAO.findRange(0, 1000)).thenReturn(List.of(ana, beto));
        when(rolDAO.findByActivo(Boolean.TRUE)).thenReturn(List.of(cajero));

        model.init();

        assertEquals(List.of(ana), model.getListaEmpleado());
        assertEquals(List.of(cajero), model.getListaRol());
    }

    @Test
    void etiquetasDelEmpleado() {
        assertEquals("Sin asignación", model.nombreEmpleado(null));
        assertEquals("Ana López", model.nombreCompleto(empleado("Ana", true)));
    }

    private Empleado empleado(String nombre, boolean activo) {
        Empleado empleado = new Empleado(UUID.randomUUID());
        empleado.setNombre(nombre);
        empleado.setApellido("López");
        empleado.setActivo(activo);
        return empleado;
    }

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
