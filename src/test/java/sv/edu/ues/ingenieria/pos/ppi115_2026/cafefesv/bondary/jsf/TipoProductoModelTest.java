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
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoTipoProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoTipoProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoProducto;

/**
 *
 * @author valen
 */
public class TipoProductoModelTest extends ModelTestBase {

    @InjectMocks
    private TipoProductoModel model;

    @Mock
    private TipoProductoDAO tipoProductoDAO;

    @Mock
    private ProductoTipoProductoDAO ptpDAO;

    @Test
    void noInactivaSiUnProductoActivoLoUsa() {
        TipoProducto tipo = tipo(false);
        when(ptpDAO.findByTipoProducto(tipo)).thenReturn(List.of(asignacion(tipo, true)));
        model.setRegistro(tipo);

        model.btnModificarHandler();

        verify(tipoProductoDAO, never()).update(any());
        assertEquals("No se puede inactivar", ultimoMensaje().getSummary());
    }

    @Test
    void inactivaSiSusProductosEstanInactivos() {
        TipoProducto tipo = tipo(false);
        when(ptpDAO.findByTipoProducto(tipo)).thenReturn(List.of(asignacion(tipo, false)));
        model.setRegistro(tipo);

        model.btnModificarHandler();

        verify(tipoProductoDAO).update(tipo);
    }

    @Test
    void noEliminaSiEstaAsignadoAUnProducto() {
        TipoProducto tipo = tipo(true);
        when(ptpDAO.findByTipoProducto(tipo)).thenReturn(List.of(asignacion(tipo, true)));
        model.setRegistro(tipo);

        model.btnEliminarHandler();

        verify(tipoProductoDAO, never()).delete(any());
        assertEquals("No se puede eliminar", ultimoMensaje().getSummary());
    }

    private TipoProducto tipo(boolean activo) {
        TipoProducto t = new TipoProducto(UUID.randomUUID());
        t.setNombre("Bebidas");
        t.setActivo(activo);
        return t;
    }

    private ProductoTipoProducto asignacion(TipoProducto tipo, boolean productoActivo) {
        Producto p = new Producto(UUID.randomUUID());
        p.setActivo(productoActivo);
        ProductoTipoProducto ptp = new ProductoTipoProducto(UUID.randomUUID());
        ptp.setProducto(p);
        ptp.setIdTipoProducto(tipo);
        return ptp;
    }

}
