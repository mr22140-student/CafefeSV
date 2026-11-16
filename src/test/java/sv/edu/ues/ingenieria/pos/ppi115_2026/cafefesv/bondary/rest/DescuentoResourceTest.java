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
public class DescuentoResourceTest extends AbstractResourceTestBase<Descuento, DescuentoResource, DescuentoDAO> {

    protected DescuentoDAO crearDao() { return mock(DescuentoDAO.class); }

    protected DescuentoResource crearRecurso(DescuentoDAO dao) {
        DescuentoResource r = new DescuentoResource();
        r.descuentoDAO = dao;
        return r;
    }

    protected Descuento entidad(UUID id) { return new Descuento(id); }

    @Test
    void crear_sinFechaDesde_empiezaHoy() {
        assertNotNull(((Descuento) crear("{}").getEntity()).getFechaDesde());
    }

    @Test
    void crear_conTipoQueNoExiste_devuelve422() {
        assertEquals("idTipoDescuento", crear("{" + relacion("idTipoDescuento") + "}").getHeaderString("Wrong-Parameter"));
    }

    @Test
    void crear_conFechasAlReves_devuelve422() {
        when(dao.validarFechas(any())).thenReturn("La fecha hasta no puede ser antes que la fecha desde.");
        assertError422(crear("{}"), "La fecha hasta no puede ser antes que la fecha desde.");
    }

    @Test
    void actualizar_conTipoInactivo_devuelve422() {
        when(dao.validarTipoActivo(any())).thenReturn("El tipo de descuento está inactivo.");
        assertError422(actualizar(existente(), "{}"), "El tipo de descuento está inactivo.");
    }

    @Test
    void eliminar_siEstaAplicado_devuelve422() {
        when(dao.validarEliminar(any())).thenReturn("El descuento está aplicado a productos.");
        assertError422(eliminar(existente()), "El descuento está aplicado a productos.");
    }
}
