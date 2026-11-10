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
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.CaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.DescuentoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.DescuentoProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoCaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoTipoProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoCaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoDescuentoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.DescuentoProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoCaracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoTipoProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoDescuento;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoProducto;

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
    private ProductoTipoProductoDAO productoTipoProductoDAO;
    @Mock
    private TipoProductoDAO tipoProductoDAO;
    @Mock
    private ProductoCaracteristicaDAO productoCaracteristicaDAO;
    @Mock
    private TipoCaracteristicaDAO tipoCaracteristicaDAO;
    @Mock
    private CaracteristicaDAO caracteristicaDAO;
    @Mock
    private DescuentoProductoDAO descuentoProductoDAO;
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
    void noCreaElProductoSiElDaoRechazaElPrecio() {
        model.btnNuevoHandler();
        model.getRegistro().setNombre("Café");
        model.getRegistro().setPrecioSugerido(BigDecimal.ZERO);
        when(productoDAO.validarPrecio(model.getRegistro())).thenReturn("El precio sugerido debe ser mayor que $0.00.");

        model.btnCrearHandler();

        verify(productoDAO, never()).save(any());
        assertEquals("Precio inválido", ultimoMensaje().getSummary());
    }

    @Test
    void noModificaElProductoSiElDaoRechazaElPrecio() {
        model.seleccionarRegistro(producto);
        when(productoDAO.validarPrecio(producto)).thenReturn("El precio sugerido debe ser mayor que $0.00.");

        model.btnModificarHandler();

        verify(productoDAO, never()).update(any());
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

    @Test
    void noEliminaElProductoSiElDaoDevuelveError() {
        when(productoDAO.validarEliminar(producto)).thenReturn("El producto ya se ha pedido en órdenes.");
        model.setRegistro(producto);

        model.btnEliminarHandler();

        verify(productoDAO, never()).delete(any());
        assertEquals("No se puede eliminar", ultimoMensaje().getSummary());
    }

    @Test
    void eliminaElProductoSiElDaoNoDevuelveError() {
        when(productoDAO.validarEliminar(producto)).thenReturn(null);
        model.setRegistro(producto);

        model.btnEliminarHandler();

        verify(productoDAO).delete(producto);
    }

    // ===================== Tipos de producto =====================
    @Test
    void noAsignaElTipoSiElDaoLoRechaza() {
        model.seleccionarRegistro(producto);
        model.nuevoTipo();
        model.getRegistroTipo().setIdTipoProducto(tipoProducto());
        when(productoTipoProductoDAO.validarTipo(model.getRegistroTipo())).thenReturn("El tipo está inactivo.");

        model.guardarTipo();

        verify(productoTipoProductoDAO, never()).save(any());
        assertEquals("Tipo no válido", ultimoMensaje().getSummary());
    }

    @Test
    void noAsignaElTipoSiElDaoLoEncuentraRepetido() {
        model.seleccionarRegistro(producto);
        model.nuevoTipo();
        model.getRegistroTipo().setIdTipoProducto(tipoProducto());
        when(productoTipoProductoDAO.validarDuplicado(model.getRegistroTipo())).thenReturn("El producto ya tiene asignado ese tipo.");

        model.guardarTipo();

        verify(productoTipoProductoDAO, never()).save(any());
        assertEquals("Registro duplicado", ultimoMensaje().getSummary());
    }

    @Test
    void asignaElTipoSiElDaoNoEncuentraErrores() {
        model.seleccionarRegistro(producto);
        model.nuevoTipo();
        model.getRegistroTipo().setIdTipoProducto(tipoProducto());

        model.guardarTipo();

        verify(productoTipoProductoDAO).save(any(ProductoTipoProducto.class));
    }

    @Test
    void elDialogoDeTiposSoloMuestraLosActivos() {
        List<TipoProducto> tiposActivos = List.of(tipoProducto());
        when(tipoProductoDAO.findByActivo(Boolean.TRUE)).thenReturn(tiposActivos);

        model.abrirDialogoTipo();

        assertEquals(tiposActivos, model.getListaTiposActivos());
    }

    // ===================== Características =====================
    @Test
    void noAsignaLaCaracteristicaSiElDaoLaRechaza() {
        prepararCaracteristica("250");
        when(productoCaracteristicaDAO.validarCaracteristica(model.getRegistroCaracteristica())).thenReturn("Está inactiva.");

        model.guardarCaracteristica();

        verify(productoCaracteristicaDAO, never()).save(any());
        assertEquals("Característica no válida", ultimoMensaje().getSummary());
    }

    @Test
    void noAsignaLaCaracteristicaSiElDaoRechazaElValor() {
        prepararCaracteristica("mucho");
        when(productoCaracteristicaDAO.validarValor(model.getRegistroCaracteristica())).thenReturn("No cumple el formato.");

        model.guardarCaracteristica();

        verify(productoCaracteristicaDAO, never()).save(any());
        assertEquals("Valor no válido", ultimoMensaje().getSummary());
    }

    @Test
    void noAsignaLaCaracteristicaSiElDaoLaEncuentraRepetida() {
        prepararCaracteristica("250");
        when(productoCaracteristicaDAO.validarDuplicado(model.getRegistroCaracteristica())).thenReturn("El producto ya tiene esa característica.");

        model.guardarCaracteristica();

        verify(productoCaracteristicaDAO, never()).save(any());
        assertEquals("Registro duplicado", ultimoMensaje().getSummary());
    }

    @Test
    void asignaLaCaracteristicaSiElDaoNoEncuentraErrores() {
        prepararCaracteristica("8.50");

        model.guardarCaracteristica();

        verify(productoCaracteristicaDAO).save(any(ProductoCaracteristica.class));
    }

    @Test
    void elDialogoDeCaracteristicasPideAlDaoLosTiposYCaracteristicasActivas() {
        Caracteristica onzas = caracteristica();
        TipoCaracteristica volumen = onzas.getIdTipoCaracteristica();
        when(tipoCaracteristicaDAO.findByActivo(Boolean.TRUE)).thenReturn(List.of(volumen));
        when(caracteristicaDAO.buscarActivasPorTipo(volumen)).thenReturn(List.of(onzas));
        model.seleccionarRegistro(producto);
        model.nuevaCaracteristica();
        model.getRegistroCaracteristica().setIdCaracteristica(onzas);

        model.abrirDialogoCaracteristica();

        assertEquals(List.of(volumen), model.getListaTiposCaracteristicaActivos());
        assertEquals(List.of(onzas), model.getListaCaracteristicasDelTipo());
    }

    // ===================== Descuentos =====================
    @Test
    void noAsignaElDescuentoSiElDaoLoRechaza() {
        prepararDescuento(descuento(enDias(-1), enDias(10)), 10, enDias(0), enDias(5));
        when(descuentoProductoDAO.validarDescuento(model.getRegistroDescuento())).thenReturn("El descuento terminó.");

        model.guardarDescuento();

        verify(descuentoProductoDAO, never()).save(any());
        assertEquals("Descuento no válido", ultimoMensaje().getSummary());
    }

    @Test
    void noAsignaElDescuentoSiElDaoRechazaElPorcentaje() {
        prepararDescuento(descuento(enDias(-1), enDias(10)), 40, enDias(0), enDias(5));
        when(descuentoProductoDAO.validarValor(model.getRegistroDescuento())).thenReturn("Supera el máximo.");

        model.guardarDescuento();

        verify(descuentoProductoDAO, never()).save(any());
        assertEquals("Porcentaje no válido", ultimoMensaje().getSummary());
    }

    @Test
    void noAsignaElDescuentoSiElDaoRechazaLasFechas() {
        prepararDescuento(descuento(enDias(2), enDias(10)), 10, enDias(1), enDias(5));
        when(descuentoProductoDAO.validarFechas(model.getRegistroDescuento())).thenReturn("La fecha de inicio es anterior al descuento.");

        model.guardarDescuento();

        verify(descuentoProductoDAO, never()).save(any());
        assertEquals("Fechas no válidas", ultimoMensaje().getSummary());
    }

    @Test
    void noAsignaElDescuentoSiElDaoLoEncuentraRepetido() {
        prepararDescuento(descuento(enDias(-1), null), 10, enDias(0), null);
        when(descuentoProductoDAO.validarDuplicado(model.getRegistroDescuento())).thenReturn("El producto ya tiene asignado ese descuento.");

        model.guardarDescuento();

        verify(descuentoProductoDAO, never()).save(any());
        assertEquals("Registro duplicado", ultimoMensaje().getSummary());
    }

    @Test
    void asignaElDescuentoSiElDaoNoEncuentraErrores() {
        prepararDescuento(descuento(enDias(-1), null), 10, enDias(0), null);

        model.guardarDescuento();

        verify(descuentoProductoDAO).save(any(DescuentoProducto.class));
    }

    @Test
    void elDialogoDeDescuentosSoloMuestraTiposActivos() {
        TipoDescuento temporada = descuento(enDias(-1), null).getIdTipoDescuento();
        when(tipoDescuentoDAO.findByActivo(Boolean.TRUE)).thenReturn(List.of(temporada));

        model.abrirDialogoDescuento();

        assertEquals(List.of(temporada), model.getListaTiposDescuentoActivos());
    }

    @Test
    void alSeleccionarUnDescuentoFuturoSeProponenSusFechas() {
        Date inicio = enDias(3);
        Date fin = enDias(10);
        model.seleccionarRegistro(producto);
        model.nuevoDescuento();
        model.setDescuentoElegido(descuento(inicio, fin));

        model.confirmarDescuento();

        assertEquals(inicio, model.getRegistroDescuento().getFechaDesde());
        assertEquals(fin, model.getRegistroDescuento().getFechaHasta());
    }

    // ===================== Ayudantes =====================
    private TipoProducto tipoProducto() {
        TipoProducto bebidas = new TipoProducto(UUID.randomUUID());
        bebidas.setNombre("Bebidas");
        bebidas.setActivo(true);
        return bebidas;
    }

    private Caracteristica caracteristica() {
        TipoCaracteristica volumen = new TipoCaracteristica(UUID.randomUUID());
        volumen.setNombre("Volumen");
        volumen.setActivo(true);
        volumen.setExpresionRegular("^\\d+(\\.\\d{1,2})?$");
        Caracteristica onzas = new Caracteristica(UUID.randomUUID());
        onzas.setNombre("Onzas");
        onzas.setActivo(true);
        onzas.setIdTipoCaracteristica(volumen);
        return onzas;
    }

    private Descuento descuento(Date fechaDesde, Date fechaHasta) {
        TipoDescuento temporada = new TipoDescuento(UUID.randomUUID());
        temporada.setNombre("Temporada");
        temporada.setActivo(true);
        temporada.setDescuentoMaximo(30);
        Descuento navidad = new Descuento(UUID.randomUUID());
        navidad.setNombre("Navidad");
        navidad.setIdTipoDescuento(temporada);
        navidad.setFechaDesde(fechaDesde);
        navidad.setFechaHasta(fechaHasta);
        return navidad;
    }

    private void prepararCaracteristica(String valor) {
        model.seleccionarRegistro(producto);
        model.nuevaCaracteristica();
        model.getRegistroCaracteristica().setIdCaracteristica(caracteristica());
        model.getRegistroCaracteristica().setValor(valor);
    }

    private void prepararDescuento(Descuento descuento, Integer porcentaje, Date fechaDesde, Date fechaHasta) {
        model.seleccionarRegistro(producto);
        model.nuevoDescuento();
        model.getRegistroDescuento().setIdDescuento(descuento);
        model.getRegistroDescuento().setValor(porcentaje);
        model.getRegistroDescuento().setFechaDesde(fechaDesde);
        model.getRegistroDescuento().setFechaHasta(fechaHasta);
    }

}
