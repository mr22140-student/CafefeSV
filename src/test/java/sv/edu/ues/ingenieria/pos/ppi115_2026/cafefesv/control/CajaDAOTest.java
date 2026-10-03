package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caja;

/**
 *
 * @author valen
 */
class CajaDAOTest extends AbstractDAOTestBase<Caja, CajaDAO> {

    @Override
    protected CajaDAO crearDAO() {
        return new CajaDAO();
    }

    @Override
    protected Caja crearEntidad() {
        return new Caja();
    }
}
