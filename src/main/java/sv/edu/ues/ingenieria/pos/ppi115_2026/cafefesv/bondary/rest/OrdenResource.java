package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import java.util.Date;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.AbstractDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.OrdenDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Orden;

/**
 *
 * @author daniel
 */ 
@Path("orden")
@RequestScoped
public class OrdenResource extends AbstractResource<Orden> {

    @Inject
    OrdenDAO dao;

    public OrdenResource() {
        super(Orden.class);
    }

    @Override
    protected AbstractDAO<Orden, UUID> dao() {
        return dao;
    }

    @Override
    protected UUID getId(Orden registro) {
        return registro.getIdOrden();
    }

    @Override
    protected void setId(Orden registro, UUID id) {
        registro.setIdOrden(id);
    }

    @Override
    protected void resolverRelaciones(Orden registro) {
        registro.setIdEmpleadoRol(referencia(EmpleadoRol.class, registro.getIdEmpleadoRol(),
                EmpleadoRol::getIdEmpleadoRol, "idEmpleadoRol"));
    }

    /** Sin fecha, la orden toma la fecha y hora actual (igual que en JSF). */
    @Override
    protected void prepararNuevo(Orden registro) {
        if (registro.getFechaCreacion() == null) {
            registro.setFechaCreacion(new Date());
        }
    }

    /** Fecha obligatoria y solo un camarero puede tomar la orden. */
    @Override
    protected String validarCrear(Orden nuevo) {
        return dao.validarOrden(nuevo);
    }

    @Override
    protected String validarModificar(Orden nuevo, Orden guardado) {
        return dao.validarOrden(nuevo);
    }

    /** Una orden con productos no se puede eliminar. */
    @Override
    protected String validarEliminar(Orden guardado) {
        return dao.validarEliminar(guardado);
    }
}
