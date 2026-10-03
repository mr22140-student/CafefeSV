package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoCaracteristica;

/**
 *
 * @author valen
 */
public class ProductoCaracteristicaDAOTest extends AbstractDAOTestBase<ProductoCaracteristica, ProductoCaracteristicaDAO> {

    @Override
    protected ProductoCaracteristicaDAO crearDAO() {
        return new ProductoCaracteristicaDAO();
    }

    @Override
    protected ProductoCaracteristica crearEntidad() {
        return new ProductoCaracteristica();
    }

}
