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
        if (registro == null) {
            super.btnEliminarHandler();
            return;
        }
        List<EmpleadoRol> roles = empleadoRolDAO.findByEmpleado(registro.getIdEmpleado());

        // Si algún rol del empleado ya tiene órdenes o facturas, no se elimina
        for (EmpleadoRol er : roles) {
            if (empleadoRolDAO.countOrdenes(er) > 0 || empleadoRolDAO.countFacturas(er) > 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                        FacesMessage.SEVERITY_WARN,
                        "No se puede eliminar: el empleado ya tiene órdenes o facturas. Puede desmarcar 'Activo'.", null));
                return;
            }
        }

        // Si no tiene historial, primero se borra su rol y luego el empleado
        try {
            for (EmpleadoRol er : roles) {
                empleadoRolDAO.delete(er);
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "No se pudo quitar el rol del empleado.", null));
            return;
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

    /**
     * Asigna el rol elegido, solo si el empleado todavía no tiene uno.
     */
    public void btnAgregarRolHandler() {
        if (!rolesEmpleado.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_WARN, "El empleado ya tiene un rol asignado. Use Modificar para cambiarlo.", null));
            return;
        }
        if (rolSeleccionado == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_WARN, "Seleccione un rol.", null));
            return;
        }
        if (!Boolean.TRUE.equals(rolSeleccionado.getActivo())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_WARN, "El rol " + rolSeleccionado.getNombre() + " está inactivo.", null));
            return;
        }
        EmpleadoRol nuevo = new EmpleadoRol(UUID.randomUUID());
        nuevo.setIdEmpleado(registro.getIdEmpleado());
        nuevo.setIdRol(rolSeleccionado);
        nuevo.setActivo(Boolean.TRUE);
        empleadoRolDAO.save(nuevo);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_INFO, "Rol " + rolSeleccionado.getNombre() + " asignado.", null));
        rolesEmpleado = empleadoRolDAO.findByEmpleado(registro.getIdEmpleado());
    }

    /**
     * Cambia el rol actual del empleado por el elegido en el combo.
     */
    public void btnModificarRolHandler() {
        if (rolesEmpleado.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_WARN, "El empleado no tiene rol. Use Asignar.", null));
            return;
        }
        if (rolSeleccionado == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_WARN, "Seleccione un rol.", null));
            return;
        }
        if (!Boolean.TRUE.equals(rolSeleccionado.getActivo())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_WARN, "El rol " + rolSeleccionado.getNombre() + " está inactivo.", null));
            return;
        }

        EmpleadoRol actual = rolesEmpleado.get(0);
        if (rolSeleccionado.equals(actual.getIdRol())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_WARN, "El empleado ya tiene ese rol.", null));
            return;
        }
        actual.setIdRol(rolSeleccionado);
        empleadoRolDAO.update(actual);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_INFO, "Rol cambiado a " + rolSeleccionado.getNombre() + ".", null));
        rolesEmpleado = empleadoRolDAO.findByEmpleado(registro.getIdEmpleado());
    }

    /**
     * Quita el rol seleccionado en la tabla, si no se ha usado en órdenes o
     * facturas.
     */
    // ================= Getters / Setters =================
    /**
     * Roles activos para el combo (más el rol actual del empleado, aunque esté
     * inactivo).
     */
    public List<Rol> getListaRol() {
        Rol rolActual = rolesEmpleado.isEmpty() ? null : rolesEmpleado.get(0).getIdRol();
        List<Rol> lista = new ArrayList<>();
        for (Rol r : rolDAO.findRange(0, 1000)) {
            if (Boolean.TRUE.equals(r.getActivo()) || r.equals(rolActual)) {
                lista.add(r);
            }
        }
        return lista;
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

}
