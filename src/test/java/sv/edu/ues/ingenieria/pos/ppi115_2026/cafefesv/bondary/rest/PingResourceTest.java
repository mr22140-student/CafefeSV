package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

/** @author valen */
public class PingResourceTest {

    @Test
    void ping_respondeQueElApiFunciona() {
        Response respuesta = new PingResource().ping();
        assertEquals(200, respuesta.getStatus());
        assertEquals("CafefeSV REST v1 funcionando", respuesta.getEntity());
    }
}
