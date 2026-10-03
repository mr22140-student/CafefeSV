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
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoRolDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.RolDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Rol;

/**
 *
 * @author valen
 */
@Named("rolModel")
@ViewScoped
public class RolModel extends AbstractModel<Rol, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private RolDAO rolDAO;

    @Inject
    private EmpleadoRolDAO empleadoRolDAO;

    @PostConstruct
    public void init() {
        iniciar();
    }

    @Override
    protected GenericDAO<Rol, UUID> getDAO() {
        return rolDAO;
    }

    @Override
    protected Rol crearNuevo() {
        Rol nuevo = new Rol(UUID.randomUUID());
        nuevo.setActivo(Boolean.TRUE);
        return nuevo;
    }

    @Override
    public void btnEliminarHandler() {
        if (registro != null && !empleadoRolDAO.findByRol(registro).isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    "No se puede eliminar: el rol está asignado a uno o más empleados. Puede desmarcar 'Activo'.", null));
            return;
        }
        super.btnEliminarHandler();
    }
    
        @Override
    public void btnModificarHandler() {
        if (registro != null && !Boolean.TRUE.equals(registro.getActivo())) {
            int empleados = empleadoRolDAO.findByRol(registro).size();
            if (empleados > 0) {
                registro.setActivo(Boolean.TRUE); // se regresa el check para que no quede desmarcado
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                        FacesMessage.SEVERITY_WARN,
                        "No se puede desactivar: el rol está asignado a " + empleados
                        + " empleado(s). Primero cámbieles el rol.", null));
                return;
            }
        }
        super.btnModificarHandler();
    }
}
