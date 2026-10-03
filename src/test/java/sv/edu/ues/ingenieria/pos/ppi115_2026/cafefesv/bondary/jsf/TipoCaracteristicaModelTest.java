package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.CaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoCaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;

/**
 *
 * @author valen
 */
public class TipoCaracteristicaModelTest extends ModelTestBase {

    @InjectMocks
    private TipoCaracteristicaModel model;

    @Mock
    private TipoCaracteristicaDAO tipoCaracteristicaDAO;

    @Mock
    private CaracteristicaDAO caracteristicaDAO;

    @Test
    void rechazaExpresionRegularInvalida() {
        model.setRegistro(tipo(true, "^\\d+("));   // falta cerrar el paréntesis

        model.btnCrearHandler();

        verify(tipoCaracteristicaDAO, never()).save(any());
        assertEquals("Expresión regular inválida", ultimoMensaje().getSummary());
    }

    @Test
    void aceptaExpresionRegularValida() {
        TipoCaracteristica tipo = tipo(true, "^\\d+(\\.\\d{1,2})?$");
        model.setRegistro(tipo);

        model.btnCrearHandler();

        verify(tipoCaracteristicaDAO).save(tipo);
    }

    @Test
    void noInactivaSiTieneCaracteristicasActivas() {
        TipoCaracteristica tipo = tipo(false, null);
        when(caracteristicaDAO.findByTipoCaracteristica(tipo)).thenReturn(List.of(caracteristica(true)));
        model.setRegistro(tipo);

        model.btnModificarHandler();

        verify(tipoCaracteristicaDAO, never()).update(any());
        assertEquals("No se puede inactivar", ultimoMensaje().getSummary());
    }

    @Test
    void inactivaSiSusCaracteristicasEstanInactivas() {
        TipoCaracteristica tipo = tipo(false, null);
        when(caracteristicaDAO.findByTipoCaracteristica(tipo)).thenReturn(List.of(caracteristica(false)));
        model.setRegistro(tipo);

        model.btnModificarHandler();

        verify(tipoCaracteristicaDAO).update(tipo);
    }

    private TipoCaracteristica tipo(boolean activo, String regex) {
        TipoCaracteristica t = new TipoCaracteristica();
        t.setIdTipoCaracteristica(UUID.randomUUID());
        t.setNombre("Volumen");
        t.setActivo(activo);
        t.setExpresionRegular(regex);
        return t;
    }

    private Caracteristica caracteristica(boolean activa) {
        Caracteristica c = new Caracteristica();
        c.setIdCaracteristica(UUID.randomUUID());
        c.setNombre("Onzas");
        c.setActivo(activa);
        return c;
    }

}
