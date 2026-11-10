package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.AbstractDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.CaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;

/**
 * /resources/v1/caracteristica
 */
@Path("caracteristica")
@RequestScoped
public class CaracteristicaResource extends AbstractResource<Caracteristica> {

    @Inject
    CaracteristicaDAO caracteristicaDAO;

    public CaracteristicaResource() {
        super(Caracteristica.class);
    }

    @Override
    protected AbstractDAO<Caracteristica, UUID> dao() {
        return caracteristicaDAO;
    }

    @Override
    protected UUID getId(Caracteristica registro) {
        return registro.getIdCaracteristica();
    }

    @Override
    protected void setId(Caracteristica registro, UUID id) {
        registro.setIdCaracteristica(id);
    }

    @Override
    protected void resolverRelaciones(Caracteristica registro) {
        registro.setIdTipoCaracteristica(referencia(TipoCaracteristica.class, registro.getIdTipoCaracteristica(),
                TipoCaracteristica::getIdTipoCaracteristica, "idTipoCaracteristica"));
    }

    @Override
    protected void prepararNuevo(Caracteristica registro) {
        if (registro.getActivo() == null) {
            registro.setActivo(Boolean.TRUE);
        }
    }

    /** Una característica activa no puede tener un tipo inactivo. */
    @Override
    protected String validarCrear(Caracteristica nuevo) {
        return caracteristicaDAO.validarTipoActivo(nuevo);
    }

    @Override
    protected String validarModificar(Caracteristica nuevo, Caracteristica guardado) {
        return caracteristicaDAO.validarTipoActivo(nuevo);
    }

    /** Una característica que usa algún producto no se puede eliminar. */
    @Override
    protected String validarEliminar(Caracteristica guardado) {
        return caracteristicaDAO.validarEliminar(guardado);
    }

    /** GET /caracteristica/tipo_caracteristica/{id}: las características de un tipo. */
    @GET
    @Path("tipo_caracteristica/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response porTipo(@PathParam("id") String id) {
        return listarPorPadre(id, TipoCaracteristica.class, caracteristicaDAO::findByTipoCaracteristica);
    }
}
