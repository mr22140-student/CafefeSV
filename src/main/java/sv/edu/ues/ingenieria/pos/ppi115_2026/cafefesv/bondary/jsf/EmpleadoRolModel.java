package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoRolDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.RolDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Empleado;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Rol;

/**
 * idEmpleado es UUID (la BD no tiene FK hacia empleado); idRol es @ManyToOne Rol.
 */
@Named("empleadoRolModel")
@ViewScoped
public class EmpleadoRolModel extends AbstractModel<EmpleadoRol, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private EmpleadoRolDAO empleadoRolDAO;

    @Inject
    private EmpleadoDAO empleadoDAO;

    @Inject
    private RolDAO rolDAO;

    /** Todos los empleados, para mostrar nombres en la tabla. */
    private Map<UUID, Empleado> empleados = new HashMap<>();

    /** Opciones de los combos (solo activos). */
    private List<Empleado> listaEmpleado = new ArrayList<>();
    private List<Rol> listaRol = new ArrayList<>();

    @PostConstruct
    public void init() {
        List<Empleado> emps = empleadoDAO.findRange(0, 1000);
        if (emps != null) {
            for (Empleado e : emps) {
                empleados.put(e.getIdEmpleado(), e);
                if (Boolean.TRUE.equals(e.getActivo())) {
                    listaEmpleado.add(e);
                }
            }
            listaEmpleado.sort(Comparator.comparing(this::nombreCompleto, String.CASE_INSENSITIVE_ORDER));
        }

        List<Rol> roles = rolDAO.findRange(0, 1000);
        if (roles != null) {
            for (Rol r : roles) {
                if (Boolean.TRUE.equals(r.getActivo())) {
                    listaRol.add(r);
                }
            }
        }

        iniciar();
    }

    @Override
    protected GenericDAO<EmpleadoRol, UUID> getDAO() {
        return empleadoRolDAO;
    }

    @Override
    protected EmpleadoRol crearNuevo() {
        EmpleadoRol nuevo = new EmpleadoRol(UUID.randomUUID());
        nuevo.setActivo(Boolean.TRUE);
        return nuevo;
    }

    @Override
    public void seleccionarRegistro(EmpleadoRol entidad) {
        super.seleccionarRegistro(entidad);
        if (registro == null) {
            return;
        }
        // Si el empleado o el rol estan inactivos, se agregan al combo para que se vean al modificar
        Empleado e = (registro.getIdEmpleado() != null) ? empleados.get(registro.getIdEmpleado()) : null;
        if (e != null && !listaEmpleado.contains(e)) {
            listaEmpleado.add(e);
        }
        if (registro.getIdRol() != null && !listaRol.contains(registro.getIdRol())) {
            listaRol.add(registro.getIdRol());
        }
    }

    // ================= Validaciones =================

    @Override
    public void btnCrearHandler() {
        if (esValido()) {
            super.btnCrearHandler();
        }
    }

    @Override
    public void btnModificarHandler() {
        if (esValido()) {
            super.btnModificarHandler();
        }
    }

    @Override
    public void btnEliminarHandler() {
        if (registro != null
                && (empleadoRolDAO.countOrdenes(registro) > 0 || empleadoRolDAO.countFacturas(registro) > 0)) {
            mensaje(FacesMessage.SEVERITY_WARN,
                    "No se puede eliminar: esta asignación ya tiene órdenes o facturas. Puede desmarcar 'Activo'.");
            return;
        }
        super.btnEliminarHandler();
    }

    private boolean esValido() {
        if (registro == null) {
            return true; // AbstractModel muestra su propio mensaje
        }
        if (registro.getIdEmpleado() == null) {
            mensaje(FacesMessage.SEVERITY_WARN, "Debe seleccionar el empleado.");
            return false;
        }
        if (registro.getIdRol() == null) {
            mensaje(FacesMessage.SEVERITY_WARN, "Debe seleccionar el rol.");
            return false;
        }
        UUID excluir = ESTADO_MODIFICAR.equals(estado) ? registro.getIdEmpleadoRol() : null;
        if (empleadoRolDAO.existeAsignacion(registro.getIdEmpleado(), registro.getIdRol(), excluir)) {
            mensaje(FacesMessage.SEVERITY_WARN, "Ese empleado ya tiene asignado el rol "
                    + registro.getIdRol().getNombre() + ".");
            return false;
        }
        return true;
    }

    // ================= Etiquetas =================

    /** "Nombre Apellido" a partir del UUID guardado en empleado_rol. */
    public String nombreEmpleado(UUID idEmpleado) {
        if (idEmpleado == null) {
            return "Sin asignación";
        }
        Empleado e = empleados.get(idEmpleado);
        return (e != null) ? nombreCompleto(e) : "Empleado no encontrado";
    }

    public String nombreCompleto(Empleado e) {
        String nombre = e.getNombre() != null ? e.getNombre() : "";
        String apellido = e.getApellido() != null ? e.getApellido() : "";
        return (nombre + " " + apellido).trim();
    }

    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
    }

    // ================= Getters =================

    public List<Empleado> getListaEmpleado() {
        return listaEmpleado;
    }

    public List<Rol> getListaRol() {
        return listaRol;
    }
}