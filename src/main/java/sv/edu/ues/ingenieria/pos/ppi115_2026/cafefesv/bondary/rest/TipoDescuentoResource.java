package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.AbstractDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoDescuentoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoDescuento;

/**
 * /resources/v1/tipo_descuento
 */
@Path("tipo_descuento")
@RequestScoped
public class TipoDescuentoResource extends AbstractResource<TipoDescuento> {

    @Inject
    TipoDescuentoDAO tipoDescuentoDAO;

    public TipoDescuentoResource() {
        super(TipoDescuento.class);
    }

    @Override
    protected AbstractDAO<TipoDescuento, UUID> dao() {
        return tipoDescuentoDAO;
    }

    @Override
    protected UUID getId(TipoDescuento registro) {
        return registro.getIdTipoDescuento();
    }

    @Override
    protected void setId(TipoDescuento registro, UUID id) {
        registro.setIdTipoDescuento(id);
    }

    /** Igual que TipoDescuentoModel: activo y máximo de 50 % si no vienen. */
    @Override
    protected void prepararNuevo(TipoDescuento registro) {
        if (registro.getActivo() == null) {
            registro.setActivo(Boolean.TRUE);
        }
        if (registro.getDescuentoMaximo() == null) {
            registro.setDescuentoMaximo(50);
        }
    }

    /** Un tipo con descuentos vigentes no se puede desactivar. */
    @Override
    protected String validarModificar(TipoDescuento nuevo, TipoDescuento guardado) {
        return tipoDescuentoDAO.validarInactivar(nuevo);
    }

    /** Un tipo con descuentos no se puede eliminar. */
    @Override
    protected String validarEliminar(TipoDescuento guardado) {
        return tipoDescuentoDAO.validarEliminar(guardado);
    }
}
