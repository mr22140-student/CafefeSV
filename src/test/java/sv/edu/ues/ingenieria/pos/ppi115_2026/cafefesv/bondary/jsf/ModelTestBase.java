package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.util.Date;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 *
 * @author valen
 */
/**
 * Base de las pruebas de los models: simula el FacesContext para poder ejecutar
 * los handlers fuera del servidor y leer los mensajes que muestran.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class ModelTestBase {

    @Mock
    protected FacesContext facesContext;

    private MockedStatic<FacesContext> facesEstatico;

    @BeforeEach
    void simularFacesContext() {
        facesEstatico = mockStatic(FacesContext.class);
        facesEstatico.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
    }

    @AfterEach
    void cerrarFacesContext() {
        facesEstatico.close();
    }

    /**
     * Devuelve el último mensaje que el model mandó a la pantalla.
     */
    protected FacesMessage ultimoMensaje() {
        ArgumentCaptor<FacesMessage> captor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext, atLeastOnce()).addMessage(isNull(), captor.capture());
        return captor.getValue();
    }

    /**
     * Fecha relativa a hoy: enDias(1) = mañana, enDias(-1) = ayer.
     */
    protected static Date enDias(int dias) {
        return new Date(System.currentTimeMillis() + dias * 24L * 60 * 60 * 1000);
    }

}
