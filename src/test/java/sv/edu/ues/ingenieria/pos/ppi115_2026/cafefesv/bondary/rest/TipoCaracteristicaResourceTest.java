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
public class TipoCaracteristicaResourceTest extends AbstractResourceTestBase<TipoCaracteristica, TipoCaracteristicaResource, TipoCaracteristicaDAO> {

    protected TipoCaracteristicaDAO crearDao() { return mock(TipoCaracteristicaDAO.class); }

    protected TipoCaracteristicaResource crearRecurso(TipoCaracteristicaDAO dao) {
        TipoCaracteristicaResource r = new TipoCaracteristicaResource();
        r.tipoCaracteristicaDAO = dao;
        return r;
    }

    protected TipoCaracteristica entidad(UUID id) { return new TipoCaracteristica(id); }

    @Test
    void crear_sinActivo_quedaActivo() {
        assertEquals(Boolean.TRUE, ((TipoCaracteristica) crear("{}").getEntity()).getActivo());
    }

    @Test
    void crear_conExpresionInvalida_devuelve422SinGuardar() {
        when(dao.validarExpresion(any())).thenReturn("La expresión regular no es válida.");
        assertError422(crear("{\"expresionRegular\":\"[\"}"), "La expresión regular no es válida.");
        verify(dao, never()).save(any());
    }

    @Test
    void actualizar_conExpresionValidaPeroSinPoderInactivar_devuelve422() {
        when(dao.validarInactivar(any())).thenReturn("Tiene características activas.");
        assertError422(actualizar(existente(), "{\"activo\":false}"), "Tiene características activas.");
    }

    @Test
    void eliminar_siTieneCaracteristicas_devuelve422() {
        when(dao.validarEliminar(any())).thenReturn("Tiene características.");
        assertError422(eliminar(existente()), "Tiene características.");
    }
}
