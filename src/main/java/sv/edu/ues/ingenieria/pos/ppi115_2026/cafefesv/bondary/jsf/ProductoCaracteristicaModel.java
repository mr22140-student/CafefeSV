package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
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
    private ProductoCaracteristicaDAO pcDAO;

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
        return pcDAO;
    }

    @Override
    protected ProductoCaracteristica crearNuevo() {
        return new ProductoCaracteristica(UUID.randomUUID());
    }

    @Override
    public void btnCrearHandler() {
        if (!existeDuplicado()) {
            super.btnCrearHandler();
        }
    }

    @Override
    public void btnModificarHandler() {
        if (!existeDuplicado()) {
            super.btnModificarHandler();
        }
    }

    /** Un producto no debe tener la misma caracteristica repetida. */
    private boolean existeDuplicado() {
        if (registro == null || registro.getIdProducto() == null || registro.getIdCaracteristica() == null) {
            return false;
        }
        boolean duplicado = pcDAO.findByProducto(registro.getIdProducto()).stream()
                .anyMatch(x -> !x.getIdProductoCaracteristica().equals(registro.getIdProductoCaracteristica())
                && registro.getIdCaracteristica().equals(x.getIdCaracteristica()));
        if (duplicado) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Registro duplicado", "El producto ya tiene asignada esa característica."));
        }
        return duplicado;
    }

    public List<Producto> getListaProducto() {
        return listaProducto;
    }

    public List<Caracteristica> getListaCaracteristica() {
        return listaCaracteristica;
    }
}