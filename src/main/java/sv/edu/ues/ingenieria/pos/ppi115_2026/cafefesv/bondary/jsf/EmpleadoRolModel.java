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
 * idEmpleado es UUID (la BD no tiene FK hacia empleado); idRol es @ManyToOne
 * Rol.
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

    /**
     * Todos los empleados, para mostrar nombres en la tabla.
     */
    private Map<UUID, Empleado> empleados = new HashMap<>();

    /**
     * Opciones de los combos (solo activos).
     */
    private List<Empleado> listaEmpleado = new ArrayList<>();
    private List<Rol> listaRol = new ArrayList<>();

    @PostConstruct
    public void init() {
        List<Empleado> todosLosEmpleados = empleadoDAO.findRange(0, 1000);
        if (todosLosEmpleados != null) {
            for (Empleado empleado : todosLosEmpleados) {
                empleados.put(empleado.getIdEmpleado(), empleado);
                if (Boolean.TRUE.equals(empleado.getActivo())) {
                    listaEmpleado.add(empleado);
                }
            }
            listaEmpleado.sort(Comparator.comparing(this::nombreCompleto, String.CASE_INSENSITIVE_ORDER));
        }
        listaRol = new ArrayList<>(rolDAO.findByActivo(Boolean.TRUE));
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
        Empleado empleado = (registro.getIdEmpleado() != null) ? empleados.get(registro.getIdEmpleado()) : null;
        if (empleado != null && !listaEmpleado.contains(empleado)) {
            listaEmpleado.add(empleado);
        }
        if (registro.getIdRol() != null && !listaRol.contains(registro.getIdRol())) {
            listaRol.add(registro.getIdRol());
        }
    }

    // ================= Validaciones =================
    @Override
    public void btnCrearHandler() {
        if (sinError("Asignación no válida", empleadoRolDAO.validarAsignacion(registro))) {
            super.btnCrearHandler();
        }
    }

    @Override
    public void btnModificarHandler() {
        if (sinError("Asignación no válida", empleadoRolDAO.validarAsignacion(registro))) {
            super.btnModificarHandler();
        }
    }

    @Override
    public void btnEliminarHandler() {
        if (sinError("No se puede eliminar", empleadoRolDAO.validarEliminar(registro))) {
            super.btnEliminarHandler();
        }
    }

    // ================= Etiquetas =================
    /**
     * "Nombre Apellido" a partir del UUID guardado en empleado_rol.
     */
    public String nombreEmpleado(UUID idEmpleado) {
        if (idEmpleado == null) {
            return "Sin asignación";
        }
        Empleado empleado = empleados.get(idEmpleado);
        return (empleado != null) ? nombreCompleto(empleado) : "Empleado no encontrado";
    }

    public String nombreCompleto(Empleado empleado) {
        String nombre = empleado.getNombre() != null ? empleado.getNombre() : "";
        String apellido = empleado.getApellido() != null ? empleado.getApellido() : "";
        return (nombre + " " + apellido).trim();
    }

    // ================= Getters =================
    public List<Empleado> getListaEmpleado() {
        return listaEmpleado;
    }

    public List<Rol> getListaRol() {
        return listaRol;
    }
}
