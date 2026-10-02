package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoTipoProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoTipoProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoProducto;

/**
 *
 * @author valen
 */
@Named("productoModel")
@ViewScoped
public class ProductoModel extends AbstractModel<Producto, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProductoDAO productoDAO;

    @Inject
    private ProductoTipoProductoDAO ptpDAO;

    @Inject
    private TipoProductoDAO tipoProductoDAO;

    // Pestaña visible: 0 = Generalidades, 1 = Tipos de Producto
    private int tabActiva = 0;

    // === Pestaña Tipos de Producto ===
    private List<ProductoTipoProducto> tiposDelProducto = new ArrayList<>(); // tabla de la pestaña
    private ProductoTipoProducto tipoFila;          // fila seleccionada en esa tabla
    private ProductoTipoProducto registroTipo;      // lo que se edita en el formulario de la pestaña
    private String estadoTipo = ESTADO_NINGUNO;     // NINGUNO, CREAR o MODIFICAR
    private TipoProducto tipoElegido;               // lo que se escoge en el diálogo
    private List<TipoProducto> listaTiposActivos = new ArrayList<>(); // opciones del diálogo

    @PostConstruct
    public void init() {
        iniciar();
    }

    @Override
    protected GenericDAO<Producto, UUID> getDAO() {
        return productoDAO;
    }

    @Override
    protected Producto crearNuevo() {
        Producto nuevo = new Producto(UUID.randomUUID());
        nuevo.setActivo(Boolean.TRUE);
        nuevo.setPrecioSugerido(BigDecimal.ZERO);
        return nuevo;
    }

    @Override
    public void btnCrearHandler() {
        if (!precioValido()) {
            return;
        }
        Producto creado = registro;
        super.btnCrearHandler();

        //Gracias al AbstractModel deja registro en null solo si se guardado bien
        if (registro == null && creado != null) {
            seleccionarRegistro(creado);
            tabActiva = 1;

        }
    }

    @Override
    public void btnModificarHandler() {
        if (precioValido()) {
            super.btnModificarHandler();
        }
    }

    @Override
    public void seleccionarRegistro(Producto entidad) {
        super.seleccionarRegistro(entidad);
        tabActiva = 0;
        cancelarTipo();
    }

    @Override
    public void btnCancelarHandler() {
        super.btnCancelarHandler();
        tabActiva = 0;
        cancelarTipo();
    }

    /**
     * El precio sugerido debe ser mayor que $0.00.
     */
    private boolean precioValido() {
        if (registro != null && registro.getPrecioSugerido() != null
                && registro.getPrecioSugerido().compareTo(BigDecimal.ZERO) <= 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Precio inválido", "El precio sugerido debe ser mayor que $0.00."));
            return false;
        }
        return true;
    }

    // === Pestaña Tipos de Producto ===
    /**
     * Carga de la base los tipos asignados al producto actual.
     */
    private void cargarTipos() {
        if (registro != null) {
            tiposDelProducto = new ArrayList<>(ptpDAO.findByProducto(registro));
        } else {
            tiposDelProducto = new ArrayList<>();
        }
    }

    /**
     * Botón "Nuevo Tipo de Producto".
     */
    public void nuevoTipo() {
        registroTipo = new ProductoTipoProducto(UUID.randomUUID());
        registroTipo.setProducto(registro);
        registroTipo.setFechaCreacion(new Date());
        estadoTipo = ESTADO_CREAR;
    }

    /**
     * Clic en una fila de la tabla de tipos.
     */
    public void seleccionarTipo() {
        if (tipoFila != null) {
            registroTipo = tipoFila;
            estadoTipo = ESTADO_MODIFICAR;
        }
    }

    /**
     * Botón "Cancelar Tipo de Producto": vuelve a la tabla de la pestaña.
     */
    public void cancelarTipo() {
        registroTipo = null;
        tipoFila = null;
        tipoElegido = null;
        estadoTipo = ESTADO_NINGUNO;
        cargarTipos();
    }

    /**
     * Botón "Seleccionar tipo": prepara el diálogo solo con tipos ACTIVOS.
     */
    public void abrirDialogoTipo() {
        listaTiposActivos = tipoProductoDAO.findByActivo(Boolean.TRUE);
        tipoElegido = (registroTipo != null) ? registroTipo.getIdTipoProducto() : null;
    }

    /**
     * Botón "Seleccionar" dentro del diálogo.
     */
    public void confirmarTipo() {
        if (registroTipo != null) {
            registroTipo.setIdTipoProducto(tipoElegido);
        }
    }

    /**
     * Botón "Guardar Tipo de Producto".
     */
    public void guardarTipo() {
        if (!tipoValido()) {
            return;
        }
        try {
            if (ESTADO_CREAR.equals(estadoTipo)) {
                ptpDAO.save(registroTipo);
            } else {
                ptpDAO.update(registroTipo);
            }
            mensaje(FacesMessage.SEVERITY_INFO, "Tipo de producto guardado", null);
            cancelarTipo();
        } catch (Exception ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar el tipo de producto", null);
        }
    }

    /**
     * Botón "Eliminar" del tipo.
     */
    public void eliminarTipo() {
        try {
            ptpDAO.delete(registroTipo);
            mensaje(FacesMessage.SEVERITY_INFO, "Tipo de producto eliminado", null);
            cancelarTipo();
        } catch (Exception ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo eliminar el tipo de producto", null);
        }
    }

    /**
     * Validaciones antes de guardar: que haya tipo, que esté activo y que no se
     * repita.
     */
    private boolean tipoValido() {
        if (registroTipo == null) {
            return false;
        }
        TipoProducto tipo = registroTipo.getIdTipoProducto();
        if (tipo == null) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Falta el tipo", "Seleccione un tipo de producto.");
            return false;
        }
        if (!Boolean.TRUE.equals(tipo.getActivo())) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Tipo inactivo",
                    "El tipo \"" + tipo.getNombre() + "\" está inactivo. Seleccione un tipo activo.");
            return false;
        }
        boolean duplicado = tiposDelProducto.stream()
                .anyMatch(x -> !x.getIdProductoTipoProducto().equals(registroTipo.getIdProductoTipoProducto())
                        && tipo.equals(x.getIdTipoProducto()));
        if (duplicado) {
            mensaje(FacesMessage.SEVERITY_WARN, "Registro duplicado", "El producto ya tiene asignado ese tipo.");
            return false;
        }
        return true;
    }

    /**
     * Atajo para no repetir tanto FacesContext...addMessage.
     */
    private void mensaje(FacesMessage.Severity severidad, String titulo, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, titulo, detalle));
    }

    public int getTabActiva() {
        return tabActiva;
    }

    public void setTabActiva(int tabActiva) {
        this.tabActiva = tabActiva;
    }

    public List<ProductoTipoProducto> getTiposDelProducto() {
        return tiposDelProducto;
    }

    public ProductoTipoProducto getTipoFila() {
        return tipoFila;
    }

    public void setTipoFila(ProductoTipoProducto tipoFila) {
        this.tipoFila = tipoFila;
    }

    public ProductoTipoProducto getRegistroTipo() {
        return registroTipo;
    }

    public String getEstadoTipo() {
        return estadoTipo;
    }

    public TipoProducto getTipoElegido() {
        return tipoElegido;
    }

    public void setTipoElegido(TipoProducto tipoElegido) {
        this.tipoElegido = tipoElegido;
    }

    public List<TipoProducto> getListaTiposActivos() {
        return listaTiposActivos;
    }
}
