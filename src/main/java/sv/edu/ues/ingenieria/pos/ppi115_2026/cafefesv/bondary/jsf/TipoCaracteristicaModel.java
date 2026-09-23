package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoCaracteristicaDAO;
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
}