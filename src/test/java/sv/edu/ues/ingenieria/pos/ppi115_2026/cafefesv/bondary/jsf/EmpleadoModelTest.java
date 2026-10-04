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
public class EmpleadoModelTest extends ModelTestBase {

    @InjectMocks
    private EmpleadoModel model;

    @Mock
    private EmpleadoDAO empleadoDAO;
    @Mock
    private EmpleadoRolDAO empleadoRolDAO;
    @Mock
    private RolDAO rolDAO;

    @Test
    void alSeleccionarUnEmpleado_cargaSuRolActual() {
        Empleado e = empleado();
        Rol cajero = rol("Cajero", true);
        when(empleadoRolDAO.findByEmpleado(e.getIdEmpleado())).thenReturn(List.of(asignacion(e, cajero)));

        model.seleccionarRegistro(e);

        assertEquals(cajero, model.getRolSeleccionado());
        assertEquals(1, model.getRolesEmpleado().size());
    }

    @Test
    void asignarRol_sinElegirRol_avisaYNoGuarda() {
        model.seleccionarRegistro(empleado());

        model.btnAgregarRolHandler();

        verify(empleadoRolDAO, never()).save(any());
        assertEquals("Seleccione un rol.", ultimoMensaje().getSummary());
    }

    @Test
    void asignarRol_guardaLaAsignacion() {
        model.seleccionarRegistro(empleado());
        model.setRolSeleccionado(rol("Cajero", true));

        model.btnAgregarRolHandler();

        verify(empleadoRolDAO).save(any(EmpleadoRol.class));
        assertEquals("Rol Cajero asignado.", ultimoMensaje().getSummary());
    }

    @Test
    void asignarRol_inactivo_avisaYNoGuarda() {
        model.seleccionarRegistro(empleado());
        model.setRolSeleccionado(rol("Barista", false));

        model.btnAgregarRolHandler();

        verify(empleadoRolDAO, never()).save(any());
        assertEquals("El rol Barista está inactivo.", ultimoMensaje().getSummary());
    }

    @Test
    void asignarRol_siYaTieneUno_avisaYNoGuarda() {
        Empleado e = empleado();
        when(empleadoRolDAO.findByEmpleado(e.getIdEmpleado())).thenReturn(List.of(asignacion(e, rol("Cajero", true))));
        model.seleccionarRegistro(e);

        model.btnAgregarRolHandler();

        verify(empleadoRolDAO, never()).save(any());
    }

    @Test
    void cambiarAlMismoRol_avisaYNoActualiza() {
        Empleado e = empleado();
        when(empleadoRolDAO.findByEmpleado(e.getIdEmpleado())).thenReturn(List.of(asignacion(e, rol("Cajero", true))));
        model.seleccionarRegistro(e);

        model.btnModificarRolHandler();

        verify(empleadoRolDAO, never()).update(any());
        assertEquals("El empleado ya tiene ese rol.", ultimoMensaje().getSummary());
    }

    @Test
    void cambiarAUnRolInactivo_avisaYNoActualiza() {
        Empleado e = empleado();
        when(empleadoRolDAO.findByEmpleado(e.getIdEmpleado())).thenReturn(List.of(asignacion(e, rol("Cajero", true))));
        model.seleccionarRegistro(e);
        model.setRolSeleccionado(rol("Barista", false));

        model.btnModificarRolHandler();

        verify(empleadoRolDAO, never()).update(any());
        assertEquals("El rol Barista está inactivo.", ultimoMensaje().getSummary());
    }

    @Test
    void cambiarRol_actualizaLaAsignacion() {
        Empleado e = empleado();
        EmpleadoRol er = asignacion(e, rol("Cajero", true));
        when(empleadoRolDAO.findByEmpleado(e.getIdEmpleado())).thenReturn(List.of(er));
        model.seleccionarRegistro(e);
        Rol camarero = rol("Camarero", true);
        model.setRolSeleccionado(camarero);

        model.btnModificarRolHandler();

        verify(empleadoRolDAO).update(er);
        assertEquals(camarero, er.getIdRol());
    }

    @Test
    void laListaDeRoles_muestraActivosYElRolActualAunqueEsteInactivo() {
        Empleado e = empleado();
        Rol activo = rol("Cajero", true);
        Rol inactivo = rol("Barista", false);
        Rol actualInactivo = rol("Mesero", false);
        when(rolDAO.findRange(0, 1000)).thenReturn(List.of(activo, inactivo, actualInactivo));
        when(empleadoRolDAO.findByEmpleado(e.getIdEmpleado())).thenReturn(List.of(asignacion(e, actualInactivo)));
        model.seleccionarRegistro(e);

        assertEquals(List.of(activo, actualInactivo), model.getListaRol());
    }

    @Test
    void noEliminaUnEmpleadoConOrdenes() {
        Empleado e = empleado();
        EmpleadoRol er = asignacion(e, rol("Camarero", true));
        when(empleadoRolDAO.findByEmpleado(e.getIdEmpleado())).thenReturn(List.of(er));
        when(empleadoRolDAO.countOrdenes(er)).thenReturn(1L);
        model.seleccionarRegistro(e);

        model.btnEliminarHandler();

        verify(empleadoDAO, never()).delete(any());
    }

    @Test
    void eliminaUnEmpleadoSinHistorial_borrandoPrimeroSuRol() {
        Empleado e = empleado();
        EmpleadoRol er = asignacion(e, rol("Camarero", true));
        when(empleadoRolDAO.findByEmpleado(e.getIdEmpleado())).thenReturn(List.of(er));
        model.seleccionarRegistro(e);

        model.btnEliminarHandler();

        verify(empleadoRolDAO).delete(er);
        verify(empleadoDAO).delete(e);
    }

    private Empleado empleado() {
        Empleado e = new Empleado(UUID.randomUUID());
        e.setNombre("Ana");
        e.setApellido("López");
        e.setActivo(true);
        return e;
    }

    private Rol rol(String nombre, boolean activo) {
        Rol r = new Rol(UUID.randomUUID());
        r.setNombre(nombre);
        r.setActivo(activo);
        return r;
    }

    private EmpleadoRol asignacion(Empleado e, Rol r) {
        EmpleadoRol er = new EmpleadoRol(UUID.randomUUID());
        er.setIdEmpleado(e.getIdEmpleado());
        er.setIdRol(r);
        er.setActivo(true);
        return er;
    }
    

}
