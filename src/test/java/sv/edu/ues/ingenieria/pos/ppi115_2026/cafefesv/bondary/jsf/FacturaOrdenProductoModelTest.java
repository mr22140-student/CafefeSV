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
    void noAgregaProductosAUnaFacturaQueNoEstaPendiente() {
        model.setRegistro(registro(factura(FacturaModel.PAGADA), new OrdenProducto(UUID.randomUUID())));

        model.btnCrearHandler();

        verify(facturaOrdenProductoDAO, never()).save(any());
        assertEquals("La factura está PAGADA; solo se pueden cambiar facturas PENDIENTES.",
                ultimoMensaje().getSummary());
    }

    @Test
    void noFacturaDosVecesElMismoProductoDeLaOrden() {
        OrdenProducto op = new OrdenProducto(UUID.randomUUID());
        when(facturaOrdenProductoDAO.findByOrdenProducto(op))
                .thenReturn(List.of(registro(factura(FacturaModel.PENDIENTE), op)));
        model.setRegistro(registro(factura(FacturaModel.PENDIENTE), op));

        model.btnCrearHandler();

        verify(facturaOrdenProductoDAO, never()).save(any());
        assertEquals("Ese producto de la orden ya está facturado.", ultimoMensaje().getSummary());
    }

    @Test
    void guardaUnProductoValido() {
        FacturaOrdenProducto fop = registro(factura(FacturaModel.PENDIENTE), new OrdenProducto(UUID.randomUUID()));
        model.setRegistro(fop);

        model.btnCrearHandler();

        verify(facturaOrdenProductoDAO).save(fop);
    }

    @Test
    void alElegirElProducto_copiaElPrecioDeLaOrden() {
        OrdenProducto op = new OrdenProducto(UUID.randomUUID());
        op.setPrecio(new BigDecimal("4.25"));
        FacturaOrdenProducto fop = new FacturaOrdenProducto(UUID.randomUUID());
        fop.setIdOrdenProducto(op);
        model.setRegistro(fop);

        model.ordenProductoSeleccionadoHandler();

        assertEquals(new BigDecimal("4.25"), fop.getPrecio());
    }

    private Factura factura(String estado) {
        Factura f = new Factura(UUID.randomUUID());
        f.setEstado(estado);
        return f;
    }

    private FacturaOrdenProducto registro(Factura f, OrdenProducto op) {
        FacturaOrdenProducto fop = new FacturaOrdenProducto(UUID.randomUUID());
        fop.setIdFactura(f);
        fop.setIdOrdenProducto(op);
        fop.setPrecio(new BigDecimal("2.50"));
        return fop;
    }

}
