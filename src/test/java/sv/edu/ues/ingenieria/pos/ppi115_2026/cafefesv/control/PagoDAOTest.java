package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Pago;

/**
 *
 * @author valen
 */
class PagoDAOTest extends AbstractDAOTestBase<Pago, PagoDAO> {

    @Override
    protected PagoDAO crearDAO() {
        return new PagoDAO();
    }

    @Override
    protected Pago crearEntidad() {
        return new Pago();
    }

    @Test
    void sumaMontos_sinDetalles_devuelveCero() {
        consultaDecimalQueDevuelve(null);
        assertEquals(BigDecimal.ZERO, dao.sumaMontos(new Pago()));
    }

    @Test
    void totalFactura_sinProductos_devuelveCero() {
        consultaDecimalQueDevuelve(null);
        assertEquals(BigDecimal.ZERO, dao.totalFactura(new Factura()));
    }

    @Test
    void existePagoActivo_sinPagos_devuelveFalse() {
        @SuppressWarnings("unchecked")
        TypedQuery<Long> consulta = mock(TypedQuery.class, RETURNS_SELF);
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(consulta);
        when(consulta.getSingleResult()).thenReturn(0L);

        assertFalse(dao.existePagoActivo(new Factura(), null));
    }

    private void consultaDecimalQueDevuelve(BigDecimal valor) {
        @SuppressWarnings("unchecked")
        TypedQuery<BigDecimal> consulta = mock(TypedQuery.class, RETURNS_SELF);
        when(em.createQuery(anyString(), eq(BigDecimal.class))).thenReturn(consulta);
        when(consulta.getSingleResult()).thenReturn(valor);
    }
}
