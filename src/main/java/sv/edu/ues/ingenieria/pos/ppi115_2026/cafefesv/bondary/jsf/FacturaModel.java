package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.text.Normalizer;
import java.util.ArrayList;
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
        listaCaja = new ArrayList<>(cajaDAO.findByActivo(Boolean.TRUE));
        List<Empleado> todosLosEmpleados = empleadoDAO.findRange(0, 1000);
        if (todosLosEmpleados != null) {
            for (Empleado empleado : todosLosEmpleados) {
                empleados.put(empleado.getIdEmpleado(), empleado);
            }
        }
        List<EmpleadoRol> todasLasAsignaciones = empleadoRolDAO.findRange(0, 1000);
        if (todasLasAsignaciones != null) {
            for (EmpleadoRol asignacion : todasLasAsignaciones) {
                empleadosRol.put(asignacion.getIdEmpleadoRol(), asignacion);
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
        nueva.setEstado(FacturaDAO.PENDIENTE);
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
            registro.setEstado(FacturaDAO.PENDIENTE); // toda factura nueva inicia pendiente
            completarDatosPorDefecto();
        }
        if (sinError("Factura no válida", facturaDAO.validarFactura(registro))) {
            super.btnCrearHandler();
        }
    }

    @Override
    public void btnModificarHandler() {
        if (!sinError("No se puede modificar", facturaDAO.validarModificable(estadoOriginal))) {
            return;
        }
        if (registro != null) {
            completarDatosPorDefecto();
        }
        if (sinError("Factura no válida", facturaDAO.validarFactura(registro))) {
            super.btnModificarHandler();
        }
    }

    @Override
    public void btnEliminarHandler() {
        if (sinError("No se puede eliminar", facturaDAO.validarEliminar(registro))) {
            super.btnEliminarHandler();
        }
    }

    @Override
    public void btnCancelarHandler() {
        super.btnCancelarHandler();
        estadoOriginal = null;
    }

    /**
     * Arregla lo que el usuario dejó vacío en el formulario: sin cliente se
     * guarda como "Consumidor final" y sin fecha se usa la fecha actual.
     */
    private void completarDatosPorDefecto() {
        String cliente = (registro.getCliente() != null) ? registro.getCliente().trim() : "";
        registro.setCliente(cliente.isEmpty() ? CLIENTE_DEFECTO : cliente);
        if (registro.getFechaFacturacion() == null) {
            registro.setFechaFacturacion(new Date());
        }
    }

    // ================= AutoComplete del cajero (igual que en Orden) =================

    public List<EmpleadoRol> completarEmpleadoRol(String consulta) {
        String[] palabrasBuscadas = normalizar(consulta).split("\\s+");
        List<EmpleadoRol> resultado = new ArrayList<>();
        for (EmpleadoRol asignacion : empleadosRol.values()) {
            if (!estaDisponible(asignacion)) {
                continue;
            }
            String textoAsignacion = normalizar(obtenerTextoEmpleadoRol(asignacion));
            boolean coincide = true;
            for (String palabra : palabrasBuscadas) {
                if (!textoAsignacion.contains(palabra)) {
                    coincide = false;
                    break;
                }
            }
            if (coincide) {
                resultado.add(asignacion);
            }
        }
        resultado.sort((primera, segunda) -> obtenerTextoEmpleadoRol(primera).compareToIgnoreCase(obtenerTextoEmpleadoRol(segunda)));
        return resultado.size() > MAX_SUGERENCIAS ? resultado.subList(0, MAX_SUGERENCIAS) : resultado;
    }

    /** "Nombre Apellido (Rol)". */
    public String obtenerTextoEmpleadoRol(EmpleadoRol asignacion) {
        if (asignacion == null) {
            return "";
        }
        Empleado empleado = (asignacion.getIdEmpleado() != null) ? empleados.get(asignacion.getIdEmpleado()) : null;
        String nombre = (empleado != null)
                ? (texto(empleado.getNombre()) + " " + texto(empleado.getApellido())).trim()
                : "Empleado no encontrado";
        String rol = (asignacion.getIdRol() != null && asignacion.getIdRol().getNombre() != null)
                ? asignacion.getIdRol().getNombre()
                : "Sin rol";
        return nombre + " (" + rol + ")";
    }

    public Converter<EmpleadoRol> getEmpleadoRolConverter() {
        return new Converter<EmpleadoRol>() {
            @Override
            public EmpleadoRol getAsObject(FacesContext contexto, UIComponent componente, String valor) {
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
            public String getAsString(FacesContext contexto, UIComponent componente, EmpleadoRol asignacion) {
                return (asignacion == null || asignacion.getIdEmpleadoRol() == null) ? "" : asignacion.getIdEmpleadoRol().toString();
            }
        };
    }

    // ================= Utilidades =================

    /** Color de la etiqueta del estado en la tabla. */
    public String severidadEstado(String estadoFactura) {
        if (FacturaDAO.PAGADA.equals(estadoFactura)) {
            return "success";
        }
        if (FacturaDAO.ANULADA.equals(estadoFactura)) {
            return "danger";
        }
        return "warning";
    }

    // En el buscador solo salen asignaciones activas de empleados activos.
    private boolean estaDisponible(EmpleadoRol asignacion) {
        Empleado empleado = (asignacion.getIdEmpleado() != null) ? empleados.get(asignacion.getIdEmpleado()) : null;
        return Boolean.TRUE.equals(asignacion.getActivo())
                && empleado != null
                && Boolean.TRUE.equals(empleado.getActivo());
    }

    private static String texto(String valor) {
        return valor != null ? valor : "";
    }

    // Para el buscador: sin tildes, en minúsculas y sin espacios de los extremos.
    private static String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .trim();
    }

    // ================= Getters =================

    public List<Caja> getListaCaja() {
        return listaCaja;
    }

    public List<String> getEstados() {
        return FacturaDAO.ESTADOS;
    }
}