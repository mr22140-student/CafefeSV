package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.FacturaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.PagoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Pago;

/**
 * El pago no guarda montos: los montos estan en PagoDetalle.
 * Al marcar un pago como COMPLETADO la factura pasa a PAGADA;
 * al ANULAR un pago completado, la factura regresa a PENDIENTE.
 */
@Named("pagoModel")
@ViewScoped
public class PagoModel extends AbstractModel<Pago, UUID> {

    private static final long serialVersionUID = 1L;

    public static final String PENDIENTE = "PENDIENTE";
    public static final String COMPLETADO = "COMPLETADO";
    public static final String ANULADO = "ANULADO";
    public static final List<String> ESTADOS = Arrays.asList(PENDIENTE, COMPLETADO, ANULADO);

    @Inject
    private PagoDAO pagoDAO;

    @Inject
    private FacturaDAO facturaDAO;

    /** Facturas PENDIENTES que se pueden elegir en el formulario. */
    private List<Factura> listaFacturaPendiente = new ArrayList<>();

    /** Totales calculados una vez por carga (evita consultas por cada fila). */
    private Map<UUID, BigDecimal> pagadoPorPago = new HashMap<>();
    private Map<UUID, BigDecimal> totalPorFactura = new HashMap<>();

    /** Estado que tenia el pago en la BD al seleccionarlo. */
    private String estadoOriginal;

    @PostConstruct
    public void init() {
        cargarFacturas();
        iniciar();
    }

    private void cargarFacturas() {
        listaFacturaPendiente = new ArrayList<>();
        List<Factura> facturas = facturaDAO.findRange(0, 1000);
        if (facturas != null) {
            for (Factura f : facturas) {
                if (FacturaModel.PENDIENTE.equals(f.getEstado())) {
                    listaFacturaPendiente.add(f);
                }
            }
            listaFacturaPendiente.sort(Comparator.comparing(Factura::getFechaFacturacion,
                    Comparator.nullsLast(Comparator.reverseOrder())));
        }
    }

    /** Carga los pagos y calcula sus totales. */
    @Override
    public void iniciar() {
        super.iniciar();
        pagadoPorPago.clear();
        totalPorFactura.clear();
        if (registros != null) {
            for (Pago p : registros) {
                pagadoPorPago.put(p.getIdPago(), pagoDAO.sumaMontos(p));
                Factura f = p.getIdFactura();
                if (f != null && !totalPorFactura.containsKey(f.getIdFactura())) {
                    totalPorFactura.put(f.getIdFactura(), pagoDAO.totalFactura(f));
                }
            }
        }
    }

    @Override
    protected GenericDAO<Pago, UUID> getDAO() {
        return pagoDAO;
    }

    @Override
    protected Pago crearNuevo() {
        Pago nuevo = new Pago(UUID.randomUUID());
        nuevo.setEstado(PENDIENTE);
        estadoOriginal = null;
        return nuevo;
    }

    @Override
    public void seleccionarRegistro(Pago entidad) {
        super.seleccionarRegistro(entidad);
        estadoOriginal = (registro != null) ? registro.getEstado() : null;
        // Para que la factura se vea en el combo aunque ya no este PENDIENTE
        if (registro != null && registro.getIdFactura() != null
                && !listaFacturaPendiente.contains(registro.getIdFactura())) {
            listaFacturaPendiente.add(0, registro.getIdFactura());
        }
    }

    // ================= Crear / Modificar / Eliminar =================

    @Override
    public void btnCrearHandler() {
        if (registro != null) {
            registro.setEstado(PENDIENTE); // todo pago nuevo inicia pendiente
        }
        if (esValido()) {
            super.btnCrearHandler();
        }
    }

    @Override
    public void btnModificarHandler() {
        if (registro == null) {
            super.btnModificarHandler();
            return;
        }
        if (ANULADO.equals(estadoOriginal)) {
            mensaje(FacesMessage.SEVERITY_WARN, "No se puede modificar un pago ANULADO.");
            return;
        }
        String nuevoEstado = registro.getEstado();
        if (COMPLETADO.equals(estadoOriginal) && !COMPLETADO.equals(nuevoEstado) && !ANULADO.equals(nuevoEstado)) {
            mensaje(FacesMessage.SEVERITY_WARN, "Un pago COMPLETADO solo puede cambiarse a ANULADO.");
            return;
        }
        if (!esValido()) {
            return;
        }
        if (COMPLETADO.equals(nuevoEstado) && !COMPLETADO.equals(estadoOriginal) && !cubreTotal()) {
            return;
        }

        Factura factura = registro.getIdFactura();
        String anterior = estadoOriginal;
        super.btnModificarHandler();

        if (registro == null) { // AbstractModel limpia registro solo si guardo con exito
            actualizarEstadoFactura(factura, anterior, nuevoEstado);
            cargarFacturas();
            iniciar();
            estadoOriginal = null;
        }
    }

    @Override
    public void btnEliminarHandler() {
        if (registro != null) {
            if (COMPLETADO.equals(estadoOriginal)) {
                mensaje(FacesMessage.SEVERITY_WARN, "No se puede eliminar un pago COMPLETADO. Puede cambiarlo a ANULADO.");
                return;
            }
            if (pagoDAO.countDetalles(registro) > 0) {
                mensaje(FacesMessage.SEVERITY_WARN, "No se puede eliminar: el pago tiene montos registrados. Puede cambiarlo a ANULADO.");
                return;
            }
        }
        super.btnEliminarHandler();
    }

    @Override
    public void btnCancelarHandler() {
        super.btnCancelarHandler();
        estadoOriginal = null;
    }

    private boolean esValido() {
        if (registro == null) {
            return true; // AbstractModel muestra su propio mensaje
        }
        if (registro.getIdFactura() == null) {
            mensaje(FacesMessage.SEVERITY_WARN, "Debe seleccionar la factura.");
            return false;
        }
        if (!ESTADOS.contains(registro.getEstado())) {
            mensaje(FacesMessage.SEVERITY_WARN, "El estado no es válido.");
            return false;
        }
        // Una factura solo puede tener un pago activo (no anulado)
        if (!ANULADO.equals(registro.getEstado())) {
            UUID excluir = ESTADO_MODIFICAR.equals(estado) ? registro.getIdPago() : null;
            if (pagoDAO.existePagoActivo(registro.getIdFactura(), excluir)) {
                mensaje(FacesMessage.SEVERITY_WARN, "Esa factura ya tiene un pago registrado que no está anulado.");
                return false;
            }
        }
        return true;
    }

    /** Para marcar COMPLETADO, los montos deben cubrir el total de la factura. */
    private boolean cubreTotal() {
        BigDecimal total = pagoDAO.totalFactura(registro.getIdFactura());
        BigDecimal pagado = pagoDAO.sumaMontos(registro);
        if (total.signum() <= 0) {
            mensaje(FacesMessage.SEVERITY_WARN, "La factura no tiene productos; no se puede completar el pago.");
            return false;
        }
        if (pagado.compareTo(total) < 0) {
            mensaje(FacesMessage.SEVERITY_WARN, "No se puede completar: se han pagado $" + pagado
                    + " de $" + total + ". Registre los montos en Detalle de pago.");
            return false;
        }
        return true;
    }

    /** COMPLETADO -> factura PAGADA;  COMPLETADO a ANULADO -> factura PENDIENTE. */
    private void actualizarEstadoFactura(Factura factura, String anterior, String nuevo) {
        if (factura == null) {
            return;
        }
        String estadoFactura = null;
        if (COMPLETADO.equals(nuevo) && !COMPLETADO.equals(anterior)) {
            estadoFactura = FacturaModel.PAGADA;
        } else if (COMPLETADO.equals(anterior) && ANULADO.equals(nuevo)) {
            estadoFactura = FacturaModel.PENDIENTE;
        }
        if (estadoFactura == null) {
            return;
        }
        try {
            factura.setEstado(estadoFactura);
            facturaDAO.update(factura);
            mensaje(FacesMessage.SEVERITY_INFO, "La factura cambió a " + estadoFactura + ".");
        } catch (Exception ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "El pago se guardó, pero no se pudo actualizar el estado de la factura.");
        }
    }

    // ================= Etiquetas y totales =================

    /** "dd/MM/yyyy HH:mm - Cliente". */
    public String etiquetaFactura(Factura f) {
        if (f == null) {
            return "Sin asignación";
        }
        String fecha = (f.getFechaFacturacion() != null)
                ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format(f.getFechaFacturacion())
                : "Sin fecha";
        String cliente = (f.getCliente() != null) ? f.getCliente() : "Sin cliente";
        return fecha + " - " + cliente;
    }

    public BigDecimal totalFactura(Factura f) {
        if (f == null) {
            return BigDecimal.ZERO;
        }
        return totalPorFactura.getOrDefault(f.getIdFactura(), BigDecimal.ZERO);
    }

    public BigDecimal pagado(Pago p) {
        if (p == null) {
            return BigDecimal.ZERO;
        }
        return pagadoPorPago.getOrDefault(p.getIdPago(), BigDecimal.ZERO);
    }

    /** Color de la etiqueta del estado en la tabla. */
    public String severidadEstado(String estado) {
        if (COMPLETADO.equals(estado)) {
            return "success";
        }
        if (ANULADO.equals(estado)) {
            return "danger";
        }
        return "warning";
    }

    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
    }

    // ================= Getters =================

    public List<Factura> getListaFacturaPendiente() {
        return listaFacturaPendiente;
    }

    public List<String> getEstados() {
        return ESTADOS;
    }
}
