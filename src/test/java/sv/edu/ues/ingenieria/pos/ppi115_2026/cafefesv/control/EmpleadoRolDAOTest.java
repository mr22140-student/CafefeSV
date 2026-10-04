package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.persistence.TypedQuery;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Rol;

/**
 *
 * @author valen
 */
class EmpleadoRolDAOTest extends AbstractDAOTestBase<EmpleadoRol, EmpleadoRolDAO> {

    @Override
    protected EmpleadoRolDAO crearDAO() {
        return new EmpleadoRolDAO();
    }

    @Override
    protected EmpleadoRol crearEntidad() {
        return new EmpleadoRol();
    }

    @Test
    void existeAsignacion_sinCoincidencias_devuelveFalse() {
        @SuppressWarnings("unchecked")
        TypedQuery<Long> consulta = mock(TypedQuery.class, RETURNS_SELF);
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(consulta);
        when(consulta.getSingleResult()).thenReturn(0L);

        assertFalse(dao.existeAsignacion(UUID.randomUUID(), new Rol(), null));
    }
}
