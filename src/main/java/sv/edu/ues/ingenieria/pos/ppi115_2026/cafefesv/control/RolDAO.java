package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Rol;

/**
 *
 * @author daniel
 */
@Stateless
public class RolDAO extends AbstractDAO<Rol, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public RolDAO() {
        super(Rol.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Rol> findByActivo(Boolean activo) {
        return em.createNamedQuery("Rol.findByActivo", Rol.class)
                .setParameter("activo", activo)
                .getResultList();
    }

    /**
     * Cuenta los empleados que tienen asignado este rol.
     */
    public long countEmpleados(Rol rol) {
        return em.createQuery(
                "SELECT COUNT(asignacion) FROM EmpleadoRol asignacion WHERE asignacion.idRol = :rol",
                Long.class)
                .setParameter("rol", rol)
                .getSingleResult();
    }

    /**
     * Roles activos para el combo de la pestaña de roles del empleado. Si el
     * empleado ya tiene un rol inactivo, se agrega para que se siga viendo.
     */
    public List<Rol> buscarActivosIncluyendo(Rol rolActual) {
        List<Rol> roles = new ArrayList<>(findByActivo(Boolean.TRUE));
        if (rolActual != null && !roles.contains(rolActual)) {
            roles.add(rolActual);
        }
        return roles;
    }

    /**
     * Un rol no se puede desactivar mientras algún empleado lo tenga.
     *
     * @return el mensaje de error, o null si se puede desactivar
     */
    public String validarInactivar(Rol rol) {
        if (rol == null || Boolean.TRUE.equals(rol.getActivo())) {
            return null;
        }
        long empleados = countEmpleados(rol);
        if (empleados > 0) {
            return "El rol está asignado a " + empleados + " empleado(s). Primero cámbieles el rol.";
        }
        return null;
    }

    /**
     * Un rol no se puede eliminar si algún empleado lo tiene asignado.
     *
     * @return el mensaje de error, o null si se puede eliminar
     */
    public String validarEliminar(Rol rol) {
        if (rol != null && countEmpleados(rol) > 0) {
            return "El rol está asignado a uno o más empleados. Puede desmarcar 'Activo'.";
        }
        return null;
    }

}
