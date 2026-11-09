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
        Empleado ana = empleado();
        Rol cajero = rol("Cajero");
        when(empleadoRolDAO.findByEmpleado(ana.getIdEmpleado())).thenReturn(List.of(asignacion(ana, cajero)));

        model.seleccionarRegistro(ana);

        assertEquals(cajero, model.getRolSeleccionado());
        assertEquals(1, model.getRolesEmpleado().size());
    }

    // ===================== Asignar rol =====================

    @Test
    void asignarRol_siElDaoDiceQueYaTieneUno_noGuarda() {
        Empleado ana = empleado();
        when(empleadoRolDAO.validarSinRolAsignado(ana.getIdEmpleado())).thenReturn("El empleado ya tiene un rol asignado.");
        model.seleccionarRegistro(ana);
        model.setRolSeleccionado(rol("Cajero"));

        model.btnAgregarRolHandler();

        verify(empleadoRolDAO, never()).save(any());
        assertEquals("Ya tiene rol", ultimoMensaje().getSummary());
    }

    @Test
    void asignarRol_siElDaoRechazaElRol_noGuarda() {
        Rol barista = rol("Barista");
        when(empleadoRolDAO.validarRolElegido(barista)).thenReturn("El rol Barista está inactivo.");
        model.seleccionarRegistro(empleado());
        model.setRolSeleccionado(barista);

        model.btnAgregarRolHandler();

        verify(empleadoRolDAO, never()).save(any());
        assertEquals("Rol no válido", ultimoMensaje().getSummary());
    }

    @Test
    void asignarRol_siElDaoNoEncuentraErrores_guardaLaAsignacion() {
        model.seleccionarRegistro(empleado());
        model.setRolSeleccionado(rol("Cajero"));

        model.btnAgregarRolHandler();

        verify(empleadoRolDAO).save(any(EmpleadoRol.class));
        assertEquals("Rol Cajero asignado.", ultimoMensaje().getSummary());
    }

    // ===================== Cambiar rol =====================

    @Test
    void cambiarRol_siElDaoLoRechaza_noActualiza() {
        Empleado ana = empleado();
        EmpleadoRol asignacionActual = asignacion(ana, rol("Cajero"));
        when(empleadoRolDAO.findByEmpleado(ana.getIdEmpleado())).thenReturn(List.of(asignacionActual));
        when(empleadoRolDAO.validarCambioDeRol(asignacionActual, asignacionActual.getIdRol())).thenReturn("El empleado ya tiene ese rol.");
        model.seleccionarRegistro(ana);

        model.btnModificarRolHandler();

        verify(empleadoRolDAO, never()).update(any());
        assertEquals("No se puede cambiar el rol", ultimoMensaje().getSummary());
    }

    @Test
    void cambiarRol_aUnRolQueElDaoRechaza_noActualiza() {
        Empleado ana = empleado();
        when(empleadoRolDAO.findByEmpleado(ana.getIdEmpleado())).thenReturn(List.of(asignacion(ana, rol("Cajero"))));
        Rol barista = rol("Barista");
        when(empleadoRolDAO.validarRolElegido(barista)).thenReturn("El rol Barista está inactivo.");
        model.seleccionarRegistro(ana);
        model.setRolSeleccionado(barista);

        model.btnModificarRolHandler();

        verify(empleadoRolDAO, never()).update(any());
        assertEquals("Rol no válido", ultimoMensaje().getSummary());
    }

    @Test
    void cambiarRol_siElDaoNoEncuentraErrores_actualizaLaAsignacion() {
        Empleado ana = empleado();
        EmpleadoRol asignacionActual = asignacion(ana, rol("Cajero"));
        when(empleadoRolDAO.findByEmpleado(ana.getIdEmpleado())).thenReturn(List.of(asignacionActual));
        model.seleccionarRegistro(ana);
        Rol camarero = rol("Camarero");
        model.setRolSeleccionado(camarero);

        model.btnModificarRolHandler();

        verify(empleadoRolDAO).update(asignacionActual);
        assertEquals(camarero, asignacionActual.getIdRol());
    }

    @Test
    void laListaDeRoles_laArmaElDaoConElRolActual() {
        Empleado ana = empleado();
        Rol meseroInactivo = rol("Mesero");
        List<Rol> rolesDelCombo = List.of(rol("Cajero"), meseroInactivo);
        when(empleadoRolDAO.findByEmpleado(ana.getIdEmpleado())).thenReturn(List.of(asignacion(ana, meseroInactivo)));
        when(rolDAO.buscarActivosIncluyendo(meseroInactivo)).thenReturn(rolesDelCombo);
        model.seleccionarRegistro(ana);

        assertEquals(rolesDelCombo, model.getListaRol());
    }

    // ===================== Eliminar =====================

    @Test
    void noEliminaSiElDaoLoRechaza() {
        Empleado ana = empleado();
        when(empleadoDAO.validarEliminar(ana)).thenReturn("El empleado ya tiene órdenes o facturas.");
        model.seleccionarRegistro(ana);

        model.btnEliminarHandler();

        verify(empleadoRolDAO, never()).delete(any());
        verify(empleadoDAO, never()).delete(any());
        assertEquals("No se puede eliminar", ultimoMensaje().getSummary());
    }

    @Test
    void eliminaUnEmpleadoSinHistorial_borrandoPrimeroSuRol() {
        Empleado ana = empleado();
        EmpleadoRol asignacion = asignacion(ana, rol("Camarero"));
        when(empleadoRolDAO.findByEmpleado(ana.getIdEmpleado())).thenReturn(List.of(asignacion));
        model.seleccionarRegistro(ana);

        model.btnEliminarHandler();

        verify(empleadoRolDAO).delete(asignacion);
        verify(empleadoDAO).delete(ana);
    }

    // ===================== ayudantes =====================

    private Empleado empleado() {
        Empleado ana = new Empleado(UUID.randomUUID());
        ana.setNombre("Ana");
        ana.setApellido("López");
        ana.setActivo(true);
        return ana;
    }

    private Rol rol(String nombre) {
        Rol rol = new Rol(UUID.randomUUID());
        rol.setNombre(nombre);
        rol.setActivo(true);
        return rol;
    }

    private EmpleadoRol asignacion(Empleado empleado, Rol rol) {
        EmpleadoRol asignacion = new EmpleadoRol(UUID.randomUUID());
        asignacion.setIdEmpleado(empleado.getIdEmpleado());
        asignacion.setIdRol(rol);
        asignacion.setActivo(true);
        return asignacion;
    }
}