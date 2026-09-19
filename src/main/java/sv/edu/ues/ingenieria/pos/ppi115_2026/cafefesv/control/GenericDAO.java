/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import java.util.List;

/**
 *
 * @author daniel
 */

/**
 * Contrato genérico de operaciones CRUD para cualquier entidad JPA del
 * proyecto CafefeSV.
 *
 * @param <T>  tipo de la entidad administrada
 * @param <ID> tipo de la clave primaria de la entidad (por ejemplo
 *             {@code UUID}, o {@code ProductoTipoProductoPK} cuando la
 *             entidad usa clave compuesta)
 */
public interface GenericDAO<T, ID> {

    /**
     * Inserta una nueva entidad (equivalente a {@code EntityManager#persist}).
     */
    void save(T entity);

    /**
     * Actualiza (o adjunta) una entidad y devuelve la instancia administrada
     * resultante (equivalente a {@code EntityManager#merge}).
     */
    T update(T entity);

    /**
     * Elimina la entidad indicada.
     */
    void delete(T entity);

    /**
     * Busca y elimina la entidad con el id indicado. No hace nada si no existe.
     */
    void deleteById(ID id);

    /**
     * Busca una entidad por su clave primaria.
     *
     * @return la entidad encontrada, o {@code null} si no existe (mismo
     *         comportamiento que {@code EntityManager#find}).
     */
    T findById(ID id);

    /**
     * Devuelve todas las filas de la tabla asociada a la entidad.
     */
    List<T> findAll();

    /**
     * Devuelve un subconjunto de filas, útil para paginación.
     *
     * @param start índice inicial (inclusive, base 0)
     * @param end   índice final (exclusive)
     */
    List<T> findRange(int start, int end);

    /**
     * Cuenta el total de filas de la tabla asociada a la entidad.
     */
    long count();
}