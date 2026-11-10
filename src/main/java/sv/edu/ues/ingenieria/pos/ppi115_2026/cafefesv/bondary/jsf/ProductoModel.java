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
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.CaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoCaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoCaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoCaracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.DescuentoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.DescuentoProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoDescuentoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.DescuentoProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoDescuento;

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
    private ProductoTipoProductoDAO productoTipoProductoDAO;

    @Inject
    private TipoProductoDAO tipoProductoDAO;

    @Inject
    private ProductoCaracteristicaDAO productoCaracteristicaDAO;

    @Inject
    private TipoCaracteristicaDAO tipoCaracteristicaDAO;

    @Inject
    private CaracteristicaDAO caracteristicaDAO;

    @Inject
    private DescuentoProductoDAO descuentoProductoDAO;

    @Inject
    private TipoDescuentoDAO tipoDescuentoDAO;

    @Inject
    private DescuentoDAO descuentoDAO;

    // Pestaña visible: 0 = Generalidades, 1 = Tipos de Producto, 2 = Características, 3 = Descuentos
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
    // === Pestaña Descuentos ===
    private List<DescuentoProducto> descuentosDelProducto = new ArrayList<>();
    private DescuentoProducto descuentoFila;
    private DescuentoProducto registroDescuento;
    private String estadoDescuento = ESTADO_NINGUNO;
    // Diálogo en cascada
    private TipoDescuento tipoDescuentoElegido;
    private Descuento descuentoElegido;
    private List<TipoDescuento> listaTiposDescuentoActivos = new ArrayList<>();
    private List<Descuento> listaDescuentosDelTipo = new ArrayList<>();

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
        if (!sinError("Precio inválido", productoDAO.validarPrecio(registro))) {
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
        if (sinError("Precio inválido", productoDAO.validarPrecio(registro))) {
            super.btnModificarHandler();
        }
    }

    @Override
    public void btnEliminarHandler() {
        if (sinError("No se puede eliminar", productoDAO.validarEliminar(registro))) {
            super.btnEliminarHandler();
        }
    }

    @Override
    public void seleccionarRegistro(Producto entidad) {
        super.seleccionarRegistro(entidad);
        tabActiva = 0;
        cancelarTipo();
        cancelarCaracteristica();
        cancelarDescuento();
    }

    @Override
    public void btnCancelarHandler() {
        super.btnCancelarHandler();
        tabActiva = 0;
        cancelarTipo();
        cancelarCaracteristica();
        cancelarDescuento();
    }

    // === Pestaña Tipos de Producto ===
    /**
     * Carga de la base los tipos asignados al producto actual.
     */
    private void cargarTipos() {
        if (registro != null) {
            tiposDelProducto = new ArrayList<>(productoTipoProductoDAO.findByProducto(registro));
        } else {
            tiposDelProducto = new ArrayList<>();
        }
    }

    // Botón "Nuevo Tipo de Producto".
    public void nuevoTipo() {
        registroTipo = new ProductoTipoProducto(UUID.randomUUID());
        registroTipo.setProducto(registro);
        registroTipo.setFechaCreacion(new Date());
        estadoTipo = ESTADO_CREAR;
    }

    // Clic en una fila de la tabla de tipos.
    public void seleccionarTipo() {
        if (tipoFila != null) {
            registroTipo = tipoFila;
            estadoTipo = ESTADO_MODIFICAR;
        }
    }

    // Botón "Cancelar Tipo de Producto": vuelve a la tabla de la pestaña.
    public void cancelarTipo() {
        registroTipo = null;
        tipoFila = null;
        tipoElegido = null;
        estadoTipo = ESTADO_NINGUNO;
        cargarTipos();
    }

    //Botón "Seleccionar tipo": prepara el diálogo solo con tipos ACTIVOS.
    public void abrirDialogoTipo() {
        listaTiposActivos = tipoProductoDAO.findByActivo(Boolean.TRUE);
        tipoElegido = (registroTipo != null) ? registroTipo.getIdTipoProducto() : null;
    }

    // Botón "Seleccionar" dentro del diálogo.
    public void confirmarTipo() {
        if (registroTipo != null) {
            registroTipo.setIdTipoProducto(tipoElegido);
        }
    }

    // Botón "Guardar Tipo de Producto".
    public void guardarTipo() {
        if (registroTipo == null
                || !sinError("Tipo no válido", productoTipoProductoDAO.validarTipo(registroTipo))
                || !sinError("Registro duplicado", productoTipoProductoDAO.validarDuplicado(registroTipo))) {
            return;
        }
        try {
            if (ESTADO_CREAR.equals(estadoTipo)) {
                productoTipoProductoDAO.save(registroTipo);
            } else {
                productoTipoProductoDAO.update(registroTipo);
            }
            mensaje(FacesMessage.SEVERITY_INFO, "Tipo de producto guardado", null);
            cancelarTipo();
        } catch (Exception ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar el tipo de producto", null);
        }
    }

    // Botón "Eliminar" del tipo.
    public void eliminarTipo() {
        try {
            productoTipoProductoDAO.delete(registroTipo);
            mensaje(FacesMessage.SEVERITY_INFO, "Tipo de producto eliminado", null);
            cancelarTipo();
        } catch (Exception ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo eliminar el tipo de producto", null);
        }
    }
    // === Pestaña Características ===

    //Carga de la base las características del producto actual.
    private void cargarCaracteristicas() {
        if (registro != null) {
            caracteristicasDelProducto = new ArrayList<>(productoCaracteristicaDAO.findByProducto(registro));
        } else {
            caracteristicasDelProducto = new ArrayList<>();
        }
    }

    // Botón "Nueva Característica".
    public void nuevaCaracteristica() {
        registroCaracteristica = new ProductoCaracteristica(UUID.randomUUID());
        registroCaracteristica.setIdProducto(registro);
        estadoCaracteristica = ESTADO_CREAR;
    }

    // Clic en una fila de la tabla de características.
    public void seleccionarCaracteristica() {
        if (caracteristicaFila != null) {
            registroCaracteristica = caracteristicaFila;
            estadoCaracteristica = ESTADO_MODIFICAR;
        }
    }

    // Botón "Cancelar Característica": vuelve a la tabla de la pestaña.
    public void cancelarCaracteristica() {
        registroCaracteristica = null;
        caracteristicaFila = null;
        tipoCaracteristicaElegido = null;
        caracteristicaElegida = null;
        estadoCaracteristica = ESTADO_NINGUNO;
        cargarCaracteristicas();
    }

    //Botón "Seleccionar característica": prepara el diálogo solo con tipos ACTIVOS.
    public void abrirDialogoCaracteristica() {
        listaTiposCaracteristicaActivos = tipoCaracteristicaDAO.findByActivo(Boolean.TRUE);
        caracteristicaElegida = (registroCaracteristica != null) ? registroCaracteristica.getIdCaracteristica() : null;
        tipoCaracteristicaElegido = (caracteristicaElegida != null) ? caracteristicaElegida.getIdTipoCaracteristica() : null;
        cargarCaracteristicasDelTipo();
    }

    //Al cambiar el tipo en el diálogo, se recarga el segundo combo.
    public void onTipoCaracteristicaChange() {
        caracteristicaElegida = null;
        cargarCaracteristicasDelTipo();
    }

    //Características ACTIVAS del tipo elegido en el diálogo.
    private void cargarCaracteristicasDelTipo() {
        if (tipoCaracteristicaElegido == null) {
            listaCaracteristicasDelTipo = new ArrayList<>();
            return;
        }
        listaCaracteristicasDelTipo = caracteristicaDAO.buscarActivasPorTipo(tipoCaracteristicaElegido);
    }

    // Botón "Seleccionar" dentro del diálogo.
    public void confirmarCaracteristica() {
        if (registroCaracteristica != null) {
            registroCaracteristica.setIdCaracteristica(caracteristicaElegida);
        }
    }

    //Botón "Guardar Característica".
    public void guardarCaracteristica() {
        if (registroCaracteristica == null
                || !sinError("Característica no válida", productoCaracteristicaDAO.validarCaracteristica(registroCaracteristica))
                || !sinError("Valor no válido", productoCaracteristicaDAO.validarValor(registroCaracteristica))
                || !sinError("Registro duplicado", productoCaracteristicaDAO.validarDuplicado(registroCaracteristica))) {
            return;
        }
        try {
            if (ESTADO_CREAR.equals(estadoCaracteristica)) {
                productoCaracteristicaDAO.save(registroCaracteristica);
            } else {
                productoCaracteristicaDAO.update(registroCaracteristica);
            }
            mensaje(FacesMessage.SEVERITY_INFO, "Característica guardada", null);
            cancelarCaracteristica();
        } catch (Exception ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar la característica", null);
        }
    }

    //Botón "Eliminar" de la característica.
    public void eliminarCaracteristica() {
        try {
            productoCaracteristicaDAO.delete(registroCaracteristica);
            mensaje(FacesMessage.SEVERITY_INFO, "Característica eliminada", null);
            cancelarCaracteristica();
        } catch (Exception ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo eliminar la característica", null);
        }
    }

    //Texto de ayuda con el formato esperado, para mostrarlo debajo del campo Valor.
    public String getFormatoValorCaracteristica() {
        if (registroCaracteristica == null || registroCaracteristica.getIdCaracteristica() == null
                || registroCaracteristica.getIdCaracteristica().getIdTipoCaracteristica() == null) {
            return "";
        }
        String regex = registroCaracteristica.getIdCaracteristica().getIdTipoCaracteristica().getExpresionRegular();
        return (regex == null || regex.isBlank()) ? "" : "Formato: " + regex;
    }
    // === Pestaña Descuentos ===

    //Carga de la base los descuentos del producto actual.
    private void cargarDescuentos() {
        if (registro != null) {
            descuentosDelProducto = new ArrayList<>(descuentoProductoDAO.findByProducto(registro));
        } else {
            descuentosDelProducto = new ArrayList<>();
        }
    }

    // Botón "Nuevo Descuento".
    public void nuevoDescuento() {
        registroDescuento = new DescuentoProducto(UUID.randomUUID());
        registroDescuento.setIdProducto(registro);
        estadoDescuento = ESTADO_CREAR;
    }

    // Clic en una fila de la tabla de descuentos.
    public void seleccionarDescuento() {
        if (descuentoFila != null) {
            registroDescuento = descuentoFila;
            estadoDescuento = ESTADO_MODIFICAR;
        }
    }

    // Botón "Cancelar Descuento": vuelve a la tabla de la pestaña.
    public void cancelarDescuento() {
        registroDescuento = null;
        descuentoFila = null;
        tipoDescuentoElegido = null;
        descuentoElegido = null;
        estadoDescuento = ESTADO_NINGUNO;
        cargarDescuentos();
    }

    //Botón "Seleccionar descuento": prepara el diálogo solo con tipos ACTIVOS.
    public void abrirDialogoDescuento() {
        listaTiposDescuentoActivos = tipoDescuentoDAO.findByActivo(Boolean.TRUE);
        descuentoElegido = (registroDescuento != null) ? registroDescuento.getIdDescuento() : null;
        tipoDescuentoElegido = (descuentoElegido != null) ? descuentoElegido.getIdTipoDescuento() : null;
        cargarDescuentosDelTipo();
    }

    // Al cambiar el tipo en el diálogo, se recarga el segundo combo.
    public void onTipoDescuentoChange() {
        descuentoElegido = null;
        cargarDescuentosDelTipo();
    }

    //Descuentos NO VENCIDOS del tipo elegido en el diálogo.
    private void cargarDescuentosDelTipo() {
        listaDescuentosDelTipo = new ArrayList<>(descuentoDAO.findVigentesByTipoDescuento(tipoDescuentoElegido));
    }

    //Botón "Seleccionar" del diálogo: asigna el descuento y propone las fechas.
    public void confirmarDescuento() {
        if (registroDescuento == null || descuentoElegido == null) {
            return;
        }
        registroDescuento.setIdDescuento(descuentoElegido);
        Date ahora = new Date();
        Date inicioDescuento = descuentoElegido.getFechaDesde();
        // Inicio: hoy, salvo que el descuento empiece más adelante
        if (inicioDescuento != null && inicioDescuento.after(ahora)) {
            registroDescuento.setFechaDesde(inicioDescuento);
        } else {
            registroDescuento.setFechaDesde(ahora);
        }
        // Fin: el del descuento (vacío si el descuento es para siempre)
        registroDescuento.setFechaHasta(descuentoElegido.getFechaHasta());
    }

    // Botón "Guardar Descuento".
    public void guardarDescuento() {
        if (registroDescuento == null
                || !sinError("Descuento no válido", descuentoProductoDAO.validarDescuento(registroDescuento))
                || !sinError("Porcentaje no válido", descuentoProductoDAO.validarValor(registroDescuento))
                || !sinError("Fechas no válidas", descuentoProductoDAO.validarFechas(registroDescuento))
                || !sinError("Registro duplicado", descuentoProductoDAO.validarDuplicado(registroDescuento))) {
            return;
        }
        try {
            if (ESTADO_CREAR.equals(estadoDescuento)) {
                descuentoProductoDAO.save(registroDescuento);
            } else {
                descuentoProductoDAO.update(registroDescuento);
            }
            mensaje(FacesMessage.SEVERITY_INFO, "Descuento guardado", null);
            cancelarDescuento();
        } catch (Exception ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar el descuento", null);
        }
    }

    //Botón "Eliminar" del descuento.
    public void eliminarDescuento() {
        try {
            descuentoProductoDAO.delete(registroDescuento);
            mensaje(FacesMessage.SEVERITY_INFO, "Descuento eliminado", null);
            cancelarDescuento();
        } catch (Exception ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo eliminar el descuento", null);
        }
    }

    //Texto con el máximo permitido, para mostrarlo junto al porcentaje.
    public String getTextoMaximoDescuento() {
        if (registroDescuento == null || registroDescuento.getIdDescuento() == null
                || registroDescuento.getIdDescuento().getIdTipoDescuento() == null
                || registroDescuento.getIdDescuento().getIdTipoDescuento().getDescuentoMaximo() == null) {
            return "";
        }
        return "Máximo permitido: " + registroDescuento.getIdDescuento().getIdTipoDescuento().getDescuentoMaximo() + " %";
    }

    //Atajo para no repetir tanto FacesContext...addMessage.
    private void mensaje(FacesMessage.Severity severidad, String titulo, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, titulo, detalle));
    }

    // === Getters y setters ===
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

    public void setTipoCaracteristicaElegido(TipoCaracteristica tipoCaracteristica) {
        this.tipoCaracteristicaElegido = tipoCaracteristica;
    }

    public Caracteristica getCaracteristicaElegida() {
        return caracteristicaElegida;
    }

    public void setCaracteristicaElegida(Caracteristica caracteristica) {
        this.caracteristicaElegida = caracteristica;
    }

    public List<TipoCaracteristica> getListaTiposCaracteristicaActivos() {
        return listaTiposCaracteristicaActivos;
    }

    public List<Caracteristica> getListaCaracteristicasDelTipo() {
        return listaCaracteristicasDelTipo;
    }

    public List<DescuentoProducto> getDescuentosDelProducto() {
        return descuentosDelProducto;
    }

    public DescuentoProducto getDescuentoFila() {
        return descuentoFila;
    }

    public void setDescuentoFila(DescuentoProducto descuentoFila) {
        this.descuentoFila = descuentoFila;
    }

    public DescuentoProducto getRegistroDescuento() {
        return registroDescuento;
    }

    public String getEstadoDescuento() {
        return estadoDescuento;
    }

    public TipoDescuento getTipoDescuentoElegido() {
        return tipoDescuentoElegido;
    }

    public void setTipoDescuentoElegido(TipoDescuento tipoDescuento) {
        this.tipoDescuentoElegido = tipoDescuento;
    }

    public Descuento getDescuentoElegido() {
        return descuentoElegido;
    }

    public void setDescuentoElegido(Descuento descuento) {
        this.descuentoElegido = descuento;
    }

    public List<TipoDescuento> getListaTiposDescuentoActivos() {
        return listaTiposDescuentoActivos;
    }

    public List<Descuento> getListaDescuentosDelTipo() {
        return listaDescuentosDelTipo;
    }
}
