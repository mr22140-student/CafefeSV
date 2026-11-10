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
 * El pago no guarda montos: los montos estan en PagoDetalle. Al marcar un pago
 * como COMPLETADO la factura pasa a PAGADA; al ANULAR un pago completado, la
 * factura regresa a PENDIENTE.
 */
@Named("pagoModel")
@ViewScoped
public class PagoModel extends AbstractModel<Pago, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private PagoDAO pagoDAO;

    @Inject
    private FacturaDAO facturaDAO;

    /**
     * Facturas PENDIENTES que se pueden elegir en el formulario.
     */
    private List<Factura> listaFacturaPendiente = new ArrayList<>();

    /**
     * Totales calculados una vez por carga (evita consultas por cada fila).
     */
    private Map<UUID, BigDecimal> pagadoPorPago = new HashMap<>();
    private Map<UUID, BigDecimal> totalPorFactura = new HashMap<>();

    /**
     * Estado que tenia el pago en la BD al seleccionarlo.
     */
    private String estadoOriginal;

    @PostConstruct
    public void init() {
        cargarFacturas();
        iniciar();
    }

    private void cargarFacturas() {
        listaFacturaPendiente = new ArrayList<>(facturaDAO.buscarPendientes());
    }

    /**
     * Carga los pagos y calcula sus totales.
     */
    @Override
    public void iniciar() {
        super.iniciar();
        pagadoPorPago.clear();
        totalPorFactura.clear();
        if (registros != null) {
            for (Pago pago : registros) {
                pagadoPorPago.put(pago.getIdPago(), pagoDAO.sumaMontos(pago));
                Factura factura = pago.getIdFactura();
                if (factura != null && !totalPorFactura.containsKey(factura.getIdFactura())) {
                    totalPorFactura.put(factura.getIdFactura(), pagoDAO.totalFactura(factura));
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
        nuevo.setEstado(PagoDAO.PENDIENTE);
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
            registro.setEstado(PagoDAO.PENDIENTE); // todo pago nuevo inicia pendiente
        }
        if (sinError("Pago no válido", pagoDAO.validarPago(registro))) {
            super.btnCrearHandler();
        }
    }

    @Override
    public void btnModificarHandler() {
        if (registro == null) {
            super.btnModificarHandler();
            return;
        }
        String estadoNuevo = registro.getEstado();
        if (!sinError("No se puede modificar", pagoDAO.validarCambioDeEstado(estadoOriginal, estadoNuevo))
                || !sinError("Pago no válido", pagoDAO.validarPago(registro))
                || !sinError("No se puede completar", pagoDAO.validarCompletar(registro, estadoOriginal))) {
            return;
        }

        Factura factura = registro.getIdFactura();
        String estadoAnterior = estadoOriginal;
        super.btnModificarHandler();

        if (registro == null) { // AbstractModel limpia registro solo si guardo con exito
            actualizarEstadoFactura(factura, pagoDAO.calcularEstadoFactura(estadoAnterior, estadoNuevo));
            cargarFacturas();
            iniciar();
            estadoOriginal = null;
        }
    }

    @Override
    public void btnEliminarHandler() {
        if (sinError("No se puede eliminar", pagoDAO.validarEliminar(registro, estadoOriginal))) {
            super.btnEliminarHandler();
        }
    }

    @Override
    public void btnCancelarHandler() {
        super.btnCancelarHandler();
        estadoOriginal = null;
    }

    /**
     * Guarda el nuevo estado de la factura que calculó el DAO (null = no
     * cambia).
     */
    private void actualizarEstadoFactura(Factura factura, String estadoFactura) {
        if (factura == null || estadoFactura == null) {
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

    /**
     * "dd/MM/yyyy HH:mm - Cliente".
     */
    public String etiquetaFactura(Factura factura) {
        if (factura == null) {
            return "Sin asignación";
        }
        String fecha = (factura.getFechaFacturacion() != null)
                ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format(factura.getFechaFacturacion())
                : "Sin fecha";
        String cliente = (factura.getCliente() != null) ? factura.getCliente() : "Sin cliente";
        return fecha + " - " + cliente;
    }

    public BigDecimal totalFactura(Factura factura) {
        if (factura == null) {
            return BigDecimal.ZERO;
        }
        return totalPorFactura.getOrDefault(factura.getIdFactura(), BigDecimal.ZERO);
    }

    public BigDecimal pagado(Pago pago) {
        if (pago == null) {
            return BigDecimal.ZERO;
        }
        return pagadoPorPago.getOrDefault(pago.getIdPago(), BigDecimal.ZERO);
    }

    /**
     * Color de la etiqueta del estado en la tabla.
     */
    public String severidadEstado(String estadoPago) {
        if (PagoDAO.COMPLETADO.equals(estadoPago)) {
            return "success";
        }
        if (PagoDAO.ANULADO.equals(estadoPago)) {
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
        return PagoDAO.ESTADOS;
    }
}
