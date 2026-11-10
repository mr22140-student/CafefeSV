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

    /**
     * Todas las facturas (para el filtro).
     */
    private List<Factura> listaFactura = new ArrayList<>();

    /**
     * Solo facturas PENDIENTES (para el formulario).
     */
    private List<Factura> listaFacturaPendiente = new ArrayList<>();

    /**
     * Productos de ordenes que aun no se han facturado.
     */
    private List<OrdenProducto> listaOrdenProducto = new ArrayList<>();

    private Map<UUID, Empleado> empleados = new HashMap<>();

    /**
     * Factura elegida arriba de la tabla; null = todas.
     */
    private Factura facturaFiltro;

    /**
     * Factura que tenia el registro al seleccionarlo (para validar al
     * modificar/eliminar).
     */
    private Factura facturaOriginal;

    @PostConstruct
    public void init() {
        List<Empleado> todosLosEmpleados = empleadoDAO.findRange(0, 1000);
        if (todosLosEmpleados != null) {
            for (Empleado empleado : todosLosEmpleados) {
                empleados.put(empleado.getIdEmpleado(), empleado);
            }
        }

        List<Factura> facturas = facturaDAO.findRange(0, 1000);
        if (facturas != null) {
            listaFactura = new ArrayList<>(facturas);
            listaFactura.sort(Comparator.comparing(Factura::getFechaFacturacion,
                    Comparator.nullsLast(Comparator.reverseOrder())));
        }
        listaFacturaPendiente = new ArrayList<>(facturaDAO.buscarPendientes());

        iniciar();
    }

    /**
     * Carga la tabla (filtrada si hay factura elegida) y los productos
     * disponibles.
     */
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
        if (facturaFiltro != null && FacturaDAO.PENDIENTE.equals(facturaFiltro.getEstado())) {
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

    /**
     * Al elegir el producto de la orden, copia su precio (se puede editar).
     */
    public void ordenProductoSeleccionadoHandler() {
        if (registro != null && registro.getIdOrdenProducto() != null) {
            registro.setPrecio(registro.getIdOrdenProducto().getPrecio());
        }
    }

    // ================= Crear / Modificar / Eliminar =================
    @Override
    public void btnCrearHandler() {
        if (sinError("Producto no válido", facturaOrdenProductoDAO.validarDetalle(registro))) {
            super.btnCrearHandler();
        }
    }

    @Override
    public void btnModificarHandler() {
        if (sinError("No se puede modificar", facturaOrdenProductoDAO.validarFacturaEditable(facturaOriginal))
                && sinError("Producto no válido", facturaOrdenProductoDAO.validarDetalle(registro))) {
            super.btnModificarHandler();
        }
    }

    @Override
    public void btnEliminarHandler() {
        Factura facturaDelRegistro = (registro != null) ? registro.getIdFactura() : null;
        if (sinError("No se puede eliminar", facturaOrdenProductoDAO.validarFacturaEditable(facturaDelRegistro))) {
            super.btnEliminarHandler();
        }
    }

    @Override
    public void btnCancelarHandler() {
        super.btnCancelarHandler();
        facturaOriginal = null;
    }

    // ================= Etiquetas =================
    /**
     * /** "dd/MM/yyyy HH:mm - Cliente (ESTADO)".
     */
    public String etiquetaFactura(Factura factura) {
        if (factura == null) {
            return "Sin asignación";
        }
        String fecha = (factura.getFechaFacturacion() != null)
                ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format(factura.getFechaFacturacion())
                : "Sin fecha";
        String cliente = (factura.getCliente() != null) ? factura.getCliente() : "Sin cliente";
        return fecha + " - " + cliente + " (" + factura.getEstado() + ")";
    }

    /**
     * "Producto - Orden dd/MM HH:mm - Empleado".
     */
    public String etiquetaOrdenProducto(OrdenProducto detalleOrden) {
        if (detalleOrden == null) {
            return "Sin asignación";
        }
        String producto = (detalleOrden.getIdProducto() != null) ? detalleOrden.getIdProducto().getNombre() : "Sin producto";
        return producto + " - " + etiquetaOrden(detalleOrden.getIdOrden());
    }

    /**
     * "Orden dd/MM HH:mm - Nombre Apellido".
     */
    public String etiquetaOrden(Orden orden) {
        if (orden == null) {
            return "Sin orden";
        }
        String fecha = (orden.getFechaCreacion() != null)
                ? new SimpleDateFormat("dd/MM HH:mm").format(orden.getFechaCreacion())
                : "sin fecha";
        String nombreEmpleado = "";
        EmpleadoRol asignacion = orden.getIdEmpleadoRol();
        if (asignacion != null && asignacion.getIdEmpleado() != null) {
            Empleado empleado = empleados.get(asignacion.getIdEmpleado());
            if (empleado != null) {
                nombreEmpleado = " - " + (texto(empleado.getNombre()) + " " + texto(empleado.getApellido())).trim();
            }
        }
        return "Orden " + fecha + nombreEmpleado;
    }

    /**
     * Suma de lo que se ve en la tabla (util al filtrar por factura).
     */
    public BigDecimal getTotal() {
        return facturaOrdenProductoDAO.calcularTotal(registros);
    }

    private static String texto(String valor) {
        return valor != null ? valor : "";
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
