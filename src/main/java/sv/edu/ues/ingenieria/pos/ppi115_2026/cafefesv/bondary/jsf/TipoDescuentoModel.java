package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.DescuentoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoDescuentoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoDescuento;
/**
 *
 * @author valen
 */
@Named("tipoDescuentoModel")
@ViewScoped
public class TipoDescuentoModel extends AbstractModel<TipoDescuento, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private TipoDescuentoDAO tipoDescuentoDAO;

    @PostConstruct
    public void init() {
        iniciar();
    }

    @Override
    protected GenericDAO<TipoDescuento, UUID> getDAO() {
        return tipoDescuentoDAO;
    }

    @Override
    protected TipoDescuento crearNuevo() {
        TipoDescuento nuevo = new TipoDescuento(UUID.randomUUID());
        nuevo.setActivo(Boolean.TRUE);
        nuevo.setDescuentoMaximo(50);
        return nuevo;
    }

        @Override
    public void btnModificarHandler() {
        if (sinError("No se puede inactivar", tipoDescuentoDAO.validarInactivar(registro))) {
            super.btnModificarHandler();
        }
    }

    @Override
    public void btnEliminarHandler() {
        if (sinError("No se puede eliminar", tipoDescuentoDAO.validarEliminar(registro))) {
            super.btnEliminarHandler();
        }
    }

}
