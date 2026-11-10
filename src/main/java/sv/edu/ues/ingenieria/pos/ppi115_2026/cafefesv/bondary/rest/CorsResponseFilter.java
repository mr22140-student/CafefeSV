package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;


/**
 *
 * @author daniel
 */

/**
 * Agrega a todas las respuestas los headers CORS, para que el frontend (que
 * corre en otro puerto, por ejemplo localhost:3000) pueda llamar a la API.
 *
 * No se envía Access-Control-Allow-Credentials: el navegador no acepta esa
 * opción junto con Allow-Origin "*", y la API no usa sesión ni login.
 */
@Provider
public class CorsResponseFilter implements ContainerResponseFilter {

    private static final String METODOS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
    private static final String CABECERAS_PERMITIDAS = "origin, accept, content-type";
    private static final String CABECERAS_EXPUESTAS = "location, Total-Registros, Wrong-Parameter, App-Error";
    private static final int MAX_AGE = 60 * 60; // segundos que el navegador recuerda la respuesta

    @Override
    public void filter(ContainerRequestContext peticion, ContainerResponseContext respuesta) {
        MultivaluedMap<String, Object> headers = respuesta.getHeaders();
        headers.putSingle("Access-Control-Allow-Origin", "*");
        headers.putSingle("Access-Control-Allow-Methods", METODOS);
        headers.putSingle("Access-Control-Allow-Headers", CABECERAS_PERMITIDAS);
        headers.putSingle("Access-Control-Expose-Headers", CABECERAS_EXPUESTAS);
        headers.putSingle("Access-Control-Max-Age", MAX_AGE);
    }
}
