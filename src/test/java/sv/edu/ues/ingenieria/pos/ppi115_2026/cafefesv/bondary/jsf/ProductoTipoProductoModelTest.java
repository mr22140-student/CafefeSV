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
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoTipoProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoTipoProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoProducto;

/**
 *
 * @author valen
 */
public class ProductoTipoProductoModelTest extends ModelTestBase {

    @InjectMocks
    private ProductoTipoProductoModel model;

    @Mock
    private ProductoTipoProductoDAO ptpDAO;
    @Mock
    private ProductoDAO productoDAO;
    @Mock
    private TipoProductoDAO tipoProductoDAO;

    @Test
    void crearNuevo_traeFechaDeCreacion() {
        model.btnNuevoHandler();

        assertEquals(AbstractModel.ESTADO_CREAR, model.getEstado());
        org.junit.jupiter.api.Assertions.assertNotNull(model.getRegistro().getFechaCreacion());
    }

    @Test
    void noGuardaElMismoTipoDosVecesEnUnProducto() {
        Producto producto = new Producto(UUID.randomUUID());
        TipoProducto tipo = new TipoProducto(UUID.randomUUID());
        when(ptpDAO.findByProducto(producto)).thenReturn(List.of(asignacion(producto, tipo)));
        model.setRegistro(asignacion(producto, tipo));

        model.btnCrearHandler();

        verify(ptpDAO, never()).save(any());
        assertEquals("Registro duplicado", ultimoMensaje().getSummary());
    }

    @Test
    void guardaSiElTipoNoEstaRepetido() {
        Producto producto = new Producto(UUID.randomUUID());
        ProductoTipoProducto nuevo = asignacion(producto, new TipoProducto(UUID.randomUUID()));
        model.setRegistro(nuevo);

        model.btnCrearHandler();

        verify(ptpDAO).save(nuevo);
    }

    private ProductoTipoProducto asignacion(Producto p, TipoProducto t) {
        ProductoTipoProducto ptp = new ProductoTipoProducto(UUID.randomUUID());
        ptp.setProducto(p);
        ptp.setIdTipoProducto(t);
        return ptp;
    }

}
