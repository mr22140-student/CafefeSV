/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Orden;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.OrdenProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;

/**
 *
 * @author daniel
 */
@Stateless
public class OrdenProductoDAOImpl extends AbstractDAO<OrdenProducto, UUID> implements OrdenProductoDAO {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public OrdenProductoDAOImpl() {
        super(OrdenProducto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<OrdenProducto> findByPrecio(BigDecimal precio) {
        return em.createNamedQuery("OrdenProducto.findByPrecio", OrdenProducto.class)
                .setParameter("precio", precio)
                .getResultList();
    }

    @Override
    public List<OrdenProducto> findByObservaciones(String observaciones) {
        return em.createNamedQuery("OrdenProducto.findByObservaciones", OrdenProducto.class)
                .setParameter("observaciones", observaciones)
                .getResultList();
    }

    @Override
    public List<OrdenProducto> findByOrden(Orden orden) {
        return em.createQuery(
                "SELECT x FROM OrdenProducto x WHERE x.idOrden = :orden",
                OrdenProducto.class)
                .setParameter("orden", orden)
                .getResultList();
    }

    @Override
    public List<OrdenProducto> findByProducto(Producto producto) {
        return em.createQuery(
                "SELECT x FROM OrdenProducto x WHERE x.idProducto = :producto",
                OrdenProducto.class)
                .setParameter("producto", producto)
                .getResultList();
    }
}