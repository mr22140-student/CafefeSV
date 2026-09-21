/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.DescuentoProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;

/**
 *
 * @author daniel
 */
@Stateless
public class DescuentoProductoDAOImpl extends AbstractDAO<DescuentoProducto, UUID> implements DescuentoProductoDAO {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public DescuentoProductoDAOImpl() {
        super(DescuentoProducto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<DescuentoProducto> findByFechaDesde(Date fechaDesde) {
        return em.createNamedQuery("DescuentoProducto.findByFechaDesde", DescuentoProducto.class)
                .setParameter("fechaDesde", fechaDesde)
                .getResultList();
    }

    @Override
    public List<DescuentoProducto> findByFechaHasta(Date fechaHasta) {
        return em.createNamedQuery("DescuentoProducto.findByFechaHasta", DescuentoProducto.class)
                .setParameter("fechaHasta", fechaHasta)
                .getResultList();
    }

    @Override
    public List<DescuentoProducto> findByValor(Integer valor) {
        return em.createNamedQuery("DescuentoProducto.findByValor", DescuentoProducto.class)
                .setParameter("valor", valor)
                .getResultList();
    }

    @Override
    public List<DescuentoProducto> findByObservaciones(String observaciones) {
        return em.createNamedQuery("DescuentoProducto.findByObservaciones", DescuentoProducto.class)
                .setParameter("observaciones", observaciones)
                .getResultList();
    }

    @Override
    public List<DescuentoProducto> findByDescuento(Descuento descuento) {
        return em.createQuery(
                "SELECT x FROM DescuentoProducto x WHERE x.idDescuento = :descuento",
                DescuentoProducto.class)
                .setParameter("descuento", descuento)
                .getResultList();
    }

    @Override
    public List<DescuentoProducto> findByProducto(Producto producto) {
        return em.createQuery(
                "SELECT x FROM DescuentoProducto x WHERE x.idProducto = :producto",
                DescuentoProducto.class)
                .setParameter("producto", producto)
                .getResultList();
    }
}
