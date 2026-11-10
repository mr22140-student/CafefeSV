package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import java.util.Date;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.AbstractDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.FacturaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caja;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;

/**
 *
 * @author daniel
 */
@Path("factura")
@RequestScoped
public class FacturaResource extends AbstractResource<Factura> {

    private static final String CLIENTE_DEFECTO = "Consumidor final";

    @Inject
    FacturaDAO dao;

    public FacturaResource() {
        super(Factura.class);
    }

    @Override
    protected AbstractDAO<Factura, UUID> dao() {
        return dao;
    }

    @Override
    protected UUID getId(Factura registro) {
        return registro.getIdFactura();
    }

    @Override
    protected void setId(Factura registro, UUID id) {
        registro.setIdFactura(id);
    }

    @Override
    protected void resolverRelaciones(Factura registro) {
        registro.setIdCaja(referencia(Caja.class, registro.getIdCaja(), Caja::getIdCaja, "idCaja"));
        registro.setIdEmpleadoRol(referencia(EmpleadoRol.class, registro.getIdEmpleadoRol(),
                EmpleadoRol::getIdEmpleadoRol, "idEmpleadoRol"));
    }

    /** Toda factura nueva inicia PENDIENTE, igual que en FacturaModel. */
    @Override
    protected void prepararNuevo(Factura registro) {
        registro.setEstado(FacturaDAO.PENDIENTE);
    }

    /** Sin cliente queda "Consumidor final"; sin fecha, la actual. */
    @Override
    protected void normalizar(Factura registro) {
        String cliente = (registro.getCliente() != null) ? registro.getCliente().trim() : "";
        registro.setCliente(cliente.isEmpty() ? CLIENTE_DEFECTO : cliente);
        if (registro.getFechaFacturacion() == null) {
            registro.setFechaFacturacion(new Date());
        }
    }

    /** Caja, cajero, cliente de máximo 155 caracteres y estado válido. */
    @Override
    protected String validarCrear(Factura nuevo) {
        return dao.validarFactura(nuevo);
    }

    /** Una factura ANULADA (según la base) ya no se modifica. */
    @Override
    protected String validarModificar(Factura nuevo, Factura guardado) {
        return primerError(dao.validarModificable(guardado.getEstado()), dao.validarFactura(nuevo));
    }

    /** Una factura con productos o pagos no se puede eliminar. */
    @Override
    protected String validarEliminar(Factura guardado) {
        return dao.validarEliminar(guardado);
    }
}
