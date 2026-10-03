package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;

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

}
