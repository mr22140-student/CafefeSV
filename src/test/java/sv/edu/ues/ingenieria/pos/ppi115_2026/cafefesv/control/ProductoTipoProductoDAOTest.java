package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoTipoProducto;

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

}
