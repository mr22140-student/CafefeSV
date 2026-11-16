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
public class CajaResourceTest extends AbstractResourceTestBase<Caja, CajaResource, CajaDAO> {

    protected CajaDAO crearDao() { return mock(CajaDAO.class); }

    protected CajaResource crearRecurso(CajaDAO dao) {
        CajaResource r = new CajaResource();
        r.cajaDAO = dao;
        return r;
    }

    protected Caja entidad(UUID id) { return new Caja(id); }

    @Test
    void crear_sinActivo_quedaActiva() {
        assertEquals(Boolean.TRUE, ((Caja) crear("{\"nombre\":\"Caja 3\"}").getEntity()).getActivo());
    }

    @Test
    void eliminar_siTieneFacturas_devuelve422() {
        when(dao.validarEliminar(any())).thenReturn("La caja tiene facturas.");
        assertError422(eliminar(existente()), "La caja tiene facturas.");
        verify(dao, never()).delete(any());
    }
}
