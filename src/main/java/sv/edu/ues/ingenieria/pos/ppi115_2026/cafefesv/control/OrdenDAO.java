package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Orden;

/**
 *
 * @author daniel
 */
@Stateless
public class OrdenDAO extends AbstractDAO<Orden, UUID> {

    /**
     * Único rol que puede tomar órdenes (según la rúbrica).
     */
    private static final String ROL_PERMITIDO = "camarero";

    /**
     * Estados de preparación de una orden en el local, en el orden en que
     * avanzan. El barista los cambia y el cliente los ve en la app.
     */
    public static final List<String> ESTADOS_PREPARACION = List.of("RECIBIDA", "PREPARANDO", "EN_CAMINO", "ENTREGADA");

    /**
     * El estado de preparación se guarda en memoria (no hay columna en la tabla
     * orden y la base no se modifica). Es static porque el DAO es
     *
     * @Stateless y el contenedor usa varias instancias. Si el servidor se
     * reinicia, las órdenes vuelven a RECIBIDA.
     */
    private static final Map<UUID, String> ESTADO_POR_ORDEN = new ConcurrentHashMap<>();

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public OrdenDAO() {
        super(Orden.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Cuenta los productos que tiene la orden.
     */
    public long countProductos(Orden orden) {
        return em.createQuery(
                "SELECT COUNT(detalle) FROM OrdenProducto detalle WHERE detalle.idOrden = :orden",
                Long.class)
                .setParameter("orden", orden)
                .getSingleResult();
    }

    /**
     * Órdenes pendientes: las que tienen al menos un producto que todavía no
     * está en una factura PAGADA (no facturado, o en una factura PENDIENTE).
     * Las más antiguas primero, para que el barista las prepare en orden.
     */
    public List<Orden> findPendientes() {
        return em.createQuery(
                "SELECT orden FROM Orden orden "
                + "WHERE EXISTS ("
                + "   SELECT detalle FROM OrdenProducto detalle "
                + "   WHERE detalle.idOrden = orden "
                + "   AND NOT EXISTS ("
                + "      SELECT facturado FROM FacturaOrdenProducto facturado "
                + "      WHERE facturado.idOrdenProducto = detalle "
                + "      AND facturado.idFactura.estado = :pagada)) "
                + "ORDER BY orden.fechaCreacion",
                Orden.class)
                .setParameter("pagada", FacturaDAO.PAGADA)
                .getResultList();
    }

    /**
     * Solo un camarero puede tomar una orden: el rol de la asignación debe
     * estar activo y llamarse "camarero" (sin importar tildes ni mayúsculas).
     *
     * @return el mensaje de error, o null si es camarero
     */
    public String validarCamarero(EmpleadoRol asignacion) {
        boolean esCamarero = asignacion != null
                && asignacion.getIdRol() != null
                && Boolean.TRUE.equals(asignacion.getIdRol().getActivo())
                && normalizar(asignacion.getIdRol().getNombre()).equals(ROL_PERMITIDO);
        return esCamarero ? null : "Solo un empleado con rol camarero puede tomar la orden.";
    }

    /**
     * La orden necesita fecha y debe tomarla un camarero.
     *
     * @return el mensaje de error, o null si la orden está bien
     */
    public String validarOrden(Orden orden) {
        if (orden == null) {
            return null;
        }
        if (orden.getFechaCreacion() == null) {
            return "La fecha de la orden es obligatoria.";
        }
        return validarCamarero(orden.getIdEmpleadoRol());
    }

    /**
     * Una orden con productos no se puede eliminar.
     *
     * @return el mensaje de error, o null si se puede eliminar
     */
    public String validarEliminar(Orden orden) {
        if (orden != null && countProductos(orden) > 0) {
            return "La orden tiene productos.";
        }
        return null;
    }

    /**
     * Un descuento solo se puede aplicar si la fecha de la orden está dentro de
     * su vigencia.
     *
     * @return el mensaje de error, o null si el descuento aplica en esa fecha
     */
    public String validarDescuentoEnFecha(Orden orden, Descuento descuento) {
        if (descuento == null) {
            return "Seleccione un descuento.";
        }
        Date fechaOrden = (orden == null) ? null : orden.getFechaCreacion();
        if (!estaDentroDeRango(fechaOrden, descuento.getFechaDesde(), descuento.getFechaHasta())) {
            return "El descuento " + descuento.getNombre() + " no aplica: la fecha de la orden ("
                    + formatoFecha(fechaOrden) + ") está fuera de su rango. No se aplicó a ningún producto.";
        }
        return null;
    }

    /**
     * Estado de preparación de la orden; RECIBIDA si nadie lo ha cambiado.
     */
    public String buscarEstado(UUID idOrden) {
        return ESTADO_POR_ORDEN.getOrDefault(idOrden, ESTADOS_PREPARACION.get(0));
    }

    /**
     * El estado debe ser uno de ESTADOS_PREPARACION y no puede regresar a uno
     * anterior (una orden entregada no vuelve a preparación).
     *
     * @return el mensaje de error, o null si el cambio es válido
     */
    public String validarEstado(UUID idOrden, String estadoNuevo) {
        int nuevo = (estadoNuevo == null) ? -1 : ESTADOS_PREPARACION.indexOf(estadoNuevo);
        if (nuevo < 0) {
            return "El estado debe ser uno de " + String.join(", ", ESTADOS_PREPARACION) + ".";
        }
        if (nuevo < ESTADOS_PREPARACION.indexOf(buscarEstado(idOrden))) {
            return "La orden ya está " + buscarEstado(idOrden) + " y no puede regresar a " + estadoNuevo + ".";
        }
        return null;
    }

    /**
     * Guarda el estado (llamar antes a validarEstado).
     */
    public void cambiarEstado(UUID idOrden, String estadoNuevo) {
        ESTADO_POR_ORDEN.put(idOrden, estadoNuevo);
    }

    // Quita tildes, pasa a minúsculas y quita espacios de los extremos.
    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .trim();
    }

    private static String formatoFecha(Date fecha) {
        return (fecha == null) ? "sin fecha" : new SimpleDateFormat("dd/MM/yyyy").format(fecha);
    }

}
