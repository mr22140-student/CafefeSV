package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Pago;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.PagoDetalle;
/**
 *
 * @author daniel
 */
@Stateless
public class PagoDetalleDAO extends AbstractDAO<PagoDetalle, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public PagoDetalleDAO() {
        super(PagoDetalle.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<PagoDetalle> findByMonto(BigDecimal monto) {
        return em.createNamedQuery(
                "PagoDetalle.findByMonto",
                PagoDetalle.class
        )
        .setParameter("monto", monto)
        .getResultList();
    }

    public List<PagoDetalle> findByTipoPago(String tipoPago) {
        return em.createNamedQuery(
                "PagoDetalle.findByTipoPago",
                PagoDetalle.class
        )
        .setParameter("tipoPago", tipoPago)
        .getResultList();
    }

    public List<PagoDetalle> findByObservaciones(String observaciones) {
        return em.createNamedQuery(
                "PagoDetalle.findByObservaciones",
                PagoDetalle.class
        )
        .setParameter("observaciones", observaciones)
        .getResultList();
    }

    public List<PagoDetalle> findByReferenciaExterna(String referenciaExterna) {
        return em.createNamedQuery(
                "PagoDetalle.findByReferenciaExterna",
                PagoDetalle.class
        )
        .setParameter("referenciaExterna", referenciaExterna)
        .getResultList();
    }

    public List<PagoDetalle> findByPago(Pago pago) {
        return em.createQuery(
                "SELECT x FROM PagoDetalle x "
                + "WHERE x.idPago = :pago",
                PagoDetalle.class
        )
        .setParameter("pago", pago)
        .getResultList();
    }
}