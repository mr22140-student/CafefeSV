package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.faces.component.UIComponent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoProducto;

/**
 *
 * @author valen
 */
public class EntityConverterTest {

    private EntityConverter converter;
    private UIComponent componente;

    @BeforeEach
    void preparar() {
        converter = new EntityConverter();
        componente = mock(UIComponent.class);
        Map<String, Object> atributos = new HashMap<>();
        when(componente.getAttributes()).thenReturn(atributos);
    }

    @Test
    void getAsString_conNull_devuelveTextoVacio() {
        assertEquals("", converter.getAsString(null, componente, null));
    }

    @Test
    void getAsString_conEntidadNueva_generaClaveYLaGuarda() {
        TipoProducto tipo = tipo("Bebidas");

        String clave = converter.getAsString(null, componente, tipo);

        assertTrue(clave.startsWith("entityConverter_"));
        assertSame(tipo, componente.getAttributes().get(clave));
    }

    @Test
    void getAsString_mismaEntidadDosVeces_devuelveLaMismaClave() {
        TipoProducto tipo = tipo("Bebidas");

        assertEquals(converter.getAsString(null, componente, tipo),
                converter.getAsString(null, componente, tipo));
    }

    @Test
    void getAsString_entidadesDistintas_devuelvenClavesDistintas() {
        assertNotEquals(converter.getAsString(null, componente, tipo("Bebidas")),
                converter.getAsString(null, componente, tipo("Postres")));
    }

    @Test
    void getAsObject_conLaClave_devuelveLaMismaEntidad() {
        TipoProducto tipo = tipo("Bebidas");
        String clave = converter.getAsString(null, componente, tipo);

        assertSame(tipo, converter.getAsObject(null, componente, clave));
    }

    @Test
    void getAsObject_conNullOVacio_devuelveNull() {
        assertNull(converter.getAsObject(null, componente, null));
        assertNull(converter.getAsObject(null, componente, "   "));
    }

    @Test
    void getAsObject_conClaveDesconocida_devuelveNull() {
        assertNull(converter.getAsObject(null, componente, "entityConverter_noExiste"));
    }

    private TipoProducto tipo(String nombre) {
        TipoProducto tipoProducto = new TipoProducto(UUID.randomUUID());
        tipoProducto.setNombre(nombre);
        return tipoProducto;
    }

}
