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

    /**
     * Cuenta los productos ACTIVOS que tienen asignado este tipo.
     */
    public long countProductosActivos(TipoProducto tipoProducto) {
        return em.createQuery(
                "SELECT COUNT(asignacion) FROM ProductoTipoProducto asignacion "
                + "WHERE asignacion.idTipoProducto = :tipoProducto AND asignacion.producto.activo = true",
                Long.class)
                .setParameter("tipoProducto", tipoProducto)
                .getSingleResult();
    }

    /**
     * Cuenta los productos (activos o no) que tienen asignado este tipo.
     */
    public long countProductos(TipoProducto tipoProducto) {
        return em.createQuery(
                "SELECT COUNT(asignacion) FROM ProductoTipoProducto asignacion "
                + "WHERE asignacion.idTipoProducto = :tipoProducto",
                Long.class)
                .setParameter("tipoProducto", tipoProducto)
                .getSingleResult();
    }

    /**
     * Un tipo no se puede inactivar mientras algún producto ACTIVO lo tenga.
     *
     * @return el mensaje de error, o null si se puede inactivar
     */
    public String validarInactivar(TipoProducto tipoProducto) {
        if (tipoProducto == null || Boolean.TRUE.equals(tipoProducto.getActivo())) {
            return null;
        }
        long productosActivos = countProductosActivos(tipoProducto);
        if (productosActivos > 0) {
            return "Hay " + productosActivos + " producto(s) activo(s) con este tipo. "
                    + "Quíteselo o inactive esos productos primero.";
        }
        return null;
    }

    /**
     * Un tipo no se puede eliminar si algún producto lo tiene asignado.
     *
     * @return el mensaje de error, o null si se puede eliminar
     */
    public String validarEliminar(TipoProducto tipoProducto) {
        if (tipoProducto != null && countProductos(tipoProducto) > 0) {
            return "Hay productos que tienen asignado este tipo. Quíteselo primero.";
        }
        return null;
    }

}
