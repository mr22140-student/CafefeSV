package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.FacturaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.FacturaOrdenProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Empleado;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.FacturaOrdenProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Orden;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.OrdenProducto;

@Named("facturaOrdenProductoModel")
@ViewScoped
public class FacturaOrdenProductoModel extends AbstractModel<FacturaOrdenProducto, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private FacturaOrdenProductoDAO facturaOrdenProductoDAO;

    @Inject
    private FacturaDAO facturaDAO;

    @Inject
    private EmpleadoDAO empleadoDAO;

    /** Todas las facturas (para el filtro). */
    private List<Factura> listaFactura = new ArrayList<>();

    /** Solo facturas PENDIENTES (para el formulario). */
    private List<Factura> listaFacturaPendiente = new ArrayList<>();

    /** Productos de ordenes que aun no se han facturado. */
    private List<OrdenProducto> listaOrdenProducto = new ArrayList<>();

    private Map<UUID, Empleado> empleados = new HashMap<>();

    /** Factura elegida arriba de la tabla; null = todas. */
    private Factura facturaFiltro;

    /** Factura que tenia el registro al seleccionarlo (para validar al modificar/eliminar). */
    private Factura facturaOriginal;

    @PostConstruct
    public void init() {
        List<Empleado> emps = empleadoDAO.findRange(0, 1000);
        if (emps != null) {
            for (Empleado e : emps) {
                empleados.put(e.getIdEmpleado(), e);
            }
        }

        List<Factura> facturas = facturaDAO.findRange(0, 1000);
        if (facturas != null) {
            listaFactura = new ArrayList<>(facturas);
            listaFactura.sort(Comparator.comparing(Factura::getFechaFacturacion,
                    Comparator.nullsLast(Comparator.reverseOrder())));
            for (Factura f : listaFactura) {
                if (FacturaModel.PENDIENTE.equals(f.getEstado())) {
                    listaFacturaPendiente.add(f);
                }
            }
        }

        iniciar();
    }

    /** Carga la tabla (filtrada si hay factura elegida) y los productos disponibles. */
    @Override
    public void iniciar() {
        if (facturaFiltro != null) {
            registros = facturaOrdenProductoDAO.findByFactura(facturaFiltro);
            estado = ESTADO_NINGUNO;
        } else {
            super.iniciar();
        }
        listaOrdenProducto = new ArrayList<>(facturaOrdenProductoDAO.findOrdenProductoDisponibles());
        listaOrdenProducto.sort(Comparator.comparing(this::etiquetaOrdenProducto, String.CASE_INSENSITIVE_ORDER));
    }

    @Override
    protected GenericDAO<FacturaOrdenProducto, UUID> getDAO() {
        return facturaOrdenProductoDAO;
    }

    @Override
    protected FacturaOrdenProducto crearNuevo() {
        FacturaOrdenProducto nuevo = new FacturaOrdenProducto(UUID.randomUUID());
        // Si la factura filtrada esta pendiente, ya viene seleccionada
        if (facturaFiltro != null && FacturaModel.PENDIENTE.equals(facturaFiltro.getEstado())) {
            nuevo.setIdFactura(facturaFiltro);
        }
        facturaOriginal = null;
        return nuevo;
    }

    @Override
    public void seleccionarRegistro(FacturaOrdenProducto entidad) {
        super.seleccionarRegistro(entidad);
        if (registro == null) {
            return;
        }
        facturaOriginal = registro.getIdFactura();
        // Para que se vean en los combos al modificar
        if (registro.getIdOrdenProducto() != null && !listaOrdenProducto.contains(registro.getIdOrdenProducto())) {
            listaOrdenProducto.add(0, registro.getIdOrdenProducto());
        }
        if (registro.getIdFactura() != null && !listaFacturaPendiente.contains(registro.getIdFactura())) {
            listaFacturaPendiente.add(0, registro.getIdFactura());
        }
    }

    // ================= Handlers de la vista =================

    public void filtrarHandler() {
        iniciar();
    }

    /** Al elegir el producto de la orden, copia su precio (se puede editar). */
    public void ordenProductoSeleccionadoHandler() {
        if (registro != null && registro.getIdOrdenProducto() != null) {
            registro.setPrecio(registro.getIdOrdenProducto().getPrecio());
        }
    }

    // ================= Crear / Modificar / Eliminar =================

    @Override
    public void btnCrearHandler() {
        if (esValido()) {
            super.btnCrearHandler();
        }
    }

    @Override
    public void btnModificarHandler() {
        if (!facturaEditable(facturaOriginal)) {
            return;
        }
        if (esValido()) {
            super.btnModificarHandler();
        }
    }

    @Override
    public void btnEliminarHandler() {
        if (registro != null && !facturaEditable(registro.getIdFactura())) {
            return;
        }
        super.btnEliminarHandler();
    }

    @Override
    public void btnCancelarHandler() {
        super.btnCancelarHandler();
        facturaOriginal = null;
    }

    private boolean esValido() {
        if (registro == null) {
            return true; // AbstractModel muestra su propio mensaje
        }
        if (registro.getIdFactura() == null) {
            mensaje(FacesMessage.SEVERITY_WARN, "Debe seleccionar la factura.");
            return false;
        }
        if (!facturaEditable(registro.getIdFactura())) {
            return false;
        }
        if (registro.getIdOrdenProducto() == null) {
            mensaje(FacesMessage.SEVERITY_WARN, "Debe seleccionar el producto de la orden.");
            return false;
        }
        if (registro.getPrecio() == null || registro.getPrecio().signum() < 0) {
            mensaje(FacesMessage.SEVERITY_WARN, "El precio debe ser mayor o igual a cero.");
            return false;
        }
        // No facturar dos veces el mismo producto de una orden
        for (FacturaOrdenProducto otro : facturaOrdenProductoDAO.findByOrdenProducto(registro.getIdOrdenProducto())) {
            if (!otro.getIdFacturaOrdenProducto().equals(registro.getIdFacturaOrdenProducto())) {
                mensaje(FacesMessage.SEVERITY_WARN, "Ese producto de la orden ya está facturado.");
                return false;
            }
        }
        return true;
    }

    /** Solo se pueden tocar productos de facturas PENDIENTES. */
    private boolean facturaEditable(Factura f) {
        if (f != null && !FacturaModel.PENDIENTE.equals(f.getEstado())) {
            mensaje(FacesMessage.SEVERITY_WARN,
                    "La factura está " + f.getEstado() + "; solo se pueden cambiar facturas PENDIENTES.");
            return false;
        }
        return true;
    }

    // ================= Etiquetas =================

    /** "dd/MM/yyyy HH:mm - Cliente (ESTADO)". */
    public String etiquetaFactura(Factura f) {
        if (f == null) {
            return "Sin asignación";
        }
        String fecha = (f.getFechaFacturacion() != null)
                ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format(f.getFechaFacturacion())
                : "Sin fecha";
        String cliente = (f.getCliente() != null) ? f.getCliente() : "Sin cliente";
        return fecha + " - " + cliente + " (" + f.getEstado() + ")";
    }

    /** "Producto - Orden dd/MM HH:mm - Empleado". */
    public String etiquetaOrdenProducto(OrdenProducto op) {
        if (op == null) {
            return "Sin asignación";
        }
        String producto = (op.getIdProducto() != null) ? op.getIdProducto().getNombre() : "Sin producto";
        return producto + " - " + etiquetaOrden(op.getIdOrden());
    }

    /** "Orden dd/MM HH:mm - Nombre Apellido". */
    public String etiquetaOrden(Orden o) {
        if (o == null) {
            return "Sin orden";
        }
        String fecha = (o.getFechaCreacion() != null)
                ? new SimpleDateFormat("dd/MM HH:mm").format(o.getFechaCreacion())
                : "sin fecha";
        String empleado = "";
        EmpleadoRol er = o.getIdEmpleadoRol();
        if (er != null && er.getIdEmpleado() != null) {
            Empleado e = empleados.get(er.getIdEmpleado());
            if (e != null) {
                empleado = " - " + (texto(e.getNombre()) + " " + texto(e.getApellido())).trim();
            }
        }
        return "Orden " + fecha + empleado;
    }

    /** Suma de lo que se ve en la tabla (util al filtrar por factura). */
    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        if (registros != null) {
            for (FacturaOrdenProducto fop : registros) {
                if (fop.getPrecio() != null) {
                    total = total.add(fop.getPrecio());
                }
            }
        }
        return total;
    }

    private static String texto(String s) {
        return s != null ? s : "";
    }

    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
    }

    // ================= Getters / Setters =================

    public List<Factura> getListaFactura() {
        return listaFactura;
    }

    public List<Factura> getListaFacturaPendiente() {
        return listaFacturaPendiente;
    }

    public List<OrdenProducto> getListaOrdenProducto() {
        return listaOrdenProducto;
    }

    public Factura getFacturaFiltro() {
        return facturaFiltro;
    }

    public void setFacturaFiltro(Factura facturaFiltro) {
        this.facturaFiltro = facturaFiltro;
    }
}