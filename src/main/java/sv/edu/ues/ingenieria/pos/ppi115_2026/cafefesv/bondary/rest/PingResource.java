package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * GET /resources/v1/ping: comprueba que la API está viva. Reemplaza al
 * JakartaEE11Resource que generó NetBeans.
 * 
 * @author daniel
 */
@Path("ping")
public class PingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response ping() {
        return Response.ok("CafefeSV REST v1 funcionando").build();
    }
}
