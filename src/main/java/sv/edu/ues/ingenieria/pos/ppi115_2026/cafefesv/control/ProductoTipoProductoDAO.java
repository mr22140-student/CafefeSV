package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoTipoProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoProducto;

/**
 *
 * @author daniel
 */
@Stateless
public class ProductoTipoProductoDAO extends AbstractDAO<ProductoTipoProducto, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public ProductoTipoProductoDAO() {
        super(ProductoTipoProducto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ProductoTipoProducto> findByProducto(Producto producto) {
        return em.createQuery(
                "SELECT asignacion FROM ProductoTipoProducto asignacion "
                + "WHERE asignacion.producto = :producto",
                ProductoTipoProducto.class
        )
                .setParameter("producto", producto)
                .getResultList();
    }

    /**
     * Los productos de una categoría (tipo de producto), con el producto
     * cargado en la misma consulta para que el JSON lo traiga completo.
     */
    public List<ProductoTipoProducto> findByTipoProducto(TipoProducto tipoProducto) {
        return em.createQuery(
                "SELECT asignacion FROM ProductoTipoProducto asignacion "
                + "LEFT JOIN FETCH asignacion.producto "
                + "WHERE asignacion.idTipoProducto = :tipoProducto",
                ProductoTipoProducto.class
        )
                .setParameter("tipoProducto", tipoProducto)
                .getResultList();
    }

    /**
     * La asignación debe tener un tipo y ese tipo debe estar activo. El tipo se
     * lee de la base para revisar su estado actual.
     *
     * @return el mensaje de error, o null si el tipo está bien
     */
    public String validarTipo(ProductoTipoProducto asignacion) {
        if (asignacion == null || asignacion.getIdTipoProducto() == null
                || asignacion.getIdTipoProducto().getIdTipoProducto() == null) {
            return "Seleccione un tipo de producto.";
        }
        TipoProducto tipoEnLaBase = em.find(TipoProducto.class, asignacion.getIdTipoProducto().getIdTipoProducto());
        if (tipoEnLaBase == null || !Boolean.TRUE.equals(tipoEnLaBase.getActivo())) {
            return "El tipo \"" + asignacion.getIdTipoProducto().getNombre()
                    + "\" está inactivo. Seleccione un tipo activo.";
        }
        return null;
    }

    /**
     * Un producto no puede tener el mismo tipo asignado dos veces.
     *
     * @return el mensaje de error, o null si no está repetido
     */
    public String validarDuplicado(ProductoTipoProducto asignacion) {
        if (asignacion == null || asignacion.getProducto() == null || asignacion.getIdTipoProducto() == null) {
            return null;
        }
        boolean repetido = findByProducto(asignacion.getProducto()).stream()
                .anyMatch(otraAsignacion -> !otraAsignacion.getIdProductoTipoProducto().equals(asignacion.getIdProductoTipoProducto())
                        && asignacion.getIdTipoProducto().equals(otraAsignacion.getIdTipoProducto()));
        return repetido ? "El producto ya tiene asignado ese tipo." : null;
    }
}
