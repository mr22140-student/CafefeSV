
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.PagoDetalle;

/**
 *
 * @author valen
 */
public class PagoDetalleDAOTest extends AbstractDAOTestBase<PagoDetalle, PagoDetalleDAO> {

    @Override
    protected PagoDetalleDAO crearDAO() {
        return new PagoDetalleDAO();
    }

    @Override
    protected PagoDetalle crearEntidad() {
        return new PagoDetalle();
    }

}
