package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;

/**
 *
 * @author valen
 */
public class FacturaDAOTest extends AbstractDAOTestBase<Factura, FacturaDAO> {

    @Override
    protected FacturaDAO crearDAO() {
        return new FacturaDAO();
    }

    @Override
    protected Factura crearEntidad() {
        return new Factura();
    }

}
