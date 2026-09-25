package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

    public List<Descuento> findByNombre(String nombre) {
        return em.createNamedQuery(
                "Descuento.findByNombre",
                Descuento.class
        )
        .setParameter("nombre", nombre)
        .getResultList();
    }

    public List<Descuento> findByFechaDesde(Date fechaDesde) {
        return em.createNamedQuery(
                "Descuento.findByFechaDesde",
                Descuento.class
        )
        .setParameter("fechaDesde", fechaDesde)
        .getResultList();
    }

    public List<Descuento> findByFechaHasta(Date fechaHasta) {
        return em.createNamedQuery(
                "Descuento.findByFechaHasta",
                Descuento.class
        )
        .setParameter("fechaHasta", fechaHasta)
        .getResultList();
    }

    public List<Descuento> findByObservaciones(String observaciones) {
        return em.createNamedQuery(
                "Descuento.findByObservaciones",
                Descuento.class
        )
        .setParameter("observaciones", observaciones)
        .getResultList();
    }

    /**
     * Obtiene los descuentos vigentes en la fecha indicada.
     */
    public List<Descuento> findVigentes(Date fecha) {
        return em.createQuery(
                "SELECT d FROM Descuento d "
                + "WHERE :fecha BETWEEN d.fechaDesde AND d.fechaHasta",
                Descuento.class
        )
        .setParameter("fecha", fecha)
        .getResultList();
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
}