package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoDescuento;
/**
 *
 * @author daniel
 */
@Stateless
public class TipoDescuentoDAO extends AbstractDAO<TipoDescuento, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public TipoDescuentoDAO() {
        super(TipoDescuento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<TipoDescuento> findByNombre(String nombre) {
        return em.createNamedQuery(
                "TipoDescuento.findByNombre",
                TipoDescuento.class
        )
        .setParameter("nombre", nombre)
        .getResultList();
    }

    public List<TipoDescuento> findByActivo(Boolean activo) {
        return em.createNamedQuery(
                "TipoDescuento.findByActivo",
                TipoDescuento.class
        )
        .setParameter("activo", activo)
        .getResultList();
    }

    public List<TipoDescuento> findByDescuentoMaximo(Integer descuentoMaximo) {
        return em.createNamedQuery(
                "TipoDescuento.findByDescuentoMaximo",
                TipoDescuento.class
        )
        .setParameter("descuentoMaximo", descuentoMaximo)
        .getResultList();
    }

    public List<TipoDescuento> findByObservaciones(String observaciones) {
        return em.createNamedQuery(
                "TipoDescuento.findByObservaciones",
                TipoDescuento.class
        )
        .setParameter("observaciones", observaciones)
        .getResultList();
    }
}