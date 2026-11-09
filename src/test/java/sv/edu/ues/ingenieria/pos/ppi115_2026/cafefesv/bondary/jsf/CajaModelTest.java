package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.CajaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caja;

/**
 *
 * @author valen
 */
public class CajaModelTest extends ModelTestBase {

    @InjectMocks
    private CajaModel model;

    @Mock
    private CajaDAO cajaDAO;

    @Test
    void nuevaCaja_empiezaActiva() {
        model.btnNuevoHandler();

        assertTrue(model.getRegistro().getActivo());
    }

    @Test
    void noEliminaSiElDaoLaRechaza() {
        Caja caja = new Caja(UUID.randomUUID());
        when(cajaDAO.validarEliminar(caja)).thenReturn("La caja tiene facturas registradas.");
        model.seleccionarRegistro(caja);

        model.btnEliminarHandler();

        verify(cajaDAO, never()).delete(any());
        assertEquals("No se puede eliminar", ultimoMensaje().getSummary());
    }

    @Test
    void eliminaSiElDaoNoEncuentraErrores() {
        Caja caja = new Caja(UUID.randomUUID());
        model.seleccionarRegistro(caja);

        model.btnEliminarHandler();

        verify(cajaDAO).delete(caja);
    }
}