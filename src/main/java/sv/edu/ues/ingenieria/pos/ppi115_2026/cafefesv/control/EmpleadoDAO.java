package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Empleado;
/**
 *
 * @author daniel
 */

@Stateless
public class EmpleadoDAO extends AbstractDAO<Empleado, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public EmpleadoDAO() {
        super(Empleado.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Empleado> findByNombre(String nombre) {
        return em.createNamedQuery(
                "Empleado.findByNombre",
                Empleado.class
        )
        .setParameter("nombre", nombre)
        .getResultList();
    }

    public List<Empleado> findByApellido(String apellido) {
        return em.createNamedQuery(
                "Empleado.findByApellido",
                Empleado.class
        )
        .setParameter("apellido", apellido)
        .getResultList();
    }

    public List<Empleado> findByActivo(Boolean activo) {
        return em.createNamedQuery(
                "Empleado.findByActivo",
                Empleado.class
        )
        .setParameter("activo", activo)
        .getResultList();
    }

    public List<Empleado> findByComentarios(String comentarios) {
        return em.createNamedQuery(
                "Empleado.findByComentarios",
                Empleado.class
        )
        .setParameter("comentarios", comentarios)
        .getResultList();
    }

    public Empleado obtenerPorId(UUID id) {
        if (id == null) {
            return null;
        }

        return em.find(Empleado.class, id);
    }
}