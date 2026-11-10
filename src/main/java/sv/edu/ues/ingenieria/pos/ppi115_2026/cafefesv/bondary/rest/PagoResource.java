package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.AbstractDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.FacturaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.PagoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Pago;

/**
 * /resources/v1/pago
 */
@Path("pago")
@RequestScoped
public class PagoResource extends AbstractResource<Pago> {

    @Inject
    PagoDAO pagoDAO;

    @Inject
    FacturaDAO facturaDAO;

    public PagoResource() {
        super(Pago.class);
    }

    @Override
    protected AbstractDAO<Pago, UUID> dao() {
        return pagoDAO;
    }

    @Override
    protected UUID getId(Pago registro) {
        return registro.getIdPago();
    }

    @Override
    protected void setId(Pago registro, UUID id) {
        registro.setIdPago(id);
    }

    @Override
    protected void resolverRelaciones(Pago registro) {
        registro.setIdFactura(referencia(Factura.class, registro.getIdFactura(), Factura::getIdFactura, "idFactura"));
    }

    /** Todo pago nuevo inicia PENDIENTE, igual que en PagoModel. */
    @Override
    protected void prepararNuevo(Pago registro) {
        registro.setEstado(PagoDAO.PENDIENTE);
    }

    /** Factura, estado válido y un solo pago no anulado por factura. */
    @Override
    protected String validarCrear(Pago nuevo) {
        return pagoDAO.validarPago(nuevo);
    }

    /**
     * Mismas tres reglas que PagoModel, usando el estado guardado en la base
     * en lugar de estadoOriginal.
     */
    @Override
    protected String validarModificar(Pago nuevo, Pago guardado) {
        String estadoGuardado = guardado.getEstado();
        return primerError(
                pagoDAO.validarCambioDeEstado(estadoGuardado, nuevo.getEstado()),
                pagoDAO.validarPago(nuevo),
                pagoDAO.validarCompletar(nuevo, estadoGuardado));
    }

    /** Al COMPLETAR, la factura queda PAGADA; al anular un COMPLETADO, vuelve a PENDIENTE. */
    @Override
    protected void despuesDeModificar(Pago actualizado, Pago guardado) {
        String estadoFactura = pagoDAO.calcularEstadoFactura(guardado.getEstado(), actualizado.getEstado());
        if (estadoFactura != null && actualizado.getIdFactura() != null) {
            Factura factura = facturaDAO.findById(actualizado.getIdFactura().getIdFactura());
            if (factura != null) {
                factura.setEstado(estadoFactura);
                facturaDAO.update(factura);
            }
        }
    }

    /** Un pago COMPLETADO o con montos no se puede eliminar. */
    @Override
    protected String validarEliminar(Pago guardado) {
        return pagoDAO.validarEliminar(guardado, guardado.getEstado());
    }
}
