package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.AbstractDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.RolDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Rol;

/**
 *
 * @author daniel
 */ 
@Path("rol")
@RequestScoped
public class RolResource extends AbstractResource<Rol> {

    @Inject
    RolDAO rolDAO;

    public RolResource() {
        super(Rol.class);
    }

    @Override
    protected AbstractDAO<Rol, UUID> dao() {
        return rolDAO;
    }

    @Override
    protected UUID getId(Rol registro) {
        return registro.getIdRol();
    }

    @Override
    protected void setId(Rol registro, UUID id) {
        registro.setIdRol(id);
    }

    @Override
    protected void prepararNuevo(Rol registro) {
        if (registro.getActivo() == null) {
            registro.setActivo(Boolean.TRUE);
        }
    }

    /** Un rol con empleados no se puede desactivar. */
    @Override
    protected String validarModificar(Rol nuevo, Rol guardado) {
        return rolDAO.validarInactivar(nuevo);
    }

    /** Un rol con empleados no se puede eliminar. */
    @Override
    protected String validarEliminar(Rol guardado) {
        return rolDAO.validarEliminar(guardado);
    }
}
