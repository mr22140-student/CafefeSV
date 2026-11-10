package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caja;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
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

    // ===================== validarFactura =====================

    @Test
    void validarFactura_sinCaja_devuelveError() {
        Factura factura = facturaCompleta();
        factura.setIdCaja(null);

        assertNotNull(dao.validarFactura(factura));
    }

    @Test
    void validarFactura_sinCajero_devuelveError() {
        Factura factura = facturaCompleta();
        factura.setIdEmpleadoRol(null);

        assertNotNull(dao.validarFactura(factura));
    }

    @Test
    void validarFactura_conClienteDeMasDe155Caracteres_devuelveError() {
        Factura factura = facturaCompleta();
        factura.setCliente("a".repeat(156));

        assertNotNull(dao.validarFactura(factura));
    }

    @Test
    void validarFactura_conEstadoQueNoExiste_devuelveError() {
        Factura factura = facturaCompleta();
        factura.setEstado("PERDIDA");

        assertNotNull(dao.validarFactura(factura));
    }

    @Test
    void validarFactura_completa_devuelveNull() {
        assertNull(dao.validarFactura(facturaCompleta()));
    }

    // ===================== validarModificable =====================

    @Test
    void validarModificable_facturaAnulada_devuelveError() {
        assertNotNull(dao.validarModificable(FacturaDAO.ANULADA));
    }

    @Test
    void validarModificable_facturaPendiente_devuelveNull() {
        assertNull(dao.validarModificable(FacturaDAO.PENDIENTE));
    }

    // ===================== validarEliminar =====================

    @Test
    void validarEliminar_conProductosOPagos_devuelveError() {
        simularConteo(1);
        assertNotNull(dao.validarEliminar(facturaCompleta()));
    }

    @Test
    void validarEliminar_sinProductosNiPagos_devuelveNull() {
        simularConteo(0);
        assertNull(dao.validarEliminar(facturaCompleta()));
    }

    // ===================== buscarPendientes =====================

    @Test
    void buscarPendientes_ordenaLasMasRecientesPrimero() {
        Factura deAyer = facturaCompleta();
        deAyer.setFechaFacturacion(enDias(-1));
        Factura deHoy = facturaCompleta();
        simularConsulta(Factura.class, List.of(deAyer, deHoy));

        assertEquals(List.of(deHoy, deAyer), dao.buscarPendientes());
    }

    // ===================== ayudantes =====================

    private Factura facturaCompleta() {
        Factura factura = new Factura(UUID.randomUUID());
        factura.setIdCaja(new Caja(UUID.randomUUID()));
        factura.setIdEmpleadoRol(new EmpleadoRol(UUID.randomUUID()));
        factura.setCliente("Consumidor final");
        factura.setEstado(FacturaDAO.PENDIENTE);
        factura.setFechaFacturacion(new Date());
        return factura;
    }
}