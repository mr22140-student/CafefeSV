package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.AbstractDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.PagoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.PagoDetalleDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Pago;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.PagoDetalle;

/**
 *
 * @author daniel
 */
@Path("pago_detalle")
@RequestScoped
public class PagoDetalleResource extends AbstractResource<PagoDetalle> {

    @Inject
    PagoDetalleDAO pagoDetalleDAO;

    @Inject
    PagoDAO pagoDAO;

    public PagoDetalleResource() {
        super(PagoDetalle.class);
    }

    @Override
    protected AbstractDAO<PagoDetalle, UUID> dao() {
        return pagoDetalleDAO;
    }

    @Override
    protected UUID getId(PagoDetalle registro) {
        return registro.getIdPagoDetalle();
    }

    @Override
    protected void setId(PagoDetalle registro, UUID id) {
        registro.setIdPagoDetalle(id);
    }

    @Override
    protected void resolverRelaciones(PagoDetalle registro) {
        registro.setIdPago(referencia(Pago.class, registro.getIdPago(), Pago::getIdPago, "idPago"));
    }

    /** Sin tipo de pago, EFECTIVO (igual que en PagoDetalleModel). */
    @Override
    protected void prepararNuevo(PagoDetalle registro) {
        if (registro.getTipoPago() == null) {
            registro.setTipoPago(PagoDetalleDAO.EFECTIVO);
        }
    }

    /** Una referencia vacía se guarda como null. */
    @Override
    protected void normalizar(PagoDetalle registro) {
        String referencia = (registro.getReferenciaExterna() != null) ? registro.getReferenciaExterna().trim() : "";
        registro.setReferenciaExterna(referencia.isEmpty() ? null : referencia);
    }

    /** Pago PENDIENTE, tipo válido, monto > 0, referencia y no pasarse del saldo. */
    @Override
    protected String validarCrear(PagoDetalle nuevo) {
        BigDecimal saldo = (nuevo.getIdPago() != null)
                ? pagoDAO.calcularSaldo(nuevo.getIdPago(), BigDecimal.ZERO)
                : null;
        return pagoDetalleDAO.validarDetalle(nuevo, saldo);
    }

    /**
     * Si el monto sigue en el mismo pago, el monto guardado ya está sumado en
     * la base: se descuenta para no contarlo dos veces (reemplaza a
     * montoOriginal del model).
     */
    @Override
    protected String validarModificar(PagoDetalle nuevo, PagoDetalle guardado) {
        BigDecimal yaGuardado = (guardado.getIdPago() != null && guardado.getIdPago().equals(nuevo.getIdPago()))
                ? guardado.getMonto()
                : BigDecimal.ZERO;
        BigDecimal saldo = (nuevo.getIdPago() != null)
                ? pagoDAO.calcularSaldo(nuevo.getIdPago(), yaGuardado)
                : null;
        return primerError(pagoDetalleDAO.validarPagoEditable(guardado.getIdPago()), pagoDetalleDAO.validarDetalle(nuevo, saldo));
    }

    /** Solo se quitan montos de pagos PENDIENTES. */
    @Override
    protected String validarEliminar(PagoDetalle guardado) {
        return pagoDetalleDAO.validarPagoEditable(guardado.getIdPago());
    }

    /** GET /pago_detalle/pago/{id}: los montos de un pago. */
    @GET
    @Path("pago/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response porPago(@PathParam("id") String id) {
        return listarPorPadre(id, Pago.class, pagoDetalleDAO::findByPago);
    }
}
