package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import java.math.BigDecimal;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;

/**
 *
 * @author daniel
 */
@Stateless
public class ProductoDAO extends AbstractDAO<Producto, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public ProductoDAO() {
        super(Producto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * El precio sugerido debe ser mayor que $0.00.
     *
     * @return el mensaje de error, o null si el precio está bien
     */
    public String validarPrecio(Producto producto) {
        if (producto != null && producto.getPrecioSugerido() != null
                && producto.getPrecioSugerido().compareTo(BigDecimal.ZERO) <= 0) {
            return "El precio sugerido debe ser mayor que $0.00.";
        }
        return null;
    }

}
