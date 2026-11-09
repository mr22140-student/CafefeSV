package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caja;

/**
 *
 * @author valen
 */
class CajaDAOTest extends AbstractDAOTestBase<Caja, CajaDAO> {

    @Override
    protected CajaDAO crearDAO() {
        return new CajaDAO();
    }

    @Override
    protected Caja crearEntidad() {
        return new Caja();
    }
    // ===================== validarEliminar =====================

    @Test
    void validarEliminar_conFacturas_devuelveError() {
        simularConteo(2);
        assertNotNull(dao.validarEliminar(new Caja(UUID.randomUUID())));
    }

    @Test
    void validarEliminar_sinFacturas_devuelveNull() {
        simularConteo(0);
        assertNull(dao.validarEliminar(new Caja(UUID.randomUUID())));
    }
}
