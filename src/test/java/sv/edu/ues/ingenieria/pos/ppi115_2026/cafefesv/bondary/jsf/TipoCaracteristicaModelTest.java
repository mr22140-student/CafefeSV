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
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoCaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;

/**
 * El model solo llama a las reglas del DAO. Las reglas en sí se prueban en
 * TipoCaracteristicaDAOTest.
 *
 * @author valen
 */
public class TipoCaracteristicaModelTest extends ModelTestBase {

    @InjectMocks
    private TipoCaracteristicaModel model;

    @Mock
    private TipoCaracteristicaDAO tipoCaracteristicaDAO;

    @Test
    void noCreaSiLaExpresionEsInvalida() {
        TipoCaracteristica tipoCaracteristica = tipoCaracteristica(true);
        when(tipoCaracteristicaDAO.validarExpresion(tipoCaracteristica)).thenReturn("Falta cerrar el paréntesis.");
        model.setRegistro(tipoCaracteristica);

        model.btnCrearHandler();

        verify(tipoCaracteristicaDAO, never()).save(any());
        assertEquals("Expresión regular inválida", ultimoMensaje().getSummary());
    }

    @Test
    void creaSiLaExpresionEsValida() {
        TipoCaracteristica tipoCaracteristica = tipoCaracteristica(true);
        when(tipoCaracteristicaDAO.validarExpresion(tipoCaracteristica)).thenReturn(null);
        model.setRegistro(tipoCaracteristica);

        model.btnCrearHandler();

        verify(tipoCaracteristicaDAO).save(tipoCaracteristica);
    }

    @Test
    void noInactivaSiTieneCaracteristicasActivas() {
        TipoCaracteristica tipoCaracteristica = tipoCaracteristica(false);
        when(tipoCaracteristicaDAO.validarExpresion(tipoCaracteristica)).thenReturn(null);
        when(tipoCaracteristicaDAO.validarInactivar(tipoCaracteristica)).thenReturn("Hay 1 característica activa con este tipo.");
        model.setRegistro(tipoCaracteristica);

        model.btnModificarHandler();

        verify(tipoCaracteristicaDAO, never()).update(any());
        assertEquals("No se puede inactivar", ultimoMensaje().getSummary());
    }

    @Test
    void modificaSiNingunaReglaDevuelveError() {
        TipoCaracteristica tipoCaracteristica = tipoCaracteristica(false);
        when(tipoCaracteristicaDAO.validarExpresion(tipoCaracteristica)).thenReturn(null);
        when(tipoCaracteristicaDAO.validarInactivar(tipoCaracteristica)).thenReturn(null);
        model.setRegistro(tipoCaracteristica);

        model.btnModificarHandler();

        verify(tipoCaracteristicaDAO).update(tipoCaracteristica);
    }

    @Test
    void noEliminaSiElDaoDevuelveError() {
        TipoCaracteristica tipoCaracteristica = tipoCaracteristica(true);
        when(tipoCaracteristicaDAO.validarEliminar(tipoCaracteristica)).thenReturn("Hay características que usan este tipo.");
        model.setRegistro(tipoCaracteristica);

        model.btnEliminarHandler();

        verify(tipoCaracteristicaDAO, never()).delete(any());
        assertEquals("No se puede eliminar", ultimoMensaje().getSummary());
    }

    private TipoCaracteristica tipoCaracteristica(boolean activo) {
        TipoCaracteristica tipoCaracteristica = new TipoCaracteristica(UUID.randomUUID());
        tipoCaracteristica.setNombre("Volumen");
        tipoCaracteristica.setActivo(activo);
        return tipoCaracteristica;
    }
}