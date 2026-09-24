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
    private TipoProductoDAO tpDAO;

    @PostConstruct
    public void init() {
        iniciar();
    }

    @Override
    protected GenericDAO<TipoProducto, UUID> getDAO() {
        return tpDAO;
    }

    @Override
    protected TipoProducto crearNuevo() {
        TipoProducto nuevo = new TipoProducto(UUID.randomUUID());
        nuevo.setActivo(Boolean.TRUE);
        return nuevo;
    }
}
