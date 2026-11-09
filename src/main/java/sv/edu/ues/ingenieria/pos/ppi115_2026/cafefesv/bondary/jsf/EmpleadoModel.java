package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoRolDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.RolDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Empleado;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Rol;

/**
 *
 * @author valen
 */
@Named("empleadoModel")
@ViewScoped
public class EmpleadoModel extends AbstractModel<Empleado, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private EmpleadoDAO empleadoDAO;

    @Inject
    private EmpleadoRolDAO empleadoRolDAO;

    @Inject
    private RolDAO rolDAO;

    /**
     * Roles del empleado seleccionado (tabla de la pestaña EmpleadoRol).
     */
    private List<EmpleadoRol> rolesEmpleado = new ArrayList<>();

    /**
     * Rol elegido en el combo de la pestaña EmpleadoRol.
     */
    private Rol rolSeleccionado;

    @PostConstruct
    public void init() {
        iniciar();
    }

    @Override
    protected GenericDAO<Empleado, UUID> getDAO() {
        return empleadoDAO;
    }

    @Override
    protected Empleado crearNuevo() {
        Empleado nuevo = new Empleado();
        nuevo.setIdEmpleado(UUID.randomUUID());
        nuevo.setActivo(true);
        return nuevo;
    }

    @Override
    public void btnEliminarHandler() {
        if (registro != null) {
            if (!sinError("No se puede eliminar", empleadoDAO.validarEliminar(registro))) {
                return;
            }
            // Si no tiene historial, primero se borra su rol y luego el empleado
            try {
                for (EmpleadoRol asignacion : empleadoRolDAO.findByEmpleado(registro.getIdEmpleado())) {
                    empleadoRolDAO.delete(asignacion);
                }
            } catch (Exception ex) {
                mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo quitar el rol del empleado.");
                return;
            }
        }
        super.btnEliminarHandler();
    }

    // ================= Pestaña EmpleadoRol =================
    /**
     * Al seleccionar un empleado, carga su rol y lo muestra en el combo.
     */
    @Override
    public void seleccionarRegistro(Empleado entidad) {
        super.seleccionarRegistro(entidad);
        rolSeleccionado = null;
        if (registro != null) {
            rolesEmpleado = empleadoRolDAO.findByEmpleado(registro.getIdEmpleado());
            if (!rolesEmpleado.isEmpty()) {
                rolSeleccionado = rolesEmpleado.get(0).getIdRol();
            }
        }
    }

    public void btnAgregarRolHandler() {
        if (!sinError("Ya tiene rol", empleadoRolDAO.validarSinRolAsignado(registro.getIdEmpleado()))
                || !sinError("Rol no válido", empleadoRolDAO.validarRolElegido(rolSeleccionado))) {
            return;
        }
        EmpleadoRol nuevaAsignacion = new EmpleadoRol(UUID.randomUUID());
        nuevaAsignacion.setIdEmpleado(registro.getIdEmpleado());
        nuevaAsignacion.setIdRol(rolSeleccionado);
        nuevaAsignacion.setActivo(Boolean.TRUE);
        empleadoRolDAO.save(nuevaAsignacion);
        mensaje(FacesMessage.SEVERITY_INFO, "Rol " + rolSeleccionado.getNombre() + " asignado.");
        rolesEmpleado = empleadoRolDAO.findByEmpleado(registro.getIdEmpleado());
    }

    /**
     * Cambia el rol actual del empleado por el elegido en el combo.
     */
    public void btnModificarRolHandler() {
        EmpleadoRol asignacionActual = rolesEmpleado.isEmpty() ? null : rolesEmpleado.get(0);
        if (!sinError("No se puede cambiar el rol", empleadoRolDAO.validarCambioDeRol(asignacionActual, rolSeleccionado))
                || !sinError("Rol no válido", empleadoRolDAO.validarRolElegido(rolSeleccionado))) {
            return;
        }
        asignacionActual.setIdRol(rolSeleccionado);
        empleadoRolDAO.update(asignacionActual);
        mensaje(FacesMessage.SEVERITY_INFO, "Rol cambiado a " + rolSeleccionado.getNombre() + ".");
        rolesEmpleado = empleadoRolDAO.findByEmpleado(registro.getIdEmpleado());
    }

    // ================= Getters / Setters =================
    /**
     * Roles activos para el combo (más el rol actual del empleado, aunque esté
     * inactivo).
     */
    public List<Rol> getListaRol() {
        Rol rolActual = rolesEmpleado.isEmpty() ? null : rolesEmpleado.get(0).getIdRol();
        return rolDAO.buscarActivosIncluyendo(rolActual);
    }

    public List<EmpleadoRol> getRolesEmpleado() {
        return rolesEmpleado;
    }

    public Rol getRolSeleccionado() {
        return rolSeleccionado;
    }

    public void setRolSeleccionado(Rol rolSeleccionado) {
        this.rolSeleccionado = rolSeleccionado;
    }
    // Atajo para no repetir tanto FacesContext...addMessage.

    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
    }

}
