package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Pruebas comunes para todos los DAOs. Cada test concreto solo indica qué DAO y
 * qué entidad usar; el EntityManager es falso (Mockito), así que no se necesita
 * servidor ni base de datos.
 *
 * @param <T> entidad que administra el DAO
 * @param <D> D
 *
 * @author valen
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class AbstractDAOTestBase<T, D extends AbstractDAO<T, UUID>> {

    @Mock
    protected EntityManager em;

    protected D dao;
    protected T entidad;

    protected abstract D crearDAO();

    protected abstract T crearEntidad();

    @BeforeEach
    void prepararDAO() throws Exception {
        dao = crearDAO();
        entidad = crearEntidad();
        inyectarEntityManager(dao, em);
    }

    // ===================== save =====================
    @Test
    void save_persisteLaEntidad() {
        dao.save(entidad);
        verify(em).persist(entidad);
    }

    @Test
    void save_conNull_lanzaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> dao.save(null));
        verifyNoInteractions(em);
    }

    @Test
    void save_siJpaFalla_lanzaIllegalStateException() {
        doThrow(new PersistenceException("falla simulada")).when(em).persist(entidad);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> dao.save(entidad));
        assertTrue(ex.getCause() instanceof PersistenceException);
    }

    // ===================== update =====================
    @Test
    void update_devuelveLaEntidadAdministrada() {
        when(em.merge(entidad)).thenReturn(entidad);
        assertSame(entidad, dao.update(entidad));
    }

    @Test
    void update_conNull_lanzaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> dao.update(null));
        verifyNoInteractions(em);
    }

    @Test
    void update_siJpaFalla_lanzaIllegalStateException() {
        when(em.merge(entidad)).thenThrow(new PersistenceException("falla simulada"));
        assertThrows(IllegalStateException.class, () -> dao.update(entidad));
    }

    // ===================== delete =====================
    @Test
    void delete_entidadAdministrada_laEliminaDirecto() {
        when(em.contains(entidad)).thenReturn(true);

        dao.delete(entidad);

        verify(em).remove(entidad);
        verify(em, never()).merge(any());
    }

    @Test
    void delete_entidadDesconectada_haceMergeAntesDeEliminar() {
        when(em.contains(entidad)).thenReturn(false);
        when(em.merge(entidad)).thenReturn(entidad);

        dao.delete(entidad);

        verify(em).merge(entidad);
        verify(em).remove(entidad);
    }

    @Test
    void delete_conNull_lanzaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> dao.delete(null));
        verifyNoInteractions(em);
    }

    // ===================== findById / deleteById =====================
    @Test
    void findById_buscaConLaClaseDeLaEntidad() {
        UUID id = UUID.randomUUID();
        doReturn(entidad).when(em).find(entidad.getClass(), id);

        assertSame(entidad, dao.findById(id));
    }

    @Test
    void findById_conNull_lanzaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> dao.findById(null));
        verifyNoInteractions(em);
    }

    @Test
    void deleteById_siExiste_laElimina() {
        UUID id = UUID.randomUUID();
        doReturn(entidad).when(em).find(entidad.getClass(), id);
        when(em.contains(entidad)).thenReturn(true);

        dao.deleteById(id);

        verify(em).remove(entidad);
    }

    @Test
    void deleteById_siNoExiste_noEliminaNada() {
        UUID id = UUID.randomUUID();
        doReturn(null).when(em).find(entidad.getClass(), id);

        dao.deleteById(id);

        verify(em, never()).remove(any());
    }

    @Test
    void deleteById_conNull_lanzaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> dao.deleteById(null));
        verifyNoInteractions(em);
    }

    // ===================== findRange / findAll / count =====================
    @Test
    void findRange_conRangoInvalido_lanzaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> dao.findRange(-1, 10));
        assertThrows(IllegalArgumentException.class, () -> dao.findRange(5, 2));
        verifyNoInteractions(em);
    }

    @Test
    void findRange_siJpaFalla_lanzaIllegalStateException() {
        when(em.getCriteriaBuilder()).thenThrow(new PersistenceException("falla simulada"));
        assertThrows(IllegalStateException.class, () -> dao.findRange(0, 10));
    }

    @Test
    void findAll_siJpaFalla_lanzaIllegalStateException() {
        when(em.getCriteriaBuilder()).thenThrow(new PersistenceException("falla simulada"));
        assertThrows(IllegalStateException.class, () -> dao.findAll());
    }

    @Test
    void count_siJpaFalla_lanzaIllegalStateException() {
        when(em.getCriteriaBuilder()).thenThrow(new PersistenceException("falla simulada"));
        assertThrows(IllegalStateException.class, () -> dao.count());
    }

    // ===================== consultas propias de cada DAO =====================
    // Recorre todos los métodos públicos propios del DAO (findByNombre,
    // countByOrden, etc.) y verifica que usan JPA y devuelven el resultado de la consulta.
    @Test
    void consultasPropias_usanJpaYDevuelvenSuResultado() throws Exception {
        @SuppressWarnings("unchecked")
        TypedQuery<Object> consulta = mock(TypedQuery.class, RETURNS_SELF);
        doReturn(consulta).when(em).createQuery(anyString(), any(Class.class));
        doReturn(consulta).when(em).createNamedQuery(anyString(), any(Class.class));
        List<Object> lista = List.of(entidad);

        for (Method metodo : consultasPropias()) {
            Class<?> retorno = metodo.getReturnType();
            Object esperado;

            if (List.class.isAssignableFrom(retorno)) {
                esperado = lista;
                when(consulta.getResultList()).thenReturn(lista);
            } else if (retorno == long.class || retorno == Long.class) {
                esperado = 3L;
                when(consulta.getSingleResult()).thenReturn(3L);
            } else if (retorno == BigDecimal.class) {
                esperado = BigDecimal.TEN;
                when(consulta.getSingleResult()).thenReturn(BigDecimal.TEN);
            } else if (retorno == boolean.class || retorno == Boolean.class) {
                esperado = true;
                when(consulta.getSingleResult()).thenReturn(1L);
            } else {
                esperado = crearInstancia(retorno);
                doReturn(esperado).when(em).find(any(Class.class), any(Object.class));
            }

            Object resultado;
            try {
                resultado = metodo.invoke(dao, argumentosDePrueba(metodo));
            } catch (InvocationTargetException e) {
                fail(metodo.getName() + " lanzó una excepción: " + e.getCause(), e.getCause());
                return;
            }
            assertEquals(esperado, resultado, metodo.getName() + " no devolvió el resultado de la consulta");
        }
    }

    // ===================== ayudantes =====================
    // campo privado del DAO.
    private static void inyectarEntityManager(Object dao, EntityManager em) throws IllegalAccessException {
        for (Field campo : dao.getClass().getDeclaredFields()) {
            if (EntityManager.class.isAssignableFrom(campo.getType())) {
                campo.setAccessible(true);
                campo.set(dao, em);
                return;
            }
        }
        fail("El DAO " + dao.getClass().getSimpleName() + " no tiene un campo EntityManager");
    }

    private List<Method> consultasPropias() {
        List<Method> metodos = new ArrayList<>();
        for (Method m : dao.getClass().getDeclaredMethods()) {
            int mod = m.getModifiers();
            if (Modifier.isPublic(mod) && !Modifier.isStatic(mod) && !m.isSynthetic()) {
                metodos.add(m);
            }
        }
        return metodos;
    }

    private Object[] argumentosDePrueba(Method metodo) throws Exception {
        Class<?>[] tipos = metodo.getParameterTypes();
        Object[] valores = new Object[tipos.length];
        for (int i = 0; i < tipos.length; i++) {
            valores[i] = valorDePrueba(tipos[i]);
        }
        return valores;
    }

    private Object valorDePrueba(Class<?> tipo) throws Exception {
        if (tipo == String.class) {
            return "prueba";
        }
        if (tipo == Boolean.class || tipo == boolean.class) {
            return Boolean.TRUE;
        }
        if (tipo == Integer.class || tipo == int.class) {
            return 1;
        }
        if (tipo == Long.class || tipo == long.class) {
            return 1L;
        }
        if (tipo == BigDecimal.class) {
            return BigDecimal.ONE;
        }
        if (tipo == Date.class) {
            return new Date();
        }
        if (tipo == UUID.class) {
            return UUID.randomUUID();
        }
        return crearInstancia(tipo);   // entidades: Producto, Factura, Rol...
    }

    private Object crearInstancia(Class<?> tipo) throws Exception {
        return tipo.getDeclaredConstructor().newInstance();
    }
}
