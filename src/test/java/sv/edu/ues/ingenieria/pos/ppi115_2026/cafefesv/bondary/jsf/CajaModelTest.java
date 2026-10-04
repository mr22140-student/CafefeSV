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
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.FacturaDAO;
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
    @Mock
    private FacturaDAO facturaDAO;

    @Test
    void nuevaCaja_empiezaActiva() {
        model.btnNuevoHandler();

        assertTrue(model.getRegistro().getActivo());
    }

    @Test
    void noEliminaUnaCajaConFacturas() {
        Caja caja = new Caja(UUID.randomUUID());
        when(facturaDAO.countByCaja(caja)).thenReturn(2L);
        model.seleccionarRegistro(caja);

        model.btnEliminarHandler();

        verify(cajaDAO, never()).delete(any());
        assertEquals("No se puede eliminar: la caja tiene facturas registradas.", ultimoMensaje().getSummary());
    }

    @Test
    void eliminaUnaCajaSinFacturas() {
        Caja caja = new Caja(UUID.randomUUID());
        when(facturaDAO.countByCaja(caja)).thenReturn(0L);
        model.seleccionarRegistro(caja);

        model.btnEliminarHandler();

        verify(cajaDAO).delete(caja);
    }

}
