package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import jakarta.persistence.TemporalType;
import java.util.Date;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoDescuento;

/**
 *
 * @author daniel
 */
@Stateless
public class TipoDescuentoDAO extends AbstractDAO<TipoDescuento, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public TipoDescuentoDAO() {
        super(TipoDescuento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Cuenta los descuentos que usan este tipo.
     */
    public long countDescuentos(TipoDescuento tipoDescuento) {
        return em.createQuery(
                "SELECT COUNT(descuento) FROM Descuento descuento WHERE descuento.idTipoDescuento = :tipoDescuento",
                Long.class)
                .setParameter("tipoDescuento", tipoDescuento)
                .getSingleResult();
    }

    /**
     * Cuenta los descuentos de este tipo que todavía no vencen (sin fecha fin o
     * con fecha fin futura).
     */
    public long countDescuentosVigentes(TipoDescuento tipoDescuento) {
        return em.createQuery(
                "SELECT COUNT(descuento) FROM Descuento descuento WHERE descuento.idTipoDescuento = :tipoDescuento "
                + "AND (descuento.fechaHasta IS NULL OR descuento.fechaHasta >= :ahora)",
                Long.class)
                .setParameter("tipoDescuento", tipoDescuento)
                .setParameter("ahora", new Date(), TemporalType.TIMESTAMP)
                .getSingleResult();
    }

    /**
     * Un tipo no se puede inactivar mientras lo use algún descuento que no ha
     * vencido.
     *
     * @return el mensaje de error, o null si se puede inactivar
     */
    public String validarInactivar(TipoDescuento tipoDescuento) {
        if (tipoDescuento == null || Boolean.TRUE.equals(tipoDescuento.getActivo())) {
            return null;
        }
        long descuentosVigentes = countDescuentosVigentes(tipoDescuento);
        if (descuentosVigentes > 0) {
            return "Hay " + descuentosVigentes + " descuento(s) sin vencer con este tipo. "
                    + "Cámbielos de tipo o espere a que venzan.";
        }
        return null;
    }

    /**
     * La base no tiene FK entre descuento y tipo_descuento, así que aquí se
     * revisa que el tipo no esté en uso antes de eliminarlo.
     *
     * @return el mensaje de error, o null si se puede eliminar
     */
    public String validarEliminar(TipoDescuento tipoDescuento) {
        if (tipoDescuento != null && countDescuentos(tipoDescuento) > 0) {
            return "Hay descuentos que usan este tipo. Modifícalos o márcalo como inactivo.";
        }
        return null;
    }

}
