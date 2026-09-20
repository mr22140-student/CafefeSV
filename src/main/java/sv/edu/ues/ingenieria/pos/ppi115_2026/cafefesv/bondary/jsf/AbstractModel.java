package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.List;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;

public abstract class AbstractModel<T, ID> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ESTADO_NINGUNO = "NINGUNO";
    public static final String ESTADO_CREAR = "CREAR";
    public static final String ESTADO_MODIFICAR = "MODIFICAR";

    protected List<T> registros;
    protected T registro;
    protected String estado = ESTADO_NINGUNO;

    protected abstract GenericDAO<T, ID> getDAO();
    protected abstract T crearNuevo();

    public void iniciar() {
        GenericDAO<T, ID> dao = getDAO();
        if (dao != null) {
            this.registros = dao.findRange(0, 1000);
        }
        this.estado = ESTADO_NINGUNO;
    }

    // --- HANDLER NUEVO ---
    public void btnNuevoHandler() {
        this.registro = crearNuevo();
        this.estado = ESTADO_CREAR;
    }

    public void btnNuevoHandler(ActionEvent ae) {
        btnNuevoHandler();
    }

    // --- HANDLER CANCELAR ---
    public void btnCancelarHandler() {
        this.registro = null;
        this.estado = ESTADO_NINGUNO;
    }

    public void btnCancelarHandler(ActionEvent ae) {
        btnCancelarHandler();
    }

    // --- HANDLER CREAR / GUARDAR ---
    public void btnCrearHandler() {
        FacesMessage mensaje;
        if (this.registro != null) {
            try {
                getDAO().save(this.registro);
                mensaje = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registro creado con éxito", null);
                this.registro = null;
                iniciar();
            } catch (Exception ex) {
                mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se pudo crear el registro", null);
            }
        } else {
            mensaje = new FacesMessage(FacesMessage.SEVERITY_WARN, "El registro no puede ser creado", null);
        }
        FacesContext.getCurrentInstance().addMessage(null, mensaje);
    }

    public void btnCrearHandler(ActionEvent ae) {
        btnCrearHandler();
    }

    // --- HANDLER MODIFICAR ---
    public void btnModificarHandler() {
        FacesMessage mensaje;
        if (this.registro != null) {
            try {
                getDAO().update(this.registro);
                mensaje = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registro modificado con éxito", null);
                this.registro = null;
                iniciar();
            } catch (Exception ex) {
                mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se pudo modificar el registro", null);
            }
        } else {
            mensaje = new FacesMessage(FacesMessage.SEVERITY_WARN, "El registro no existe", null);
        }
        FacesContext.getCurrentInstance().addMessage(null, mensaje);
    }

    public void btnModificarHandler(ActionEvent ae) {
        btnModificarHandler();
    }

    public void seleccionarRegistro(T entidad) {
        this.registro = entidad;
        if (this.registro != null) {
            this.estado = ESTADO_MODIFICAR;
        }
    }

    // Getters y Setters
    public List<T> getRegistros() { return registros; }
    public void setRegistros(List<T> registros) { this.registros = registros; }
    public T getRegistro() { return registro; }
    public void setRegistro(T registro) { this.registro = registro; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}