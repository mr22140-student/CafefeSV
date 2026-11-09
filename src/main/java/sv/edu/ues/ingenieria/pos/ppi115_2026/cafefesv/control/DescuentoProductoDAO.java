package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.Date;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoDescuento;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.DescuentoProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;

/**
 *
 * @author daniel
 */

@Stateless
public class DescuentoProductoDAO extends AbstractDAO<DescuentoProducto, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public DescuentoProductoDAO() {
        super(DescuentoProducto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<DescuentoProducto> findByDescuento(Descuento descuento) {
        return em.createQuery(
                "SELECT x FROM DescuentoProducto x "
                + "WHERE x.idDescuento = :descuento",
                DescuentoProducto.class
        )
                .setParameter("descuento", descuento)
                .getResultList();
    }

    public List<DescuentoProducto> findByProducto(Producto producto) {
        return em.createQuery(
                "SELECT x FROM DescuentoProducto x "
                + "WHERE x.idProducto = :producto",
                DescuentoProducto.class
        )
                .setParameter("producto", producto)
                .getResultList();
    }

    /**
     * La asignación debe tener un descuento que exista, cuyo tipo esté activo y
     * que no haya vencido. El descuento se lee de la base para revisar su
     * estado actual.
     *
     * @return el mensaje de error, o null si el descuento está bien
     */
    public String validarDescuento(DescuentoProducto asignacion) {
        if (asignacion == null || asignacion.getIdDescuento() == null
                || asignacion.getIdDescuento().getIdDescuento() == null) {
            return "Seleccione un descuento.";
        }
        Descuento descuentoEnLaBase = em.find(Descuento.class, asignacion.getIdDescuento().getIdDescuento());
        if (descuentoEnLaBase == null) {
            return "El descuento seleccionado ya no existe.";
        }
        TipoDescuento tipoDescuento = descuentoEnLaBase.getIdTipoDescuento();
        if (tipoDescuento != null && !Boolean.TRUE.equals(tipoDescuento.getActivo())) {
            return "El tipo \"" + tipoDescuento.getNombre() + "\" está inactivo. Seleccione otro descuento.";
        }
        if (descuentoEnLaBase.getFechaHasta() != null && descuentoEnLaBase.getFechaHasta().before(new Date())) {
            return "El descuento \"" + descuentoEnLaBase.getNombre() + "\" terminó el "
                    + formatoFecha(descuentoEnLaBase.getFechaHasta()) + ".";
        }
        return null;
    }

    /**
     * El porcentaje debe ser mayor que 0 y no puede superar el máximo del tipo
     * de descuento.
     *
     * @return el mensaje de error, o null si el porcentaje está bien
     */
    public String validarValor(DescuentoProducto asignacion) {
        Integer porcentaje = asignacion == null ? null : asignacion.getValor();
        if (porcentaje == null || porcentaje <= 0) {
            return "Indique un porcentaje mayor que 0.";
        }
        Integer maximo = (asignacion.getIdDescuento() == null || asignacion.getIdDescuento().getIdTipoDescuento() == null)
                ? null : asignacion.getIdDescuento().getIdTipoDescuento().getDescuentoMaximo();
        if (maximo != null && porcentaje > maximo) {
            return "El porcentaje no puede superar el máximo del tipo de descuento (" + maximo + " %).";
        }
        return null;
    }

    /**
     * Las fechas de la asignación deben quedar dentro de la vigencia del
     * descuento. Si el descuento tiene fin, la asignación también debe tenerlo.
     *
     * @return el mensaje de error, o null si las fechas están bien
     */
    public String validarFechas(DescuentoProducto asignacion) {
        if (asignacion == null || asignacion.getIdDescuento() == null) {
            return null;
        }
        Descuento descuento = asignacion.getIdDescuento();
        Date inicio = asignacion.getFechaDesde();
        Date fin = asignacion.getFechaHasta();
        if (inicio == null) {
            return "Indique la fecha de inicio.";
        }
        if (descuento.getFechaDesde() != null && inicio.before(descuento.getFechaDesde())) {
            return "La fecha de inicio no puede ser anterior al inicio del descuento ("
                    + formatoFecha(descuento.getFechaDesde()) + ").";
        }
        if (descuento.getFechaHasta() != null && inicio.after(descuento.getFechaHasta())) {
            return "La fecha de inicio no puede ser posterior al fin del descuento ("
                    + formatoFecha(descuento.getFechaHasta()) + ").";
        }
        if (descuento.getFechaHasta() != null && fin == null) {
            return "El descuento termina el " + formatoFecha(descuento.getFechaHasta())
                    + "; indique la fecha de finalización.";
        }
        if (fin != null && fin.before(inicio)) {
            return "La fecha de finalización no puede ser anterior a la fecha de inicio.";
        }
        if (fin != null && descuento.getFechaHasta() != null && fin.after(descuento.getFechaHasta())) {
            return "La fecha de finalización no puede ser posterior al fin del descuento ("
                    + formatoFecha(descuento.getFechaHasta()) + ").";
        }
        return null;
    }

    /**
     * Un producto no puede tener el mismo descuento dos veces.
     *
     * @return el mensaje de error, o null si no está repetido
     */
    public String validarDuplicado(DescuentoProducto asignacion) {
        if (asignacion == null || asignacion.getIdProducto() == null || asignacion.getIdDescuento() == null) {
            return null;
        }
        boolean repetido = findByProducto(asignacion.getIdProducto()).stream()
                .anyMatch(otraAsignacion -> !otraAsignacion.getIdDescuentoProducto().equals(asignacion.getIdDescuentoProducto())
                        && asignacion.getIdDescuento().equals(otraAsignacion.getIdDescuento()));
        return repetido ? "El producto ya tiene asignado ese descuento." : null;
    }

    // Fecha en formato dd/MM/yyyy HH:mm para los mensajes.
    private String formatoFecha(Date fecha) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(fecha);
    }
}
