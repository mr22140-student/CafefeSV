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
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoTipoProducto;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoProducto;

/**
 *
 * @author daniel
 */
@Stateless
public class ProductoTipoProductoDAOImpl extends AbstractDAO<ProductoTipoProducto, UUID> implements ProductoTipoProductoDAO {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public ProductoTipoProductoDAOImpl() {
        super(ProductoTipoProducto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<ProductoTipoProducto> findByFechaCreacion(Date fechaCreacion) {
        return em.createNamedQuery("ProductoTipoProducto.findByFechaCreacion", ProductoTipoProducto.class)
                .setParameter("fechaCreacion", fechaCreacion)
                .getResultList();
    }

    @Override
    public List<ProductoTipoProducto> findByObservaciones(String observaciones) {
        return em.createNamedQuery("ProductoTipoProducto.findByObservaciones", ProductoTipoProducto.class)
                .setParameter("observaciones", observaciones)
                .getResultList();
    }

    @Override
    public List<ProductoTipoProducto> findByProducto(Producto producto) {
        return em.createQuery(
                "SELECT x FROM ProductoTipoProducto x WHERE x.producto = :producto",
                ProductoTipoProducto.class)
                .setParameter("producto", producto)
                .getResultList();
    }

    @Override
    public List<ProductoTipoProducto> findByTipoProducto(TipoProducto tipoProducto) {
        return em.createQuery(
                "SELECT x FROM ProductoTipoProducto x WHERE x.idTipoProducto = :tipoProducto",
                ProductoTipoProducto.class)
                .setParameter("tipoProducto", tipoProducto)
                .getResultList();
    }
}
