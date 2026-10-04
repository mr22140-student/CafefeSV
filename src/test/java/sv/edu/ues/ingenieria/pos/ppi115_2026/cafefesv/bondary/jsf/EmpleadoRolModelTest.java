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
    void noGuardaSinEmpleado() {
        model.setRegistro(asignacion(null, rol("Cajero", true)));

        model.btnCrearHandler();

        verify(empleadoRolDAO, never()).save(any());
        assertEquals("Debe seleccionar el empleado.", ultimoMensaje().getSummary());
    }

    @Test
    void noGuardaSinRol() {
        model.setRegistro(asignacion(UUID.randomUUID(), null));

        model.btnCrearHandler();

        verify(empleadoRolDAO, never()).save(any());
        assertEquals("Debe seleccionar el rol.", ultimoMensaje().getSummary());
    }

    @Test
    void noGuardaUnaAsignacionRepetida() {
        when(empleadoRolDAO.existeAsignacion(any(), any(), any())).thenReturn(true);
        model.setRegistro(asignacion(UUID.randomUUID(), rol("Cajero", true)));

        model.btnCrearHandler();

        verify(empleadoRolDAO, never()).save(any());
    }

    @Test
    void guardaUnaAsignacionValida() {
        when(empleadoRolDAO.existeAsignacion(any(), any(), any())).thenReturn(false);
        EmpleadoRol er = asignacion(UUID.randomUUID(), rol("Cajero", true));
        model.setRegistro(er);

        model.btnCrearHandler();

        verify(empleadoRolDAO).save(er);
    }

    @Test
    void noEliminaUnaAsignacionConOrdenes() {
        EmpleadoRol er = asignacion(UUID.randomUUID(), rol("Camarero", true));
        when(empleadoRolDAO.countOrdenes(er)).thenReturn(1L);
        model.seleccionarRegistro(er);

        model.btnEliminarHandler();

        verify(empleadoRolDAO, never()).delete(any());
    }

    @Test
    void alIniciar_soloListaEmpleadosYRolesActivos() {
        Empleado activo = empleado("Ana", true);
        Empleado inactivo = empleado("Beto", false);
        Rol rolActivo = rol("Cajero", true);
        Rol rolInactivo = rol("Barista", false);
        when(empleadoDAO.findRange(0, 1000)).thenReturn(List.of(activo, inactivo));
        when(rolDAO.findRange(0, 1000)).thenReturn(List.of(rolActivo, rolInactivo));

        model.init();

        assertEquals(List.of(activo), model.getListaEmpleado());
        assertEquals(List.of(rolActivo), model.getListaRol());
    }

    @Test
    void etiquetasDelEmpleado() {
        assertEquals("Sin asignación", model.nombreEmpleado(null));
        assertEquals("Ana López", model.nombreCompleto(empleado("Ana", true)));
    }

    private Empleado empleado(String nombre, boolean activo) {
        Empleado e = new Empleado(UUID.randomUUID());
        e.setNombre(nombre);
        e.setApellido("López");
        e.setActivo(activo);
        return e;
    }

    private Rol rol(String nombre, boolean activo) {
        Rol r = new Rol(UUID.randomUUID());
        r.setNombre(nombre);
        r.setActivo(activo);
        return r;
    }

    private EmpleadoRol asignacion(UUID idEmpleado, Rol r) {
        EmpleadoRol er = new EmpleadoRol(UUID.randomUUID());
        er.setIdEmpleado(idEmpleado);
        er.setIdRol(r);
        er.setActivo(true);
        return er;
    }
}
