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
        if (sinError("Orden no válida", ordenDAO.validarOrden(registro))) {
            super.btnCrearHandler();
        }
    }

    @Override
    public void btnModificarHandler() {
        if (sinError("Orden no válida", ordenDAO.validarOrden(registro))) {
            super.btnModificarHandler();
        }
    }

    @Override
    public void btnEliminarHandler() {
        if (sinError("No se puede eliminar", ordenDAO.validarEliminar(registro))) {
            super.btnEliminarHandler();
        }
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
        if (!sinError("Producto no válido", ordenProductoDAO.validarProductoElegido(productoSeleccionado))) {
            return;
        }
        OrdenProducto nuevoDetalle = new OrdenProducto(UUID.randomUUID());
        nuevoDetalle.setIdOrden(registro);
        nuevoDetalle.setIdProducto(productoSeleccionado);
        nuevoDetalle.setPrecio(productoSeleccionado.getPrecioSugerido());
        ordenProductoDAO.save(nuevoDetalle);
        mensaje(FacesMessage.SEVERITY_INFO, "Producto " + productoSeleccionado.getNombre() + " agregado.");
        productoSeleccionado = null;
        cargarProductos();
    }

    /**
     * Aplica el descuento elegido a los productos de la orden que estén
     * autorizados para ese descuento en la fecha de la orden. Baja el precio y
     * escribe el detalle en las observaciones. Las reglas las decide el DAO;
     * aquí solo se recorre la orden y se guardan los cambios.
     */
    public void btnAplicarDescuentoHandler() {
        if (!sinError("Descuento no aplicado", ordenDAO.validarDescuentoEnFecha(registro, descuentoSeleccionado))) {
            return;
        }
        Descuento descuento = descuentoSeleccionado;
        int aplicados = 0;
        int yaTenianDescuento = 0;

        for (OrdenProducto detalle : productosOrden) {
            DescuentoProducto autorizacion = descuentoProductoDAO.buscarAutorizacion(
                    descuento, detalle.getIdProducto(), registro.getFechaCreacion());
            if (autorizacion == null || detalle.getPrecio() == null) {
                continue; // este producto no tiene este descuento
            }
            if (ordenProductoDAO.validarSinDescuento(detalle) != null) {
                yaTenianDescuento++;
                continue; // no se aplican dos descuentos al mismo producto
            }
            if (ordenProductoDAO.validarNoFacturado(detalle) != null) {
                continue; // ya facturado, no se cambia
            }
            BigDecimal precioOriginal = detalle.getPrecio();
            BigDecimal monto = ordenProductoDAO.calcularMontoDescuento(precioOriginal, autorizacion.getValor());
            detalle.setPrecio(precioOriginal.subtract(monto));
            detalle.setObservaciones(OrdenProductoDAO.PREFIJO_DESCUENTO + descuento.getNombre()
                    + " (" + autorizacion.getValor() + "%): -$" + monto
                    + " (precio original $" + precioOriginal + ")");
            ordenProductoDAO.update(detalle);
            aplicados++;
        }

        if (aplicados > 0) {
            mensaje(FacesMessage.SEVERITY_INFO, "Descuento " + descuento.getNombre()
                    + " aplicado a " + aplicados + " producto(s).");
        } else if (yaTenianDescuento > 0) {
            mensaje(FacesMessage.SEVERITY_WARN, "Los productos de este descuento ya tienen un descuento aplicado.");
        } else {
            mensaje(FacesMessage.SEVERITY_WARN, "El descuento " + descuento.getNombre()
                    + " no aplica a ningún producto de esta orden.");
        }
        descuentoSeleccionado = null;
        cargarProductos();
    }

    /**
     * Total de la orden (suma de precios, ya con descuentos).
     */
    public BigDecimal getTotal() {
        return ordenProductoDAO.calcularTotal(productosOrden);
    }

    /**
     * "Navidad (01/12/2026 - 31/12/2026)" o "Frecuente (desde 01/10/2026,
     * abierto)".
     */
    public String etiquetaDescuento(Descuento descuento) {
        if (descuento == null) {
            return "";
        }
        String rango = (descuento.getFechaHasta() == null)
                ? "desde " + formatear(descuento.getFechaDesde()) + ", abierto"
                : formatear(descuento.getFechaDesde()) + " - " + formatear(descuento.getFechaHasta());
        return descuento.getNombre() + " (" + rango + ")";
    }

    private static String formatear(Date fecha) {
        return (fecha == null) ? "sin fecha" : new SimpleDateFormat("dd/MM/yyyy").format(fecha);
    }

    // ================= AutoComplete (solo camareros) =================
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

    /**
     * "Nombre Apellido (Rol)" para la tabla y el AutoComplete.
     */
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

    /**
     * En el buscador solo salen camareros con la asignación activa y el
     * empleado activo. La regla de "camarero" la decide el DAO.
     */
    private boolean estaDisponible(EmpleadoRol asignacion) {
        Empleado empleado = (asignacion.getIdEmpleado() != null) ? empleados.get(asignacion.getIdEmpleado()) : null;
        return Boolean.TRUE.equals(asignacion.getActivo())
                && empleado != null
                && Boolean.TRUE.equals(empleado.getActivo())
                && ordenDAO.validarCamarero(asignacion) == null;
    }

    // ================= Utilidades =================
    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
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

    // ================= Getters / Setters =================
    /**
     * Solo productos activos para el combo.
     */
    public List<Producto> getListaProducto() {
        return productoDAO.findByActivo(Boolean.TRUE);
    }

    /**
     * Solo los descuentos vigentes en la fecha de la orden.
     */
    public List<Descuento> getListaDescuento() {
        Date fechaOrden = (registro != null) ? registro.getFechaCreacion() : null;
        return descuentoDAO.buscarVigentesEn(fechaOrden);
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