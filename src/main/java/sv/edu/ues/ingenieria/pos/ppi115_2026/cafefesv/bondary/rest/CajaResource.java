package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.AbstractDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.CajaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caja;

/**
 *
 * @author daniel
 */
@Path("caja")
@RequestScoped
public class CajaResource extends AbstractResource<Caja> {

    @Inject
    CajaDAO cajaDAO;

    public CajaResource() {
        super(Caja.class);
    }

    @Override
    protected AbstractDAO<Caja, UUID> dao() {
        return cajaDAO;
    }

    @Override
    protected UUID getId(Caja registro) {
        return registro.getIdCaja();
    }

    @Override
    protected void setId(Caja registro, UUID id) {
        registro.setIdCaja(id);
    }

    @Override
    protected void prepararNuevo(Caja registro) {
        if (registro.getActivo() == null) {
            registro.setActivo(Boolean.TRUE);
        }
    }

    /** Una caja con facturas no se puede eliminar. */
    @Override
    protected String validarEliminar(Caja guardado) {
        return cajaDAO.validarEliminar(guardado);
    }
}
