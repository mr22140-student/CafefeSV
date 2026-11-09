package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.CaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoCaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoCaracteristica;

/**
 *
 * @author valen
 */
@Named("productoCaracteristicaModel")
@ViewScoped
public class ProductoCaracteristicaModel extends AbstractModel<ProductoCaracteristica, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProductoCaracteristicaDAO productoCaracteristicaDAO;

    @Inject
    private ProductoDAO productoDAO;

    @Inject
    private CaracteristicaDAO caracteristicaDAO;

    private List<Producto> listaProducto;
    private List<Caracteristica> listaCaracteristica;

    @PostConstruct
    public void init() {
        iniciar();
        listaProducto = productoDAO.findAll();
        listaCaracteristica = caracteristicaDAO.findAll();
    }

    @Override
    protected GenericDAO<ProductoCaracteristica, UUID> getDAO() {
        return productoCaracteristicaDAO;
    }

    @Override
    protected ProductoCaracteristica crearNuevo() {
        return new ProductoCaracteristica(UUID.randomUUID());
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

        // Mismas reglas que la pestaña Características de Producto.
    private boolean asignacionValida() {
        return sinError("Característica no válida", productoCaracteristicaDAO.validarCaracteristica(registro))
                && sinError("Valor no válido", productoCaracteristicaDAO.validarValor(registro))
                && sinError("Registro duplicado", productoCaracteristicaDAO.validarDuplicado(registro));
    }
    /**
     * Texto de ayuda con el formato esperado, para mostrarlo junto al campo
     * Valor.
     */
    public String getFormatoValor() {
        if (registro == null || registro.getIdCaracteristica() == null
                || registro.getIdCaracteristica().getIdTipoCaracteristica() == null) {
            return "";
        }
        String regex = registro.getIdCaracteristica().getIdTipoCaracteristica().getExpresionRegular();
        return (regex == null || regex.isBlank()) ? "" : "Formato: " + regex;
    }

    public List<Producto> getListaProducto() {
        return listaProducto;
    }

    public List<Caracteristica> getListaCaracteristica() {
        return listaCaracteristica;
    }
}
