package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Empleado;

/**
 *
 * @author valen
 */
class EmpleadoDAOTest extends AbstractDAOTestBase<Empleado, EmpleadoDAO> {

    @Override
    protected EmpleadoDAO crearDAO() {
        return new EmpleadoDAO();
    }

    @Override
    protected Empleado crearEntidad() {
        return new Empleado();
    }

    @Test
    void obtenerPorId_conNull_devuelveNullSinConsultar() {
        verifyNoInteractions(em);
    }
    // ===================== validarEliminar =====================

    @Test
    void validarEliminar_conOrdenesOFacturas_devuelveError() {
        simularConteo(1);
        assertNotNull(dao.validarEliminar(new Empleado(UUID.randomUUID())));
    }

    @Test
    void validarEliminar_sinHistorial_devuelveNull() {
        simularConteo(0);
        assertNull(dao.validarEliminar(new Empleado(UUID.randomUUID())));
    }
}
