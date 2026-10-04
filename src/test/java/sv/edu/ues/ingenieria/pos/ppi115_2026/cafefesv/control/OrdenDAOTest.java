package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Orden;

/**
 *
 * @author valen
 */
public class OrdenDAOTest extends AbstractDAOTestBase<Orden, OrdenDAO> {

    @Override
    protected OrdenDAO crearDAO() {
        return new OrdenDAO();
    }

    @Override
    protected Orden crearEntidad() {
        return new Orden();
    }

}
