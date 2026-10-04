package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.DescuentoProducto;

/**
 *
 * @author valen
 */
public class DescuentoProductoDAOTest  extends AbstractDAOTestBase<DescuentoProducto, DescuentoProductoDAO> {

    @Override
    protected DescuentoProductoDAO crearDAO() {
          return new DescuentoProductoDAO();
    }

    @Override
    protected DescuentoProducto crearEntidad() {
        return new DescuentoProducto();
    }
    
}
