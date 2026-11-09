package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoProducto;

/**
 *
 * @author valen
 */
public class TipoProductoDAOTest extends AbstractDAOTestBase<TipoProducto, TipoProductoDAO> {

    @Override
    protected TipoProductoDAO crearDAO() {
        return new TipoProductoDAO();
    }

    @Override
    protected TipoProducto crearEntidad() {
        return new TipoProducto();
    }

    // ===================== countProductosActivos / countProductos =====================

    @Test
    void countProductosActivos_devuelveLoQueCuentaLaBase() {
        simularConteo(2);

        assertEquals(2, dao.countProductosActivos(tipoProducto(false)));
    }

    @Test
    void countProductos_devuelveLoQueCuentaLaBase() {
        simularConteo(5);

        assertEquals(5, dao.countProductos(tipoProducto(true)));
    }

    // ===================== validarInactivar =====================

    @Test
    void validarInactivar_conProductosActivos_devuelveError() {
        simularConteo(1);

        assertNotNull(dao.validarInactivar(tipoProducto(false)));
    }

    @Test
    void validarInactivar_sinProductosActivos_devuelveNull() {
        simularConteo(0);

        assertNull(dao.validarInactivar(tipoProducto(false)));
    }

    @Test
    void validarInactivar_siSigueActivo_noConsultaLaBase() {
        assertNull(dao.validarInactivar(tipoProducto(true)));
        verifyNoInteractions(em);
    }

    // ===================== validarEliminar =====================

    @Test
    void validarEliminar_asignadoAProductos_devuelveError() {
        simularConteo(3);

        assertNotNull(dao.validarEliminar(tipoProducto(true)));
    }

    @Test
    void validarEliminar_sinProductos_devuelveNull() {
        simularConteo(0);

        assertNull(dao.validarEliminar(tipoProducto(true)));
    }

    // ===================== ayudantes =====================

    private TipoProducto tipoProducto(boolean activo) {
        TipoProducto tipoProducto = new TipoProducto(UUID.randomUUID());
        tipoProducto.setNombre("Bebidas");
        tipoProducto.setActivo(activo);
        return tipoProducto;
    }
}