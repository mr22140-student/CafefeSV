package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.UUID;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.CaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoCaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;

/**
 *
 * @author valen
 */
@Named("tipoCaracteristicaModel")
@ViewScoped
public class TipoCaracteristicaModel extends AbstractModel<TipoCaracteristica, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private TipoCaracteristicaDAO tipoCaracteristicaDAO;
    @Inject
    private CaracteristicaDAO caracteristicaDAO;

    @PostConstruct
    public void init() {
        iniciar();
    }

    @Override
    protected GenericDAO<TipoCaracteristica, UUID> getDAO() {
        return tipoCaracteristicaDAO;
    }

    @Override
    protected TipoCaracteristica crearNuevo() {
        TipoCaracteristica nuevo = new TipoCaracteristica();
        nuevo.setIdTipoCaracteristica(UUID.randomUUID());
        nuevo.setActivo(Boolean.TRUE);
        return nuevo;
    }

    @Override
    public void btnCrearHandler() {
        if (expresionValida()) {
            super.btnCrearHandler();
        }
    }

    @Override
    public void btnModificarHandler() {
        if (expresionValida() && puedeInactivar()) {
            super.btnModificarHandler();
        }
    }

    /**
     * Verifica que la expresión regular escrita sea válida en Java (si viene
     * vacía, se acepta cualquier valor).
     */
    private boolean expresionValida() {
        if (registro == null || registro.getExpresionRegular() == null
                || registro.getExpresionRegular().isBlank()) {
            return true;
        }
        try {
            Pattern.compile(registro.getExpresionRegular().trim());
            registro.setExpresionRegular(registro.getExpresionRegular().trim());
            return true;
        } catch (PatternSyntaxException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Expresión regular inválida", ex.getDescription()));
            return false;
        }
    }

    /**
     * No se puede inactivar un tipo mientras tenga características ACTIVAS.
     */
    private boolean puedeInactivar() {
        if (registro == null || Boolean.TRUE.equals(registro.getActivo())) {
            return true;
        }
        long activas = caracteristicasDelTipo().stream()
                .filter(c -> Boolean.TRUE.equals(c.getActivo()))
                .count();
        if (activas > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "No se puede inactivar",
                    "Hay " + activas + " característica(s) activa(s) con este tipo. "
                    + "Inactívelas o cámbielas de tipo primero."));
            return false;
        }
        return true;
    }

    private List<Caracteristica> caracteristicasDelTipo() {
        return caracteristicaDAO.findByTipoCaracteristica(registro);
    }

    @Override
    public void btnEliminarHandler() {
        if (registro != null && !caracteristicasDelTipo().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "No se puede eliminar",
                    "Hay características que usan este tipo. Cámbielas de tipo o elimínelas primero."));
            return;
        }
        super.btnEliminarHandler();
    }

}
