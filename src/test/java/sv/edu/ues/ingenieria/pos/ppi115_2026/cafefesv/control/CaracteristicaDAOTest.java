package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caracteristica;

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
    void consultasConNull_devuelvenListaVaciaSinConsultar() {
        assertTrue(dao.findByNombre(null).isEmpty());
        assertTrue(dao.findByActivo(null).isEmpty());
        assertTrue(dao.findByObservaciones(null).isEmpty());
        assertTrue(dao.findByTipoCaracteristica(null).isEmpty());
        verifyNoInteractions(em);
    }
}
