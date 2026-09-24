/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoRolDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.OrdenDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Orden;

/**
 *
 * @author valen
 */
@Named("ordenModel")
@ViewScoped
public class OrdenModel extends AbstractModel<Orden, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private OrdenDAO ordenDAO;

    @Inject
    private EmpleadoRolDAO empleadoRolDAO;

    private List<EmpleadoRol> listaEmpleadoRoles;

    @PostConstruct
    public void init() {
        iniciar();
        cargarCatalogos();
    }

    public void cargarCatalogos() {
        try {
            if (empleadoRolDAO != null) {
                this.listaEmpleadoRoles = empleadoRolDAO.findRange(0, 1000);
            }
        } catch (Exception e) {
            // Manejo básico de error de carga si el DAO o la tabla aún no están poblados
            this.listaEmpleadoRoles = null;
        }
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
        return nueva;
    }

    public List<EmpleadoRol> getListaEmpleadoRoles() {
        return listaEmpleadoRoles;
    }

    public void setListaEmpleadoRoles(List<EmpleadoRol> listaEmpleadoRoles) {
        this.listaEmpleadoRoles = listaEmpleadoRoles;
    }

    // Método de búsqueda con el AutoComplete// Método de búsqueda con el AutoComplete
    public List<EmpleadoRol> completarEmpleadoRol(String query) {
        String queryLowerCase = query != null ? query.toLowerCase() : "";
        List<EmpleadoRol> todos = empleadoRolDAO.findRange(0, 1000);
        List<EmpleadoRol> filtrados = new java.util.ArrayList<>();

        if (todos != null) {
            for (EmpleadoRol er : todos) {
                if (er != null && er.getIdEmpleado() != null) {
                    // Obtenemos el objeto empleado asegurando el tipo correcto
                    Object empObj = er.getIdEmpleado();

                    if (empObj instanceof sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Empleado) {
                        sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Empleado emp
                                = (sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Empleado) empObj;

                        String nombre = emp.getNombre() != null ? emp.getNombre() : "";
                        String apellido = emp.getApellido() != null ? emp.getApellido() : "";
                        String nombreCompleto = (nombre + " " + apellido).toLowerCase();

                        if (nombreCompleto.contains(queryLowerCase)) {
                            filtrados.add(er);
                        }
                    }
                }
            }
        }
        return filtrados;
    }
}
