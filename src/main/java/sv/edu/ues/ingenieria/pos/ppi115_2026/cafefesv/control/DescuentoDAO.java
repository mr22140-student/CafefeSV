package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoDescuento;

/**
 *
 * @author daniel
 */
@Stateless
public class DescuentoDAO extends AbstractDAO<Descuento, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public DescuentoDAO() {
        super(Descuento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public long countByTipoDescuento(TipoDescuento tipoDescuento) {
        if (tipoDescuento == null) {
            return 0;
        }
        return em.createQuery(
                "SELECT COUNT(d) FROM Descuento d WHERE d.idTipoDescuento = :tipo", Long.class)
                .setParameter("tipo", tipoDescuento)
                .getSingleResult();
    }

    /**
     * Cuenta los descuentos de un tipo que todavía no vencen (sin fecha fin o
     * con fecha fin futura).
     */
    public long countVigentesByTipoDescuento(TipoDescuento tipoDescuento) {
        if (tipoDescuento == null) {
            return 0;
        }
        return em.createQuery(
                "SELECT COUNT(d) FROM Descuento d WHERE d.idTipoDescuento = :tipo "
                + "AND (d.fechaHasta IS NULL OR d.fechaHasta >= :ahora)", Long.class)
                .setParameter("tipo", tipoDescuento)
                .setParameter("ahora", new Date(), TemporalType.TIMESTAMP)
                .getSingleResult();
    }

    /**
     * Descuentos de un tipo que todavía no vencen (sin fecha fin o con fecha
     * fin futura).
     */
    public List<Descuento> findVigentesByTipoDescuento(TipoDescuento tipoDescuento) {
        if (tipoDescuento == null) {
            return List.of();
        }
        return em.createQuery(
                "SELECT d FROM Descuento d WHERE d.idTipoDescuento = :tipo "
                + "AND (d.fechaHasta IS NULL OR d.fechaHasta >= :ahora) ORDER BY d.nombre", Descuento.class)
                .setParameter("tipo", tipoDescuento)
                .setParameter("ahora", new Date(), TemporalType.TIMESTAMP)
                .getResultList();
    }
}
