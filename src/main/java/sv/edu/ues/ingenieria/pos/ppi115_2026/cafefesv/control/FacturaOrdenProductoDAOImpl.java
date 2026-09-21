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
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.FacturaOrdenProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.OrdenProducto;

/**
 *
 * @author daniel
 */
@Stateless
public class FacturaOrdenProductoDAOImpl extends AbstractDAO<FacturaOrdenProducto, UUID> implements FacturaOrdenProductoDAO {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public FacturaOrdenProductoDAOImpl() {
        super(FacturaOrdenProducto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<FacturaOrdenProducto> findByPrecio(BigDecimal precio) {
        return em.createNamedQuery("FacturaOrdenProducto.findByPrecio", FacturaOrdenProducto.class)
                .setParameter("precio", precio)
                .getResultList();
    }

    @Override
    public List<FacturaOrdenProducto> findByObservaciones(String observaciones) {
        return em.createNamedQuery("FacturaOrdenProducto.findByObservaciones", FacturaOrdenProducto.class)
                .setParameter("observaciones", observaciones)
                .getResultList();
    }

    @Override
    public List<FacturaOrdenProducto> findByFactura(Factura factura) {
        return em.createQuery(
                "SELECT x FROM FacturaOrdenProducto x WHERE x.idFactura = :factura",
                FacturaOrdenProducto.class)
                .setParameter("factura", factura)
                .getResultList();
    }

    @Override
    public List<FacturaOrdenProducto> findByOrdenProducto(OrdenProducto ordenProducto) {
        return em.createQuery(
                "SELECT x FROM FacturaOrdenProducto x WHERE x.idOrdenProducto = :ordenProducto",
                FacturaOrdenProducto.class)
                .setParameter("ordenProducto", ordenProducto)
                .getResultList();
    }
}