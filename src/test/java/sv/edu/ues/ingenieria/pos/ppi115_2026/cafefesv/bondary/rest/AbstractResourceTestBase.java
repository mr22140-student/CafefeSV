package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.validation.Validator;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.AbstractDAO;

/**
 * Base de las pruebas REST: simula el DAO de cada recurso y corre los casos
 * del CRUD que todos heredan de AbstractResource.
 *
 * @author valen
 */
public abstract class AbstractResourceTestBase<E, R extends AbstractResource<E>, D extends AbstractDAO<E, UUID>> {

    protected R resource;
    protected D dao;

    /** El DAO simulado con mock(...). */
    protected abstract D crearDao();

    /** El recurso con su DAO ya asignado. */
    protected abstract R crearRecurso(D dao);

    /** Entidad vacia con ese id, por ejemplo new Rol(id). */
    protected abstract E entidad(UUID id);

    @BeforeEach
    void prepararBase() {
        dao = crearDao();
        resource = crearRecurso(dao);
        Validator validator = mock(Validator.class);
        when(validator.validate(any())).thenReturn(Set.of());
        resource.validator = validator;
        doNothing().when(dao).save(any());
        doNothing().when(dao).delete(any());
        doAnswer(invocacion -> invocacion.getArgument(0)).when(dao).update(any());
    }

    // ===================== CRUD comun =====================
    @Test
    void findRange_devuelve200ConLaListaYElTotal() {
        List<E> lista = List.of(entidad(UUID.randomUUID()));
        doReturn(lista).when(dao).findRange(0, 50);
        doReturn(1L).when(dao).count();

        Response respuesta = resource.findRange(0, 50);

        assertEquals(200, respuesta.getStatus());
        assertSame(lista, respuesta.getEntity());
        assertEquals("1", respuesta.getHeaderString("Total-Registros"));
    }

    @Test
    void findById_siExiste_devuelve200() {
        E guardado = existente();

        Response respuesta = resource.findById(id(guardado));

        assertEquals(200, respuesta.getStatus());
        assertSame(guardado, respuesta.getEntity());
    }

    @Test
    void findById_siNoExiste_devuelve404() {
        assertEquals(404, resource.findById(UUID.randomUUID().toString()).getStatus());
    }

    @Test
    void findById_conIdInvalido_devuelve422() {
        assertEquals(422, resource.findById("no-es-uuid").getStatus());
    }

    @Test
    void crear_conDatosValidos_devuelve201YLoGuarda() {
        Response respuesta = crear("{}");

        assertEquals(201, respuesta.getStatus());
        verify(dao).save(any());
    }

    @Test
    void crear_conJsonInvalido_devuelve422SinGuardar() {
        assertEquals(422, crear("no es json").getStatus());
        verify(dao, never()).save(any());
    }

    @Test
    void actualizar_conDatosValidos_devuelve200() {
        assertEquals(200, actualizar(existente(), "{}").getStatus());
    }

    @Test
    void actualizar_siNoExiste_devuelve404() {
        assertEquals(404, resource.actualizar(UUID.randomUUID().toString(), "{}").getStatus());
        verify(dao, never()).update(any());
    }

    @Test
    void eliminar_siExiste_devuelve204() {
        assertEquals(204, eliminar(existente()).getStatus());
    }

    @Test
    void eliminar_siNoExiste_devuelve404() {
        assertEquals(404, resource.eliminarPorId(UUID.randomUUID().toString()).getStatus());
        verify(dao, never()).delete(any());
    }

    // ===================== ayudas =====================
    /** Una entidad que el DAO encuentra por su id. */
    protected E existente() {
        E guardado = entidad(UUID.randomUUID());
        doReturn(guardado).when(dao).findById(resource.getId(guardado));
        return guardado;
    }

    protected String id(E registro) {
        return resource.getId(registro).toString();
    }

    protected Response crear(String json) {
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getAbsolutePathBuilder()).thenReturn(UriBuilder.fromUri("http://localhost/recurso"));
        return resource.crear(json, uriInfo);
    }

    protected Response actualizar(E guardado, String json) {
        return resource.actualizar(id(guardado), json);
    }

    protected Response eliminar(E guardado) {
        return resource.eliminarPorId(id(guardado));
    }

    /** La relacion de esa clase existe en la base (devuelve el objeto dado). */
    protected <T> T existeEnLaBase(Class<T> clase, T objeto) {
        doReturn(objeto).when(dao).buscarReferencia(eq(clase), any());
        return objeto;
    }

    /** "campo":{"campo":"uuid"}, como manda el frontend una relacion. */
    protected static String relacion(String campo) {
        return "\"" + campo + "\":{\"" + campo + "\":\"" + UUID.randomUUID() + "\"}";
    }

    protected static void assertError422(Response respuesta, String mensaje) {
        assertEquals(422, respuesta.getStatus());
        assertEquals(mensaje, respuesta.getEntity());
    }
}
