package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;

/**
 *
 * @author daniel
 */
@Stateless
public class FacturaDAO extends AbstractDAO<Factura, UUID> {

    public static final String PENDIENTE = "PENDIENTE";
    public static final String PAGADA = "PAGADA";
    public static final String ANULADA = "ANULADA";
    public static final List<String> ESTADOS = Arrays.asList(PENDIENTE, PAGADA, ANULADA);

    /**
     * Largo máximo del nombre del cliente (columna cliente).
     */
    private static final int LARGO_MAXIMO_CLIENTE = 155;

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public FacturaDAO() {
        super(Factura.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Cuantos productos tiene la factura (tabla factura_orden_producto).
     */
    public long countProductos(Factura factura) {
        return em.createQuery(
                "SELECT COUNT(x) FROM FacturaOrdenProducto x WHERE x.idFactura = :factura", Long.class)
                .setParameter("factura", factura)
                .getSingleResult();
    }

    /**
     * Cuantos pagos tiene la factura (tabla pago).
     */
    public long countPagos(Factura factura) {
        return em.createQuery(
                "SELECT COUNT(x) FROM Pago x WHERE x.idFactura = :factura", Long.class)
                .setParameter("factura", factura)
                .getSingleResult();
    }

    public List<Factura> findByEstado(String estado) {
        return em.createNamedQuery("Factura.findByEstado", Factura.class)
                .setParameter("estado", estado)
                .getResultList();
    }

    /**
     * Facturas PENDIENTES, las más recientes primero. Llenan los combos de Pago
     * y de Productos de la factura.
     */
    public List<Factura> buscarPendientes() {
        List<Factura> pendientes = new ArrayList<>(findByEstado(PENDIENTE));
        pendientes.sort(Comparator.comparing(Factura::getFechaFacturacion,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return pendientes;
    }

    /**
     * La factura necesita caja, cajero, un cliente de máximo 155 caracteres y
     * un estado válido.
     *
     * @return el mensaje de error, o null si la factura está bien
     */
    public String validarFactura(Factura factura) {
        if (factura == null) {
            return null;
        }
        if (factura.getIdCaja() == null) {
            return "Debe seleccionar la caja.";
        }
        if (factura.getIdEmpleadoRol() == null) {
            return "Debe seleccionar el cajero.";
        }
        if (factura.getCliente() != null && factura.getCliente().length() > LARGO_MAXIMO_CLIENTE) {
            return "El nombre del cliente no puede superar " + LARGO_MAXIMO_CLIENTE + " caracteres.";
        }
        if (!ESTADOS.contains(factura.getEstado())) {
            return "El estado no es válido.";
        }
        return null;
    }

    /**
     * Una factura ANULADA ya no se puede modificar. Se revisa con el estado que
     * tenía al seleccionarla, no con el que se eligió en el formulario.
     *
     * @return el mensaje de error, o null si se puede modificar
     */
    public String validarModificable(String estadoGuardado) {
        if (ANULADA.equals(estadoGuardado)) {
            return "No se puede modificar una factura ANULADA.";
        }
        return null;
    }

    /**
     * Una factura con productos o pagos no se puede eliminar.
     *
     * @return el mensaje de error, o null si se puede eliminar
     */
    public String validarEliminar(Factura factura) {
        if (factura != null && (countProductos(factura) > 0 || countPagos(factura) > 0)) {
            return "La factura tiene productos o pagos. Puede cambiar su estado a ANULADA.";
        }
        return null;
    }
}
