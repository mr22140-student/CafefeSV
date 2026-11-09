package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Empleado;

/**
 *
 * @author daniel
 */

@Stateless
public class EmpleadoDAO extends AbstractDAO<Empleado, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public EmpleadoDAO() {
        super(Empleado.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Cuenta las órdenes registradas con cualquier rol del empleado.
     */
    public long countOrdenes(Empleado empleado) {
        return em.createQuery(
                "SELECT COUNT(orden) FROM Orden orden WHERE orden.idEmpleadoRol.idEmpleado = :idEmpleado",
                Long.class)
                .setParameter("idEmpleado", empleado.getIdEmpleado())
                .getSingleResult();
    }

    /**
     * Cuenta las facturas registradas con cualquier rol del empleado.
     */
    public long countFacturas(Empleado empleado) {
        return em.createQuery(
                "SELECT COUNT(factura) FROM Factura factura WHERE factura.idEmpleadoRol.idEmpleado = :idEmpleado",
                Long.class)
                .setParameter("idEmpleado", empleado.getIdEmpleado())
                .getSingleResult();
    }

    /**
     * Un empleado con órdenes o facturas no se puede eliminar, porque se
     * perdería el historial.
     *
     * @return el mensaje de error, o null si se puede eliminar
     */
    public String validarEliminar(Empleado empleado) {
        if (empleado != null && (countOrdenes(empleado) > 0 || countFacturas(empleado) > 0)) {
            return "El empleado ya tiene órdenes o facturas. Puede desmarcar 'Activo'.";
        }
        return null;
    }
}
