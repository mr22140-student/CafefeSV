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
public class TipoDescuentoResourceTest extends AbstractResourceTestBase<TipoDescuento, TipoDescuentoResource, TipoDescuentoDAO> {

    protected TipoDescuentoDAO crearDao() { return mock(TipoDescuentoDAO.class); }

    protected TipoDescuentoResource crearRecurso(TipoDescuentoDAO dao) {
        TipoDescuentoResource r = new TipoDescuentoResource();
        r.tipoDescuentoDAO = dao;
        return r;
    }

    protected TipoDescuento entidad(UUID id) { return new TipoDescuento(id); }

    @Test
    void crear_sinValores_quedaActivoConMaximo50() {
        TipoDescuento creado = (TipoDescuento) crear("{}").getEntity();
        assertEquals(Boolean.TRUE, creado.getActivo());
        assertEquals(50, creado.getDescuentoMaximo());
    }

    @Test
    void crear_conMaximo_respetaElQueVino() {
        assertEquals(30, ((TipoDescuento) crear("{\"descuentoMaximo\":30}").getEntity()).getDescuentoMaximo());
    }

    @Test
    void actualizar_siNoSePuedeInactivar_devuelve422() {
        when(dao.validarInactivar(any())).thenReturn("Tiene descuentos vigentes.");
        assertError422(actualizar(existente(), "{\"activo\":false}"), "Tiene descuentos vigentes.");
    }

    @Test
    void eliminar_siTieneDescuentos_devuelve422() {
        when(dao.validarEliminar(any())).thenReturn("Tiene descuentos.");
        assertError422(eliminar(existente()), "Tiene descuentos.");
    }
}
