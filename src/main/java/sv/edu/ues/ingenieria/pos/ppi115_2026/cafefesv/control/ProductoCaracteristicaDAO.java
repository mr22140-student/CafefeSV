package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoCaracteristica;
/**
 *
 * @author daniel
 */
@Stateless
public class ProductoCaracteristicaDAO extends AbstractDAO<ProductoCaracteristica, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public ProductoCaracteristicaDAO() {
        super(ProductoCaracteristica.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ProductoCaracteristica> findByValor(String valor) {
        return em.createNamedQuery(
                "ProductoCaracteristica.findByValor",
                ProductoCaracteristica.class
        )
        .setParameter("valor", valor)
        .getResultList();
    }

    public List<ProductoCaracteristica> findByCaracteristica(Caracteristica caracteristica) {
        return em.createQuery(
                "SELECT x FROM ProductoCaracteristica x "
                + "WHERE x.idCaracteristica = :caracteristica",
                ProductoCaracteristica.class
        )
        .setParameter("caracteristica", caracteristica)
        .getResultList();
    }

    public List<ProductoCaracteristica> findByProducto(Producto producto) {
        return em.createQuery(
                "SELECT x FROM ProductoCaracteristica x "
                + "WHERE x.idProducto = :producto",
                ProductoCaracteristica.class
        )
        .setParameter("producto", producto)
        .getResultList();
    }
}