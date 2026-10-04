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
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.DescuentoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoDescuentoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoDescuento;

/**
 *
 * @author valen
 */
public class TipoDescuentoModelTest extends ModelTestBase {
    @InjectMocks
    private TipoDescuentoModel model;

    @Mock
    private TipoDescuentoDAO tipoDescuentoDAO;

    @Mock
    private DescuentoDAO descuentoDAO;

    @Test
    void noInactivaSiTieneDescuentosVigentes() {
        TipoDescuento tipo = tipo(false);
        when(descuentoDAO.countVigentesByTipoDescuento(tipo)).thenReturn(1L);
        model.setRegistro(tipo);

        model.btnModificarHandler();

        verify(tipoDescuentoDAO, never()).update(any());
        assertEquals("No se puede inactivar", ultimoMensaje().getSummary());
    }

    @Test
    void inactivaSiSusDescuentosYaVencieron() {
        TipoDescuento tipo = tipo(false);
        when(descuentoDAO.countVigentesByTipoDescuento(tipo)).thenReturn(0L);
        model.setRegistro(tipo);

        model.btnModificarHandler();

        verify(tipoDescuentoDAO).update(tipo);
    }

    @Test
    void noEliminaSiTieneDescuentos() {
        TipoDescuento tipo = tipo(true);
        when(descuentoDAO.countByTipoDescuento(tipo)).thenReturn(2L);
        model.setRegistro(tipo);

        model.btnEliminarHandler();

        verify(tipoDescuentoDAO, never()).delete(any());
        assertEquals("No se puede eliminar", ultimoMensaje().getSummary());
    }

    private TipoDescuento tipo(boolean activo) {
        TipoDescuento t = new TipoDescuento(UUID.randomUUID());
        t.setNombre("Temporada");
        t.setActivo(activo);
        t.setDescuentoMaximo(30);
        return t;
    }

}
