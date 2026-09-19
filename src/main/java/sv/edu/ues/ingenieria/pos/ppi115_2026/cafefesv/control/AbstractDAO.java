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
import java.util.List;

/**
 *
 * @author daniel
 */
/**
 * Implementación base del patrón DAO usando JPA {@link EntityManager}.
 * <p>
 * Cada DAO concreto (un {@code @Stateless} EJB) solo necesita:
 * <ol>
 *   <li>extender esta clase pasando en el constructor la clase de la
 *       entidad que administra;</li>
 *   <li>inyectar su propio {@link EntityManager} con
 *       {@code @PersistenceContext(unitName = "CafefePU")};</li>
 *   <li>implementar {@link #getEntityManager()} devolviendo ese campo.</li>
 * </ol>
 *
 * @param <T>  tipo de la entidad administrada
 * @param <ID> tipo de la clave primaria de la entidad
 */
public abstract class AbstractDAO<T, ID> implements GenericDAO<T, ID> {

    private final Class<T> entityClass;

    protected AbstractDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Debe devolver el {@link EntityManager} inyectado por el contenedor en
     * la subclase concreta.
     */
    protected abstract EntityManager getEntityManager();

    @Override
    public void save(T entity) {
        getEntityManager().persist(entity);
    }

    @Override
    public T update(T entity) {
        return getEntityManager().merge(entity);
    }

    @Override
    public void delete(T entity) {
        EntityManager em = getEntityManager();
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    @Override
    public void deleteById(ID id) {
        T entity = findById(id);
        if (entity != null) {
            delete(entity);
        }
    }

    @Override
    public T findById(ID id) {
        return getEntityManager().find(entityClass, id);
    }

    @Override
    public List<T> findAll() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    @Override
    public List<T> findRange(int start, int end) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        cq.select(cq.from(entityClass));
        TypedQuery<T> query = getEntityManager().createQuery(cq);
        query.setFirstResult(start);
        query.setMaxResults(end - start);
        return query.getResultList();
    }

    @Override
    public long count() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> root = cq.from(entityClass);
        cq.select(cb.count(root));
        return getEntityManager().createQuery(cq).getSingleResult();
    }
}