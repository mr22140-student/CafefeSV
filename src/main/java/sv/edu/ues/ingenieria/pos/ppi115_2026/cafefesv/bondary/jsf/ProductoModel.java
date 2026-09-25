package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
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
}