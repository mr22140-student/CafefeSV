package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoRolDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.OrdenDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Empleado;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Orden;

@Named("ordenModel")
@ViewScoped
public class OrdenModel extends AbstractModel<Orden, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private OrdenDAO ordenDAO;

    @Inject
    private EmpleadoRolDAO empleadoRolDAO;

    @EJB
    private EmpleadoDAO empleadoDAO;

    private UUID empleadoRolIdSeleccionado;

    @PostConstruct
    public void init() {
        iniciar();
    }

    @Override
    protected GenericDAO<Orden, UUID> getDAO() {
        return ordenDAO;
    }

    @Override
    protected Orden crearNuevo() {
        Orden nueva = new Orden();
        nueva.setIdOrden(UUID.randomUUID());
        nueva.setFechaCreacion(new Date());
        nueva.setIdEmpleadoRol(null);
        this.empleadoRolIdSeleccionado = null;
        return nueva;
    }

    public UUID getEmpleadoRolIdSeleccionado() {
        return empleadoRolIdSeleccionado;
    }

    public void setEmpleadoRolIdSeleccionado(UUID empleadoRolIdSeleccionado) {
        this.empleadoRolIdSeleccionado = empleadoRolIdSeleccionado;
    }

    public String obtenerTextoEmpleadoRol(EmpleadoRol er) {
        if (er == null || er.getIdEmpleado() == null) {
            return "";
        }
        List<Empleado> todosEmpleados = empleadoDAO.findRange(0, 1000);
        String nombreEmp = "";
        String apellidoEmp = "";
        
        if (todosEmpleados != null) {
            for (Empleado e : todosEmpleados) {
                if (e != null && e.getIdEmpleado().equals(er.getIdEmpleado())) {
                    nombreEmp = e.getNombre() != null ? e.getNombre() : "";
                    apellidoEmp = e.getApellido() != null ? e.getApellido() : "";
                    break;
                }
            }
        }
        
        String rolNombre = (er.getIdRol() != null && er.getIdRol().getNombre() != null) ? er.getIdRol().getNombre() : "Sin rol";
        return nombreEmp + " " + apellidoEmp + " (" + rolNombre + ")";
    }

    public List<EmpleadoRol> completarEmpleadoRol(String query) {
        String queryLowerCase = query != null ? query.toLowerCase().trim() : "";
        List<EmpleadoRol> todosER = empleadoRolDAO.findRange(0, 1000);
        List<Empleado> todosEmpleados = empleadoDAO.findRange(0, 1000);
        List<EmpleadoRol> filtrados = new ArrayList<>();

        if (todosER != null && todosEmpleados != null) {
            for (EmpleadoRol er : todosER) {
                if (er != null && er.getIdEmpleado() != null) {
                    Empleado empEncontrado = null;
                    for (Empleado e : todosEmpleados) {
                        if (e != null && e.getIdEmpleado().equals(er.getIdEmpleado())) {
                            empEncontrado = e;
                            break;
                        }
                    }

                    if (empEncontrado != null) {
                        String nombre = empEncontrado.getNombre() != null ? empEncontrado.getNombre() : "";
                        String apellido = empEncontrado.getApellido() != null ? empEncontrado.getApellido() : "";
                        String nombreCompleto = (nombre + " " + apellido).toLowerCase();

                        if (queryLowerCase.isEmpty() || nombreCompleto.contains(queryLowerCase)) {
                            filtrados.add(er);
                        }
                    }
                }
            }
        }
        return filtrados;
    }

    @Override
    public void seleccionarRegistro(Orden registroSeleccionado) {
        super.seleccionarRegistro(registroSeleccionado);
        if (this.registro != null && this.registro.getIdEmpleadoRol() != null) {
            this.empleadoRolIdSeleccionado = this.registro.getIdEmpleadoRol().getIdEmpleadoRol();
        } else {
            this.empleadoRolIdSeleccionado = null;
        }
    }

    private EmpleadoRol buscarEmpleadoRolPorId(UUID id) {
        if (id == null) {
            return null;
        }
        List<EmpleadoRol> lista = empleadoRolDAO.findRange(0, 1000);
        if (lista != null) {
            for (EmpleadoRol er : lista) {
                if (er != null && er.getIdEmpleadoRol() != null && er.getIdEmpleadoRol().equals(id)) {
                    return er;
                }
            }
        }
        return null;
    }

    @Override
    public void btnCrearHandler() {
        if (this.empleadoRolIdSeleccionado != null) {
            if (this.registro == null) {
                this.registro = crearNuevo();
            }
            EmpleadoRol erEncontrado = buscarEmpleadoRolPorId(this.empleadoRolIdSeleccionado);
            this.registro.setIdEmpleadoRol(erEncontrado);
        }
        
        if (this.registro == null || this.registro.getIdEmpleadoRol() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe seleccionar un empleado y rol válido."));
            return;
        }
        
        super.btnCrearHandler();
    }

    @Override
    public void btnModificarHandler() {
        if (this.registro != null && this.empleadoRolIdSeleccionado != null) {
            EmpleadoRol erEncontrado = buscarEmpleadoRolPorId(this.empleadoRolIdSeleccionado);
            this.registro.setIdEmpleadoRol(erEncontrado);
        }
        
        if (this.registro == null || this.registro.getIdEmpleadoRol() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe seleccionar un empleado y rol válido."));
            return;
        }
        
        super.btnModificarHandler();
    }
}