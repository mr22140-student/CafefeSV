package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoRolDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.OrdenDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Empleado;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Orden;

@Named("ordenModel")
@ViewScoped
public class OrdenModel extends AbstractModel<Orden, UUID> {

    private static final long serialVersionUID = 1L;
    private static final int MAX_SUGERENCIAS = 10;

    @Inject
    private OrdenDAO ordenDAO;

    @Inject
    private EmpleadoRolDAO empleadoRolDAO;

    @Inject
    private EmpleadoDAO empleadoDAO;

    // Se cargan una sola vez al abrir la pagina (antes se consultaban en cada fila)
    private Map<UUID, Empleado> empleados = new HashMap<>();
    private Map<UUID, EmpleadoRol> empleadosRol = new HashMap<>();

    @PostConstruct
    public void init() {
        iniciar();
        List<Empleado> listaEmp = empleadoDAO.findRange(0, 1000);
        if (listaEmp != null) {
            for (Empleado e : listaEmp) {
                empleados.put(e.getIdEmpleado(), e);
            }
        }
        List<EmpleadoRol> listaER = empleadoRolDAO.findRange(0, 1000);
        if (listaER != null) {
            for (EmpleadoRol er : listaER) {
                empleadosRol.put(er.getIdEmpleadoRol(), er);
            }
        }
    }

    @Override
    protected GenericDAO<Orden, UUID> getDAO() {
        return ordenDAO;
    }

    @Override
    protected Orden crearNuevo() {
        Orden nueva = new Orden();
        nueva.setIdOrden(UUID.randomUUID());
        nueva.setFechaCreacion(new Date());
        return nueva;
    }

    // ================= Validaciones =================

    @Override
    public void btnCrearHandler() {
        if (esValida()) {
            super.btnCrearHandler();
        }
    }

    @Override
    public void btnModificarHandler() {
        if (esValida()) {
            super.btnModificarHandler();
        }
    }

    private boolean esValida() {
        if (registro != null && registro.getIdEmpleadoRol() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_WARN, "Debe seleccionar un empleado y rol válido.", null));
            return false;
        }
        return true;
    }

    // ================= AutoComplete =================

    /**
     * Busca por nombre, apellido o rol; acepta varias palabras en cualquier
     * orden e ignora tildes. Solo sugiere asignaciones y empleados activos.
     */
    public List<EmpleadoRol> completarEmpleadoRol(String consulta) {
        String[] palabras = normalizar(consulta).split("\\s+");
        List<EmpleadoRol> resultado = new ArrayList<>();

        for (EmpleadoRol er : empleadosRol.values()) {
            if (!estaDisponible(er)) {
                continue;
            }
            String texto = normalizar(obtenerTextoEmpleadoRol(er));
            boolean coincide = true;
            for (String p : palabras) {
                if (!texto.contains(p)) {
                    coincide = false;
                    break;
                }
            }
            if (coincide) {
                resultado.add(er);
            }
        }
        resultado.sort((a, b) -> obtenerTextoEmpleadoRol(a).compareToIgnoreCase(obtenerTextoEmpleadoRol(b)));
        return resultado.size() > MAX_SUGERENCIAS ? resultado.subList(0, MAX_SUGERENCIAS) : resultado;
    }

    /** "Nombre Apellido (Rol)" para la tabla y el AutoComplete. */
    public String obtenerTextoEmpleadoRol(EmpleadoRol er) {
        if (er == null) {
            return "";
        }
        Empleado e = (er.getIdEmpleado() != null) ? empleados.get(er.getIdEmpleado()) : null;
        String nombre = (e != null)
                ? (texto(e.getNombre()) + " " + texto(e.getApellido())).trim()
                : "Empleado no encontrado";
        String rol = (er.getIdRol() != null && er.getIdRol().getNombre() != null)
                ? er.getIdRol().getNombre()
                : "Sin rol";
        return nombre + " (" + rol + ")";
    }

    /** Convierte EmpleadoRol <-> texto (UUID) para el AutoComplete. */
    public Converter<EmpleadoRol> getEmpleadoRolConverter() {
        return new Converter<EmpleadoRol>() {
            @Override
            public EmpleadoRol getAsObject(FacesContext ctx, UIComponent comp, String valor) {
                if (valor == null || valor.isBlank()) {
                    return null;
                }
                try {
                    return empleadosRol.get(UUID.fromString(valor));
                } catch (IllegalArgumentException ex) {
                    return null;
                }
            }

            @Override
            public String getAsString(FacesContext ctx, UIComponent comp, EmpleadoRol er) {
                return (er == null || er.getIdEmpleadoRol() == null) ? "" : er.getIdEmpleadoRol().toString();
            }
        };
    }

    // ================= Utilidades =================

    private boolean estaDisponible(EmpleadoRol er) {
        Empleado e = (er.getIdEmpleado() != null) ? empleados.get(er.getIdEmpleado()) : null;
        return Boolean.TRUE.equals(er.getActivo())
                && e != null
                && Boolean.TRUE.equals(e.getActivo());
    }

    private static String texto(String s) {
        return s != null ? s : "";
    }

    /** Minusculas y sin tildes: "José" -> "jose". */
    private static String normalizar(String s) {
        if (s == null) {
            return "";
        }
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .trim();
    }
}