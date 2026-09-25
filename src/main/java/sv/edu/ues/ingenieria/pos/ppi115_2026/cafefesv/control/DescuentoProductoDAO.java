package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.DescuentoProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
/**
 *
 * @author daniel
 */

@Stateless
public class DescuentoProductoDAO extends AbstractDAO<DescuentoProducto, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public DescuentoProductoDAO() {
        super(DescuentoProducto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<DescuentoProducto> findByFechaDesde(Date fechaDesde) {
        return em.createNamedQuery(
                "DescuentoProducto.findByFechaDesde",
                DescuentoProducto.class
        )
        .setParameter("fechaDesde", fechaDesde)
        .getResultList();
    }

    public List<DescuentoProducto> findByFechaHasta(Date fechaHasta) {
        return em.createNamedQuery(
                "DescuentoProducto.findByFechaHasta",
                DescuentoProducto.class
        )
        .setParameter("fechaHasta", fechaHasta)
        .getResultList();
    }

    public List<DescuentoProducto> findByValor(Integer valor) {
        return em.createNamedQuery(
                "DescuentoProducto.findByValor",
                DescuentoProducto.class
        )
        .setParameter("valor", valor)
        .getResultList();
    }

    public List<DescuentoProducto> findByObservaciones(String observaciones) {
        return em.createNamedQuery(
                "DescuentoProducto.findByObservaciones",
                DescuentoProducto.class
        )
        .setParameter("observaciones", observaciones)
        .getResultList();
    }

    public List<DescuentoProducto> findByDescuento(Descuento descuento) {
        return em.createQuery(
                "SELECT x FROM DescuentoProducto x "
                + "WHERE x.idDescuento = :descuento",
                DescuentoProducto.class
        )
        .setParameter("descuento", descuento)
        .getResultList();
    }

    public List<DescuentoProducto> findByProducto(Producto producto) {
        return em.createQuery(
                "SELECT x FROM DescuentoProducto x "
                + "WHERE x.idProducto = :producto",
                DescuentoProducto.class
        )
        .setParameter("producto", producto)
        .getResultList();
    }
}