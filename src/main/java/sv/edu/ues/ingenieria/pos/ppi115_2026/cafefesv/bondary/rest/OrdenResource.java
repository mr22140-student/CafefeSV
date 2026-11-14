package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Date;
import java.util.Map;
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
    OrdenDAO ordenDAO;

    public OrdenResource() {
        super(Orden.class);
    }

    @Override
    protected AbstractDAO<Orden, UUID> dao() {
        return ordenDAO;
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

    /**
     * Sin fecha, la orden toma la fecha y hora actual (igual que en JSF).
     */
    @Override
    protected void prepararNuevo(Orden registro) {
        if (registro.getFechaCreacion() == null) {
            registro.setFechaCreacion(new Date());
        }
    }

    /**
     * Fecha obligatoria y solo un camarero puede tomar la orden.
     */
    @Override
    protected String validarCrear(Orden nuevo) {
        return ordenDAO.validarOrden(nuevo);
    }

    @Override
    protected String validarModificar(Orden nuevo, Orden guardado) {
        return ordenDAO.validarOrden(nuevo);
    }

    /**
     * Una orden con productos no se puede eliminar.
     */
    @Override
    protected String validarEliminar(Orden guardado) {
        return ordenDAO.validarEliminar(guardado);
    }

    /**
     * GET /orden/pendientes: órdenes que todavía no han sido pagadas (para el
     * barista y el cajero). JAX-RS prefiere la ruta literal "pendientes" sobre
     * la plantilla "{id}", así que no choca con GET /orden/{id}.
     */
    @GET
    @Path("pendientes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response pendientes() {
        try {
            return Response.ok(ordenDAO.findPendientes(), MediaType.APPLICATION_JSON).build();
        } catch (Exception ex) {
            return manejar(ex, "pendientes");
        }
    }

    /**
     * GET /orden/{id}/estado: estado de preparación de la orden ({"idOrden":
     * "...", "estado": "PREPARANDO"}). El cliente lo consulta cada pocos
     * segundos para ver su orden en tiempo real.
     */
    @GET
    @Path("{id}/estado")
    @Produces(MediaType.APPLICATION_JSON)
    public Response estado(@PathParam("id") String id) {
        UUID uuid = aUuid(id);
        if (uuid == null) {
            return error422("id", "El id no es un UUID valido.");
        }
        try {
            if (ordenDAO.findById(uuid) == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(Map.of("idOrden", uuid.toString(), "estado", ordenDAO.buscarEstado(uuid)),
                    MediaType.APPLICATION_JSON).build();
        } catch (Exception ex) {
            return manejar(ex, "id");
        }
    }

    /**
     * PUT /orden/{id}/estado con {"estado": "EN_CAMINO"}: el barista avanza la
     * orden. Responde igual que el GET.
     */
    @PUT
    @Path("{id}/estado")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cambiarEstado(@PathParam("id") String id, Map<String, String> cuerpo) {
        UUID uuid = aUuid(id);
        if (uuid == null) {
            return error422("id", "El id no es un UUID valido.");
        }
        String estadoNuevo = (cuerpo == null) ? null : cuerpo.get("estado");
        try {
            if (ordenDAO.findById(uuid) == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            String error = ordenDAO.validarEstado(uuid, estadoNuevo);
            if (error != null) {
                return error422("estado", error);
            }
            ordenDAO.cambiarEstado(uuid, estadoNuevo);
            return estado(id);
        } catch (Exception ex) {
            return manejar(ex, "estado");
        }
    }
}
