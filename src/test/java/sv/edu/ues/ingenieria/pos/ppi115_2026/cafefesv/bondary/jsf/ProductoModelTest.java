package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.*;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.*;

/**
 *
 * @author valen
 */
public class ProductoModelTest extends ModelTestBase {

    @InjectMocks
    private ProductoModel model;

    @Mock
    private ProductoDAO productoDAO;
    @Mock
    private ProductoTipoProductoDAO ptpDAO;
    @Mock
    private TipoProductoDAO tipoProductoDAO;
    @Mock
    private ProductoCaracteristicaDAO pcDAO;
    @Mock
    private TipoCaracteristicaDAO tipoCaracteristicaDAO;
    @Mock
    private CaracteristicaDAO caracteristicaDAO;
    @Mock
    private DescuentoProductoDAO dpDAO;
    @Mock
    private TipoDescuentoDAO tipoDescuentoDAO;
    @Mock
    private DescuentoDAO descuentoDAO;

    private Producto producto;

    @BeforeEach
    void prepararProducto() {
        producto = new Producto(UUID.randomUUID());
        producto.setNombre("Café latte");
        producto.setActivo(true);
        producto.setPrecioSugerido(new BigDecimal("2.50"));
    }

    // ===================== Generalidades =====================
    @Test
    void noCreaProductoConPrecioCero() {
        model.btnNuevoHandler();
        model.getRegistro().setNombre("Café");
        model.getRegistro().setPrecioSugerido(BigDecimal.ZERO);

        model.btnCrearHandler();

        verify(productoDAO, never()).save(any());
        assertEquals("Precio inválido", ultimoMensaje().getSummary());
    }

    @Test
    void alCrearSeQuedaEnElProductoYAbreLaPestanaTipos() {
        model.btnNuevoHandler();
        model.getRegistro().setNombre("Café");
        model.getRegistro().setPrecioSugerido(new BigDecimal("2.50"));

        model.btnCrearHandler();

        verify(productoDAO).save(any(Producto.class));
        assertEquals(AbstractModel.ESTADO_MODIFICAR, model.getEstado());
        assertEquals(1, model.getTabActiva());
    }

    // ===================== Tipos de producto =====================
    @Test
    void noAsignaUnTipoInactivo() {
        model.seleccionarRegistro(producto);
        model.nuevoTipo();
        model.getRegistroTipo().setIdTipoProducto(tipoProducto(false));

        model.guardarTipo();

        verify(ptpDAO, never()).save(any());
        assertEquals("Tipo inactivo", ultimoMensaje().getSummary());
    }

    @Test
    void noAsignaElMismoTipoDosVeces() {
        TipoProducto bebidas = tipoProducto(true);
        ProductoTipoProducto existente = new ProductoTipoProducto(UUID.randomUUID());
        existente.setProducto(producto);
        existente.setIdTipoProducto(bebidas);
        when(ptpDAO.findByProducto(producto)).thenReturn(List.of(existente));

        model.seleccionarRegistro(producto);
        model.nuevoTipo();
        model.getRegistroTipo().setIdTipoProducto(bebidas);
        model.guardarTipo();

        verify(ptpDAO, never()).save(any());
        assertEquals("Registro duplicado", ultimoMensaje().getSummary());
    }

    @Test
    void asignaUnTipoActivo() {
        model.seleccionarRegistro(producto);
        model.nuevoTipo();
        model.getRegistroTipo().setIdTipoProducto(tipoProducto(true));

        model.guardarTipo();

        verify(ptpDAO).save(any(ProductoTipoProducto.class));
    }

    // ===================== Características =====================
    @Test
    void rechazaUnValorQueNoCumpleLaExpresionRegular() {
        model.seleccionarRegistro(producto);
        model.nuevaCaracteristica();
        model.getRegistroCaracteristica().setIdCaracteristica(caracteristicaConExpresion("^\\d+(\\.\\d{1,2})?$"));
        model.getRegistroCaracteristica().setValor("abc");

        model.guardarCaracteristica();

        verify(pcDAO, never()).save(any());
        assertEquals("El valor no es válido", ultimoMensaje().getSummary());
    }

    @Test
    void aceptaUnValorQueCumpleLaExpresionRegular() {
        model.seleccionarRegistro(producto);
        model.nuevaCaracteristica();
        model.getRegistroCaracteristica().setIdCaracteristica(caracteristicaConExpresion("^\\d+(\\.\\d{1,2})?$"));
        model.getRegistroCaracteristica().setValor("8.50");

        model.guardarCaracteristica();

        verify(pcDAO).save(any(ProductoCaracteristica.class));
    }

    // ===================== Descuentos =====================
    @Test
    void rechazaUnPorcentajeMayorAlMaximoDelTipo() {
        prepararDescuento(descuento(30, enDias(-1), enDias(10)), 40, enDias(0), enDias(5));

        model.guardarDescuento();

        verify(dpDAO, never()).save(any());
        assertEquals("El valor no es válido", ultimoMensaje().getSummary());
    }

    @Test
    void rechazaUnaFechaDeInicioAntesDeQueEmpieceElDescuento() {
        prepararDescuento(descuento(30, enDias(2), enDias(10)), 10, enDias(1), enDias(5));

        model.guardarDescuento();

        verify(dpDAO, never()).save(any());
        assertEquals("La fecha inicial no es válida", ultimoMensaje().getSummary());
    }

    @Test
    void rechazaUnaFechaFinalPosteriorAlFinDelDescuento() {
        prepararDescuento(descuento(30, enDias(-1), enDias(10)), 10, enDias(0), enDias(15));

        model.guardarDescuento();

        verify(dpDAO, never()).save(any());
        assertEquals("La fecha final no es válida", ultimoMensaje().getSummary());
    }

    @Test
    void exigeFechaFinalSiElDescuentoTieneFin() {
        prepararDescuento(descuento(30, enDias(-1), enDias(10)), 10, enDias(0), null);

        model.guardarDescuento();

        verify(dpDAO, never()).save(any());
        assertEquals("La fecha final no es válida", ultimoMensaje().getSummary());
    }

    @Test
    void aceptaUnDescuentoAbiertoSinFechaFinal() {
        prepararDescuento(descuento(30, enDias(-1), null), 10, enDias(0), null);

        model.guardarDescuento();

        verify(dpDAO).save(any(DescuentoProducto.class));
    }

    @Test
    void alSeleccionarUnDescuentoFuturoSeProponenSusFechas() {
        Date inicio = enDias(3);
        Date fin = enDias(10);
        model.seleccionarRegistro(producto);
        model.nuevoDescuento();
        model.setDescuentoElegido(descuento(30, inicio, fin));

        model.confirmarDescuento();

        assertEquals(inicio, model.getRegistroDescuento().getFechaDesde());
        assertEquals(fin, model.getRegistroDescuento().getFechaHasta());
    }

    // ===================== Ayudantes =====================
    private TipoProducto tipoProducto(boolean activo) {
        TipoProducto t = new TipoProducto(UUID.randomUUID());
        t.setNombre(activo ? "Bebidas" : "Postres");
        t.setActivo(activo);
        return t;
    }

    private Caracteristica caracteristicaConExpresion(String regex) {
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

    private Descuento descuento(int maximo, Date desde, Date hasta) {
        TipoDescuento tipo = new TipoDescuento(UUID.randomUUID());
        tipo.setNombre("Temporada");
        tipo.setActivo(true);
        tipo.setDescuentoMaximo(maximo);

        Descuento d = new Descuento(UUID.randomUUID());
        d.setNombre("Navidad");
        d.setIdTipoDescuento(tipo);
        d.setFechaDesde(desde);
        d.setFechaHasta(hasta);
        return d;
    }

    private void prepararDescuento(Descuento d, Integer valor, Date desde, Date hasta) {
        model.seleccionarRegistro(producto);
        model.nuevoDescuento();
        model.getRegistroDescuento().setIdDescuento(d);
        model.getRegistroDescuento().setValor(valor);
        model.getRegistroDescuento().setFechaDesde(desde);
        model.getRegistroDescuento().setFechaHasta(hasta);
    }

}
