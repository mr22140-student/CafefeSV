package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoDescuento;

/**
 *
 * @author daniel
 */
@Stateless
public class DescuentoDAO extends AbstractDAO<Descuento, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public DescuentoDAO() {
        super(Descuento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Descuentos de un tipo que todavía no vencen (sin fecha fin o con fecha
     * fin futura).
     */
    public List<Descuento> findVigentesByTipoDescuento(TipoDescuento tipoDescuento) {
        if (tipoDescuento == null) {
            return List.of();
        }
        return em.createQuery(
                "SELECT descuento FROM Descuento descuento WHERE descuento.idTipoDescuento = :tipo "
                + "AND (descuento.fechaHasta IS NULL OR descuento.fechaHasta >= :ahora) ORDER BY descuento.nombre", Descuento.class)
                .setParameter("tipo", tipoDescuento)
                .setParameter("ahora", new Date(), TemporalType.TIMESTAMP)
                .getResultList();
    }

    /**
     * La fecha hasta es opcional, pero si viene no puede ser anterior a la
     * fecha desde.
     *
     * @return el mensaje de error, o null si las fechas están bien
     */
    public String validarFechas(Descuento descuento) {
        if (descuento != null && descuento.getFechaDesde() != null && descuento.getFechaHasta() != null
                && descuento.getFechaHasta().before(descuento.getFechaDesde())) {
            return "La fecha hasta no puede ser anterior a la fecha desde.";
        }
        return null;
    }

    /**
     * El descuento no puede tener un tipo inactivo. El tipo se lee de la base
     * para revisar su estado actual.
     *
     * @return el mensaje de error, o null si el tipo está activo
     */
    public String validarTipoActivo(Descuento descuento) {
        if (descuento == null || descuento.getIdTipoDescuento() == null
                || descuento.getIdTipoDescuento().getIdTipoDescuento() == null) {
            return null;
        }
        TipoDescuento tipoEnLaBase = em.find(TipoDescuento.class, descuento.getIdTipoDescuento().getIdTipoDescuento());
        if (tipoEnLaBase == null || !Boolean.TRUE.equals(tipoEnLaBase.getActivo())) {
            return "El tipo \"" + descuento.getIdTipoDescuento().getNombre()
                    + "\" está inactivo. Seleccione un tipo activo.";
        }
        return null;
    }

    /**
     * Estado del descuento según la fecha actual: SIN FECHA, PROGRAMADO,
     * VENCIDO o VIGENTE. Se calcula, no se guarda en la base.
     */
    public String calcularVigencia(Descuento descuento) {
        if (descuento == null || descuento.getFechaDesde() == null) {
            return "SIN FECHA";
        }
        Date ahora = new Date();
        if (ahora.before(descuento.getFechaDesde())) {
            return "PROGRAMADO";
        }
        if (descuento.getFechaHasta() != null && ahora.after(descuento.getFechaHasta())) {
            return "VENCIDO";
        }
        return "VIGENTE";
    }

    /**
     * Descuentos vigentes en una fecha (la de la orden). Llena el combo de
     * descuentos de Orden.
     */
    public List<Descuento> buscarVigentesEn(Date fecha) {
        List<Descuento> vigentes = new ArrayList<>();
        for (Descuento descuento : em.createNamedQuery("Descuento.findAll", Descuento.class).getResultList()) {
            if (estaDentroDeRango(fecha, descuento.getFechaDesde(), descuento.getFechaHasta())) {
                vigentes.add(descuento);
            }
        }
        return vigentes;
    }

    /**
     * Cuántos productos tienen asignado este descuento.
     */
    public long countProductos(Descuento descuento) {
        return em.createQuery(
                "SELECT COUNT(asignacion) FROM DescuentoProducto asignacion WHERE asignacion.idDescuento = :descuento",
                Long.class)
                .setParameter("descuento", descuento)
                .getSingleResult();
    }

    /**
     * Un descuento no se puede eliminar si algún producto lo tiene asignado.
     *
     * @return el mensaje de error, o null si se puede eliminar
     */
    public String validarEliminar(Descuento descuento) {
        if (descuento != null && countProductos(descuento) > 0) {
            return "Hay productos que tienen asignado este descuento. Quíteselo primero.";
        }
        return null;
    }
}
