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
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Orden;

/**
 *
 * @author daniel
 */
@Stateless
public class OrdenDAOImpl extends AbstractDAO<Orden, UUID> implements OrdenDAO {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public OrdenDAOImpl() {
        super(Orden.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<Orden> findByFechaCreacion(Date fechaCreacion) {
        return em.createNamedQuery("Orden.findByFechaCreacion", Orden.class)
                .setParameter("fechaCreacion", fechaCreacion)
                .getResultList();
    }

    @Override
    public List<Orden> findByEmpleadoRol(EmpleadoRol empleadoRol) {
        return em.createQuery(
                "SELECT x FROM Orden x WHERE x.idEmpleadoRol = :empleadoRol",
                Orden.class)
                .setParameter("empleadoRol", empleadoRol)
                .getResultList();
    }
}