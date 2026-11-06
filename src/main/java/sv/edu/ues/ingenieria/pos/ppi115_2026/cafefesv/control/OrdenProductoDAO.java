package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Orden;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.OrdenProducto;

@Stateless
public class OrdenProductoDAO extends AbstractDAO<OrdenProducto, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public OrdenProductoDAO() {
        super(OrdenProducto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /** Productos de una orden; trae el producto en la misma consulta. */
    public List<OrdenProducto> findByOrden(Orden orden) {
        return em.createQuery(
                "SELECT x FROM OrdenProducto x "
                + "LEFT JOIN FETCH x.idProducto "
                + "WHERE x.idOrden = :orden",
                OrdenProducto.class)
                .setParameter("orden", orden)
                .getResultList();
    }

    /** Cuantos productos tiene una orden (para no eliminar ordenes con productos). */
    public long countByOrden(Orden orden) {
        return em.createQuery(
                "SELECT COUNT(x) FROM OrdenProducto x WHERE x.idOrden = :orden", Long.class)
                .setParameter("orden", orden)
                .getSingleResult();
    }

    /** Cuantas veces esta facturado este producto de la orden. */
    public long countFacturas(OrdenProducto ordenProducto) {
        return em.createQuery(
                "SELECT COUNT(f) FROM FacturaOrdenProducto f WHERE f.idOrdenProducto = :op", Long.class)
                .setParameter("op", ordenProducto)
                .getSingleResult();
    }
}