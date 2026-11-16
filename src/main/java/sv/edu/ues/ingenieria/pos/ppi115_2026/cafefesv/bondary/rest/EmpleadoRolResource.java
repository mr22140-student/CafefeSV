package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.AbstractDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoRolDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Empleado;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Rol;

/**
 *
 * @author daniel
 */
@Path("empleado_rol")
@RequestScoped
public class EmpleadoRolResource extends AbstractResource<EmpleadoRol> {

    @Inject
    EmpleadoRolDAO empleadoRolDAO;

    public EmpleadoRolResource() {
        super(EmpleadoRol.class);
    }

    @Override
    protected AbstractDAO<EmpleadoRol, UUID> dao() {
        return empleadoRolDAO;
    }

    @Override
    protected UUID getId(EmpleadoRol registro) {
        return registro.getIdEmpleadoRol();
    }

    @Override
    protected void setId(EmpleadoRol registro, UUID id) {
        registro.setIdEmpleadoRol(id);
    }

    /**
     * idRol es una relación (@ManyToOne). idEmpleado es un UUID suelto, así que solo se revisa que exista.
     */
    @Override
    protected void resolverRelaciones(EmpleadoRol registro) {
        registro.setIdRol(referencia(Rol.class, registro.getIdRol(), Rol::getIdRol, "idRol"));
        if (registro.getIdEmpleado() != null
                && empleadoRolDAO.buscarReferencia(Empleado.class, registro.getIdEmpleado()) == null) {
            throw new DatoInvalidoException("idEmpleado", "No existe idEmpleado con id " + registro.getIdEmpleado() + ".");
        }
    }

    @Override
    protected void prepararNuevo(EmpleadoRol registro) {
        if (registro.getActivo() == null) {
            registro.setActivo(Boolean.TRUE);
        }
    }

    /**
     * Empleado y rol obligatorios, sin repetir el par, y el rol debe estar
     * activo.
     */
    @Override
    protected String validarCrear(EmpleadoRol nuevo) {
        return primerError(empleadoRolDAO.validarAsignacion(nuevo), empleadoRolDAO.validarRolElegido(nuevo.getIdRol()));
    }

    @Override
    protected String validarModificar(EmpleadoRol nuevo, EmpleadoRol guardado) {
        String rolNoValido = nuevo.getIdRol() != null && !nuevo.getIdRol().equals(guardado.getIdRol())
                ? empleadoRolDAO.validarRolElegido(nuevo.getIdRol())
                : null;
        return primerError(empleadoRolDAO.validarAsignacion(nuevo), rolNoValido);
    }

    /**
     * Una asignación con órdenes o facturas no se puede eliminar.
     */
    @Override
    protected String validarEliminar(EmpleadoRol guardado) {
        return empleadoRolDAO.validarEliminar(guardado);
    }

    /**
     * GET /empleado_rol/empleado/{id}: los roles de un empleado.
     */
    @GET
    @Path("empleado/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response porEmpleado(@PathParam("id") String id) {
        return listarPorPadre(id, Empleado.class, empleado -> empleadoRolDAO.findByEmpleado(empleado.getIdEmpleado()));
    }
}
