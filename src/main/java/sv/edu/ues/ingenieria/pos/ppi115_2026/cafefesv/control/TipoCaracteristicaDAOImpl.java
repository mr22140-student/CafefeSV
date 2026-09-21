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
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;

/**
 *
 * @author daniel
 */
@Stateless
public class TipoCaracteristicaDAOImpl extends AbstractDAO<TipoCaracteristica, UUID> implements TipoCaracteristicaDAO {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public TipoCaracteristicaDAOImpl() {
        super(TipoCaracteristica.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<TipoCaracteristica> findByNombre(String nombre) {
        return em.createNamedQuery("TipoCaracteristica.findByNombre", TipoCaracteristica.class)
                .setParameter("nombre", nombre)
                .getResultList();
    }

    @Override
    public List<TipoCaracteristica> findByExpresionRegular(String expresionRegular) {
        return em.createNamedQuery("TipoCaracteristica.findByExpresionRegular", TipoCaracteristica.class)
                .setParameter("expresionRegular", expresionRegular)
                .getResultList();
    }

    @Override
    public List<TipoCaracteristica> findByActivo(Boolean activo) {
        return em.createNamedQuery("TipoCaracteristica.findByActivo", TipoCaracteristica.class)
                .setParameter("activo", activo)
                .getResultList();
    }

    @Override
    public List<TipoCaracteristica> findByObservaciones(String observaciones) {
        return em.createNamedQuery("TipoCaracteristica.findByObservaciones", TipoCaracteristica.class)
                .setParameter("observaciones", observaciones)
                .getResultList();
    }
}
