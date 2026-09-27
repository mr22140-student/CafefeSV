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

    public static final String EFECTIVO = "EFECTIVO";
    public static final String TARJETA = "TARJETA";
    public static final String TRANSFERENCIA = "TRANSFERENCIA";
    public static final List<String> TIPOS_PAGO = Arrays.asList(EFECTIVO, TARJETA, TRANSFERENCIA);

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
        listaPago = new ArrayList<>();
        listaPagoPendiente = new ArrayList<>();
        List<Pago> pagos = pagoDAO.findRange(0, 1000);
        if (pagos != null) {
            for (Pago p : pagos) {
                listaPago.add(p);
                if (PagoModel.PENDIENTE.equals(p.getEstado())) {
                    listaPagoPendiente.add(p);
                }
            }
        }
    }

    /** Carga la tabla (filtrada si hay pago elegido) y el resumen. */
    @Override
    public void iniciar() {
        if (pagoFiltro != null) {
            registros = pagoDetalleDAO.findByPago(pagoFiltro);
            estado = ESTADO_NINGUNO;
            totalFiltro = totalFactura(pagoFiltro);
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
        nuevo.setTipoPago(EFECTIVO);
        // Si el pago filtrado esta pendiente, ya viene seleccionado con el saldo como monto
        if (pagoFiltro != null && PagoModel.PENDIENTE.equals(pagoFiltro.getEstado())) {
            nuevo.setIdPago(pagoFiltro);
            BigDecimal saldo = saldo(pagoFiltro, BigDecimal.ZERO);
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
            BigDecimal saldo = saldo(registro.getIdPago(), montoActualEnBD(registro.getIdPago()));
            registro.setMonto(saldo.signum() > 0 ? saldo : null);
        }
    }

    // ================= Crear / Modificar / Eliminar =================

    @Override
    public void btnCrearHandler() {
        Pago pago = (registro != null) ? registro.getIdPago() : null;
        if (esValido()) {
            super.btnCrearHandler();
            avisarSiCompleto(pago);
        }
    }

    @Override
    public void btnModificarHandler() {
        if (!pagoEditable(pagoOriginal)) {
            return;
        }
        Pago pago = (registro != null) ? registro.getIdPago() : null;
        if (esValido()) {
            super.btnModificarHandler();
            avisarSiCompleto(pago);
        }
    }

    @Override
    public void btnEliminarHandler() {
        if (registro != null && !pagoEditable(registro.getIdPago())) {
            return;
        }
        super.btnEliminarHandler();
    }

    @Override
    public void btnCancelarHandler() {
        super.btnCancelarHandler();
        pagoOriginal = null;
        montoOriginal = null;
    }

    private boolean esValido() {
        if (registro == null) {
            return true; // AbstractModel muestra su propio mensaje
        }
        Pago pago = registro.getIdPago();
        if (pago == null) {
            mensaje(FacesMessage.SEVERITY_WARN, "Debe seleccionar el pago.");
            return false;
        }
        if (!pagoEditable(pago)) {
            return false;
        }
        if (!TIPOS_PAGO.contains(registro.getTipoPago())) {
            mensaje(FacesMessage.SEVERITY_WARN, "Debe seleccionar el tipo de pago.");
            return false;
        }
        if (registro.getMonto() == null || registro.getMonto().signum() <= 0) {
            mensaje(FacesMessage.SEVERITY_WARN, "El monto debe ser mayor a cero.");
            return false;
        }
        String ref = (registro.getReferenciaExterna() != null) ? registro.getReferenciaExterna().trim() : "";
        registro.setReferenciaExterna(ref.isEmpty() ? null : ref);
        if (!EFECTIVO.equals(registro.getTipoPago()) && ref.isEmpty()) {
            mensaje(FacesMessage.SEVERITY_WARN,
                    "Para " + registro.getTipoPago() + " debe indicar la referencia (número de autorización o transferencia).");
            return false;
        }
        // La suma de montos no puede pasar del total de la factura
        BigDecimal saldo = saldo(pago, montoActualEnBD(pago));
        if (registro.getMonto().compareTo(saldo) > 0) {
            mensaje(FacesMessage.SEVERITY_WARN, "El monto supera lo que falta por pagar ($" + saldo + ").");
            return false;
        }
        return true;
    }

    /** Solo se pueden tocar montos de pagos PENDIENTES. */
    private boolean pagoEditable(Pago p) {
        if (p != null && !PagoModel.PENDIENTE.equals(p.getEstado())) {
            mensaje(FacesMessage.SEVERITY_WARN,
                    "El pago está " + p.getEstado() + "; solo se pueden cambiar montos de pagos PENDIENTES.");
            return false;
        }
        return true;
    }

    /** Si ya se cubrio el total, recuerda completar el pago. */
    private void avisarSiCompleto(Pago pago) {
        if (registro == null && pago != null   // registro null = se guardo con exito
                && saldo(pago, BigDecimal.ZERO).signum() <= 0) {
            mensaje(FacesMessage.SEVERITY_INFO,
                    "Ya se cubrió el total de la factura. Puede marcar el pago como COMPLETADO en la página de Pagos.");
        }
    }

    // ================= Calculos =================

    /**
     * Lo que falta por pagar. "descontar" es el monto del registro que se esta
     * modificando (ya incluido en la BD), para no contarlo dos veces.
     */
    private BigDecimal saldo(Pago pago, BigDecimal descontar) {
        BigDecimal pagado = pagoDAO.sumaMontos(pago).subtract(descontar);
        return totalFactura(pago).subtract(pagado);
    }

    /** Monto que el registro en edicion ya tiene guardado en ese pago (0 si es nuevo). */
    private BigDecimal montoActualEnBD(Pago pago) {
        if (ESTADO_MODIFICAR.equals(estado) && montoOriginal != null && pago != null && pago.equals(pagoOriginal)) {
            return montoOriginal;
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal totalFactura(Pago pago) {
        return (pago != null && pago.getIdFactura() != null)
                ? pagoDAO.totalFactura(pago.getIdFactura())
                : BigDecimal.ZERO;
    }

    // ================= Etiquetas =================

    /** "dd/MM/yyyy HH:mm - Cliente (ESTADO)". */
    public String etiquetaPago(Pago p) {
        if (p == null) {
            return "Sin asignación";
        }
        Factura f = p.getIdFactura();
        String texto = "Sin factura";
        if (f != null) {
            String fecha = (f.getFechaFacturacion() != null)
                    ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format(f.getFechaFacturacion())
                    : "Sin fecha";
            texto = fecha + " - " + (f.getCliente() != null ? f.getCliente() : "Sin cliente");
        }
        return texto + " (" + p.getEstado() + ")";
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
        return TIPOS_PAGO;
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