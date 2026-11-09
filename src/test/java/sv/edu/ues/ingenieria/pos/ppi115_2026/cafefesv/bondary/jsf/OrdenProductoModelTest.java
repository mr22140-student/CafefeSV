package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.OrdenDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.OrdenProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Orden;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.OrdenProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;

/**
 *
 * @author valen
 */
public class OrdenProductoModelTest extends ModelTestBase {

    @InjectMocks
    private OrdenProductoModel model;

    @Mock
    private OrdenProductoDAO ordenProductoDAO;
    @Mock
    private OrdenDAO ordenDAO;
    @Mock
    private ProductoDAO productoDAO;
    @Mock
    private EmpleadoDAO empleadoDAO;

        @Test
    void noGuardaSiElDaoRechazaElDetalle() {
        prepararRegistro(new BigDecimal("-1"));
        when(ordenProductoDAO.validarDetalle(model.getRegistro())).thenReturn("El precio debe ser mayor o igual a cero.");

        model.btnCrearHandler();

        verify(ordenProductoDAO, never()).save(any());
        assertEquals("Producto no válido", ultimoMensaje().getSummary());
    }

    @Test
    void alElegirProducto_copiaSuPrecioSugerido() {
        model.btnNuevoHandler();
        model.getRegistro().setIdProducto(producto("3.75"));

        model.productoSeleccionadoHandler();

        assertEquals(new BigDecimal("3.75"), model.getRegistro().getPrecio());
    }

    @Test
    void conVariasUnidades_guardaUnaFilaPorUnidad() {
        prepararRegistro(new BigDecimal("2.50"));
        model.setCantidad(3);

        model.btnCrearHandler();

        verify(ordenProductoDAO, times(3)).save(any(OrdenProducto.class));
        assertEquals("3 productos agregados a la orden", ultimoMensaje().getSummary());
    }

    @Test
    void noModificaSiElDaoDiceQueYaEstaFacturado() {
        OrdenProducto detalle = new OrdenProducto(UUID.randomUUID());
        when(ordenProductoDAO.validarNoFacturado(detalle)).thenReturn("Este producto ya está facturado.");
        model.seleccionarRegistro(detalle);

        model.btnModificarHandler();

        verify(ordenProductoDAO, never()).update(any());
        assertEquals("No se puede modificar", ultimoMensaje().getSummary());
    }

    @Test
    void noEliminaSiElDaoDiceQueYaEstaFacturado() {
        OrdenProducto detalle = new OrdenProducto(UUID.randomUUID());
        when(ordenProductoDAO.validarNoFacturado(detalle)).thenReturn("Este producto ya está facturado.");
        model.seleccionarRegistro(detalle);

        model.btnEliminarHandler();

        verify(ordenProductoDAO, never()).delete(any());
        assertEquals("No se puede eliminar", ultimoMensaje().getSummary());
    }

    @Test
    void alIniciar_losProductosDelComboLosTraeElDao() {
        Producto cafe = producto("2.50");
        when(productoDAO.findByActivo(Boolean.TRUE)).thenReturn(List.of(cafe));

        model.init();

        assertEquals(List.of(cafe), model.getListaProducto());
    }

    @Test
    void elTotal_loCalculaElDaoConLoQueSeVeEnLaTabla() {
        List<OrdenProducto> detalles = List.of(new OrdenProducto(UUID.randomUUID()));
        model.setRegistros(detalles);
        when(ordenProductoDAO.calcularTotal(detalles)).thenReturn(new BigDecimal("3.50"));

        assertEquals(new BigDecimal("3.50"), model.getTotal());
    }

    private void prepararRegistro(BigDecimal precio) {
        model.btnNuevoHandler();
        model.getRegistro().setIdOrden(new Orden(UUID.randomUUID()));
        model.getRegistro().setIdProducto(producto("2.50"));
        model.getRegistro().setPrecio(precio);
    }

    private Producto producto(String precio) {
        Producto cafe = new Producto(UUID.randomUUID());
        cafe.setNombre("Café");
        cafe.setActivo(true);
        cafe.setPrecioSugerido(new BigDecimal(precio));
        return cafe;
    }
}