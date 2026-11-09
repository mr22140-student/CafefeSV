package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Orden;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;

import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.OrdenProducto;

/**
 *
 * @author valen
 */
public class OrdenProductoDAOTest extends AbstractDAOTestBase<OrdenProducto, OrdenProductoDAO> {

    @Override
    protected OrdenProductoDAO crearDAO() {
        return new OrdenProductoDAO();
    }

    @Override
    protected OrdenProducto crearEntidad() {
        return new OrdenProducto();
    }
    // ===================== validarDetalle =====================

    @Test
    void validarDetalle_sinOrden_devuelveError() {
        assertNotNull(dao.validarDetalle(detalle(null, producto(true), "2.50")));
    }

    @Test
    void validarDetalle_sinProducto_devuelveError() {
        assertNotNull(dao.validarDetalle(detalle(new Orden(UUID.randomUUID()), null, "2.50")));
    }

    @Test
    void validarDetalle_conPrecioNegativo_devuelveError() {
        assertNotNull(dao.validarDetalle(detalle(new Orden(UUID.randomUUID()), producto(true), "-1")));
    }

    @Test
    void validarDetalle_conPrecioCero_devuelveNull() {
        assertNull(dao.validarDetalle(detalle(new Orden(UUID.randomUUID()), producto(true), "0")));
    }

    // ===================== validarProductoElegido =====================
    @Test
    void validarProductoElegido_sinProducto_devuelveError() {
        assertNotNull(dao.validarProductoElegido(null));
    }

    @Test
    void validarProductoElegido_inactivo_devuelveError() {
        Producto te = producto(false);
        when(em.find(Producto.class, te.getIdProducto())).thenReturn(te);

        assertNotNull(dao.validarProductoElegido(te));
    }

    @Test
    void validarProductoElegido_activo_devuelveNull() {
        Producto cafe = producto(true);
        when(em.find(Producto.class, cafe.getIdProducto())).thenReturn(cafe);

        assertNull(dao.validarProductoElegido(cafe));
    }

    // ===================== validarNoFacturado =====================
    @Test
    void validarNoFacturado_yaFacturado_devuelveError() {
        simularConteo(1);
        assertNotNull(dao.validarNoFacturado(detalle(new Orden(UUID.randomUUID()), producto(true), "2.50")));
    }

    @Test
    void validarNoFacturado_sinFactura_devuelveNull() {
        simularConteo(0);
        assertNull(dao.validarNoFacturado(detalle(new Orden(UUID.randomUUID()), producto(true), "2.50")));
    }

    // ===================== validarSinDescuento =====================
    @Test
    void validarSinDescuento_conDescuentoAnterior_devuelveError() {
        OrdenProducto detalle = detalle(new Orden(UUID.randomUUID()), producto(true), "8.00");
        detalle.setObservaciones("Descuento Anterior (20%): -$2.00 (precio original $10.00)");

        assertNotNull(dao.validarSinDescuento(detalle));
    }

    @Test
    void validarSinDescuento_sinObservaciones_devuelveNull() {
        assertNull(dao.validarSinDescuento(detalle(new Orden(UUID.randomUUID()), producto(true), "10.00")));
    }

    // ===================== calcularMontoDescuento =====================
    @Test
    void calcularMontoDescuento_veintePorCientoDeDiez_esDos() {
        assertEquals(new BigDecimal("2.00"), dao.calcularMontoDescuento(new BigDecimal("10.00"), 20));
    }

    @Test
    void calcularMontoDescuento_redondeaADosDecimales() {
        assertEquals(new BigDecimal("0.50"), dao.calcularMontoDescuento(new BigDecimal("3.33"), 15));
    }

    // ===================== calcularTotal =====================
    @Test
    void calcularTotal_sumaLosPreciosEIgnoraLosVacios() {
        Orden orden = new Orden(UUID.randomUUID());
        List<OrdenProducto> detalles = Arrays.asList(
                detalle(orden, producto(true), "2.50"),
                detalle(orden, producto(true), "1.00"),
                new OrdenProducto(UUID.randomUUID()));

        assertEquals(new BigDecimal("3.50"), dao.calcularTotal(detalles));
    }

    @Test
    void calcularTotal_sinDetalles_esCero() {
        assertEquals(BigDecimal.ZERO, dao.calcularTotal(null));
    }

    // ===================== ayudantes =====================
    private Producto producto(boolean activo) {
        Producto cafe = new Producto(UUID.randomUUID());
        cafe.setNombre("Café");
        cafe.setActivo(activo);
        cafe.setPrecioSugerido(new BigDecimal("2.50"));
        return cafe;
    }

    private OrdenProducto detalle(Orden orden, Producto producto, String precio) {
        OrdenProducto detalle = new OrdenProducto(UUID.randomUUID());
        detalle.setIdOrden(orden);
        detalle.setIdProducto(producto);
        detalle.setPrecio(new BigDecimal(precio));
        return detalle;
    }

}
