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
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Empleado;
/**
 *
 * @author daniel
 */
@Stateless
public class EmpleadoDAOImpl extends AbstractDAO<Empleado, UUID> implements EmpleadoDAO {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public EmpleadoDAOImpl() {
        super(Empleado.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<Empleado> findByNombre(String nombre) {
        return em.createNamedQuery("Empleado.findByNombre", Empleado.class)
                .setParameter("nombre", nombre)
                .getResultList();
    }

    @Override
    public List<Empleado> findByApellido(String apellido) {
        return em.createNamedQuery("Empleado.findByApellido", Empleado.class)
                .setParameter("apellido", apellido)
                .getResultList();
    }

    @Override
    public List<Empleado> findByActivo(Boolean activo) {
        return em.createNamedQuery("Empleado.findByActivo", Empleado.class)
                .setParameter("activo", activo)
                .getResultList();
    }

    @Override
    public List<Empleado> findByComentarios(String comentarios) {
        return em.createNamedQuery("Empleado.findByComentarios", Empleado.class)
                .setParameter("comentarios", comentarios)
                .getResultList();
    }
}
