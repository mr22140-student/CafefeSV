package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;

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
    void consultasPorTipoConNull_noConsultanLaBase() {
        assertEquals(0, dao.countByTipoDescuento(null));
        assertEquals(0, dao.countVigentesByTipoDescuento(null));
        assertTrue(dao.findVigentesByTipoDescuento(null).isEmpty());
        verifyNoInteractions(em);
    }
}
