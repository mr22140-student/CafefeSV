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

    /**
     * Fila elegida en la tabla de roles del empleado.
     */
    private EmpleadoRol rolEmpleadoSeleccionado;

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
        if (registro != null && empleadoRolDAO.countByEmpleado(registro.getIdEmpleado()) > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_WARN, "No se puede eliminar: el empleado tiene roles asignados.", null));
            return;
        }
        super.btnEliminarHandler();
    }

    // ================= Pestaña EmpleadoRol =================
    /**
     * Al seleccionar un empleado en la tabla, carga sus roles.
     */
    @Override
    public void seleccionarRegistro(Empleado entidad) {
        super.seleccionarRegistro(entidad);
        rolSeleccionado = null;
        if (registro != null) {
            rolesEmpleado = empleadoRolDAO.findByEmpleado(registro.getIdEmpleado());
        }
    }

    /**
     * Asigna el rol elegido en el combo al empleado seleccionado.
     */
    public void btnAgregarRolHandler() {
        if (rolSeleccionado == null
                || empleadoRolDAO.existeAsignacion(registro.getIdEmpleado(), rolSeleccionado, null)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_WARN, "Seleccione un rol que el empleado no tenga.", null));
            return;
        }
        EmpleadoRol nuevo = new EmpleadoRol(UUID.randomUUID());
        nuevo.setIdEmpleado(registro.getIdEmpleado());
        nuevo.setIdRol(rolSeleccionado);
        nuevo.setActivo(Boolean.TRUE);
        empleadoRolDAO.save(nuevo);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_INFO, "Rol " + rolSeleccionado.getNombre() + " asignado.", null));
        rolSeleccionado = null;
        rolesEmpleado = empleadoRolDAO.findByEmpleado(registro.getIdEmpleado());
    }

    /**
     * Quita un rol al empleado, si no se ha usado en órdenes o facturas.
     */
    public void btnQuitarRolHandler(EmpleadoRol er) {
        if (empleadoRolDAO.countOrdenes(er) > 0 || empleadoRolDAO.countFacturas(er) > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_WARN, "No se puede quitar: el rol ya tiene órdenes o facturas.", null));
            return;
        }
        empleadoRolDAO.delete(er);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_INFO, "Rol quitado.", null));
        rolesEmpleado = empleadoRolDAO.findByEmpleado(registro.getIdEmpleado());
    }

    /**
     * Quita el rol seleccionado en la tabla, si no se ha usado en órdenes o
     * facturas.
     */
    public void btnQuitarRolHandler() {
        EmpleadoRol er = rolEmpleadoSeleccionado;
        if (er == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_WARN, "Seleccione en la tabla el rol que desea quitar.", null));
            return;
        }
        if (empleadoRolDAO.countOrdenes(er) > 0 || empleadoRolDAO.countFacturas(er) > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_WARN, "No se puede quitar: el rol ya tiene órdenes o facturas.", null));
            return;
        }
        empleadoRolDAO.delete(er);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_INFO, "Rol quitado.", null));
        rolEmpleadoSeleccionado = null;
        rolesEmpleado = empleadoRolDAO.findByEmpleado(registro.getIdEmpleado());
    }

    // ================= Getters / Setters =================
    public List<Rol> getListaRol() {
        return rolDAO.findRange(0, 1000);
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

    public EmpleadoRol getRolEmpleadoSeleccionado() {
        return rolEmpleadoSeleccionado;
    }

    public void setRolEmpleadoSeleccionado(EmpleadoRol rolEmpleadoSeleccionado) {
        this.rolEmpleadoSeleccionado = rolEmpleadoSeleccionado;
    }
}
