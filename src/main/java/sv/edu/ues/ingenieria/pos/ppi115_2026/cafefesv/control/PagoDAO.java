package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Pago;

@Stateless
public class PagoDAO extends AbstractDAO<Pago, UUID> {

    public static final String PENDIENTE = "PENDIENTE";
    public static final String COMPLETADO = "COMPLETADO";
    public static final String ANULADO = "ANULADO";
    public static final List<String> ESTADOS = Arrays.asList(PENDIENTE, COMPLETADO, ANULADO);

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public PagoDAO() {
        super(Pago.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Cuantos detalles (montos) tiene el pago.
     */
    public long countDetalles(Pago pago) {
        return em.createQuery(
                "SELECT COUNT(d) FROM PagoDetalle d WHERE d.idPago = :pago", Long.class)
                .setParameter("pago", pago)
                .getSingleResult();
    }

    /**
     * Suma de los montos registrados en los detalles del pago.
     */
    public BigDecimal sumaMontos(Pago pago) {
        BigDecimal suma = em.createQuery(
                "SELECT SUM(d.monto) FROM PagoDetalle d WHERE d.idPago = :pago", BigDecimal.class)
                .setParameter("pago", pago)
                .getSingleResult();
        return suma != null ? suma : BigDecimal.ZERO;
    }

    /**
     * Total de la factura (suma de sus productos).
     */
    public BigDecimal totalFactura(Factura factura) {
        BigDecimal total = em.createQuery(
                "SELECT SUM(x.precio) FROM FacturaOrdenProducto x WHERE x.idFactura = :factura", BigDecimal.class)
                .setParameter("factura", factura)
                .getSingleResult();
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * true si la factura ya tiene otro pago que no esta ANULADO. idExcluir es
     * el pago que se esta modificando (null al crear).
     */
    public boolean existePagoActivo(Factura factura, UUID idExcluir) {
        Long total = em.createQuery(
                "SELECT COUNT(x) FROM Pago x "
                + "WHERE x.idFactura = :factura "
                + "AND (x.estado IS NULL OR x.estado <> 'ANULADO') "
                + "AND x.idPago <> :excluir", Long.class)
                .setParameter("factura", factura)
                .setParameter("excluir", idExcluir != null ? idExcluir : new UUID(0L, 0L))
                .getSingleResult();
        return total > 0;
    }
        public List<Pago> findByEstado(String estado) {
        return em.createNamedQuery("Pago.findByEstado", Pago.class)
                .setParameter("estado", estado)
                .getResultList();
    }

    /**
     * Lo que falta por pagar: total de la factura menos lo ya pagado.
     * "montoYaGuardado" es el monto del detalle que se está modificando (ya
     * está sumado en la base), para no contarlo dos veces.
     */
    public BigDecimal calcularSaldo(Pago pago, BigDecimal montoYaGuardado) {
        if (pago == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = (pago.getIdFactura() != null) ? totalFactura(pago.getIdFactura()) : BigDecimal.ZERO;
        BigDecimal pagado = sumaMontos(pago).subtract(montoYaGuardado != null ? montoYaGuardado : BigDecimal.ZERO);
        return total.subtract(pagado);
    }

    /**
     * El pago necesita factura y un estado válido. Una factura solo puede
     * tener un pago que no esté ANULADO.
     *
     * @return el mensaje de error, o null si el pago está bien
     */
    public String validarPago(Pago pago) {
        if (pago == null) {
            return null;
        }
        if (pago.getIdFactura() == null) {
            return "Debe seleccionar la factura.";
        }
        if (!ESTADOS.contains(pago.getEstado())) {
            return "El estado no es válido.";
        }
        if (!ANULADO.equals(pago.getEstado()) && existePagoActivo(pago.getIdFactura(), pago.getIdPago())) {
            return "Esa factura ya tiene un pago registrado que no está anulado.";
        }
        return null;
    }

    /**
     * Un pago ANULADO ya no cambia, y uno COMPLETADO solo puede pasar a
     * ANULADO.
     *
     * @return el mensaje de error, o null si el cambio de estado se permite
     */
    public String validarCambioDeEstado(String estadoGuardado, String estadoNuevo) {
        if (ANULADO.equals(estadoGuardado)) {
            return "No se puede modificar un pago ANULADO.";
        }
        if (COMPLETADO.equals(estadoGuardado) && !COMPLETADO.equals(estadoNuevo) && !ANULADO.equals(estadoNuevo)) {
            return "Un pago COMPLETADO solo puede cambiarse a ANULADO.";
        }
        return null;
    }

    /**
     * Para pasar un pago a COMPLETADO, los montos registrados deben cubrir el
     * total de la factura. Si el pago no está pasando a COMPLETADO, no se
     * revisa nada.
     *
     * @return el mensaje de error, o null si se puede completar
     */
    public String validarCompletar(Pago pago, String estadoGuardado) {
        if (pago == null || !COMPLETADO.equals(pago.getEstado()) || COMPLETADO.equals(estadoGuardado)) {
            return null;
        }
        BigDecimal total = totalFactura(pago.getIdFactura());
        if (total.signum() <= 0) {
            return "La factura no tiene productos; no se puede completar el pago.";
        }
        BigDecimal pagado = sumaMontos(pago);
        if (pagado.compareTo(total) < 0) {
            return "No se puede completar: se han pagado $" + pagado + " de $" + total
                    + ". Registre los montos en Detalle de pago.";
        }
        return null;
    }

    /**
     * Un pago COMPLETADO no se puede eliminar, ni uno que ya tenga montos.
     *
     * @return el mensaje de error, o null si se puede eliminar
     */
    public String validarEliminar(Pago pago, String estadoGuardado) {
        if (pago == null) {
            return null;
        }
        if (COMPLETADO.equals(estadoGuardado)) {
            return "No se puede eliminar un pago COMPLETADO. Puede cambiarlo a ANULADO.";
        }
        if (countDetalles(pago) > 0) {
            return "El pago tiene montos registrados. Puede cambiarlo a ANULADO.";
        }
        return null;
    }

    /**
     * Estado al que debe pasar la factura según el cambio del pago:
     * COMPLETADO deja la factura PAGADA; de COMPLETADO a ANULADO la regresa a
     * PENDIENTE.
     *
     * @return el nuevo estado de la factura, o null si no cambia
     */
    public String calcularEstadoFactura(String estadoGuardado, String estadoNuevo) {
        if (COMPLETADO.equals(estadoNuevo) && !COMPLETADO.equals(estadoGuardado)) {
            return FacturaDAO.PAGADA;
        }
        if (COMPLETADO.equals(estadoGuardado) && ANULADO.equals(estadoNuevo)) {
            return FacturaDAO.PENDIENTE;
        }
        return null;
    }

}
