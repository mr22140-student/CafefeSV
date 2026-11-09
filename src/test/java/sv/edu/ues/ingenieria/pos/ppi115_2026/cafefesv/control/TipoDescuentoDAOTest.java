package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoDescuento;

/**
 *
 * @author valen
 */
public class TipoDescuentoDAOTest extends AbstractDAOTestBase<TipoDescuento, TipoDescuentoDAO> {

    @Override
    protected TipoDescuentoDAO crearDAO() {
        return new TipoDescuentoDAO();
    }

    @Override
    protected TipoDescuento crearEntidad() {
        return new TipoDescuento();
    }

    // ===================== countDescuentos / countDescuentosVigentes =====================

    @Test
    void countDescuentos_devuelveLoQueCuentaLaBase() {
        simularConteo(4);

        assertEquals(4, dao.countDescuentos(tipoDescuento(true)));
    }

    @Test
    void countDescuentosVigentes_devuelveLoQueCuentaLaBase() {
        simularConteo(1);

        assertEquals(1, dao.countDescuentosVigentes(tipoDescuento(false)));
    }

    // ===================== validarInactivar =====================

    @Test
    void validarInactivar_conDescuentosVigentes_devuelveError() {
        simularConteo(1);

        assertNotNull(dao.validarInactivar(tipoDescuento(false)));
    }

    @Test
    void validarInactivar_conDescuentosVencidos_devuelveNull() {
        simularConteo(0);

        assertNull(dao.validarInactivar(tipoDescuento(false)));
    }

    @Test
    void validarInactivar_siSigueActivo_noConsultaLaBase() {
        assertNull(dao.validarInactivar(tipoDescuento(true)));
        verifyNoInteractions(em);
    }

    // ===================== validarEliminar =====================

    @Test
    void validarEliminar_conDescuentos_devuelveError() {
        simularConteo(2);

        assertNotNull(dao.validarEliminar(tipoDescuento(true)));
    }

    @Test
    void validarEliminar_sinDescuentos_devuelveNull() {
        simularConteo(0);

        assertNull(dao.validarEliminar(tipoDescuento(true)));
    }

    // ===================== ayudantes =====================

    private TipoDescuento tipoDescuento(boolean activo) {
        TipoDescuento tipoDescuento = new TipoDescuento(UUID.randomUUID());
        tipoDescuento.setNombre("Temporada");
        tipoDescuento.setActivo(activo);
        tipoDescuento.setDescuentoMaximo(30);
        return tipoDescuento;
    }
}