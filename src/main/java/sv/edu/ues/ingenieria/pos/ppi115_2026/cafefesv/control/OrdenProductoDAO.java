package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;
import java.math.RoundingMode;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Orden;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.OrdenProducto;

@Stateless
public class OrdenProductoDAO extends AbstractDAO<OrdenProducto, UUID> {

    /**
     * Las observaciones de un producto con descuento empiezan con este texto.
     */
    public static final String PREFIJO_DESCUENTO = "Descuento ";

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public OrdenProductoDAO() {
        super(OrdenProducto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Productos de una orden; trae el producto en la misma consulta.
     */
    public List<OrdenProducto> findByOrden(Orden orden) {
        return em.createQuery(
                "SELECT detalle FROM OrdenProducto detalle "
                + "LEFT JOIN FETCH detalle.idProducto "
                + "WHERE detalle.idOrden = :orden",
                OrdenProducto.class)
                .setParameter("orden", orden)
                .getResultList();
    }

    /**
     * Cuantas veces esta facturado este producto de la orden.
     */
    public long countFacturas(OrdenProducto ordenProducto) {
        return em.createQuery(
                "SELECT COUNT(facturado) FROM FacturaOrdenProducto facturado WHERE facturado.idOrdenProducto = :detalleOrden", Long.class)
                .setParameter("detalleOrden", ordenProducto)
                .getSingleResult();
    }

    /**
     * El producto de la orden necesita orden, producto y un precio que no sea
     * negativo.
     *
     * @return el mensaje de error, o null si está bien
     */
    public String validarDetalle(OrdenProducto detalle) {
        if (detalle == null) {
            return null;
        }
        if (detalle.getIdOrden() == null) {
            return "Debe seleccionar la orden.";
        }
        if (detalle.getIdProducto() == null) {
            return "Debe seleccionar el producto.";
        }
        if (detalle.getPrecio() == null || detalle.getPrecio().signum() < 0) {
            return "El precio debe ser mayor o igual a cero.";
        }
        return null;
    }

    /**
     * El producto elegido para agregar a la orden debe existir y estar activo.
     * Se lee de la base para revisar su estado actual.
     *
     * @return el mensaje de error, o null si el producto está bien
     */
    public String validarProductoElegido(Producto producto) {
        if (producto == null || producto.getIdProducto() == null) {
            return "Seleccione un producto.";
        }
        Producto productoEnLaBase = em.find(Producto.class, producto.getIdProducto());
        if (productoEnLaBase == null || !Boolean.TRUE.equals(productoEnLaBase.getActivo())) {
            return "El producto " + producto.getNombre() + " está inactivo.";
        }
        return null;
    }

    /**
     * Un producto ya facturado no se puede modificar, eliminar ni descontar.
     *
     * @return el mensaje de error, o null si todavía no está facturado
     */
    public String validarNoFacturado(OrdenProducto detalle) {
        if (detalle != null && countFacturas(detalle) > 0) {
            return "Este producto ya está facturado.";
        }
        return null;
    }

    /**
     * No se aplican dos descuentos al mismo producto de la orden.
     *
     * @return el mensaje de error, o null si todavía no tiene descuento
     */
    public String validarSinDescuento(OrdenProducto detalle) {
        if (detalle != null && detalle.getObservaciones() != null
                && detalle.getObservaciones().startsWith(PREFIJO_DESCUENTO)) {
            return "El producto ya tiene un descuento aplicado.";
        }
        return null;
    }

    /**
     * Monto a descontar: porcentaje del precio, redondeado a 2 decimales (10 =
     * 10 %).
     */
    public BigDecimal calcularMontoDescuento(BigDecimal precio, Integer porcentaje) {
        return precio.multiply(BigDecimal.valueOf(porcentaje))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    /**
     * Suma de los precios (ya con descuentos). Los que no tienen precio no
     * suman.
     */
    public BigDecimal calcularTotal(List<OrdenProducto> detalles) {
        BigDecimal total = BigDecimal.ZERO;
        if (detalles != null) {
            for (OrdenProducto detalle : detalles) {
                if (detalle.getPrecio() != null) {
                    total = total.add(detalle.getPrecio());
                }
            }
        }
        return total;
    }
}
