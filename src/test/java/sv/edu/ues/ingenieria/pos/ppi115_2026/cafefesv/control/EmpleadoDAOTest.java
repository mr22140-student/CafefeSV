package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

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
}
