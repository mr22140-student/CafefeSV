package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Rol;
/**
 *
 * @author daniel
 */
@Stateless
public class RolDAO extends AbstractDAO<Rol, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public RolDAO() {
        super(Rol.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Rol> findByNombre(String nombre) {
        return em.createNamedQuery(
                "Rol.findByNombre",
                Rol.class
        )
        .setParameter("nombre", nombre)
        .getResultList();
    }

    public List<Rol> findByActivo(Boolean activo) {
        return em.createNamedQuery(
                "Rol.findByActivo",
                Rol.class
        )
        .setParameter("activo", activo)
        .getResultList();
    }

    public List<Rol> findByObservaciones(String observaciones) {
        return em.createNamedQuery(
                "Rol.findByObservaciones",
                Rol.class
        )
        .setParameter("observaciones", observaciones)
        .getResultList();
    }
}