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
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Rol;

/**
 *
 * @author daniel
 */
@Stateless
public class RolDAOImpl extends AbstractDAO<Rol, UUID> implements RolDAO {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public RolDAOImpl() {
        super(Rol.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<Rol> findByNombre(String nombre) {
        return em.createNamedQuery("Rol.findByNombre", Rol.class)
                .setParameter("nombre", nombre)
                .getResultList();
    }

    @Override
    public List<Rol> findByActivo(Boolean activo) {
        return em.createNamedQuery("Rol.findByActivo", Rol.class)
                .setParameter("activo", activo)
                .getResultList();
    }

    @Override
    public List<Rol> findByObservaciones(String observaciones) {
        return em.createNamedQuery("Rol.findByObservaciones", Rol.class)
                .setParameter("observaciones", observaciones)
                .getResultList();
    }
}
