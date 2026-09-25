package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caja;

/**
 *
 * @author daniel
 */
@Stateless
public class CajaDAO extends AbstractDAO<Caja, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public CajaDAO() {
        super(Caja.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Caja> findByNombre(String nombre) {
        return em.createNamedQuery("Caja.findByNombre", Caja.class)
                .setParameter("nombre", nombre)
                .getResultList();
    }

    public List<Caja> findByActivo(Boolean activo) {
        return em.createNamedQuery("Caja.findByActivo", Caja.class)
                .setParameter("activo", activo)
                .getResultList();
    }

    public List<Caja> findByObservaciones(String observaciones) {
        return em.createNamedQuery("Caja.findByObservaciones", Caja.class)
                .setParameter("observaciones", observaciones)
                .getResultList();
    }
}