package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.OrdenProducto;

/**
 *
 * @author valen
 */
public class OrdenProductoDAOTest extends AbstractDAOTestBase<OrdenProducto, OrdenProductoDAO> {

    @Override
    protected OrdenProductoDAO crearDAO() {
        return new OrdenProductoDAO();
    }

    @Override
    protected OrdenProducto crearEntidad() {
        return new OrdenProducto();
    }

}
