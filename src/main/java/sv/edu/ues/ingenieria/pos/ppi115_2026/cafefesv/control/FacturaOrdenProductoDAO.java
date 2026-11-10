package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.FacturaOrdenProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.OrdenProducto;

/**
 *
 * @author daniel
 */

@Stateless
public class FacturaOrdenProductoDAO extends AbstractDAO<FacturaOrdenProducto, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public FacturaOrdenProductoDAO() {
        super(FacturaOrdenProducto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<FacturaOrdenProducto> findByFactura(Factura factura) {
        return em.createQuery(
                "SELECT detalle FROM FacturaOrdenProducto detalle "
                + "WHERE detalle.idFactura = :factura",
                FacturaOrdenProducto.class
        )
                .setParameter("factura", factura)
                .getResultList();
    }

    public List<FacturaOrdenProducto> findByOrdenProducto(OrdenProducto ordenProducto) {
        return em.createQuery(
                "SELECT detalle FROM FacturaOrdenProducto detalle "
                + "WHERE detalle.idOrdenProducto = :ordenProducto",
                FacturaOrdenProducto.class
        )
                .setParameter("ordenProducto", ordenProducto)
                .getResultList();
    }

    /**
     * Productos de ordenes que todavia NO estan en ninguna factura. Trae el
     * producto y la orden en la misma consulta.
     */
    public List<OrdenProducto> findOrdenProductoDisponibles() {
        return em.createQuery(
                "SELECT detalleOrden FROM OrdenProducto detalleOrden "
                + "LEFT JOIN FETCH detalleOrden.idProducto "
                + "LEFT JOIN FETCH detalleOrden.idOrden "
                + "WHERE NOT EXISTS ("
                + "   SELECT facturado FROM FacturaOrdenProducto facturado WHERE facturado.idOrdenProducto = detalleOrden)",
                OrdenProducto.class)
                .getResultList();
    }

    /**
     * Solo se pueden tocar productos de facturas PENDIENTES.
     *
     * @return el mensaje de error, o null si la factura se puede cambiar
     */
    public String validarFacturaEditable(Factura factura) {
        if (factura != null && !FacturaDAO.PENDIENTE.equals(factura.getEstado())) {
            return "La factura está " + factura.getEstado() + "; solo se pueden cambiar facturas PENDIENTES.";
        }
        return null;
    }

    /**
     * El producto de la factura necesita una factura PENDIENTE, un producto de
     * orden, un precio que no sea negativo, y ese producto de la orden no puede
     * estar ya en otra factura.
     *
     * @return el mensaje de error, o null si está bien
     */
    public String validarDetalle(FacturaOrdenProducto detalle) {
        if (detalle == null) {
            return null;
        }
        if (detalle.getIdFactura() == null) {
            return "Debe seleccionar la factura.";
        }
        String facturaNoEditable = validarFacturaEditable(detalle.getIdFactura());
        if (facturaNoEditable != null) {
            return facturaNoEditable;
        }
        if (detalle.getIdOrdenProducto() == null) {
            return "Debe seleccionar el producto de la orden.";
        }
        if (detalle.getPrecio() == null || detalle.getPrecio().signum() < 0) {
            return "El precio debe ser mayor o igual a cero.";
        }
        boolean yaFacturado = findByOrdenProducto(detalle.getIdOrdenProducto()).stream()
                .anyMatch(otroDetalle -> !otroDetalle.getIdFacturaOrdenProducto().equals(detalle.getIdFacturaOrdenProducto()));
        if (yaFacturado) {
            return "Ese producto de la orden ya está facturado.";
        }
        return null;
    }

    /**
     * Suma de los precios. Los que no tienen precio no suman.
     */
    public BigDecimal calcularTotal(List<FacturaOrdenProducto> detalles) {
        BigDecimal total = BigDecimal.ZERO;
        if (detalles != null) {
            for (FacturaOrdenProducto detalle : detalles) {
                if (detalle.getPrecio() != null) {
                    total = total.add(detalle.getPrecio());
                }
            }
        }
        return total;
    }
}
