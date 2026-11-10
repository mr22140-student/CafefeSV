package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.Date;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.DescuentoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.DescuentoProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.DescuentoProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;

/**
 *
 * @author valen
 */
@Named("descuentoProductoModel")
@ViewScoped
public class DescuentoProductoModel extends AbstractModel<DescuentoProducto, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private DescuentoProductoDAO descuentoProductoDAO;

    @Inject
    private DescuentoDAO descuentoDAO;

    @Inject
    private ProductoDAO productoDAO;

    private List<Descuento> listaDescuento;
    private List<Producto> listaProducto;

    @PostConstruct
    public void init() {
        iniciar();
        listaDescuento = descuentoDAO.findAll();
        listaProducto = productoDAO.findAll();
    }

    @Override
    protected GenericDAO<DescuentoProducto, UUID> getDAO() {
        return descuentoProductoDAO;
    }

    @Override
    protected DescuentoProducto crearNuevo() {
        DescuentoProducto nuevo = new DescuentoProducto(UUID.randomUUID());
        nuevo.setValor(0);
        nuevo.setFechaDesde(new Date());
        return nuevo;
    }

    /**
     * Al elegir un descuento: - fecha desde: hoy, salvo que el descuento
     * empiece más adelante. - fecha hasta: la del descuento. - si el descuento
     * ya venció, avisa.
     */
    public void onDescuentoChange() {
        if (registro == null || registro.getIdDescuento() == null) {
            return;
        }
        Descuento descuentoElegido = registro.getIdDescuento();
        Date ahora = new Date();

        if (descuentoElegido.getFechaDesde() != null && descuentoElegido.getFechaDesde().after(ahora)) {
            registro.setFechaDesde(descuentoElegido.getFechaDesde());
        } else if (registro.getFechaDesde() == null) {
            registro.setFechaDesde(ahora);
        }

        registro.setFechaHasta(descuentoElegido.getFechaHasta());

        if (descuentoElegido.getFechaHasta() != null && descuentoElegido.getFechaHasta().before(ahora)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Descuento vencido",
                    "El descuento seleccionado ya terminó. Elige otro o cambia sus fechas en Descuentos."));
        }
    }

    /**
     * Porcentaje máximo según el tipo del descuento elegido (null = sin
     * límite).
     */
    public Integer getDescuentoMaximo() {
        if (registro == null || registro.getIdDescuento() == null
                || registro.getIdDescuento().getIdTipoDescuento() == null) {
            return null;
        }
        return registro.getIdDescuento().getIdTipoDescuento().getDescuentoMaximo();
    }

    @Override
    public void btnCrearHandler() {
        if (asignacionValida()) {
            super.btnCrearHandler();
        }
    }

    @Override
    public void btnModificarHandler() {
        if (asignacionValida()) {
            super.btnModificarHandler();
        }
    }

        // Mismas reglas que la pestaña Descuentos de Producto.
    private boolean asignacionValida() {
        return sinError("Descuento no válido", descuentoProductoDAO.validarDescuento(registro))
                && sinError("Porcentaje no válido", descuentoProductoDAO.validarValor(registro))
                && sinError("Fechas no válidas", descuentoProductoDAO.validarFechas(registro))
                && sinError("Registro duplicado", descuentoProductoDAO.validarDuplicado(registro));
    }

    public List<Descuento> getListaDescuento() {
        return listaDescuento;
    }

    public List<Producto> getListaProducto() {
        return listaProducto;
    }
}
