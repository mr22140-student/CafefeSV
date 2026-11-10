package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.FacturaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.FacturaOrdenProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.FacturaOrdenProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.OrdenProducto;

/**
 *
 * @author valen
 */
public class FacturaOrdenProductoModelTest extends ModelTestBase {

    @InjectMocks
    private FacturaOrdenProductoModel model;

    @Mock
    private FacturaOrdenProductoDAO facturaOrdenProductoDAO;
    @Mock
    private FacturaDAO facturaDAO;
    @Mock
    private EmpleadoDAO empleadoDAO;

    @Test
    void noGuardaSiElDaoRechazaElDetalle() {
        FacturaOrdenProducto detalle = detalle(factura(FacturaDAO.PAGADA), new OrdenProducto(UUID.randomUUID()));
        when(facturaOrdenProductoDAO.validarDetalle(detalle))
                .thenReturn("La factura está PAGADA; solo se pueden cambiar facturas PENDIENTES.");
        model.setRegistro(detalle);

        model.btnCrearHandler();

        verify(facturaOrdenProductoDAO, never()).save(any());
        assertEquals("Producto no válido", ultimoMensaje().getSummary());
    }

    @Test
    void guardaSiElDaoNoEncuentraErrores() {
        FacturaOrdenProducto detalle = detalle(factura(FacturaDAO.PENDIENTE), new OrdenProducto(UUID.randomUUID()));
        model.setRegistro(detalle);

        model.btnCrearHandler();

        verify(facturaOrdenProductoDAO).save(detalle);
    }

    @Test
    void noModificaSiLaFacturaOriginalYaNoSePuedeCambiar() {
        Factura pagada = factura(FacturaDAO.PAGADA);
        FacturaOrdenProducto detalle = detalle(pagada, new OrdenProducto(UUID.randomUUID()));
        when(facturaOrdenProductoDAO.validarFacturaEditable(pagada))
                .thenReturn("La factura está PAGADA; solo se pueden cambiar facturas PENDIENTES.");
        model.seleccionarRegistro(detalle);

        model.btnModificarHandler();

        verify(facturaOrdenProductoDAO, never()).update(any());
        assertEquals("No se puede modificar", ultimoMensaje().getSummary());
    }

    @Test
    void noEliminaSiLaFacturaYaNoSePuedeCambiar() {
        Factura pagada = factura(FacturaDAO.PAGADA);
        FacturaOrdenProducto detalle = detalle(pagada, new OrdenProducto(UUID.randomUUID()));
        when(facturaOrdenProductoDAO.validarFacturaEditable(pagada))
                .thenReturn("La factura está PAGADA; solo se pueden cambiar facturas PENDIENTES.");
        model.seleccionarRegistro(detalle);

        model.btnEliminarHandler();

        verify(facturaOrdenProductoDAO, never()).delete(any());
        assertEquals("No se puede eliminar", ultimoMensaje().getSummary());
    }

    @Test
    void alElegirElProducto_copiaElPrecioDeLaOrden() {
        OrdenProducto cafeDeLaOrden = new OrdenProducto(UUID.randomUUID());
        cafeDeLaOrden.setPrecio(new BigDecimal("4.25"));
        FacturaOrdenProducto detalle = new FacturaOrdenProducto(UUID.randomUUID());
        detalle.setIdOrdenProducto(cafeDeLaOrden);
        model.setRegistro(detalle);

        model.ordenProductoSeleccionadoHandler();

        assertEquals(new BigDecimal("4.25"), detalle.getPrecio());
    }

    @Test
    void alIniciar_lasFacturasPendientesLasTraeElDao() {
        Factura pendiente = factura(FacturaDAO.PENDIENTE);
        when(facturaDAO.buscarPendientes()).thenReturn(List.of(pendiente));

        model.init();

        assertEquals(List.of(pendiente), model.getListaFacturaPendiente());
    }

    @Test
    void elTotal_loCalculaElDao() {
        List<FacturaOrdenProducto> detalles = List.of(new FacturaOrdenProducto(UUID.randomUUID()));
        model.setRegistros(detalles);
        when(facturaOrdenProductoDAO.calcularTotal(detalles)).thenReturn(new BigDecimal("3.50"));

        assertEquals(new BigDecimal("3.50"), model.getTotal());
    }

    private Factura factura(String estado) {
        Factura factura = new Factura(UUID.randomUUID());
        factura.setEstado(estado);
        return factura;
    }

    private FacturaOrdenProducto detalle(Factura factura, OrdenProducto detalleOrden) {
        FacturaOrdenProducto detalle = new FacturaOrdenProducto(UUID.randomUUID());
        detalle.setIdFactura(factura);
        detalle.setIdOrdenProducto(detalleOrden);
        detalle.setPrecio(new BigDecimal("2.50"));
        return detalle;
    }
}