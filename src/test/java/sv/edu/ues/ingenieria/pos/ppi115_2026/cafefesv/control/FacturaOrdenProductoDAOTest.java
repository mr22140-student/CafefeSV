package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.FacturaOrdenProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.OrdenProducto;

/**
 *
 * @author valen
 */
public class FacturaOrdenProductoDAOTest extends AbstractDAOTestBase<FacturaOrdenProducto, FacturaOrdenProductoDAO> {

    @Override
    protected FacturaOrdenProductoDAO crearDAO() {
        return new FacturaOrdenProductoDAO();
    }

    @Override
    protected FacturaOrdenProducto crearEntidad() {
        return new FacturaOrdenProducto();
    }

    // ===================== validarFacturaEditable =====================

    @Test
    void validarFacturaEditable_facturaPagada_devuelveError() {
        assertNotNull(dao.validarFacturaEditable(factura(FacturaDAO.PAGADA)));
    }

    @Test
    void validarFacturaEditable_facturaPendiente_devuelveNull() {
        assertNull(dao.validarFacturaEditable(factura(FacturaDAO.PENDIENTE)));
    }

    // ===================== validarDetalle =====================

    @Test
    void validarDetalle_sinFactura_devuelveError() {
        assertNotNull(dao.validarDetalle(detalle(null, new OrdenProducto(UUID.randomUUID()), "2.50")));
    }

    @Test
    void validarDetalle_facturaQueNoEstaPendiente_devuelveError() {
        assertNotNull(dao.validarDetalle(detalle(factura(FacturaDAO.PAGADA), new OrdenProducto(UUID.randomUUID()), "2.50")));
    }

    @Test
    void validarDetalle_sinProductoDeLaOrden_devuelveError() {
        assertNotNull(dao.validarDetalle(detalle(factura(FacturaDAO.PENDIENTE), null, "2.50")));
    }

    @Test
    void validarDetalle_conPrecioNegativo_devuelveError() {
        assertNotNull(dao.validarDetalle(detalle(factura(FacturaDAO.PENDIENTE), new OrdenProducto(UUID.randomUUID()), "-1")));
    }

    @Test
    void validarDetalle_productoYaFacturadoEnOtraFactura_devuelveError() {
        OrdenProducto cafeDeLaOrden = new OrdenProducto(UUID.randomUUID());
        simularConsulta(FacturaOrdenProducto.class, List.of(detalle(factura(FacturaDAO.PENDIENTE), cafeDeLaOrden, "2.50")));

        assertNotNull(dao.validarDetalle(detalle(factura(FacturaDAO.PENDIENTE), cafeDeLaOrden, "2.50")));
    }

    @Test
    void validarDetalle_alModificarElMismoRegistro_noSeCuentaComoRepetido() {
        FacturaOrdenProducto guardado = detalle(factura(FacturaDAO.PENDIENTE), new OrdenProducto(UUID.randomUUID()), "2.50");
        simularConsulta(FacturaOrdenProducto.class, List.of(guardado));

        assertNull(dao.validarDetalle(guardado));
    }

    @Test
    void validarDetalle_completo_devuelveNull() {
        simularConsulta(FacturaOrdenProducto.class, List.of());

        assertNull(dao.validarDetalle(detalle(factura(FacturaDAO.PENDIENTE), new OrdenProducto(UUID.randomUUID()), "2.50")));
    }

    // ===================== calcularTotal =====================

    @Test
    void calcularTotal_sumaLosPreciosEIgnoraLosVacios() {
        Factura factura = factura(FacturaDAO.PENDIENTE);
        List<FacturaOrdenProducto> detalles = Arrays.asList(
                detalle(factura, new OrdenProducto(UUID.randomUUID()), "2.50"),
                detalle(factura, new OrdenProducto(UUID.randomUUID()), "1.00"),
                new FacturaOrdenProducto(UUID.randomUUID()));

        assertEquals(new BigDecimal("3.50"), dao.calcularTotal(detalles));
    }

    // ===================== ayudantes =====================

    private Factura factura(String estado) {
        Factura factura = new Factura(UUID.randomUUID());
        factura.setEstado(estado);
        return factura;
    }

    private FacturaOrdenProducto detalle(Factura factura, OrdenProducto detalleOrden, String precio) {
        FacturaOrdenProducto detalle = new FacturaOrdenProducto(UUID.randomUUID());
        detalle.setIdFactura(factura);
        detalle.setIdOrdenProducto(detalleOrden);
        detalle.setPrecio(new BigDecimal(precio));
        return detalle;
    }
}