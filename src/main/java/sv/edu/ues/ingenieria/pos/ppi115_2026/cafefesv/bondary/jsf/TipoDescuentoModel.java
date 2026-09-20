package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoDescuentoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoDescuento;

@Named("tipoDescuentoModel")
@ViewScoped
public class TipoDescuentoModel extends AbstractModel<TipoDescuento, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private TipoDescuentoDAO tdDAO;

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
}