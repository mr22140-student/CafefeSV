package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
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
        if (fechasValidas()) {
            super.btnCrearHandler();
        }
    }

    @Override
    public void btnModificarHandler() {
        if (fechasValidas()) {
            super.btnModificarHandler();
        }
    }

    /** La fecha hasta es opcional, pero si viene no puede ser anterior a la fecha desde. */
    private boolean fechasValidas() {
        if (registro != null && registro.getFechaDesde() != null && registro.getFechaHasta() != null
                && registro.getFechaHasta().before(registro.getFechaDesde())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Fechas inválidas", "La fecha hasta no puede ser anterior a la fecha desde."));
            return false;
        }
        return true;
    }

    /** Estado calculado del descuento según la fecha actual, para mostrarlo en la tabla. */
    public String vigencia(Descuento d) {
        if (d == null || d.getFechaDesde() == null) {
            return "SIN FECHA";
        }
        Date ahora = new Date();
        if (ahora.before(d.getFechaDesde())) {
            return "PROGRAMADO";
        }
        if (d.getFechaHasta() != null && ahora.after(d.getFechaHasta())) {
            return "VENCIDO";
        }
        return "VIGENTE";
    }

    public List<TipoDescuento> getListaTipoDescuento() {
        return listaTipoDescuento;
    }
}