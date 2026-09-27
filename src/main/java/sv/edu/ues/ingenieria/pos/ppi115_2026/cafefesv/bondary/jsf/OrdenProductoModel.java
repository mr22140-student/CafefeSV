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
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.OrdenDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.OrdenProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Empleado;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Orden;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.OrdenProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;

@Named("ordenProductoModel")
@ViewScoped
public class OrdenProductoModel extends AbstractModel<OrdenProducto, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private OrdenProductoDAO ordenProductoDAO;

    @Inject
    private OrdenDAO ordenDAO;

    @Inject
    private ProductoDAO productoDAO;

    @Inject
    private EmpleadoDAO empleadoDAO;

    private List<Orden> listaOrden = new ArrayList<>();
    private List<Producto> listaProducto = new ArrayList<>();
    private Map<UUID, Empleado> empleados = new HashMap<>();

    /** Orden elegida arriba de la tabla; null = todas. */
    private Orden ordenFiltro;

    /** Cuantas unidades del producto agregar de una vez (solo al crear). */
    private Integer cantidad = 1;

    @PostConstruct
    public void init() {
        List<Empleado> emps = empleadoDAO.findRange(0, 1000);
        if (emps != null) {
            for (Empleado e : emps) {
                empleados.put(e.getIdEmpleado(), e);
            }
        }

        List<Orden> ordenes = ordenDAO.findRange(0, 1000);
        if (ordenes != null) {
            listaOrden = new ArrayList<>(ordenes);
            // Las mas recientes primero
            listaOrden.sort(Comparator.comparing(Orden::getFechaCreacion,
                    Comparator.nullsLast(Comparator.reverseOrder())));
        }

        List<Producto> productos = productoDAO.findRange(0, 1000);
        if (productos != null) {
            for (Producto p : productos) {
                if (Boolean.TRUE.equals(p.getActivo())) {
                    listaProducto.add(p);
                }
            }
        }

        iniciar();
    }

    /** Si hay una orden elegida, la tabla solo muestra sus productos. */
    @Override
    public void iniciar() {
        if (ordenFiltro != null) {
            registros = ordenProductoDAO.findByOrden(ordenFiltro);
            estado = ESTADO_NINGUNO;
        } else {
            super.iniciar();
        }
    }

    @Override
    protected GenericDAO<OrdenProducto, UUID> getDAO() {
        return ordenProductoDAO;
    }

    @Override
    protected OrdenProducto crearNuevo() {
        OrdenProducto nuevo = new OrdenProducto(UUID.randomUUID());
        nuevo.setIdOrden(ordenFiltro); // si hay una orden filtrada, ya viene seleccionada
        cantidad = 1;
        return nuevo;
    }

    @Override
    public void seleccionarRegistro(OrdenProducto entidad) {
        super.seleccionarRegistro(entidad);
        // Si el producto ya no esta activo, se agrega al combo para que se vea al modificar
        if (registro != null && registro.getIdProducto() != null
                && !listaProducto.contains(registro.getIdProducto())) {
            listaProducto.add(registro.getIdProducto());
        }
    }

    // ================= Handlers de la vista =================

    /** Al cambiar la orden de arriba, recarga la tabla. */
    public void filtrarHandler() {
        iniciar();
    }

    /** Al elegir un producto, copia su precio sugerido (se puede editar). */
    public void productoSeleccionadoHandler() {
        if (registro != null && registro.getIdProducto() != null) {
            registro.setPrecio(registro.getIdProducto().getPrecioSugerido());
        }
    }

    // ================= Crear / Modificar / Eliminar =================

    @Override
    public void btnCrearHandler() {
        if (!esValido()) {
            return;
        }
        int n = (cantidad == null || cantidad < 1) ? 1 : cantidad;
        if (n == 1) {
            super.btnCrearHandler();
            return;
        }
        // Varias unidades: se guarda una fila por unidad
        try {
            for (int i = 0; i < n; i++) {
                OrdenProducto op = (i == 0) ? registro : copiar(registro);
                ordenProductoDAO.save(op);
            }
            mensaje(FacesMessage.SEVERITY_INFO, n + " productos agregados a la orden");
            registro = null;
            cantidad = 1;
            iniciar();
        } catch (Exception ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron agregar todos los productos");
        }
    }

    @Override
    public void btnModificarHandler() {
        if (estaFacturado()) {
            mensaje(FacesMessage.SEVERITY_WARN, "No se puede modificar: este producto ya está facturado.");
            return;
        }
        if (esValido()) {
            super.btnModificarHandler();
        }
    }

    @Override
    public void btnEliminarHandler() {
        if (estaFacturado()) {
            mensaje(FacesMessage.SEVERITY_WARN, "No se puede eliminar: este producto ya está facturado.");
            return;
        }
        super.btnEliminarHandler();
    }

    private boolean esValido() {
        if (registro == null) {
            return true; // AbstractModel muestra su propio mensaje
        }
        if (registro.getIdOrden() == null) {
            mensaje(FacesMessage.SEVERITY_WARN, "Debe seleccionar la orden.");
            return false;
        }
        if (registro.getIdProducto() == null) {
            mensaje(FacesMessage.SEVERITY_WARN, "Debe seleccionar el producto.");
            return false;
        }
        if (registro.getPrecio() == null || registro.getPrecio().signum() < 0) {
            mensaje(FacesMessage.SEVERITY_WARN, "El precio debe ser mayor o igual a cero.");
            return false;
        }
        return true;
    }

    private boolean estaFacturado() {
        return registro != null && ordenProductoDAO.countFacturas(registro) > 0;
    }

    private OrdenProducto copiar(OrdenProducto base) {
        OrdenProducto c = new OrdenProducto(UUID.randomUUID());
        c.setIdOrden(base.getIdOrden());
        c.setIdProducto(base.getIdProducto());
        c.setPrecio(base.getPrecio());
        c.setObservaciones(base.getObservaciones());
        return c;
    }

    // ================= Etiquetas =================

    /** "dd/MM/yyyy HH:mm - Nombre Apellido" para combos y tabla. */
    public String etiquetaOrden(Orden o) {
        if (o == null) {
            return "Sin asignación";
        }
        String fecha = (o.getFechaCreacion() != null)
                ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format(o.getFechaCreacion())
                : "Sin fecha";
        String empleado = "Sin empleado";
        EmpleadoRol er = o.getIdEmpleadoRol();
        if (er != null && er.getIdEmpleado() != null) {
            Empleado e = empleados.get(er.getIdEmpleado());
            if (e != null) {
                empleado = (texto(e.getNombre()) + " " + texto(e.getApellido())).trim();
            }
        }
        return fecha + " - " + empleado;
    }

    /** Suma de precios de lo que se ve en la tabla (util al filtrar por orden). */
    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        if (registros != null) {
            for (OrdenProducto op : registros) {
                if (op.getPrecio() != null) {
                    total = total.add(op.getPrecio());
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

    public List<Orden> getListaOrden() {
        return listaOrden;
    }

    public List<Producto> getListaProducto() {
        return listaProducto;
    }

    public Orden getOrdenFiltro() {
        return ordenFiltro;
    }

    public void setOrdenFiltro(Orden ordenFiltro) {
        this.ordenFiltro = ordenFiltro;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}