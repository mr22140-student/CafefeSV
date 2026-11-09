package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private ProductoTipoProductoDAO productoTipoProductoDAO;
    @Mock
    private ProductoDAO productoDAO;
    @Mock
    private TipoProductoDAO tipoProductoDAO;

    @Test
    void crearNuevo_traeFechaDeCreacion() {
        model.btnNuevoHandler();

        assertEquals(AbstractModel.ESTADO_CREAR, model.getEstado());
        assertNotNull(model.getRegistro().getFechaCreacion());
    }

    @Test
    void noGuardaSiElDaoRechazaElTipo() {
        ProductoTipoProducto asignacion = asignacion();
        when(productoTipoProductoDAO.validarTipo(asignacion)).thenReturn("El tipo está inactivo.");
        model.setRegistro(asignacion);

        model.btnCrearHandler();

        verify(productoTipoProductoDAO, never()).save(any());
        assertEquals("Tipo no válido", ultimoMensaje().getSummary());
    }

    @Test
    void noGuardaSiElDaoEncuentraElTipoRepetido() {
        ProductoTipoProducto asignacion = asignacion();
        when(productoTipoProductoDAO.validarDuplicado(asignacion)).thenReturn("El producto ya tiene asignado ese tipo.");
        model.setRegistro(asignacion);

        model.btnCrearHandler();

        verify(productoTipoProductoDAO, never()).save(any());
        assertEquals("Registro duplicado", ultimoMensaje().getSummary());
    }

    @Test
    void guardaSiElDaoNoEncuentraErrores() {
        ProductoTipoProducto asignacion = asignacion();
        model.setRegistro(asignacion);

        model.btnCrearHandler();

        verify(productoTipoProductoDAO).save(asignacion);
    }

    private ProductoTipoProducto asignacion() {
        ProductoTipoProducto asignacion = new ProductoTipoProducto(UUID.randomUUID());
        asignacion.setProducto(new Producto(UUID.randomUUID()));
        asignacion.setIdTipoProducto(new TipoProducto(UUID.randomUUID()));
        return asignacion;
    }
}