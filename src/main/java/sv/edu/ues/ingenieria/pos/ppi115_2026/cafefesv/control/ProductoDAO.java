package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
/**
 *
 * @author daniel
 */
@Stateless
public class ProductoDAO extends AbstractDAO<Producto, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public ProductoDAO() {
        super(Producto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Producto> findByNombre(String nombre) {
        return em.createNamedQuery(
                "Producto.findByNombre",
                Producto.class
        )
        .setParameter("nombre", nombre)
        .getResultList();
    }

    public List<Producto> findByActivo(Boolean activo) {
        return em.createNamedQuery(
                "Producto.findByActivo",
                Producto.class
        )
        .setParameter("activo", activo)
        .getResultList();
    }

    public List<Producto> findByComentarios(String comentarios) {
        return em.createNamedQuery(
                "Producto.findByComentarios",
                Producto.class
        )
        .setParameter("comentarios", comentarios)
        .getResultList();
    }
}