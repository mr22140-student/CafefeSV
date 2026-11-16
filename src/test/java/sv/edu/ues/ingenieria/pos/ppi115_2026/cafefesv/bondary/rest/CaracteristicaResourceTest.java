package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.*;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.*;

/** @author valen */
public class CaracteristicaResourceTest extends AbstractResourceTestBase<Caracteristica, CaracteristicaResource, CaracteristicaDAO> {

    protected CaracteristicaDAO crearDao() { return mock(CaracteristicaDAO.class); }

    protected CaracteristicaResource crearRecurso(CaracteristicaDAO dao) {
        CaracteristicaResource r = new CaracteristicaResource();
        r.caracteristicaDAO = dao;
        return r;
    }

    protected Caracteristica entidad(UUID id) { return new Caracteristica(id); }

    @Test
    void crear_conTipoQueExiste_loAsignaYQuedaActiva() {
        TipoCaracteristica tipo = existeEnLaBase(TipoCaracteristica.class, new TipoCaracteristica(UUID.randomUUID()));
        Caracteristica creada = (Caracteristica) crear("{" + relacion("idTipoCaracteristica") + "}").getEntity();
        assertSame(tipo, creada.getIdTipoCaracteristica());
        assertEquals(Boolean.TRUE, creada.getActivo());
    }

    @Test
    void crear_conTipoQueNoExiste_devuelve422ConElCampo() {
        Response respuesta = crear("{" + relacion("idTipoCaracteristica") + "}");
        assertEquals(422, respuesta.getStatus());
        assertEquals("idTipoCaracteristica", respuesta.getHeaderString("Wrong-Parameter"));
        verify(dao, never()).save(any());
    }

    @Test
    void crear_conTipoInactivo_devuelve422() {
        when(dao.validarTipoActivo(any())).thenReturn("El tipo está inactivo.");
        assertError422(crear("{}"), "El tipo está inactivo.");
    }

    @Test
    void eliminar_siEstaAsignada_devuelve422() {
        when(dao.validarEliminar(any())).thenReturn("Está asignada a productos.");
        assertError422(eliminar(existente()), "Está asignada a productos.");
    }

    @Test
    void porTipo_siElTipoExiste_devuelveSusCaracteristicas() {
        TipoCaracteristica tipo = existeEnLaBase(TipoCaracteristica.class, new TipoCaracteristica(UUID.randomUUID()));
        List<Caracteristica> lista = List.of(new Caracteristica(UUID.randomUUID()));
        when(dao.findByTipoCaracteristica(tipo)).thenReturn(lista);

        Response respuesta = resource.porTipo(tipo.getIdTipoCaracteristica().toString());

        assertEquals(200, respuesta.getStatus());
        assertSame(lista, respuesta.getEntity());
    }

    @Test
    void porTipo_siElTipoNoExiste_devuelve404() {
        assertEquals(404, resource.porTipo(UUID.randomUUID().toString()).getStatus());
    }

    @Test
    void porTipo_conIdInvalido_devuelve422() {
        assertEquals(422, resource.porTipo("abc").getStatus());
    }
}
