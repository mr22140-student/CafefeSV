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
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoProducto;

/**
 * El model solo llama a las reglas del DAO. Las reglas en sí se prueban en
 * TipoProductoDAOTest.
 *
 * @author valen
 */
public class TipoProductoModelTest extends ModelTestBase {

    @InjectMocks
    private TipoProductoModel model;

    @Mock
    private TipoProductoDAO tipoProductoDAO;

    @Test
    void noInactivaSiElDaoDevuelveError() {
        TipoProducto tipoProducto = tipoProducto(false);
        when(tipoProductoDAO.validarInactivar(tipoProducto)).thenReturn("Hay 1 producto activo con este tipo.");
        model.setRegistro(tipoProducto);

        model.btnModificarHandler();

        verify(tipoProductoDAO, never()).update(any());
        assertEquals("No se puede inactivar", ultimoMensaje().getSummary());
    }

    @Test
    void modificaSiElDaoNoDevuelveError() {
        TipoProducto tipoProducto = tipoProducto(false);
        when(tipoProductoDAO.validarInactivar(tipoProducto)).thenReturn(null);
        model.setRegistro(tipoProducto);

        model.btnModificarHandler();

        verify(tipoProductoDAO).update(tipoProducto);
    }

    @Test
    void noEliminaSiElDaoDevuelveError() {
        TipoProducto tipoProducto = tipoProducto(true);
        when(tipoProductoDAO.validarEliminar(tipoProducto)).thenReturn("Hay productos que tienen asignado este tipo.");
        model.setRegistro(tipoProducto);

        model.btnEliminarHandler();

        verify(tipoProductoDAO, never()).delete(any());
        assertEquals("No se puede eliminar", ultimoMensaje().getSummary());
    }

    @Test
    void eliminaSiElDaoNoDevuelveError() {
        TipoProducto tipoProducto = tipoProducto(true);
        when(tipoProductoDAO.validarEliminar(tipoProducto)).thenReturn(null);
        model.setRegistro(tipoProducto);

        model.btnEliminarHandler();

        verify(tipoProductoDAO).delete(tipoProducto);
    }

    private TipoProducto tipoProducto(boolean activo) {
        TipoProducto tipoProducto = new TipoProducto(UUID.randomUUID());
        tipoProducto.setNombre("Bebidas");
        tipoProducto.setActivo(activo);
        return tipoProducto;
    }
}