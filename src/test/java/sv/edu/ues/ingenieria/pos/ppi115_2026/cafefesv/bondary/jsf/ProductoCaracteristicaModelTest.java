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
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoCaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoCaracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;

/**
 *
 * @author valen
 */
public class ProductoCaracteristicaModelTest extends ModelTestBase {

    private static final String DECIMAL = "^\\d+(\\.\\d{1,2})?$";

    @InjectMocks
    private ProductoCaracteristicaModel model;

    @Mock
    private ProductoCaracteristicaDAO pcDAO;
    @Mock
    private ProductoDAO productoDAO;
    @Mock
    private CaracteristicaDAO caracteristicaDAO;

    @Test
    void rechazaUnValorQueNoCumpleLaExpresion() {
        model.setRegistro(registro(new Producto(UUID.randomUUID()), caracteristica(DECIMAL), "abc"));

        model.btnCrearHandler();

        verify(pcDAO, never()).save(any());
        assertEquals("El valor no es válido", ultimoMensaje().getSummary());
    }

    @Test
    void guardaUnValorQueCumpleLaExpresion() {
        ProductoCaracteristica pc = registro(new Producto(UUID.randomUUID()), caracteristica(DECIMAL), "8.50");
        model.setRegistro(pc);

        model.btnCrearHandler();

        verify(pcDAO).save(pc);
    }

    @Test
    void noGuardaLaMismaCaracteristicaDosVeces() {
        Producto producto = new Producto(UUID.randomUUID());
        Caracteristica c = caracteristica(DECIMAL);
        when(pcDAO.findByProducto(producto)).thenReturn(List.of(registro(producto, c, "1")));
        model.setRegistro(registro(producto, c, "2"));

        model.btnCrearHandler();

        verify(pcDAO, never()).save(any());
        assertEquals("Registro duplicado", ultimoMensaje().getSummary());
    }

    @Test
    void muestraElFormatoEsperado() {
        model.setRegistro(registro(new Producto(UUID.randomUUID()), caracteristica(DECIMAL), null));

        assertEquals("Formato: " + DECIMAL, model.getFormatoValor());
    }

    private Caracteristica caracteristica(String regex) {
        TipoCaracteristica tipo = new TipoCaracteristica();
        tipo.setIdTipoCaracteristica(UUID.randomUUID());
        tipo.setNombre("Volumen");
        tipo.setActivo(true);
        tipo.setExpresionRegular(regex);
        Caracteristica c = new Caracteristica();
        c.setIdCaracteristica(UUID.randomUUID());
        c.setNombre("Onzas");
        c.setActivo(true);
        c.setIdTipoCaracteristica(tipo);
        return c;
    }

    private ProductoCaracteristica registro(Producto p, Caracteristica c, String valor) {
        ProductoCaracteristica pc = new ProductoCaracteristica(UUID.randomUUID());
        pc.setIdProducto(p);
        pc.setIdCaracteristica(c);
        pc.setValor(valor);
        return pc;
    }

}
