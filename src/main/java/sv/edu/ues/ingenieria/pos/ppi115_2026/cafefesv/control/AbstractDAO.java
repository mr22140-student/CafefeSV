/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniel
 */
/**
 * Implementación base del patrón DAO usando JPA {@link EntityManager}.
 * <p>
 * Cada DAO concreto (un {@code @Stateless} EJB) solo necesita:
 * <ol>
 * <li>extender esta clase pasando en el constructor la clase de la entidad que
 * administra;</li>
 * <li>inyectar su propio {@link EntityManager} con
 * {@code @PersistenceContext(unitName = "CafefePU")};</li>
 * <li>implementar {@link #getEntityManager()} devolviendo ese campo.</li>
 * </ol>
 * <p>
 * Cada operación valida sus parámetros antes de tocar la base de datos
 * (lanzando {@link IllegalArgumentException} si algo requerido viene nulo o mal
 * formado) y envuelve cualquier falla de JPA en un
 * {@link IllegalStateException} con un mensaje claro, dejando la excepción
 * original como causa y registrándola en el {@link Logger} — así cualquier capa
 * de arriba (por ejemplo un Model de JSF) puede mostrar un mensaje genérico al
 * usuario sin perder el detalle real del error en el log del servidor.
 *
 * @param <Entidad> tipo de la entidad administrada
 * @param <ID> tipo de la clave primaria de la entidad
 */
public abstract class AbstractDAO<Entidad, ID> implements GenericDAO<Entidad, ID>, Serializable {

    private static final long serialVersionUID = 1L;
    private final Class<Entidad> entityClass;
    private final Logger logger = Logger.getLogger(getClass().getName());

    protected AbstractDAO(Class<Entidad> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Debe devolver el {@link EntityManager} inyectado por el contenedor en la
     * subclase concreta.
     */
    protected abstract EntityManager getEntityManager();

    @Override
    public void save(Entidad entity) {
        if (entity == null) {
            throw new IllegalArgumentException("La entidad a guardar no puede ser nula");
        }
        try {
            getEntityManager().persist(entity);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al crear el registro", e);
            throw new IllegalStateException("Error al crear el registro", e);
        }
    }

    @Override
    public Entidad update(Entidad entity) {
        if (entity == null) {
            throw new IllegalArgumentException("La entidad a actualizar no puede ser nula");
        }
        try {
            return getEntityManager().merge(entity);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al modificar el registro", e);
            throw new IllegalStateException("Error al modificar el registro", e);
        }
    }

    @Override
    public void delete(Entidad entity) {
        if (entity == null) {
            throw new IllegalArgumentException("La entidad a eliminar no puede ser nula");
        }
        try {
            EntityManager em = getEntityManager();
            em.remove(em.contains(entity) ? entity : em.merge(entity));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al eliminar el registro", e);
            throw new IllegalStateException("Error al eliminar el registro", e);
        }
    }

    @Override
    public void deleteById(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }
        Entidad entity = findById(id);
        if (entity != null) {
            delete(entity);
        }
    }

    @Override
    public Entidad findById(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }
        try {
            return getEntityManager().find(entityClass, id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al buscar el registro por id", e);
            throw new IllegalStateException("Error al buscar el registro por id", e);
        }
    }

    @Override
    public List<Entidad> findAll() {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Entidad> cq = cb.createQuery(entityClass);
            cq.select(cq.from(entityClass));
            return getEntityManager().createQuery(cq).getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al listar los registros", e);
            throw new IllegalStateException("Error al listar los registros", e);
        }
    }

    @Override
    public List<Entidad> findRange(int start, int end) {
        if (start < 0 || end < start) {
            throw new IllegalArgumentException("Rango inválido: start=" + start + ", end=" + end);
        }
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Entidad> cq = cb.createQuery(entityClass);
            cq.select(cq.from(entityClass));
            TypedQuery<Entidad> query = getEntityManager().createQuery(cq);
            query.setFirstResult(start);
            query.setMaxResults(end - start);
            return query.getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al listar los registros por rango", e);
            throw new IllegalStateException("Error al listar los registros por rango", e);
        }
    }

    @Override
    public long count() {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<Entidad> root = cq.from(entityClass);
            cq.select(cb.count(root));
            return getEntityManager().createQuery(cq).getSingleResult();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al contar los registros", e);
            throw new IllegalStateException("Error al contar los registros", e);
        }
    }

    /**
     * true si la fecha está entre desde y hasta (incluidas). Si desde o hasta
     * son null, ese lado del rango queda abierto. Lo usan las reglas de
     * descuentos de Orden.
     */
    protected static boolean estaDentroDeRango(Date fecha, Date desde, Date hasta) {
        if (fecha == null) {
            return false;
        }
        boolean despuesDelInicio = (desde == null) || !fecha.before(desde);
        boolean antesDelFin = (hasta == null) || !fecha.after(hasta);
        return despuesDelInicio && antesDelFin;
    }
}
