package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoTipoProductoDAO;
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

    @Inject
    private ProductoTipoProductoDAO ptpDAO;

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

    @Override
    public void btnModificarHandler() {
        if (puedeInactivar()) {
            super.btnModificarHandler();
        }
    }

    @Override
    public void btnEliminarHandler() {
        if (registro != null && !ptpDAO.findByTipoProducto(registro).isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "No se puede eliminar",
                    "Hay productos que tienen asignado este tipo. Quíteselo primero."));
            return;
        }
        super.btnEliminarHandler();
    }

    /**
     * No se puede inactivar un tipo mientras lo tenga asignado algún producto
     * ACTIVO.
     */
    private boolean puedeInactivar() {
        if (registro == null || Boolean.TRUE.equals(registro.getActivo())) {
            return true;
        }
        long activos = ptpDAO.findByTipoProducto(registro).stream()
                .filter(x -> x.getProducto() != null && Boolean.TRUE.equals(x.getProducto().getActivo()))
                .count();
        if (activos > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "No se puede inactivar",
                    "Hay " + activos + " producto(s) activo(s) con este tipo. "
                    + "Quíteselo o inactive esos productos primero."));
            return false;
        }
        return true;
    }

}
