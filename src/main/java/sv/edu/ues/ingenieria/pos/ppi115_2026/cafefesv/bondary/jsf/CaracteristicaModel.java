package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.CaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoCaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoCaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;

/**
 *
 * @author valen
 */
@Named("caracteristicaModel")
@ViewScoped
public class CaracteristicaModel extends AbstractModel<Caracteristica, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private CaracteristicaDAO caracteristicaDAO;

    @Inject
    private TipoCaracteristicaDAO tipoCaracteristicaDAO;

    @Inject
    private ProductoCaracteristicaDAO pcDAO;

    private List<TipoCaracteristica> listaTipoCaracteristica;

    @PostConstruct
    public void init() {
        iniciar();
        if (tipoCaracteristicaDAO != null) {
            this.listaTipoCaracteristica = tipoCaracteristicaDAO.findAll();
        }
    }

    @Override
    protected GenericDAO<Caracteristica, UUID> getDAO() {
        return caracteristicaDAO;
    }

    @Override
    protected Caracteristica crearNuevo() {
        Caracteristica nuevo = new Caracteristica();
        nuevo.setIdCaracteristica(UUID.randomUUID());
        nuevo.setActivo(true);
        return nuevo;
    }

    @Override
    public void btnCrearHandler() {
        if (tipoActivo()) {
            super.btnCrearHandler();
        }
    }

    @Override
    public void btnModificarHandler() {
        if (tipoActivo()) {
            super.btnModificarHandler();
        }
    }

    @Override
    public void btnEliminarHandler() {
        if (registro != null && !pcDAO.findByCaracteristica(registro).isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "No se puede eliminar",
                    "Hay productos que tienen esta característica. Quítesela primero."));
            return;
        }
        super.btnEliminarHandler();
    }

    public List<TipoCaracteristica> getListaTipoCaracteristica() {
        return listaTipoCaracteristica;
    }

    public void setListaTipoCaracteristica(List<TipoCaracteristica> listaTipoCaracteristica) {
        this.listaTipoCaracteristica = listaTipoCaracteristica;
    }

    /**
     * Buscador del autoComplete: solo muestra tipos ACTIVOS.
     */
    public List<TipoCaracteristica> completarTipoCaracteristica(String query) {
        this.listaTipoCaracteristica = tipoCaracteristicaDAO.findAll();
        String queryLower = query == null ? "" : query.toLowerCase().trim();
        return this.listaTipoCaracteristica.stream()
                .filter(tipo -> Boolean.TRUE.equals(tipo.getActivo()))
                .filter(tipo -> tipo.getNombre() != null
                        && tipo.getNombre().toLowerCase().contains(queryLower))
                .collect(Collectors.toList());
    }

    /**
     * No se puede guardar una característica con un tipo inactivo.
     */
    private boolean tipoActivo() {
        if (registro != null && Boolean.TRUE.equals(registro.getActivo()) && registro.getIdTipoCaracteristica() != null
                && !Boolean.TRUE.equals(registro.getIdTipoCaracteristica().getActivo())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Tipo inactivo",
                    "El tipo \"" + registro.getIdTipoCaracteristica().getNombre()
                    + "\" está inactivo. Seleccione un tipo activo."));
            return false;
        }
        return true;
    }

}
