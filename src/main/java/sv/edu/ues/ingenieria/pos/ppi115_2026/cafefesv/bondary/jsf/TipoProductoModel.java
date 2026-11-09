package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoProducto;

/**
 *
 * @author valen
 */
@Named("tipoProductoModel")
@ViewScoped
public class TipoProductoModel extends AbstractModel<TipoProducto, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private TipoProductoDAO tipoProductoDAO;

    @PostConstruct
    public void init() {
        iniciar();
    }

    @Override
    protected GenericDAO<TipoProducto, UUID> getDAO() {
        return tipoProductoDAO;
    }

    @Override
    protected TipoProducto crearNuevo() {
        TipoProducto nuevo = new TipoProducto(UUID.randomUUID());
        nuevo.setActivo(Boolean.TRUE);
        return nuevo;
    }

    @Override
    public void btnModificarHandler() {
        if (sinError("No se puede inactivar", tipoProductoDAO.validarInactivar(registro))) {
            super.btnModificarHandler();
        }
    }

    @Override
    public void btnEliminarHandler() {
        if (sinError("No se puede eliminar", tipoProductoDAO.validarEliminar(registro))) {
            super.btnEliminarHandler();
        }
    }

}
