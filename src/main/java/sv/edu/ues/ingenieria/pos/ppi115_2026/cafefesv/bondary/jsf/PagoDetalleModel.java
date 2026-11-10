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
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.PagoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.PagoDetalleDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Pago;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.PagoDetalle;

/**
 * Montos de un pago (ej. $5 en efectivo + $10 con tarjeta).
 * Solo se pueden agregar, modificar o quitar montos de pagos PENDIENTES.
 */
@Named("pagoDetalleModel")
@ViewScoped
public class PagoDetalleModel extends AbstractModel<PagoDetalle, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private PagoDetalleDAO pagoDetalleDAO;

    @Inject
    private PagoDAO pagoDAO;

    /** Todos los pagos (para el filtro). */
    private List<Pago> listaPago = new ArrayList<>();

    /** Solo pagos PENDIENTES (para el formulario). */
    private List<Pago> listaPagoPendiente = new ArrayList<>();

    /** Pago elegido arriba de la tabla; null = todos. */
    private Pago pagoFiltro;

    /** Resumen del pago filtrado. */
    private BigDecimal totalFiltro = BigDecimal.ZERO;
    private BigDecimal pagadoFiltro = BigDecimal.ZERO;

    /** Valores que tenia el registro al seleccionarlo. */
    private Pago pagoOriginal;
    private BigDecimal montoOriginal;

    @PostConstruct
    public void init() {
        cargarPagos();
        iniciar();
    }

    private void cargarPagos() {
        List<Pago> pagos = pagoDAO.findRange(0, 1000);
        listaPago = (pagos != null) ? new ArrayList<>(pagos) : new ArrayList<>();
        listaPagoPendiente = new ArrayList<>(pagoDAO.findByEstado(PagoDAO.PENDIENTE));
    }

    /** Carga la tabla (filtrada si hay pago elegido) y el resumen. */
    @Override
    public void iniciar() {
        if (pagoFiltro != null) {
            registros = pagoDetalleDAO.findByPago(pagoFiltro);
            estado = ESTADO_NINGUNO;
            totalFiltro = (pagoFiltro.getIdFactura() != null)
                    ? pagoDAO.totalFactura(pagoFiltro.getIdFactura())
                    : BigDecimal.ZERO;
            pagadoFiltro = pagoDAO.sumaMontos(pagoFiltro);
        } else {
            super.iniciar();
            totalFiltro = BigDecimal.ZERO;
            pagadoFiltro = BigDecimal.ZERO;
        }
    }

    @Override
    protected GenericDAO<PagoDetalle, UUID> getDAO() {
        return pagoDetalleDAO;
    }

    @Override
    protected PagoDetalle crearNuevo() {
        PagoDetalle nuevo = new PagoDetalle(UUID.randomUUID());
        nuevo.setTipoPago(PagoDetalleDAO.EFECTIVO);
        // Si el pago filtrado esta pendiente, ya viene seleccionado con el saldo como monto
        if (pagoFiltro != null && PagoDAO.PENDIENTE.equals(pagoFiltro.getEstado())) {
            nuevo.setIdPago(pagoFiltro);
            BigDecimal saldo = pagoDAO.calcularSaldo(pagoFiltro, BigDecimal.ZERO);
            if (saldo.signum() > 0) {
                nuevo.setMonto(saldo);
            }
        }
        pagoOriginal = null;
        montoOriginal = null;
        return nuevo;
    }

    @Override
    public void seleccionarRegistro(PagoDetalle entidad) {
        super.seleccionarRegistro(entidad);
        if (registro == null) {
            return;
        }
        pagoOriginal = registro.getIdPago();
        montoOriginal = registro.getMonto();
        if (registro.getIdPago() != null && !listaPagoPendiente.contains(registro.getIdPago())) {
            listaPagoPendiente.add(0, registro.getIdPago());
        }
    }

    // ================= Handlers de la vista =================

    public void filtrarHandler() {
        iniciar();
    }

    /** Al elegir el pago, propone como monto lo que falta por pagar. */
    public void pagoSeleccionadoHandler() {
        if (registro != null && registro.getIdPago() != null) {
            BigDecimal saldo = pagoDAO.calcularSaldo(registro.getIdPago(), montoActualEnBD(registro.getIdPago()));
            registro.setMonto(saldo.signum() > 0 ? saldo : null);
        }
    }

    // ================= Crear / Modificar / Eliminar =================

    @Override
    public void btnCrearHandler() {
        Pago pago = (registro != null) ? registro.getIdPago() : null;
        if (montoValido()) {
            super.btnCrearHandler();
            avisarSiCompleto(pago);
        }
    }

    @Override
    public void btnModificarHandler() {
        if (!sinError("No se puede modificar", pagoDetalleDAO.validarPagoEditable(pagoOriginal))) {
            return;
        }
        Pago pago = (registro != null) ? registro.getIdPago() : null;
        if (montoValido()) {
            super.btnModificarHandler();
            avisarSiCompleto(pago);
        }
    }

    @Override
    public void btnEliminarHandler() {
        Pago pagoDelRegistro = (registro != null) ? registro.getIdPago() : null;
        if (sinError("No se puede eliminar", pagoDetalleDAO.validarPagoEditable(pagoDelRegistro))) {
            super.btnEliminarHandler();
        }
    }

    @Override
    public void btnCancelarHandler() {
        super.btnCancelarHandler();
        pagoOriginal = null;
        montoOriginal = null;
    }

    /**
     * Limpia la referencia que se escribió y le pide al DAO que revise el
     * monto contra lo que falta por pagar.
     */
    private boolean montoValido() {
        if (registro == null) {
            return true; // AbstractModel muestra su propio mensaje
        }
        String referencia = (registro.getReferenciaExterna() != null) ? registro.getReferenciaExterna().trim() : "";
        registro.setReferenciaExterna(referencia.isEmpty() ? null : referencia);
        BigDecimal saldo = (registro.getIdPago() != null)
                ? pagoDAO.calcularSaldo(registro.getIdPago(), montoActualEnBD(registro.getIdPago()))
                : null;
        return sinError("Monto no válido", pagoDetalleDAO.validarDetalle(registro, saldo));
    }

    /** Si ya se cubrio el total, recuerda completar el pago. */
    private void avisarSiCompleto(Pago pago) {
        if (registro == null && pago != null   // registro null = se guardo con exito
                && pagoDAO.calcularSaldo(pago, BigDecimal.ZERO).signum() <= 0) {
            mensaje(FacesMessage.SEVERITY_INFO,
                    "Ya se cubrió el total de la factura. Puede marcar el pago como COMPLETADO en la página de Pagos.");
        }
    }

    /** Monto que el registro en edicion ya tiene guardado en ese pago (0 si es nuevo). */
    private BigDecimal montoActualEnBD(Pago pago) {
        if (ESTADO_MODIFICAR.equals(estado) && montoOriginal != null && pago != null && pago.equals(pagoOriginal)) {
            return montoOriginal;
        }
        return BigDecimal.ZERO;
    }

    // ================= Etiquetas =================

    /** "dd/MM/yyyy HH:mm - Cliente (ESTADO)". */
    public String etiquetaPago(Pago pago) {
        if (pago == null) {
            return "Sin asignación";
        }
        Factura factura = pago.getIdFactura();
        String descripcion = "Sin factura";
        if (factura != null) {
            String fecha = (factura.getFechaFacturacion() != null)
                    ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format(factura.getFechaFacturacion())
                    : "Sin fecha";
            descripcion = fecha + " - " + (factura.getCliente() != null ? factura.getCliente() : "Sin cliente");
        }
        return descripcion + " (" + pago.getEstado() + ")";
    }

    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
    }

    // ================= Getters / Setters =================

    public List<Pago> getListaPago() {
        return listaPago;
    }

    public List<Pago> getListaPagoPendiente() {
        return listaPagoPendiente;
    }

    public List<String> getTiposPago() {
        return PagoDetalleDAO.TIPOS_PAGO;
    }

    public Pago getPagoFiltro() {
        return pagoFiltro;
    }

    public void setPagoFiltro(Pago pagoFiltro) {
        this.pagoFiltro = pagoFiltro;
    }

    public BigDecimal getTotalFiltro() {
        return totalFiltro;
    }

    public BigDecimal getPagadoFiltro() {
        return pagadoFiltro;
    }

    public BigDecimal getSaldoFiltro() {
        return totalFiltro.subtract(pagadoFiltro);
    }
}