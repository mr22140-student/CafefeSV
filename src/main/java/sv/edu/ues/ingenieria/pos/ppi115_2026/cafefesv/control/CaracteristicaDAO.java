package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;

/**
 *
 * @author daniel
 */
@Stateless
public class CaracteristicaDAO extends AbstractDAO<Caracteristica, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public CaracteristicaDAO() {
        super(Caracteristica.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Caracteristica> findByNombre(String nombre) {
        if (nombre == null) return Collections.emptyList();
        return em.createNamedQuery("Caracteristica.findByNombre", Caracteristica.class)
                .setParameter("nombre", nombre)
                .getResultList();
    }

    public List<Caracteristica> findByActivo(Boolean activo) {
        if (activo == null) return Collections.emptyList();
        return em.createNamedQuery("Caracteristica.findByActivo", Caracteristica.class)
                .setParameter("activo", activo)
                .getResultList();
    }

    public List<Caracteristica> findByObservaciones(String observaciones) {
        if (observaciones == null) return Collections.emptyList();
        return em.createNamedQuery("Caracteristica.findByObservaciones", Caracteristica.class)
                .setParameter("observaciones", observaciones)
                .getResultList();
    }

    public List<Caracteristica> findByTipoCaracteristica(TipoCaracteristica tipoCaracteristica) {
        if (tipoCaracteristica == null) return Collections.emptyList();
        return em.createQuery(
                "SELECT x FROM Caracteristica x WHERE x.idTipoCaracteristica = :tipoCaracteristica",
                Caracteristica.class)
                .setParameter("tipoCaracteristica", tipoCaracteristica)
                .getResultList();
    }
}