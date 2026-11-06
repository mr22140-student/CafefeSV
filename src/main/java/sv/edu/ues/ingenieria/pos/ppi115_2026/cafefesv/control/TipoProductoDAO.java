package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoProducto;
/**
 *
 * @author daniel
 */
@Stateless
public class TipoProductoDAO extends AbstractDAO<TipoProducto, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public TipoProductoDAO() {
        super(TipoProducto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<TipoProducto> findByActivo(Boolean activo) {
        return em.createNamedQuery(
                "TipoProducto.findByActivo",
                TipoProducto.class
        )
        .setParameter("activo", activo)
        .getResultList();
    }

}