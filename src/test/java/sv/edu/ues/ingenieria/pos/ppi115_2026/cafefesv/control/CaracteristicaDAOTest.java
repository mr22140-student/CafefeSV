package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;

/**
 *
 * @author valen
 */
class CaracteristicaDAOTest extends AbstractDAOTestBase<Caracteristica, CaracteristicaDAO> {

    @Override
    protected CaracteristicaDAO crearDAO() {
        return new CaracteristicaDAO();
    }

    @Override
    protected Caracteristica crearEntidad() {
        return new Caracteristica();
    }

    @Test
    void findByTipoCaracteristica_conNull_devuelveListaVaciaSinConsultar() {
        assertTrue(dao.findByTipoCaracteristica(null).isEmpty());
        verifyNoInteractions(em);
    }

    // ===================== countProductos =====================

    @Test
    void countProductos_devuelveLoQueCuentaLaBase() {
        simularConteo(3);

        assertEquals(3, dao.countProductos(caracteristica(true, tipoCaracteristica(true))));
    }

    // ===================== validarTipoActivo =====================

    @Test
    void validarTipoActivo_caracteristicaActivaConTipoInactivo_devuelveError() {
        TipoCaracteristica tipoInactivo = tipoCaracteristica(false);
        when(em.find(TipoCaracteristica.class, tipoInactivo.getIdTipoCaracteristica())).thenReturn(tipoInactivo);

        assertNotNull(dao.validarTipoActivo(caracteristica(true, tipoInactivo)));
    }

    @Test
    void validarTipoActivo_caracteristicaActivaConTipoActivo_devuelveNull() {
        TipoCaracteristica tipoActivo = tipoCaracteristica(true);
        when(em.find(TipoCaracteristica.class, tipoActivo.getIdTipoCaracteristica())).thenReturn(tipoActivo);

        assertNull(dao.validarTipoActivo(caracteristica(true, tipoActivo)));
    }

    @Test
    void validarTipoActivo_caracteristicaInactiva_sePuedeGuardarConTipoInactivo() {
        assertNull(dao.validarTipoActivo(caracteristica(false, tipoCaracteristica(false))));
        verifyNoInteractions(em);
    }

    // ===================== validarEliminar =====================

    @Test
    void validarEliminar_usadaPorProductos_devuelveError() {
        simularConteo(1);

        assertNotNull(dao.validarEliminar(caracteristica(true, tipoCaracteristica(true))));
    }

    @Test
    void validarEliminar_sinProductos_devuelveNull() {
        simularConteo(0);

        assertNull(dao.validarEliminar(caracteristica(true, tipoCaracteristica(true))));
    }

    // ===================== ayudantes =====================

    private TipoCaracteristica tipoCaracteristica(boolean activo) {
        TipoCaracteristica tipoCaracteristica = new TipoCaracteristica(UUID.randomUUID());
        tipoCaracteristica.setNombre(activo ? "Volumen" : "Colores");
        tipoCaracteristica.setActivo(activo);
        return tipoCaracteristica;
    }

    private Caracteristica caracteristica(boolean activa, TipoCaracteristica tipoCaracteristica) {
        Caracteristica caracteristica = new Caracteristica(UUID.randomUUID());
        caracteristica.setNombre("Vaso grande");
        caracteristica.setActivo(activa);
        caracteristica.setIdTipoCaracteristica(tipoCaracteristica);
        return caracteristica;
    }
}