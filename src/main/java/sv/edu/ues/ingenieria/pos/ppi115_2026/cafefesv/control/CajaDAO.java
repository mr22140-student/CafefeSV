package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caja;

/**
 *
 * @author daniel
 */
@Stateless
public class CajaDAO extends AbstractDAO<Caja, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public CajaDAO() {
        super(Caja.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Cuenta las facturas que se hicieron en esta caja.
     */
    public long countFacturas(Caja caja) {
        return em.createQuery(
                "SELECT COUNT(factura) FROM Factura factura WHERE factura.idCaja = :caja",
                Long.class)
                .setParameter("caja", caja)
                .getSingleResult();
    }

    /**
     * Una caja no se puede eliminar si ya tiene facturas.
     *
     * @return el mensaje de error, o null si se puede eliminar
     */
    public String validarEliminar(Caja caja) {
        if (caja != null && countFacturas(caja) > 0) {
            return "La caja tiene facturas registradas. Puede desmarcar 'Activo'.";
        }
        return null;
    }

}
