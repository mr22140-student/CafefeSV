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
     * true si el empleado ya tiene asignado ese rol en otro registro. idExcluir
     * es el registro que se esta modificando (null al crear).
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

    /**
     * Cuantas ordenes se registraron con esta asignacion.
     */
    public long countOrdenes(EmpleadoRol empleadoRol) {
        return em.createQuery(
                "SELECT COUNT(o) FROM Orden o WHERE o.idEmpleadoRol = :er", Long.class)
                .setParameter("er", empleadoRol)
                .getSingleResult();
    }

    /**
     * Cuantas facturas se registraron con esta asignacion.
     */
    public long countFacturas(EmpleadoRol empleadoRol) {
        return em.createQuery(
                "SELECT COUNT(f) FROM Factura f WHERE f.idEmpleadoRol = :er", Long.class)
                .setParameter("er", empleadoRol)
                .getSingleResult();
    }

    /**
     * Roles asignados a un empleado; trae el rol en la misma consulta.
     */
    public List<EmpleadoRol> findByEmpleado(UUID idEmpleado) {
        return em.createQuery(
                "SELECT x FROM EmpleadoRol x "
                + "LEFT JOIN FETCH x.idRol "
                + "WHERE x.idEmpleado = :emp", EmpleadoRol.class)
                .setParameter("emp", idEmpleado)
                .getResultList();
    }

    /**
     * El rol elegido en el combo debe existir y estar activo. Se lee de la base
     * para revisar su estado actual.
     *
     * @return el mensaje de error, o null si el rol está bien
     */
    public String validarRolElegido(Rol rol) {
        if (rol == null || rol.getIdRol() == null) {
            return "Seleccione un rol.";
        }
        Rol rolEnLaBase = em.find(Rol.class, rol.getIdRol());
        if (rolEnLaBase == null || !Boolean.TRUE.equals(rolEnLaBase.getActivo())) {
            return "El rol " + rol.getNombre() + " está inactivo.";
        }
        return null;
    }

    /**
     * En la pestaña de Empleado cada empleado tiene un solo rol: no se le puede
     * asignar otro si ya tiene uno.
     *
     * @return el mensaje de error, o null si todavía no tiene rol
     */
    public String validarSinRolAsignado(UUID idEmpleado) {
        if (idEmpleado != null && !findByEmpleado(idEmpleado).isEmpty()) {
            return "El empleado ya tiene un rol asignado. Use Modificar para cambiarlo.";
        }
        return null;
    }

    /**
     * Para cambiar el rol, el empleado debe tener uno y el nuevo debe ser
     * distinto.
     *
     * @return el mensaje de error, o null si se puede cambiar
     */
    public String validarCambioDeRol(EmpleadoRol asignacionActual, Rol rolNuevo) {
        if (asignacionActual == null) {
            return "El empleado no tiene rol. Use Asignar.";
        }
        if (rolNuevo != null && rolNuevo.equals(asignacionActual.getIdRol())) {
            return "El empleado ya tiene ese rol.";
        }
        return null;
    }

    /**
     * La asignación debe tener empleado y rol, y el empleado no puede tener ese
     * rol en otro registro.
     *
     * @return el mensaje de error, o null si la asignación está bien
     */
    public String validarAsignacion(EmpleadoRol asignacion) {
        if (asignacion == null) {
            return null;
        }
        if (asignacion.getIdEmpleado() == null) {
            return "Debe seleccionar el empleado.";
        }
        if (asignacion.getIdRol() == null) {
            return "Debe seleccionar el rol.";
        }
        if (existeAsignacion(asignacion.getIdEmpleado(), asignacion.getIdRol(), asignacion.getIdEmpleadoRol())) {
            return "Ese empleado ya tiene asignado el rol " + asignacion.getIdRol().getNombre() + ".";
        }
        return null;
    }

    /**
     * Una asignación con órdenes o facturas no se puede eliminar.
     *
     * @return el mensaje de error, o null si se puede eliminar
     */
    public String validarEliminar(EmpleadoRol asignacion) {
        if (asignacion != null && (countOrdenes(asignacion) > 0 || countFacturas(asignacion) > 0)) {
            return "Esta asignación ya tiene órdenes o facturas. Puede desmarcar 'Activo'.";
        }
        return null;
    }
}
