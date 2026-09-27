/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.CajaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.FacturaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caja;

/**
 *
 * @author valen
 */
@Named("cajaModel")
@ViewScoped
public class CajaModel extends AbstractModel<Caja, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private CajaDAO cajaDAO;

    @Inject
    private FacturaDAO facturaDAO;

    @PostConstruct
    public void init() {
        iniciar();
    }

    @Override
    protected GenericDAO<Caja, UUID> getDAO() {
        return cajaDAO;
    }

    @Override
    protected Caja crearNuevo() {
        Caja nueva = new Caja();
        nueva.setIdCaja(UUID.randomUUID());
        nueva.setActivo(true);
        return nueva;
    }

    @Override
    public void btnEliminarHandler() {
        if (registro != null && facturaDAO.countByCaja(registro) > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_WARN, "No se puede eliminar: la caja tiene facturas registradas.", null));
            return;
        }
        super.btnEliminarHandler();
    }
}
