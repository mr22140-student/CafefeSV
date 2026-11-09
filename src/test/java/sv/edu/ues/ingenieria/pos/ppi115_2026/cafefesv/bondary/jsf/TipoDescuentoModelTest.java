package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoDescuentoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoDescuento;

/**
 * El model solo llama a las reglas del DAO. Las reglas en sí se prueban en
 * TipoDescuentoDAOTest.
 *
 * @author valen
 */
public class TipoDescuentoModelTest extends ModelTestBase {

    @InjectMocks
    private TipoDescuentoModel model;

    @Mock
    private TipoDescuentoDAO tipoDescuentoDAO;

    @Test
    void noInactivaSiTieneDescuentosVigentes() {
        TipoDescuento tipoDescuento = tipoDescuento(false);
        when(tipoDescuentoDAO.validarInactivar(tipoDescuento)).thenReturn("Hay 1 descuento sin vencer con este tipo.");
        model.setRegistro(tipoDescuento);

        model.btnModificarHandler();

        verify(tipoDescuentoDAO, never()).update(any());
        assertEquals("No se puede inactivar", ultimoMensaje().getSummary());
    }

    @Test
    void modificaSiElDaoNoDevuelveError() {
        TipoDescuento tipoDescuento = tipoDescuento(false);
        when(tipoDescuentoDAO.validarInactivar(tipoDescuento)).thenReturn(null);
        model.setRegistro(tipoDescuento);

        model.btnModificarHandler();

        verify(tipoDescuentoDAO).update(tipoDescuento);
    }

    @Test
    void noEliminaSiTieneDescuentos() {
        TipoDescuento tipoDescuento = tipoDescuento(true);
        when(tipoDescuentoDAO.validarEliminar(tipoDescuento)).thenReturn("Hay descuentos que usan este tipo.");
        model.setRegistro(tipoDescuento);

        model.btnEliminarHandler();

        verify(tipoDescuentoDAO, never()).delete(any());
        assertEquals("No se puede eliminar", ultimoMensaje().getSummary());
    }

    private TipoDescuento tipoDescuento(boolean activo) {
        TipoDescuento tipoDescuento = new TipoDescuento(UUID.randomUUID());
        tipoDescuento.setNombre("Temporada");
        tipoDescuento.setActivo(activo);
        tipoDescuento.setDescuentoMaximo(30);
        return tipoDescuento;
    }
}