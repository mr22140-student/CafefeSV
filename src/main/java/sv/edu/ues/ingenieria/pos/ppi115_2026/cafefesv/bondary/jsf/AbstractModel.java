package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.List;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;

public abstract class AbstractModel<Entidad, ID> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ESTADO_NINGUNO = "NINGUNO";
    public static final String ESTADO_CREAR = "CREAR";
    public static final String ESTADO_MODIFICAR = "MODIFICAR";

    protected List<Entidad> registros;
    protected Entidad registro;
    protected Entidad filaSeleccionada;
    protected String estado = ESTADO_NINGUNO;

    protected abstract GenericDAO<Entidad, ID> getDAO();

    protected abstract Entidad crearNuevo();

    public void iniciar() {
        GenericDAO<Entidad, ID> dao = getDAO();
        if (dao != null) {
            this.registros = dao.findRange(0, 1000);
        }
        this.estado = ESTADO_NINGUNO;
    }

    public void btnNuevoHandler() {
        this.registro = crearNuevo();
        this.estado = ESTADO_CREAR;
    }

    public void btnNuevoHandler(ActionEvent ae) {
        btnNuevoHandler();
    }

    public void btnCancelarHandler() {
        this.registro = null;
        iniciar();
    }

    public void btnCancelarHandler(ActionEvent ae) {
        btnCancelarHandler();
    }

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

    public void btnEliminarHandler() {
        FacesMessage mensaje;
        if (this.registro != null) {
            try {
                getDAO().delete(this.registro);
                mensaje = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registro eliminado con éxito", null);
                this.registro = null;
                iniciar();
            } catch (Exception ex) {
                mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se pudo eliminar el registro", null);
            }
        } else {
            mensaje = new FacesMessage(FacesMessage.SEVERITY_WARN, "El registro no existe", null);
        }
        FacesContext.getCurrentInstance().addMessage(null, mensaje);
    }

    public void btnEliminarHandler(ActionEvent ae) {
        btnEliminarHandler();
    }

    public void seleccionarRegistro(Entidad entidad) {
        this.registro = entidad;
        if (this.registro != null) {
            this.estado = ESTADO_MODIFICAR;
        }
    }

    /**
     * Revisa el resultado de una regla del DAO. Si trae un error, lo muestra
     * con el título indicado.
     *
     * @param titulo título del aviso, por ejemplo "No se puede inactivar"
     * @param error mensaje que devolvió el DAO, o null si la regla se cumple
     * @return true si no hubo error y se puede continuar
     */
    protected boolean sinError(String titulo, String error) {
        if (error == null) {
            return true;
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, titulo, error));
        return false;
    }

    public List<Entidad> getRegistros() {
        return registros;
    }

    public void setRegistros(List<Entidad> registros) {
        this.registros = registros;
    }

    public Entidad getRegistro() {
        return registro;
    }

    public void setRegistro(Entidad registro) {
        this.registro = registro;
    }

    public Entidad getFilaSeleccionada() {
        return filaSeleccionada;
    }

    public void setFilaSeleccionada(Entidad filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
