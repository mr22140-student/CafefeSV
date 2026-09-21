/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoProducto;

/**
 *
 * @author daniel
 */
@Stateless
public class TipoProductoDAOImpl extends AbstractDAO<TipoProducto, UUID> implements TipoProductoDAO {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public TipoProductoDAOImpl() {
        super(TipoProducto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<TipoProducto> findByNombre(String nombre) {
        return em.createNamedQuery("TipoProducto.findByNombre", TipoProducto.class)
                .setParameter("nombre", nombre)
                .getResultList();
    }

    @Override
    public List<TipoProducto> findByActivo(Boolean activo) {
        return em.createNamedQuery("TipoProducto.findByActivo", TipoProducto.class)
                .setParameter("activo", activo)
                .getResultList();
    }

    @Override
    public List<TipoProducto> findByObservaciones(String observaciones) {
        return em.createNamedQuery("TipoProducto.findByObservaciones", TipoProducto.class)
                .setParameter("observaciones", observaciones)
                .getResultList();
    }
}