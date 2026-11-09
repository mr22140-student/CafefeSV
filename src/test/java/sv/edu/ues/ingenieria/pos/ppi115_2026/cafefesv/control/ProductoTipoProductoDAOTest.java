package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoTipoProducto;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoProducto;

/**
 *
 * @author valen
 */
public class ProductoTipoProductoDAOTest extends AbstractDAOTestBase<ProductoTipoProducto, ProductoTipoProductoDAO> {

    @Override
    protected ProductoTipoProductoDAO crearDAO() {
        return new ProductoTipoProductoDAO();
    }

    @Override
    protected ProductoTipoProducto crearEntidad() {
        return new ProductoTipoProducto();
    }
    // ===================== validarTipo =====================

    @Test
    void validarTipo_sinTipo_devuelveError() {
        assertNotNull(dao.validarTipo(asignacion(producto(), null)));
    }

    @Test
    void validarTipo_conTipoInactivo_devuelveError() {
        TipoProducto postres = tipoProducto("Postres", false);
        when(em.find(TipoProducto.class, postres.getIdTipoProducto())).thenReturn(postres);

        assertNotNull(dao.validarTipo(asignacion(producto(), postres)));
    }

    @Test
    void validarTipo_conTipoActivo_devuelveNull() {
        TipoProducto bebidas = tipoProducto("Bebidas", true);
        when(em.find(TipoProducto.class, bebidas.getIdTipoProducto())).thenReturn(bebidas);

        assertNull(dao.validarTipo(asignacion(producto(), bebidas)));
    }

    // ===================== validarDuplicado =====================
    @Test
    void validarDuplicado_tipoYaAsignado_devuelveError() {
        Producto producto = producto();
        TipoProducto bebidas = tipoProducto("Bebidas", true);
        simularConsulta(ProductoTipoProducto.class, List.of(asignacion(producto, bebidas)));

        assertNotNull(dao.validarDuplicado(asignacion(producto, bebidas)));
    }

    @Test
    void validarDuplicado_tipoNuevo_devuelveNull() {
        Producto producto = producto();
        simularConsulta(ProductoTipoProducto.class, List.of(asignacion(producto, tipoProducto("Calientes", true))));

        assertNull(dao.validarDuplicado(asignacion(producto, tipoProducto("Bebidas", true))));
    }

    @Test
    void validarDuplicado_alModificarLaMismaAsignacion_noSeCuentaComoRepetida() {
        Producto producto = producto();
        ProductoTipoProducto asignacionGuardada = asignacion(producto, tipoProducto("Bebidas", true));
        simularConsulta(ProductoTipoProducto.class, List.of(asignacionGuardada));

        assertNull(dao.validarDuplicado(asignacionGuardada));
    }

    // ===================== ayudantes =====================
    private Producto producto() {
        Producto producto = new Producto(UUID.randomUUID());
        producto.setNombre("Café latte");
        return producto;
    }

    private TipoProducto tipoProducto(String nombre, boolean activo) {
        TipoProducto tipoProducto = new TipoProducto(UUID.randomUUID());
        tipoProducto.setNombre(nombre);
        tipoProducto.setActivo(activo);
        return tipoProducto;
    }

    private ProductoTipoProducto asignacion(Producto producto, TipoProducto tipoProducto) {
        ProductoTipoProducto asignacion = new ProductoTipoProducto(UUID.randomUUID());
        asignacion.setProducto(producto);
        asignacion.setIdTipoProducto(tipoProducto);
        return asignacion;
    }

}
