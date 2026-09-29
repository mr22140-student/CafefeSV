package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Rol;

/**
 *
 * @author daniel
 */

@Stateless
public class EmpleadoRolDAO extends AbstractDAO<EmpleadoRol, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public EmpleadoRolDAO() {
        super(EmpleadoRol.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<EmpleadoRol> findByActivo(Boolean activo) {
        return em.createNamedQuery(
                "EmpleadoRol.findByActivo",
                EmpleadoRol.class
        )
                .setParameter("activo", activo)
                .getResultList();
    }

    public List<EmpleadoRol> findByObservaciones(String observaciones) {
        return em.createNamedQuery(
                "EmpleadoRol.findByObservaciones",
                EmpleadoRol.class
        )
                .setParameter("observaciones", observaciones)
                .getResultList();
    }

    public List<EmpleadoRol> findByRol(Rol rol) {
        return em.createQuery(
                "SELECT x FROM EmpleadoRol x "
                + "WHERE x.idRol = :rol",
                EmpleadoRol.class
        )
                .setParameter("rol", rol)
                .getResultList();
    }

    /**
     * Cuantos roles tiene asignados un empleado (para no eliminar empleados en
     * uso).
     */
    public long countByEmpleado(UUID idEmpleado) {
        return em.createQuery(
                "SELECT COUNT(x) FROM EmpleadoRol x WHERE x.idEmpleado = :emp", Long.class)
                .setParameter("emp", idEmpleado)
                .getSingleResult();
    }
    
        /**
     * true si el empleado ya tiene asignado ese rol en otro registro.
     * idExcluir es el registro que se esta modificando (null al crear).
     */
    public boolean existeAsignacion(UUID idEmpleado, Rol rol, UUID idExcluir) {
        Long total = em.createQuery(
                "SELECT COUNT(x) FROM EmpleadoRol x "
                + "WHERE x.idEmpleado = :emp AND x.idRol = :rol "
                + "AND x.idEmpleadoRol <> :excluir", Long.class)
                .setParameter("emp", idEmpleado)
                .setParameter("rol", rol)
                .setParameter("excluir", idExcluir != null ? idExcluir : new UUID(0L, 0L))
                .getSingleResult();
        return total > 0;
    }

    /** Cuantas ordenes se registraron con esta asignacion. */
    public long countOrdenes(EmpleadoRol empleadoRol) {
        return em.createQuery(
                "SELECT COUNT(o) FROM Orden o WHERE o.idEmpleadoRol = :er", Long.class)
                .setParameter("er", empleadoRol)
                .getSingleResult();
    }

    /** Cuantas facturas se registraron con esta asignacion. */
    public long countFacturas(EmpleadoRol empleadoRol) {
        return em.createQuery(
                "SELECT COUNT(f) FROM Factura f WHERE f.idEmpleadoRol = :er", Long.class)
                .setParameter("er", empleadoRol)
                .getSingleResult();
    }
    
        /** Roles asignados a un empleado; trae el rol en la misma consulta. */
    public List<EmpleadoRol> findByEmpleado(UUID idEmpleado) {
        return em.createQuery(
                "SELECT x FROM EmpleadoRol x "
                + "LEFT JOIN FETCH x.idRol "
                + "WHERE x.idEmpleado = :emp", EmpleadoRol.class)
                .setParameter("emp", idEmpleado)
                .getResultList();
    }
}
