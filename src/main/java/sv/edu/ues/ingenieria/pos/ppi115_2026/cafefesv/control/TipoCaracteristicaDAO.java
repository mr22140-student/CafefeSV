package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;
/**
 *
 * @author daniel
 */
@Stateless
public class TipoCaracteristicaDAO extends AbstractDAO<TipoCaracteristica, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public TipoCaracteristicaDAO() {
        super(TipoCaracteristica.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<TipoCaracteristica> findByNombre(String nombre) {
        return em.createNamedQuery(
                "TipoCaracteristica.findByNombre",
                TipoCaracteristica.class
        )
        .setParameter("nombre", nombre)
        .getResultList();
    }

    public List<TipoCaracteristica> findByExpresionRegular(String expresionRegular) {
        return em.createNamedQuery(
                "TipoCaracteristica.findByExpresionRegular",
                TipoCaracteristica.class
        )
        .setParameter("expresionRegular", expresionRegular)
        .getResultList();
    }

    public List<TipoCaracteristica> findByActivo(Boolean activo) {
        return em.createNamedQuery(
                "TipoCaracteristica.findByActivo",
                TipoCaracteristica.class
        )
        .setParameter("activo", activo)
        .getResultList();
    }

    public List<TipoCaracteristica> findByObservaciones(String observaciones) {
        return em.createNamedQuery(
                "TipoCaracteristica.findByObservaciones",
                TipoCaracteristica.class
        )
        .setParameter("observaciones", observaciones)
        .getResultList();
    }
}