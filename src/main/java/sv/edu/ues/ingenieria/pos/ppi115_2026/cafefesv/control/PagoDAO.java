package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Pago;

@Stateless
public class PagoDAO extends AbstractDAO<Pago, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public PagoDAO() {
        super(Pago.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Pago> findByEstado(String estado) {
        return em.createNamedQuery(
                "Pago.findByEstado",
                Pago.class
        )
        .setParameter("estado", estado)
        .getResultList();
    }

    public List<Pago> findByObservaciones(String observaciones) {
        return em.createNamedQuery(
                "Pago.findByObservaciones",
                Pago.class
        )
        .setParameter("observaciones", observaciones)
        .getResultList();
    }

    public List<Pago> findByFactura(Factura factura) {
        return em.createQuery(
                "SELECT x FROM Pago x "
                + "WHERE x.idFactura = :factura",
                Pago.class
        )
        .setParameter("factura", factura)
        .getResultList();
    }
    
        /** Cuantos detalles (montos) tiene el pago. */
    public long countDetalles(Pago pago) {
        return em.createQuery(
                "SELECT COUNT(d) FROM PagoDetalle d WHERE d.idPago = :pago", Long.class)
                .setParameter("pago", pago)
                .getSingleResult();
    }

    /** Suma de los montos registrados en los detalles del pago. */
    public BigDecimal sumaMontos(Pago pago) {
        BigDecimal suma = em.createQuery(
                "SELECT SUM(d.monto) FROM PagoDetalle d WHERE d.idPago = :pago", BigDecimal.class)
                .setParameter("pago", pago)
                .getSingleResult();
        return suma != null ? suma : BigDecimal.ZERO;
    }

    /** Total de la factura (suma de sus productos). */
    public BigDecimal totalFactura(Factura factura) {
        BigDecimal total = em.createQuery(
                "SELECT SUM(x.precio) FROM FacturaOrdenProducto x WHERE x.idFactura = :factura", BigDecimal.class)
                .setParameter("factura", factura)
                .getSingleResult();
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * true si la factura ya tiene otro pago que no esta ANULADO.
     * idExcluir es el pago que se esta modificando (null al crear).
     */
    public boolean existePagoActivo(Factura factura, UUID idExcluir) {
        Long total = em.createQuery(
                "SELECT COUNT(x) FROM Pago x "
                + "WHERE x.idFactura = :factura "
                + "AND (x.estado IS NULL OR x.estado <> 'ANULADO') "
                + "AND x.idPago <> :excluir", Long.class)
                .setParameter("factura", factura)
                .setParameter("excluir", idExcluir != null ? idExcluir : new UUID(0L, 0L))
                .getSingleResult();
        return total > 0;
    }
}