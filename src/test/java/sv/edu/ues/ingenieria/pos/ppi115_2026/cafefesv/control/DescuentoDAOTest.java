package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoDescuento;

/**
 *
 * @author valen
 */
class DescuentoDAOTest extends AbstractDAOTestBase<Descuento, DescuentoDAO> {

    @Override
    protected DescuentoDAO crearDAO() {
        return new DescuentoDAO();
    }

    @Override
    protected Descuento crearEntidad() {
        return new Descuento();
    }

    @Test
    void findVigentesByTipoDescuento_conNull_devuelveListaVaciaSinConsultar() {
        assertTrue(dao.findVigentesByTipoDescuento(null).isEmpty());
        verifyNoInteractions(em);
    }

    // ===================== validarFechas =====================

    @Test
    void validarFechas_hastaAnteriorADesde_devuelveError() {
        assertNotNull(dao.validarFechas(descuento(tipoDescuento(true), enDias(5), enDias(1))));
    }

    @Test
    void validarFechas_sinFechaHasta_devuelveNull() {
        assertNull(dao.validarFechas(descuento(tipoDescuento(true), enDias(0), null)));
    }

    @Test
    void validarFechas_hastaDespuesDeDesde_devuelveNull() {
        assertNull(dao.validarFechas(descuento(tipoDescuento(true), enDias(0), enDias(7))));
    }

    // ===================== validarTipoActivo =====================

    @Test
    void validarTipoActivo_conTipoInactivo_devuelveError() {
        TipoDescuento tipoInactivo = tipoDescuento(false);
        when(em.find(TipoDescuento.class, tipoInactivo.getIdTipoDescuento())).thenReturn(tipoInactivo);

        assertNotNull(dao.validarTipoActivo(descuento(tipoInactivo, enDias(0), null)));
    }

    @Test
    void validarTipoActivo_conTipoActivo_devuelveNull() {
        TipoDescuento tipoActivo = tipoDescuento(true);
        when(em.find(TipoDescuento.class, tipoActivo.getIdTipoDescuento())).thenReturn(tipoActivo);

        assertNull(dao.validarTipoActivo(descuento(tipoActivo, enDias(0), null)));
    }

    // ===================== calcularVigencia =====================

    @Test
    void calcularVigencia_segunLaFechaActual() {
        TipoDescuento tipoDescuento = tipoDescuento(true);
        assertEquals("PROGRAMADO", dao.calcularVigencia(descuento(tipoDescuento, enDias(2), enDias(5))));
        assertEquals("VIGENTE", dao.calcularVigencia(descuento(tipoDescuento, enDias(-1), enDias(5))));
        assertEquals("VIGENTE", dao.calcularVigencia(descuento(tipoDescuento, enDias(-1), null)));
        assertEquals("VENCIDO", dao.calcularVigencia(descuento(tipoDescuento, enDias(-5), enDias(-1))));
        assertEquals("SIN FECHA", dao.calcularVigencia(descuento(tipoDescuento, null, null)));
    }

    // ===================== ayudantes =====================

    private TipoDescuento tipoDescuento(boolean activo) {
        TipoDescuento tipoDescuento = new TipoDescuento(UUID.randomUUID());
        tipoDescuento.setNombre("Temporada");
        tipoDescuento.setActivo(activo);
        tipoDescuento.setDescuentoMaximo(30);
        return tipoDescuento;
    }

    private Descuento descuento(TipoDescuento tipoDescuento, Date fechaDesde, Date fechaHasta) {
        Descuento descuento = new Descuento(UUID.randomUUID());
        descuento.setNombre("Navidad");
        descuento.setIdTipoDescuento(tipoDescuento);
        descuento.setFechaDesde(fechaDesde);
        descuento.setFechaHasta(fechaHasta);
        return descuento;
    }
}