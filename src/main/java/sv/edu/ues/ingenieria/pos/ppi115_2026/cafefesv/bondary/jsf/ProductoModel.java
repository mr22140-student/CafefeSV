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
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoTipoProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoTipoProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.CaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoCaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoCaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoCaracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;

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

    @Inject
    private ProductoCaracteristicaDAO pcDAO;

    @Inject
    private TipoCaracteristicaDAO tipoCaracteristicaDAO;

    @Inject
    private CaracteristicaDAO caracteristicaDAO;

    // Pestaña visible: 0 = Generalidades, 1 = Tipos de Producto, 2 = Características
    private int tabActiva = 0;

    // === Pestaña Tipos de Producto ===
    private List<ProductoTipoProducto> tiposDelProducto = new ArrayList<>(); // tabla de la pestaña
    private ProductoTipoProducto tipoFila;          // fila seleccionada en esa tabla
    private ProductoTipoProducto registroTipo;      // lo que se edita en el formulario de la pestaña
    private String estadoTipo = ESTADO_NINGUNO;     // NINGUNO, CREAR o MODIFICAR
    private TipoProducto tipoElegido;               // lo que se escoge en el diálogo
    private List<TipoProducto> listaTiposActivos = new ArrayList<>(); // opciones del diálogo
    // === Pestaña Características ===
    private List<ProductoCaracteristica> caracteristicasDelProducto = new ArrayList<>();
    private ProductoCaracteristica caracteristicaFila;
    private ProductoCaracteristica registroCaracteristica;
    private String estadoCaracteristica = ESTADO_NINGUNO;
    // Diálogo en cascada
    private TipoCaracteristica tipoCaracteristicaElegido;
    private Caracteristica caracteristicaElegida;
    private List<TipoCaracteristica> listaTiposCaracteristicaActivos = new ArrayList<>();
    private List<Caracteristica> listaCaracteristicasDelTipo = new ArrayList<>();

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
        cancelarCaracteristica();
    }

    @Override
    public void btnCancelarHandler() {
        super.btnCancelarHandler();
        tabActiva = 0;
        cancelarTipo();
        cancelarCaracteristica();
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
    // === Pestaña Características ===

    /**
     * Carga de la base las características del producto actual.
     */
    private void cargarCaracteristicas() {
        if (registro != null) {
            caracteristicasDelProducto = new ArrayList<>(pcDAO.findByProducto(registro));
        } else {
            caracteristicasDelProducto = new ArrayList<>();
        }
    }

    /**
     * Botón "Nueva Característica".
     */
    public void nuevaCaracteristica() {
        registroCaracteristica = new ProductoCaracteristica(UUID.randomUUID());
        registroCaracteristica.setIdProducto(registro);
        estadoCaracteristica = ESTADO_CREAR;
    }

    /**
     * Clic en una fila de la tabla de características.
     */
    public void seleccionarCaracteristica() {
        if (caracteristicaFila != null) {
            registroCaracteristica = caracteristicaFila;
            estadoCaracteristica = ESTADO_MODIFICAR;
        }
    }

    /**
     * Botón "Cancelar Característica": vuelve a la tabla de la pestaña.
     */
    public void cancelarCaracteristica() {
        registroCaracteristica = null;
        caracteristicaFila = null;
        tipoCaracteristicaElegido = null;
        caracteristicaElegida = null;
        estadoCaracteristica = ESTADO_NINGUNO;
        cargarCaracteristicas();
    }

    /**
     * Botón "Seleccionar característica": prepara el diálogo solo con tipos
     * ACTIVOS.
     */
    public void abrirDialogoCaracteristica() {
        listaTiposCaracteristicaActivos = tipoCaracteristicaDAO.findAll().stream()
                .filter(t -> Boolean.TRUE.equals(t.getActivo()))
                .collect(Collectors.toList());
        caracteristicaElegida = (registroCaracteristica != null) ? registroCaracteristica.getIdCaracteristica() : null;
        tipoCaracteristicaElegido = (caracteristicaElegida != null) ? caracteristicaElegida.getIdTipoCaracteristica() : null;
        cargarCaracteristicasDelTipo();
    }

    /**
     * Al cambiar el tipo en el diálogo, se recarga el segundo combo.
     */
    public void onTipoCaracteristicaChange() {
        caracteristicaElegida = null;
        cargarCaracteristicasDelTipo();
    }

    /**
     * Características ACTIVAS del tipo elegido en el diálogo.
     */
    private void cargarCaracteristicasDelTipo() {
        if (tipoCaracteristicaElegido == null) {
            listaCaracteristicasDelTipo = new ArrayList<>();
            return;
        }
        listaCaracteristicasDelTipo = caracteristicaDAO.findByTipoCaracteristica(tipoCaracteristicaElegido).stream()
                .filter(c -> Boolean.TRUE.equals(c.getActivo()))
                .collect(Collectors.toList());
    }

    /**
     * Botón "Seleccionar" dentro del diálogo.
     */
    public void confirmarCaracteristica() {
        if (registroCaracteristica != null) {
            registroCaracteristica.setIdCaracteristica(caracteristicaElegida);
        }
    }

    /**
     * Botón "Guardar Característica".
     */
    public void guardarCaracteristica() {
        if (!caracteristicaValida()) {
            return;
        }
        try {
            if (ESTADO_CREAR.equals(estadoCaracteristica)) {
                pcDAO.save(registroCaracteristica);
            } else {
                pcDAO.update(registroCaracteristica);
            }
            mensaje(FacesMessage.SEVERITY_INFO, "Característica guardada", null);
            cancelarCaracteristica();
        } catch (Exception ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar la característica", null);
        }
    }

    /**
     * Botón "Eliminar" de la característica.
     */
    public void eliminarCaracteristica() {
        try {
            pcDAO.delete(registroCaracteristica);
            mensaje(FacesMessage.SEVERITY_INFO, "Característica eliminada", null);
            cancelarCaracteristica();
        } catch (Exception ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo eliminar la característica", null);
        }
    }

    /**
     * Validaciones: que haya característica, activa, con tipo activo, valor
     * correcto y sin repetir.
     */
    private boolean caracteristicaValida() {
        if (registroCaracteristica == null) {
            return false;
        }
        Caracteristica c = registroCaracteristica.getIdCaracteristica();
        if (c == null) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Falta la característica", "Seleccione una característica.");
            return false;
        }
        if (!Boolean.TRUE.equals(c.getActivo())) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Característica inactiva",
                    "\"" + c.getNombre() + "\" está inactiva. Seleccione otra.");
            return false;
        }
        TipoCaracteristica tipo = c.getIdTipoCaracteristica();
        if (tipo != null && !Boolean.TRUE.equals(tipo.getActivo())) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Tipo inactivo",
                    "El tipo \"" + tipo.getNombre() + "\" está inactivo. Seleccione otra característica.");
            return false;
        }
        String valor = registroCaracteristica.getValor() == null ? "" : registroCaracteristica.getValor().trim();
        if (valor.isEmpty()) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Falta el valor", "Escriba el valor de la característica.");
            return false;
        }
        if (!valorCumpleExpresion(valor, tipo)) {
            return false;
        }
        registroCaracteristica.setValor(valor);
        boolean duplicado = caracteristicasDelProducto.stream()
                .anyMatch(x -> !x.getIdProductoCaracteristica().equals(registroCaracteristica.getIdProductoCaracteristica())
                        && c.equals(x.getIdCaracteristica()));
        if (duplicado) {
            mensaje(FacesMessage.SEVERITY_WARN, "Registro duplicado", "El producto ya tiene esa característica.");
            return false;
        }
        return true;
    }

    /**
     * Revisa el valor contra la expresión regular del tipo (si el tipo no
     * tiene, acepta todo).
     */
    private boolean valorCumpleExpresion(String valor, TipoCaracteristica tipo) {
        if (tipo == null || tipo.getExpresionRegular() == null || tipo.getExpresionRegular().isBlank()) {
            return true;
        }
        try {
            if (!Pattern.matches(tipo.getExpresionRegular(), valor)) {
                mensaje(FacesMessage.SEVERITY_ERROR, "El valor no es válido",
                        "El valor no cumple la validación de " + tipo.getNombre() + ": " + tipo.getExpresionRegular());
                return false;
            }
            return true;
        } catch (PatternSyntaxException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Expresión regular mal configurada",
                    "Revise la expresión regular del tipo " + tipo.getNombre() + ".");
            return false;
        }
    }

    /**
     * Texto de ayuda con el formato esperado, para mostrarlo debajo del campo
     * Valor.
     */
    public String getFormatoValorCaracteristica() {
        if (registroCaracteristica == null || registroCaracteristica.getIdCaracteristica() == null
                || registroCaracteristica.getIdCaracteristica().getIdTipoCaracteristica() == null) {
            return "";
        }
        String regex = registroCaracteristica.getIdCaracteristica().getIdTipoCaracteristica().getExpresionRegular();
        return (regex == null || regex.isBlank()) ? "" : "Formato: " + regex;
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

    public List<ProductoCaracteristica> getCaracteristicasDelProducto() {
        return caracteristicasDelProducto;
    }

    public ProductoCaracteristica getCaracteristicaFila() {
        return caracteristicaFila;
    }

    public void setCaracteristicaFila(ProductoCaracteristica caracteristicaFila) {
        this.caracteristicaFila = caracteristicaFila;
    }

    public ProductoCaracteristica getRegistroCaracteristica() {
        return registroCaracteristica;
    }

    public String getEstadoCaracteristica() {
        return estadoCaracteristica;
    }

    public TipoCaracteristica getTipoCaracteristicaElegido() {
        return tipoCaracteristicaElegido;
    }

    public void setTipoCaracteristicaElegido(TipoCaracteristica tipoCaracteristicaElegido) {
        this.tipoCaracteristicaElegido = tipoCaracteristicaElegido;
    }

    public Caracteristica getCaracteristicaElegida() {
        return caracteristicaElegida;
    }

    public void setCaracteristicaElegida(Caracteristica caracteristicaElegida) {
        this.caracteristicaElegida = caracteristicaElegida;
    }

    public List<TipoCaracteristica> getListaTiposCaracteristicaActivos() {
        return listaTiposCaracteristicaActivos;
    }

    public List<Caracteristica> getListaCaracteristicasDelTipo() {
        return listaCaracteristicasDelTipo;
    }
}
