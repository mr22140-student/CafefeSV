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

@Named("tipoDescuentoModel")
@ViewScoped
public class TipoDescuentoModel extends AbstractModel<TipoDescuento, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private TipoDescuentoDAO tdDAO;

    @Inject
    private DescuentoDAO descuentoDAO;

    @PostConstruct
    public void init() {
        iniciar();
    }

    @Override
    protected GenericDAO<TipoDescuento, UUID> getDAO() {
        return tdDAO;
    }

    @Override
    protected TipoDescuento crearNuevo() {
        TipoDescuento nuevo = new TipoDescuento(UUID.randomUUID());
        nuevo.setActivo(Boolean.TRUE);
        nuevo.setDescuentoMaximo(50);
        return nuevo;
    }

    /**
     * La base no tiene FK entre descuento y tipo_descuento, así que se valida
     * aquí que el tipo no esté en uso antes de eliminarlo.
     */
    @Override
    public void btnEliminarHandler() {
        if (registro != null && descuentoDAO.countByTipoDescuento(registro) > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_WARN, "No se puede eliminar",
                    "Hay descuentos que usan este tipo. Modifícalos o márcalo como inactivo."));
            return;
        }
        super.btnEliminarHandler();
    }
}