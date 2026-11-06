package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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