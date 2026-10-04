package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
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
    void noGuardaSinOrden() {
        model.btnNuevoHandler();
        model.getRegistro().setIdProducto(producto("2.50"));
        model.getRegistro().setPrecio(new BigDecimal("2.50"));

        model.btnCrearHandler();

        verify(ordenProductoDAO, never()).save(any());
        assertEquals("Debe seleccionar la orden.", ultimoMensaje().getSummary());
    }

    @Test
    void noGuardaConPrecioNegativo() {
        prepararRegistro(new BigDecimal("-1"));

        model.btnCrearHandler();

        verify(ordenProductoDAO, never()).save(any());
        assertEquals("El precio debe ser mayor o igual a cero.", ultimoMensaje().getSummary());
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
    void noModificaUnProductoYaFacturado() {
        OrdenProducto op = new OrdenProducto(UUID.randomUUID());
        when(ordenProductoDAO.countFacturas(op)).thenReturn(1L);
        model.seleccionarRegistro(op);

        model.btnModificarHandler();

        verify(ordenProductoDAO, never()).update(any());
        assertEquals("No se puede modificar: este producto ya está facturado.", ultimoMensaje().getSummary());
    }

    @Test
    void elTotal_sumaLosPreciosDeLaOrden() {
        OrdenProducto a = new OrdenProducto(UUID.randomUUID());
        a.setPrecio(new BigDecimal("2.50"));
        OrdenProducto b = new OrdenProducto(UUID.randomUUID());
        b.setPrecio(new BigDecimal("1.00"));
        OrdenProducto sinPrecio = new OrdenProducto(UUID.randomUUID());
        model.setRegistros(Arrays.asList(a, b, sinPrecio));

        assertEquals(new BigDecimal("3.50"), model.getTotal());
    }

    private void prepararRegistro(BigDecimal precio) {
        model.btnNuevoHandler();
        model.getRegistro().setIdOrden(new Orden(UUID.randomUUID()));
        model.getRegistro().setIdProducto(producto("2.50"));
        model.getRegistro().setPrecio(precio);
    }

    private Producto producto(String precio) {
        Producto p = new Producto(UUID.randomUUID());
        p.setNombre("Café");
        p.setActivo(true);
        p.setPrecioSugerido(new BigDecimal(precio));
        return p;
    }

}
