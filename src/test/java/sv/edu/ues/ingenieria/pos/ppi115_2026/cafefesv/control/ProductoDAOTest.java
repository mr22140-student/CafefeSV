package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 *
 * @author valen
 */
public class ProductoDAOTest extends AbstractDAOTestBase<Producto, ProductoDAO> {

    @Override
    protected ProductoDAO crearDAO() {
        return new ProductoDAO();
    }

    @Override
    protected Producto crearEntidad() {
        return new Producto();
    }
    // ===================== validarPrecio =====================

    @Test
    void validarPrecio_cero_devuelveError() {
        assertNotNull(dao.validarPrecio(producto(BigDecimal.ZERO)));
    }

    @Test
    void validarPrecio_negativo_devuelveError() {
        assertNotNull(dao.validarPrecio(producto(new BigDecimal("-1.00"))));
    }

    @Test
    void validarPrecio_mayorQueCero_devuelveNull() {
        assertNull(dao.validarPrecio(producto(new BigDecimal("2.50"))));
    }
// ===================== validarEliminar =====================

    @Test
    void validarEliminar_conOrdenesOAsignaciones_devuelveError() {
        simularConteo(2);

        assertNotNull(dao.validarEliminar(producto(new BigDecimal("2.50"))));
    }

    @Test
    void validarEliminar_sinOrdenesNiAsignaciones_devuelveNull() {
        simularConteo(0);

        assertNull(dao.validarEliminar(producto(new BigDecimal("2.50"))));
    }

    @Test
    void validarEliminar_conNull_devuelveNull() {
        assertNull(dao.validarEliminar(null));
    }
    // ===================== ayudantes =====================
    private Producto producto(BigDecimal precioSugerido) {
        Producto producto = new Producto(UUID.randomUUID());
        producto.setNombre("Café latte");
        producto.setActivo(true);
        producto.setPrecioSugerido(precioSugerido);
        return producto;
    }

    
}
