package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import java.math.BigDecimal;
import java.util.List;
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

    /**
     * El precio sugerido debe ser mayor que $0.00.
     *
     * @return el mensaje de error, o null si el precio está bien
     */
    public String validarPrecio(Producto producto) {
        if (producto != null && producto.getPrecioSugerido() != null
                && producto.getPrecioSugerido().compareTo(BigDecimal.ZERO) <= 0) {
            return "El precio sugerido debe ser mayor que $0.00.";
        }
        return null;
    }

    public List<Producto> findByActivo(Boolean activo) {
        return em.createNamedQuery("Producto.findByActivo", Producto.class)
                .setParameter("activo", activo)
                .getResultList();
    }

    /**
     * Cuántas veces se ha pedido este producto en alguna orden.
     */
    public long countOrdenes(Producto producto) {
        return em.createQuery(
                "SELECT COUNT(detalle) FROM OrdenProducto detalle WHERE detalle.idProducto = :producto",
                Long.class)
                .setParameter("producto", producto)
                .getSingleResult();
    }

    /**
     * Cuántas características tiene asignadas este producto.
     */
    public long countCaracteristicas(Producto producto) {
        return em.createQuery(
                "SELECT COUNT(asignacion) FROM ProductoCaracteristica asignacion WHERE asignacion.idProducto = :producto",
                Long.class)
                .setParameter("producto", producto)
                .getSingleResult();
    }

    /**
     * Cuántos descuentos tiene asignados este producto.
     */
    public long countDescuentos(Producto producto) {
        return em.createQuery(
                "SELECT COUNT(asignacion) FROM DescuentoProducto asignacion WHERE asignacion.idProducto = :producto",
                Long.class)
                .setParameter("producto", producto)
                .getSingleResult();
    }

    /**
     * Un producto que ya se pidió en una orden no se elimina (se inactiva), y
     * tampoco uno que todavía tiene características o descuentos asignados. Los
     * tipos se borran junto con el producto (cascade en
     * productoTipoProductoList).
     *
     * @return el mensaje de error, o null si se puede eliminar
     */
    public String validarEliminar(Producto producto) {
        if (producto == null) {
            return null;
        }
        if (countOrdenes(producto) > 0) {
            return "El producto ya se ha pedido en órdenes. Inactívelo en lugar de eliminarlo.";
        }
        if (countCaracteristicas(producto) > 0 || countDescuentos(producto) > 0) {
            return "El producto tiene características o descuentos asignados. Quíteselos primero.";
        }
        return null;
    }

}
