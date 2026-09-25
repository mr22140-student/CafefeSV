package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Rol;
/**
 *
 * @author daniel
 */

@Stateless
public class EmpleadoRolDAO extends AbstractDAO<EmpleadoRol, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public EmpleadoRolDAO() {
        super(EmpleadoRol.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<EmpleadoRol> findByActivo(Boolean activo) {
        return em.createNamedQuery(
                "EmpleadoRol.findByActivo",
                EmpleadoRol.class
        )
        .setParameter("activo", activo)
        .getResultList();
    }

    public List<EmpleadoRol> findByObservaciones(String observaciones) {
        return em.createNamedQuery(
                "EmpleadoRol.findByObservaciones",
                EmpleadoRol.class
        )
        .setParameter("observaciones", observaciones)
        .getResultList();
    }

    public List<EmpleadoRol> findByRol(Rol rol) {
        return em.createQuery(
                "SELECT x FROM EmpleadoRol x "
                + "WHERE x.idRol = :rol",
                EmpleadoRol.class
        )
        .setParameter("rol", rol)
        .getResultList();
    }
}