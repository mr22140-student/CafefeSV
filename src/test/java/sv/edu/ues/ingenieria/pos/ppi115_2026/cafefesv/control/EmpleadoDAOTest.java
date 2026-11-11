package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verifyNoInteractions;
import jakarta.persistence.Query;
import org.mockito.InOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Empleado;

/**
 *
 * @author valen
 */
class EmpleadoDAOTest extends AbstractDAOTestBase<Empleado, EmpleadoDAO> {

    @Override
    protected EmpleadoDAO crearDAO() {
        return new EmpleadoDAO();
    }

    @Override
    protected Empleado crearEntidad() {
        return new Empleado();
    }

    @Test
    void obtenerPorId_conNull_devuelveNullSinConsultar() {
        verifyNoInteractions(em);
    }
    // ===================== validarEliminar =====================

    @Test
    void validarEliminar_conOrdenesOFacturas_devuelveError() {
        simularConteo(1);
        assertNotNull(dao.validarEliminar(new Empleado(UUID.randomUUID())));
    }

    @Test
    void validarEliminar_sinHistorial_devuelveNull() {
        simularConteo(0);
        assertNull(dao.validarEliminar(new Empleado(UUID.randomUUID())));
    }
    // ===================== eliminarConRoles =====================

    @Test
    void eliminarConRoles_borraLasAsignacionesYLuegoElEmpleado() {
        Query borrado = mock(Query.class);
        when(em.createQuery(anyString())).thenReturn(borrado);
        when(borrado.setParameter(anyString(), any())).thenReturn(borrado);
        Empleado empleado = new Empleado(UUID.randomUUID());
        when(em.contains(empleado)).thenReturn(true);

        dao.eliminarConRoles(empleado);

        InOrder orden = inOrder(borrado, em);
        orden.verify(borrado).executeUpdate();   // primero sus roles
        orden.verify(em).remove(empleado);       // después el empleado
    }

    @Test
    void eliminarConRoles_conNull_lanzaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> dao.eliminarConRoles(null));
        verifyNoInteractions(em);
    }
}
