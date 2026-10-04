package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.DescuentoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.DescuentoProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoRolDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.OrdenDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.OrdenProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.DescuentoProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Empleado;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Orden;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.OrdenProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;

@Named("ordenModel")
@ViewScoped
public class OrdenModel extends AbstractModel<Orden, UUID> {

    private static final long serialVersionUID = 1L;
    private static final int MAX_SUGERENCIAS = 10;

    /**
     * Único rol que puede tomar órdenes (según la rúbrica).
     */
    private static final String ROL_PERMITIDO = "camarero";

    /**
     * Las observaciones de un producto con descuento empiezan con este texto.
     */
    private static final String PREFIJO_DESCUENTO = "Descuento ";

    @Inject
    private OrdenDAO ordenDAO;

    @Inject
    private EmpleadoRolDAO empleadoRolDAO;

    @Inject
    private EmpleadoDAO empleadoDAO;

    @Inject
    private OrdenProductoDAO ordenProductoDAO;

    @Inject
    private ProductoDAO productoDAO;

    @Inject
    private DescuentoDAO descuentoDAO;

    @Inject
    private DescuentoProductoDAO descuentoProductoDAO;

    // Se cargan una sola vez al abrir la pagina
    private Map<UUID, Empleado> empleados = new HashMap<>();
    private Map<UUID, EmpleadoRol> empleadosRol = new HashMap<>();

    // ===== Pestaña "Productos" =====
    /**
     * Productos de la orden seleccionada.
     */
    private List<OrdenProducto> productosOrden = new ArrayList<>();

    /**
     * Producto elegido en el combo para agregar.
     */
    private Producto productoSeleccionado;

    /**
     * Descuento elegido en el combo para aplicar.
     */
    private Descuento descuentoSeleccionado;

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

    // ================= Pestaña "Datos de la orden" =================
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

    @Override
    public void btnEliminarHandler() {
        if (registro != null && ordenProductoDAO.countByOrden(registro) > 0) {
            mensaje(FacesMessage.SEVERITY_WARN, "No se puede eliminar: la orden tiene productos.");
            return;
        }
        super.btnEliminarHandler();
    }

    private boolean esValida() {
        if (registro == null) {
            return true;
        }
        if (registro.getFechaCreacion() == null) {
            mensaje(FacesMessage.SEVERITY_WARN, "La fecha de la orden es obligatoria.");
            return false;
        }
        if (registro.getIdEmpleadoRol() == null || !esCamarero(registro.getIdEmpleadoRol())) {
            mensaje(FacesMessage.SEVERITY_WARN, "Solo un empleado con rol camarero puede tomar la orden.");
            return false;
        }
        return true;
    }

    // ================= Pestaña "Productos" =================
    /**
     * Al seleccionar una orden, carga sus productos.
     */
    @Override
    public void seleccionarRegistro(Orden entidad) {
        super.seleccionarRegistro(entidad);
        productoSeleccionado = null;
        descuentoSeleccionado = null;
        cargarProductos();
    }

    private void cargarProductos() {
        productosOrden = (registro == null)
                ? new ArrayList<>()
                : new ArrayList<>(ordenProductoDAO.findByOrden(registro));
    }

    /**
     * Agrega a la orden el producto elegido, con su precio sugerido.
     */
    public void btnAgregarProductoHandler() {
        if (productoSeleccionado == null) {
            mensaje(FacesMessage.SEVERITY_WARN, "Seleccione un producto.");
            return;
        }
        OrdenProducto op = new OrdenProducto(UUID.randomUUID());
        op.setIdOrden(registro);
        op.setIdProducto(productoSeleccionado);
        op.setPrecio(productoSeleccionado.getPrecioSugerido());
        ordenProductoDAO.save(op);
        mensaje(FacesMessage.SEVERITY_INFO, "Producto " + productoSeleccionado.getNombre() + " agregado.");
        productoSeleccionado = null;
        cargarProductos();
    }

    /**
     * Aplica el descuento elegido a los productos de la orden que: 1) estén
     * autorizados para ese descuento (DescuentoProducto), y 2) la fecha de la
     * orden esté dentro del rango del descuento (si no tiene fecha hasta, es
     * abierto y no vence). Baja el precio y escribe el detalle en las
     * observaciones.
     */
    public void btnAplicarDescuentoHandler() {
        if (descuentoSeleccionado == null) {
            mensaje(FacesMessage.SEVERITY_WARN, "Seleccione un descuento.");
            return;
        }
        Descuento d = descuentoSeleccionado;
        Date fechaOrden = registro.getFechaCreacion();

        // 1) ¿La fecha de la orden está dentro del rango del descuento?
        if (!dentroDeRango(fechaOrden, d.getFechaDesde(), d.getFechaHasta())) {
            mensaje(FacesMessage.SEVERITY_WARN, "El descuento " + d.getNombre()
                    + " no aplica: la fecha de la orden (" + formatear(fechaOrden)
                    + ") está fuera de su rango. No se aplicó a ningún producto.");
            return;
        }

        // 2) ¿Qué productos de la orden están autorizados para este descuento?
        List<DescuentoProducto> autorizados = descuentoProductoDAO.findByDescuento(d);
        int aplicados = 0;
        int yaTenian = 0;

        for (OrdenProducto op : productosOrden) {
            DescuentoProducto dp = buscarAutorizacion(autorizados, op.getIdProducto(), fechaOrden);
            if (dp == null || dp.getValor() == null || op.getPrecio() == null) {
                continue; // este producto no tiene este descuento
            }
            if (yaTieneDescuento(op)) {
                yaTenian++;
                continue; // no se aplican dos descuentos al mismo producto
            }
            if (ordenProductoDAO.countFacturas(op) > 0) {
                continue; // ya facturado, no se cambia
            }
            BigDecimal precioOriginal = op.getPrecio();
            BigDecimal monto = calcularMonto(precioOriginal, dp.getValor());
            op.setPrecio(precioOriginal.subtract(monto));
            op.setObservaciones(PREFIJO_DESCUENTO + d.getNombre()
                    + " (" + dp.getValor() + "%): -$" + monto
                    + " (precio original $" + precioOriginal + ")");
            ordenProductoDAO.update(op);
            aplicados++;
        }

        if (aplicados > 0) {
            mensaje(FacesMessage.SEVERITY_INFO, "Descuento " + d.getNombre()
                    + " aplicado a " + aplicados + " producto(s).");
        } else if (yaTenian > 0) {
            mensaje(FacesMessage.SEVERITY_WARN, "Los productos de este descuento ya tienen un descuento aplicado.");
        } else {
            mensaje(FacesMessage.SEVERITY_WARN, "El descuento " + d.getNombre()
                    + " no aplica a ningún producto de esta orden.");
        }
        descuentoSeleccionado = null;
        cargarProductos();
    }

    /**
     * Busca si el producto está autorizado para el descuento en la fecha de la
     * orden.
     */
    private DescuentoProducto buscarAutorizacion(List<DescuentoProducto> autorizados, Producto producto, Date fecha) {
        for (DescuentoProducto dp : autorizados) {
            if (producto != null && producto.equals(dp.getIdProducto())
                    && dentroDeRango(fecha, dp.getFechaDesde(), dp.getFechaHasta())) {
                return dp;
            }
        }
        return null;
    }

    /**
     * true si la fecha está entre desde y hasta. Si hasta es null, el rango es
     * abierto.
     */
    private boolean dentroDeRango(Date fecha, Date desde, Date hasta) {
        if (fecha == null) {
            return false;
        }
        boolean despuesDeInicio = (desde == null) || !fecha.before(desde);
        boolean antesDeFin = (hasta == null) || !fecha.after(hasta);
        return despuesDeInicio && antesDeFin;
    }

    /**
     * Monto a descontar. valor = porcentaje (10 = 10%). Si fuera monto fijo,
     * devolver BigDecimal.valueOf(valor).
     */
    private BigDecimal calcularMonto(BigDecimal precio, Integer valor) {
        return precio.multiply(BigDecimal.valueOf(valor))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private boolean yaTieneDescuento(OrdenProducto op) {
        return op.getObservaciones() != null && op.getObservaciones().startsWith(PREFIJO_DESCUENTO);
    }

    /**
     * Total de la orden (suma de precios, ya con descuentos).
     */
    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrdenProducto op : productosOrden) {
            if (op.getPrecio() != null) {
                total = total.add(op.getPrecio());
            }
        }
        return total;
    }

    /**
     * "Navidad (01/12/2026 - 31/12/2026)" o "Frecuente (desde 01/10/2026,
     * abierto)".
     */
    public String etiquetaDescuento(Descuento d) {
        if (d == null) {
            return "";
        }
        String rango = (d.getFechaHasta() == null)
                ? "desde " + formatear(d.getFechaDesde()) + ", abierto"
                : formatear(d.getFechaDesde()) + " - " + formatear(d.getFechaHasta());
        return d.getNombre() + " (" + rango + ")";
    }

    private static String formatear(Date fecha) {
        return (fecha == null) ? "sin fecha" : new SimpleDateFormat("dd/MM/yyyy").format(fecha);
    }

    // ================= AutoComplete (solo camareros) =================
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

    /**
     * "Nombre Apellido (Rol)" para la tabla y el AutoComplete.
     */
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

    /**
     * Solo camareros: asignación activa, empleado activo y rol activo llamado
     * "camarero".
     */
    private boolean estaDisponible(EmpleadoRol er) {
        Empleado e = (er.getIdEmpleado() != null) ? empleados.get(er.getIdEmpleado()) : null;
        return Boolean.TRUE.equals(er.getActivo())
                && e != null
                && Boolean.TRUE.equals(e.getActivo())
                && esCamarero(er);
    }

    private boolean esCamarero(EmpleadoRol er) {
        return er.getIdRol() != null
                && Boolean.TRUE.equals(er.getIdRol().getActivo())
                && normalizar(er.getIdRol().getNombre()).equals(ROL_PERMITIDO);
    }

    // ================= Utilidades =================
    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
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

    // ================= Getters / Setters =================
    /**
     * Solo productos activos para el combo.
     */
    public List<Producto> getListaProducto() {
        List<Producto> lista = new ArrayList<>();
        for (Producto p : productoDAO.findRange(0, 1000)) {
            if (Boolean.TRUE.equals(p.getActivo())) {
                lista.add(p);
            }
        }
        return lista;
    }

    /**
     * Solo los descuentos vigentes en la fecha de la orden.
     */
    public List<Descuento> getListaDescuento() {
        List<Descuento> lista = new ArrayList<>();
        Date fechaOrden = (registro != null) ? registro.getFechaCreacion() : null;
        for (Descuento d : descuentoDAO.findRange(0, 1000)) {
            if (dentroDeRango(fechaOrden, d.getFechaDesde(), d.getFechaHasta())) {
                lista.add(d);
            }
        }
        return lista;
    }

    public List<OrdenProducto> getProductosOrden() {
        return productosOrden;
    }

    public Producto getProductoSeleccionado() {
        return productoSeleccionado;
    }

    public void setProductoSeleccionado(Producto productoSeleccionado) {
        this.productoSeleccionado = productoSeleccionado;
    }

    public Descuento getDescuentoSeleccionado() {
        return descuentoSeleccionado;
    }

    public void setDescuentoSeleccionado(Descuento descuentoSeleccionado) {
        this.descuentoSeleccionado = descuentoSeleccionado;
    }
}
