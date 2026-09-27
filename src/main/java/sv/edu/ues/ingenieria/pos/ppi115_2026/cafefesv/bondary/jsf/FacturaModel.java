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
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.CajaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoRolDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.FacturaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caja;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Empleado;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;

@Named("facturaModel")
@ViewScoped
public class FacturaModel extends AbstractModel<Factura, UUID> {

    private static final long serialVersionUID = 1L;

    public static final String PENDIENTE = "PENDIENTE";
    public static final String PAGADA = "PAGADA";
    public static final String ANULADA = "ANULADA";
    public static final List<String> ESTADOS = Arrays.asList(PENDIENTE, PAGADA, ANULADA);

    private static final String CLIENTE_DEFECTO = "Consumidor final";
    private static final int MAX_SUGERENCIAS = 10;

    @Inject
    private FacturaDAO facturaDAO;

    @Inject
    private CajaDAO cajaDAO;

    @Inject
    private EmpleadoRolDAO empleadoRolDAO;

    @Inject
    private EmpleadoDAO empleadoDAO;

    private List<Caja> listaCaja = new ArrayList<>();
    private Map<UUID, Empleado> empleados = new HashMap<>();
    private Map<UUID, EmpleadoRol> empleadosRol = new HashMap<>();

    /** Estado que tenia la factura en la BD al seleccionarla. */
    private String estadoOriginal;

    @PostConstruct
    public void init() {
        List<Caja> cajas = cajaDAO.findRange(0, 1000);
        if (cajas != null) {
            for (Caja c : cajas) {
                if (Boolean.TRUE.equals(c.getActivo())) {
                    listaCaja.add(c);
                }
            }
        }
        List<Empleado> emps = empleadoDAO.findRange(0, 1000);
        if (emps != null) {
            for (Empleado e : emps) {
                empleados.put(e.getIdEmpleado(), e);
            }
        }
        List<EmpleadoRol> ers = empleadoRolDAO.findRange(0, 1000);
        if (ers != null) {
            for (EmpleadoRol er : ers) {
                empleadosRol.put(er.getIdEmpleadoRol(), er);
            }
        }
        iniciar();
    }

    /** Carga las facturas y muestra primero las mas recientes. */
    @Override
    public void iniciar() {
        super.iniciar();
        if (registros != null) {
            registros = new ArrayList<>(registros);
            registros.sort(Comparator.comparing(Factura::getFechaFacturacion,
                    Comparator.nullsLast(Comparator.reverseOrder())));
        }
    }

    @Override
    protected GenericDAO<Factura, UUID> getDAO() {
        return facturaDAO;
    }

    @Override
    protected Factura crearNuevo() {
        Factura nueva = new Factura(UUID.randomUUID());
        nueva.setFechaFacturacion(new Date());
        nueva.setEstado(PENDIENTE);
        nueva.setCliente(CLIENTE_DEFECTO);
        estadoOriginal = null;
        return nueva;
    }

    @Override
    public void seleccionarRegistro(Factura entidad) {
        super.seleccionarRegistro(entidad);
        estadoOriginal = (registro != null) ? registro.getEstado() : null;
        // Si la caja esta inactiva, se agrega al combo para que se vea al modificar
        if (registro != null && registro.getIdCaja() != null && !listaCaja.contains(registro.getIdCaja())) {
            listaCaja.add(registro.getIdCaja());
        }
    }

    // ================= Crear / Modificar / Eliminar =================

    @Override
    public void btnCrearHandler() {
        if (registro != null) {
            registro.setEstado(PENDIENTE); // toda factura nueva inicia pendiente
        }
        if (esValida()) {
            super.btnCrearHandler();
        }
    }

    @Override
    public void btnModificarHandler() {
        if (ANULADA.equals(estadoOriginal)) {
            mensaje(FacesMessage.SEVERITY_WARN, "No se puede modificar una factura ANULADA.");
            return;
        }
        if (esValida()) {
            super.btnModificarHandler();
        }
    }

    @Override
    public void btnEliminarHandler() {
        if (registro != null
                && (facturaDAO.countProductos(registro) > 0 || facturaDAO.countPagos(registro) > 0)) {
            mensaje(FacesMessage.SEVERITY_WARN,
                    "No se puede eliminar: la factura tiene productos o pagos. Puede cambiar su estado a ANULADA.");
            return;
        }
        super.btnEliminarHandler();
    }

    @Override
    public void btnCancelarHandler() {
        super.btnCancelarHandler();
        estadoOriginal = null;
    }

    private boolean esValida() {
        if (registro == null) {
            return true; // AbstractModel muestra su propio mensaje
        }
        if (registro.getIdCaja() == null) {
            mensaje(FacesMessage.SEVERITY_WARN, "Debe seleccionar la caja.");
            return false;
        }
        if (registro.getIdEmpleadoRol() == null) {
            mensaje(FacesMessage.SEVERITY_WARN, "Debe seleccionar el cajero.");
            return false;
        }
        String cliente = (registro.getCliente() != null) ? registro.getCliente().trim() : "";
        registro.setCliente(cliente.isEmpty() ? CLIENTE_DEFECTO : cliente);
        if (registro.getCliente().length() > 155) {
            mensaje(FacesMessage.SEVERITY_WARN, "El nombre del cliente no puede superar 155 caracteres.");
            return false;
        }
        if (!ESTADOS.contains(registro.getEstado())) {
            mensaje(FacesMessage.SEVERITY_WARN, "El estado no es válido.");
            return false;
        }
        if (registro.getFechaFacturacion() == null) {
            registro.setFechaFacturacion(new Date());
        }
        return true;
    }

    // ================= AutoComplete del cajero (igual que en Orden) =================

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

    /** "Nombre Apellido (Rol)". */
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

    /** Color de la etiqueta del estado en la tabla. */
    public String severidadEstado(String estado) {
        if (PAGADA.equals(estado)) {
            return "success";
        }
        if (ANULADA.equals(estado)) {
            return "danger";
        }
        return "warning";
    }

    private boolean estaDisponible(EmpleadoRol er) {
        Empleado e = (er.getIdEmpleado() != null) ? empleados.get(er.getIdEmpleado()) : null;
        return Boolean.TRUE.equals(er.getActivo())
                && e != null
                && Boolean.TRUE.equals(e.getActivo());
    }

    private static String texto(String s) {
        return s != null ? s : "";
    }

    private static String normalizar(String s) {
        if (s == null) {
            return "";
        }
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .trim();
    }

    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
    }

    // ================= Getters =================

    public List<Caja> getListaCaja() {
        return listaCaja;
    }

    public List<String> getEstados() {
        return ESTADOS;
    }
}