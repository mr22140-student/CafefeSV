package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;
import java.util.Arrays;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Pago;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.PagoDetalle;

/**
 *
 * @author daniel
 */
@Stateless
public class PagoDetalleDAO extends AbstractDAO<PagoDetalle, UUID> {

    public static final String EFECTIVO = "EFECTIVO";
    public static final String TARJETA = "TARJETA";
    public static final String TRANSFERENCIA = "TRANSFERENCIA";
    public static final List<String> TIPOS_PAGO = Arrays.asList(EFECTIVO, TARJETA, TRANSFERENCIA);

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public PagoDetalleDAO() {
        super(PagoDetalle.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<PagoDetalle> findByPago(Pago pago) {
        return em.createQuery(
                "SELECT x FROM PagoDetalle x "
                + "WHERE x.idPago = :pago",
                PagoDetalle.class
        )
                .setParameter("pago", pago)
                .getResultList();
    }

    /**
     * Solo se pueden tocar montos de pagos PENDIENTES.
     *
     * @return el mensaje de error, o null si el pago se puede cambiar
     */
    public String validarPagoEditable(Pago pago) {
        if (pago != null && !PagoDAO.PENDIENTE.equals(pago.getEstado())) {
            return "El pago está " + pago.getEstado() + "; solo se pueden cambiar montos de pagos PENDIENTES.";
        }
        return null;
    }

    /**
     * El monto necesita un pago PENDIENTE, un tipo de pago válido, un monto
     * mayor que cero que no pase de lo que falta por pagar, y referencia si no
     * es en efectivo.
     *
     * @param saldo lo que falta por pagar en ese pago (PagoDAO.calcularSaldo)
     * @return el mensaje de error, o null si el monto está bien
     */
    public String validarDetalle(PagoDetalle detalle, BigDecimal saldo) {
        if (detalle == null) {
            return null;
        }
        if (detalle.getIdPago() == null) {
            return "Debe seleccionar el pago.";
        }
        String pagoNoEditable = validarPagoEditable(detalle.getIdPago());
        if (pagoNoEditable != null) {
            return pagoNoEditable;
        }
        if (!TIPOS_PAGO.contains(detalle.getTipoPago())) {
            return "Debe seleccionar el tipo de pago.";
        }
        if (detalle.getMonto() == null || detalle.getMonto().signum() <= 0) {
            return "El monto debe ser mayor a cero.";
        }
        boolean sinReferencia = detalle.getReferenciaExterna() == null || detalle.getReferenciaExterna().isBlank();
        if (!EFECTIVO.equals(detalle.getTipoPago()) && sinReferencia) {
            return "Para " + detalle.getTipoPago()
                    + " debe indicar la referencia (número de autorización o transferencia).";
        }
        if (saldo != null && detalle.getMonto().compareTo(saldo) > 0) {
            return "El monto supera lo que falta por pagar ($" + saldo + ").";
        }
        return null;
    }
}
