package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.lang.reflect.Method;
import java.util.Date;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockMakers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 *
 * @author valen
 */
/**
 * Base de las pruebas de los models: registra un FacesContext falso como el
 * contexto actual, para poder ejecutar los handlers fuera del servidor y leer
 * los mensajes que muestran.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class ModelTestBase {

    /**
     * FacesContext se crea manualmente con el mock maker SUBCLASS.
     *
     * Esto evita que Mockito intente instrumentar la jerarquía interna de
     * FacesContext mediante el mock maker INLINE, que en el classpath de
     * pruebas no encuentra com.sun.faces.config.InitFacesContext.
     */
    protected FacesContext facesContext;

    @BeforeEach
    void registrarFacesContext() throws Exception {
        facesContext = mock(
                FacesContext.class,
                withSettings().mockMaker(MockMakers.SUBCLASS)
        );

        cambiarContextoActual(facesContext);
    }

    @AfterEach
    void limpiarFacesContext() throws Exception {
        cambiarContextoActual(null);
    }

    /**
     * FacesContext.setCurrentInstance(...) es protected; se llama por reflexión
     * para que FacesContext.getCurrentInstance() devuelva el falso.
     */
    private static void cambiarContextoActual(FacesContext contexto) throws Exception {
        Method metodo = FacesContext.class.getDeclaredMethod(
                "setCurrentInstance",
                FacesContext.class
        );

        metodo.setAccessible(true);
        metodo.invoke(null, contexto);
    }

    /**
     * Devuelve el último mensaje que el model mandó a la pantalla.
     */
    protected FacesMessage ultimoMensaje() {
        ArgumentCaptor<FacesMessage> captor
                = ArgumentCaptor.forClass(FacesMessage.class);

        verify(facesContext, atLeastOnce())
                .addMessage(isNull(), captor.capture());

        return captor.getValue();
    }

    /**
     * Fecha relativa a hoy: enDias(1) = mañana, enDias(-1) = ayer.
     */
    protected static Date enDias(int dias) {
        return new Date(
                System.currentTimeMillis()
                + dias * 24L * 60 * 60 * 1000
        );
    }

}
