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
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;

/**
 * El model solo llama a las reglas del DAO. Las reglas en sí se prueban en
 * DescuentoDAOTest.
 *
 * @author valen
 */
public class DescuentoModelTest extends ModelTestBase {

    @InjectMocks
    private DescuentoModel model;

    @Mock
    private DescuentoDAO descuentoDAO;

    @Mock
    private TipoDescuentoDAO tipoDescuentoDAO;

    @Test
    void noCreaSiLasFechasSonInvalidas() {
        Descuento descuento = new Descuento(UUID.randomUUID());
        when(descuentoDAO.validarFechas(descuento)).thenReturn("La fecha hasta no puede ser anterior a la fecha desde.");
        model.setRegistro(descuento);

        model.btnCrearHandler();

        verify(descuentoDAO, never()).save(any());
        assertEquals("Fechas inválidas", ultimoMensaje().getSummary());
    }

    @Test
    void noCreaSiElTipoEstaInactivo() {
        Descuento descuento = new Descuento(UUID.randomUUID());
        when(descuentoDAO.validarFechas(descuento)).thenReturn(null);
        when(descuentoDAO.validarTipoActivo(descuento)).thenReturn("El tipo está inactivo.");
        model.setRegistro(descuento);

        model.btnCrearHandler();

        verify(descuentoDAO, never()).save(any());
        assertEquals("Tipo inactivo", ultimoMensaje().getSummary());
    }

    @Test
    void creaSiNingunaReglaDevuelveError() {
        Descuento descuento = new Descuento(UUID.randomUUID());
        when(descuentoDAO.validarFechas(descuento)).thenReturn(null);
        when(descuentoDAO.validarTipoActivo(descuento)).thenReturn(null);
        model.setRegistro(descuento);

        model.btnCrearHandler();

        verify(descuentoDAO).save(descuento);
    }

    @Test
    void noModificaSiLasFechasSonInvalidas() {
        Descuento descuento = new Descuento(UUID.randomUUID());
        when(descuentoDAO.validarFechas(descuento)).thenReturn("La fecha hasta no puede ser anterior a la fecha desde.");
        model.setRegistro(descuento);

        model.btnModificarHandler();

        verify(descuentoDAO, never()).update(any());
        assertEquals("Fechas inválidas", ultimoMensaje().getSummary());
    }

    @Test
    void laVigenciaLaCalculaElDao() {
        Descuento descuento = new Descuento(UUID.randomUUID());
        when(descuentoDAO.calcularVigencia(descuento)).thenReturn("VIGENTE");

        assertEquals("VIGENTE", model.vigencia(descuento));
    }
}