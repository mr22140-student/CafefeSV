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

    private static final String SOLO_NUMEROS = "^\\d+(\\.\\d{1,2})?$";

    @InjectMocks
    private ProductoCaracteristicaModel model;

    @Mock
    private ProductoCaracteristicaDAO productoCaracteristicaDAO;
    @Mock
    private ProductoDAO productoDAO;
    @Mock
    private CaracteristicaDAO caracteristicaDAO;

    @Test
    void noGuardaSiElDaoRechazaLaCaracteristica() {
        ProductoCaracteristica asignacion = asignacion("250");
        when(productoCaracteristicaDAO.validarCaracteristica(asignacion)).thenReturn("Está inactiva.");
        model.setRegistro(asignacion);

        model.btnCrearHandler();

        verify(productoCaracteristicaDAO, never()).save(any());
        assertEquals("Característica no válida", ultimoMensaje().getSummary());
    }

    @Test
    void noGuardaSiElDaoRechazaElValor() {
        ProductoCaracteristica asignacion = asignacion("mucho");
        when(productoCaracteristicaDAO.validarValor(asignacion)).thenReturn("No cumple el formato.");
        model.setRegistro(asignacion);

        model.btnCrearHandler();

        verify(productoCaracteristicaDAO, never()).save(any());
        assertEquals("Valor no válido", ultimoMensaje().getSummary());
    }

    @Test
    void noGuardaSiElDaoEncuentraLaCaracteristicaRepetida() {
        ProductoCaracteristica asignacion = asignacion("250");
        when(productoCaracteristicaDAO.validarDuplicado(asignacion)).thenReturn("El producto ya tiene esa característica.");
        model.setRegistro(asignacion);

        model.btnCrearHandler();

        verify(productoCaracteristicaDAO, never()).save(any());
        assertEquals("Registro duplicado", ultimoMensaje().getSummary());
    }

    @Test
    void guardaSiElDaoNoEncuentraErrores() {
        ProductoCaracteristica asignacion = asignacion("8.50");
        model.setRegistro(asignacion);

        model.btnCrearHandler();

        verify(productoCaracteristicaDAO).save(asignacion);
    }

    @Test
    void muestraElFormatoEsperado() {
        model.setRegistro(asignacion(null));

        assertEquals("Formato: " + SOLO_NUMEROS, model.getFormatoValor());
    }

    private ProductoCaracteristica asignacion(String valor) {
        TipoCaracteristica volumen = new TipoCaracteristica(UUID.randomUUID());
        volumen.setNombre("Volumen");
        volumen.setActivo(true);
        volumen.setExpresionRegular(SOLO_NUMEROS);
        Caracteristica onzas = new Caracteristica(UUID.randomUUID());
        onzas.setNombre("Onzas");
        onzas.setActivo(true);
        onzas.setIdTipoCaracteristica(volumen);
        ProductoCaracteristica asignacion = new ProductoCaracteristica(UUID.randomUUID());
        asignacion.setIdProducto(new Producto(UUID.randomUUID()));
        asignacion.setIdCaracteristica(onzas);
        asignacion.setValor(valor);
        return asignacion;
    }
}
