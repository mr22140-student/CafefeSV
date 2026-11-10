package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.AbstractDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Empleado;


/**
 *
 * @author daniel
 */
@Path("empleado")
@RequestScoped
public class EmpleadoResource extends AbstractResource<Empleado> {

    @Inject
    EmpleadoDAO dao;

    public EmpleadoResource() {
        super(Empleado.class);
    }

    @Override
    protected AbstractDAO<Empleado, UUID> dao() {
        return dao;
    }

    @Override
    protected UUID getId(Empleado registro) {
        return registro.getIdEmpleado();
    }

    @Override
    protected void setId(Empleado registro, UUID id) {
        registro.setIdEmpleado(id);
    }

    @Override
    protected void prepararNuevo(Empleado registro) {
        if (registro.getActivo() == null) {
            registro.setActivo(Boolean.TRUE);
        }
    }

    /** Un empleado con órdenes o facturas no se puede eliminar. */
    @Override
    protected String validarEliminar(Empleado guardado) {
        return dao.validarEliminar(guardado);
    }

    /**
     * Igual que en EmpleadoModel: primero se borran sus asignaciones de rol y
     * luego el empleado, en la misma transacción.
     */
    @Override
    protected void eliminar(Empleado guardado) {
        dao.eliminarConRoles(guardado);
    }
}
