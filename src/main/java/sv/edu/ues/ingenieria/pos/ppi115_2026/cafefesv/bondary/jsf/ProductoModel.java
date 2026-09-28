package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.math.BigDecimal;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;

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
        if (precioValido()) {
            super.btnCrearHandler();
        }
    }

    @Override
    public void btnModificarHandler() {
        if (precioValido()) {
            super.btnModificarHandler();
        }
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
}
