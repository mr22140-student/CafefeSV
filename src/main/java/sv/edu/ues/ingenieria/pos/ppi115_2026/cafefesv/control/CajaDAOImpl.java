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
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caja;

/**
 *
 * @author daniel
 */
@Stateless
public class CajaDAOImpl extends AbstractDAO<Caja, UUID> implements CajaDAO {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public CajaDAOImpl() {
        super(Caja.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<Caja> findByNombre(String nombre) {
        return em.createNamedQuery("Caja.findByNombre", Caja.class)
                .setParameter("nombre", nombre)
                .getResultList();
    }

    @Override
    public List<Caja> findByActivo(Boolean activo) {
        return em.createNamedQuery("Caja.findByActivo", Caja.class)
                .setParameter("activo", activo)
                .getResultList();
    }

    @Override
    public List<Caja> findByObservaciones(String observaciones) {
        return em.createNamedQuery("Caja.findByObservaciones", Caja.class)
                .setParameter("observaciones", observaciones)
                .getResultList();
    }
}
