package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.CaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
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

    public List<TipoCaracteristica> getListaTipoCaracteristica() {
        return listaTipoCaracteristica;
    }

    public void setListaTipoCaracteristica(List<TipoCaracteristica> listaTipoCaracteristica) {
        this.listaTipoCaracteristica = listaTipoCaracteristica;
    }
    public List<TipoCaracteristica> completarTipoCaracteristica(String query) {
    if (this.listaTipoCaracteristica == null) {
        return Collections.emptyList();
    }
    
    String queryLower = query.toLowerCase().trim();
    return this.listaTipoCaracteristica.stream()
            .filter(tipo -> tipo.getNombre() != null && 
                            tipo.getNombre().toLowerCase().contains(queryLower))
            .collect(Collectors.toList());
}
}