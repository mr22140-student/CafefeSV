package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.AbstractDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoProducto;

/**
 *
 * @author daniel
 */
@Path("tipo_producto")
@RequestScoped
public class TipoProductoResource extends AbstractResource<TipoProducto> {

    @Inject
    TipoProductoDAO tipoProductoDAO;

    public TipoProductoResource() {
        super(TipoProducto.class);
    }

    @Override
    protected AbstractDAO<TipoProducto, UUID> dao() {
        return tipoProductoDAO;
    }

    @Override
    protected UUID getId(TipoProducto registro) {
        return registro.getIdTipoProducto();
    }

    @Override
    protected void setId(TipoProducto registro, UUID id) {
        registro.setIdTipoProducto(id);
    }

    @Override
    protected void prepararNuevo(TipoProducto registro) {
        if (registro.getActivo() == null) {
            registro.setActivo(Boolean.TRUE);
        }
    }

    /** Un tipo con productos activos no se puede desactivar. */
    @Override
    protected String validarModificar(TipoProducto nuevo, TipoProducto guardado) {
        return tipoProductoDAO.validarInactivar(nuevo);
    }

    /** Un tipo con productos no se puede eliminar. */
    @Override
    protected String validarEliminar(TipoProducto guardado) {
        return tipoProductoDAO.validarEliminar(guardado);
    }
}
