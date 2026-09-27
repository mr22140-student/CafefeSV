package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.FacturaOrdenProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.OrdenProducto;
/**
 *
 * @author daniel
 */

@Stateless
public class FacturaOrdenProductoDAO extends AbstractDAO<FacturaOrdenProducto, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public FacturaOrdenProductoDAO() {
        super(FacturaOrdenProducto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<FacturaOrdenProducto> findByPrecio(BigDecimal precio) {
        return em.createNamedQuery(
                "FacturaOrdenProducto.findByPrecio",
                FacturaOrdenProducto.class
        )
        .setParameter("precio", precio)
        .getResultList();
    }

    public List<FacturaOrdenProducto> findByObservaciones(String observaciones) {
        return em.createNamedQuery(
                "FacturaOrdenProducto.findByObservaciones",
                FacturaOrdenProducto.class
        )
        .setParameter("observaciones", observaciones)
        .getResultList();
    }

    public List<FacturaOrdenProducto> findByFactura(Factura factura) {
        return em.createQuery(
                "SELECT x FROM FacturaOrdenProducto x "
                + "WHERE x.idFactura = :factura",
                FacturaOrdenProducto.class
        )
        .setParameter("factura", factura)
        .getResultList();
    }

    public List<FacturaOrdenProducto> findByOrdenProducto(OrdenProducto ordenProducto) {
        return em.createQuery(
                "SELECT x FROM FacturaOrdenProducto x "
                + "WHERE x.idOrdenProducto = :ordenProducto",
                FacturaOrdenProducto.class
        )
        .setParameter("ordenProducto", ordenProducto)
        .getResultList();
    }
    
        /**
     * Productos de ordenes que todavia NO estan en ninguna factura.
     * Trae el producto y la orden en la misma consulta.
     */
    public List<OrdenProducto> findOrdenProductoDisponibles() {
        return em.createQuery(
                "SELECT op FROM OrdenProducto op "
                + "LEFT JOIN FETCH op.idProducto "
                + "LEFT JOIN FETCH op.idOrden "
                + "WHERE NOT EXISTS ("
                + "   SELECT f FROM FacturaOrdenProducto f WHERE f.idOrdenProducto = op)",
                OrdenProducto.class)
                .getResultList();
    }
}