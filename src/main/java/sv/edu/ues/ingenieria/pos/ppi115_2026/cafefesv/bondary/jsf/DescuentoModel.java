package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.DescuentoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoDescuentoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoDescuento;

/**
 *
 * @author valen
 */
@Named("descuentoModel")
@ViewScoped
public class DescuentoModel extends AbstractModel<Descuento, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private DescuentoDAO descuentoDAO;

    @Inject
    private TipoDescuentoDAO tipoDescuentoDAO;

    private List<TipoDescuento> listaTipoDescuento;

    @PostConstruct
    public void init() {
        iniciar();
        listaTipoDescuento = tipoDescuentoDAO.findAll();
    }

    @Override
    protected GenericDAO<Descuento, UUID> getDAO() {
        return descuentoDAO;
    }

    @Override
    protected Descuento crearNuevo() {
        Descuento nuevo = new Descuento(UUID.randomUUID());
        nuevo.setFechaDesde(new Date());
        return nuevo;
    }

    @Override
    public void btnCrearHandler() {
        if (sinError("Fechas inválidas", descuentoDAO.validarFechas(registro))
                && sinError("Tipo inactivo", descuentoDAO.validarTipoActivo(registro))) {
            super.btnCrearHandler();
        }
    }

    @Override
    public void btnModificarHandler() {
        if (sinError("Fechas inválidas", descuentoDAO.validarFechas(registro))
                && sinError("Tipo inactivo", descuentoDAO.validarTipoActivo(registro))) {
            super.btnModificarHandler();
        }
    }

    /**
     * Estado del descuento para la columna de vigencia de la tabla.
     */
    public String vigencia(Descuento descuento) {
        return descuentoDAO.calcularVigencia(descuento);
    }

    public List<TipoDescuento> getListaTipoDescuento() {
        return listaTipoDescuento;
    }
}
