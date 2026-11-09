/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.UUID;
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
    public void btnModificarHandler() {
        if (!sinError("No se puede desactivar", rolDAO.validarInactivar(registro))) {
            registro.setActivo(Boolean.TRUE); // se regresa el check para que no quede desmarcado
            return;
        }
        super.btnModificarHandler();
    }

    @Override
    public void btnEliminarHandler() {
        if (sinError("No se puede eliminar", rolDAO.validarEliminar(registro))) {
            super.btnEliminarHandler();
        }
    }
}
