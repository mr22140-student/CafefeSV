/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author daniel
 */

/**
 * Contrato genérico de operaciones CRUD para cualquier entidad JPA del
 * proyecto CafefeSV.
 *
 * <p>Todos los métodos pueden lanzar (sin declararlo obligatorio, porque son
 * excepciones no verificadas — "unchecked"):</p>
 * <ul>
 *   <li>{@link IllegalArgumentException} si un parámetro requerido viene
 *       nulo o inválido (por ejemplo, un {@code id} nulo, o un rango de
 *       {@code findRange} mal formado);</li>
 *   <li>{@link IllegalStateException} si algo falla del lado de la base de
 *       datos/JPA al ejecutar la operación (la causa original queda
 *       encadenada con {@link Throwable#getCause()} para poder revisarla en
 *       el log).</li>
 * </ul>
 *
 * @param <Entidad>  tipo de la entidad administrada
 * @param <ID> tipo de la clave primaria de la entidad (por ejemplo
 *             {@code UUID}, o {@code ProductoTipoProductoPK} cuando la
 *             entidad usa clave compuesta)
 */
public interface GenericDAO<Entidad, ID> extends Serializable {
 
    /**
     * Inserta una nueva entidad (equivalente a {@code EntityManager#persist}).
     */
    void save(Entidad entity) throws IllegalArgumentException, IllegalStateException;
 
    /**
     * Actualiza (o adjunta) una entidad y devuelve la instancia administrada
     * resultante (equivalente a {@code EntityManager#merge}).
     */
    Entidad update(Entidad entity) throws IllegalArgumentException, IllegalStateException;
 
    /**
     * Elimina la entidad indicada.
     */
    void delete(Entidad entity) throws IllegalArgumentException, IllegalStateException;
 
    /**
     * Busca y elimina la entidad con el id indicado. No hace nada si no existe.
     */
    void deleteById(ID id) throws IllegalArgumentException, IllegalStateException;
 
    /**
     * Busca una entidad por su clave primaria.
     *
     * @return la entidad encontrada, o {@code null} si no existe (mismo
     *         comportamiento que {@code EntityManager#find}).
     */
    Entidad findById(ID id) throws IllegalArgumentException, IllegalStateException;
 
    /**
     * Devuelve todas las filas de la tabla asociada a la entidad.
     */
    List<Entidad> findAll() throws IllegalStateException;
 
    /**
     * Devuelve un subconjunto de filas, útil para paginación.
     *
     * @param start índice inicial (inclusive, base 0)
     * @param end   índice final (exclusive)
     */
    List<Entidad> findRange(int start, int end) throws IllegalArgumentException, IllegalStateException;
 
    /**
     * Cuenta el total de filas de la tabla asociada a la entidad.
     */
    long count() throws IllegalStateException;
}
 