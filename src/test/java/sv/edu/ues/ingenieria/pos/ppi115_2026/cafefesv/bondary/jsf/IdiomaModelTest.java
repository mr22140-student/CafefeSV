package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import jakarta.faces.component.UIViewRoot;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockMakers;

/**
 *
 * @author valen
 */
public class IdiomaModelTest extends ModelTestBase {

    private IdiomaModel model;
    private UIViewRoot vista;

    @BeforeEach
    void preparar() {
        model = new IdiomaModel();
        vista = mock(UIViewRoot.class, withSettings().mockMaker(MockMakers.SUBCLASS));
        when(facesContext.getViewRoot()).thenReturn(vista);
    }

    @Test
    void elIdiomaPorDefectoEsEspanol() {
        assertEquals("es", model.getLocale());
    }

    @Test
    void cambiarAIngles_cambiaElIdiomaDeLaPagina() {
        model.setLocale("en");

        assertEquals("en", model.getLocale());
        verify(vista).setLocale(Locale.of("en", "US"));
    }

    @Test
    void cambiarAChino_usaChinoTradicional() {
        model.setLocale("zh");

        verify(vista).setLocale(Locale.of("zh", "TW"));
    }

    @Test
    void unIdiomaDesconocido_vuelveAEspanol() {
        model.setLocale("fr");

        verify(vista).setLocale(Locale.of("es"));
    }

}
