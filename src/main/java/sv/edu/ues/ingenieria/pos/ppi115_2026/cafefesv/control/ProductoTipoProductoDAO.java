package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoTipoProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoProducto;
/**
 *
 * @author daniel
 */
@Stateless
public class ProductoTipoProductoDAO extends AbstractDAO<ProductoTipoProducto, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public ProductoTipoProductoDAO() {
        super(ProductoTipoProducto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ProductoTipoProducto> findByProducto(Producto producto) {
        return em.createQuery(
                "SELECT x FROM ProductoTipoProducto x "
                + "WHERE x.producto = :producto",
                ProductoTipoProducto.class
        )
        .setParameter("producto", producto)
        .getResultList();
    }

    public List<ProductoTipoProducto> findByTipoProducto(TipoProducto tipoProducto) {
        return em.createQuery(
                "SELECT x FROM ProductoTipoProducto x "
                + "WHERE x.idTipoProducto = :tipoProducto",
                ProductoTipoProducto.class
        )
        .setParameter("tipoProducto", tipoProducto)
        .getResultList();
    }
}