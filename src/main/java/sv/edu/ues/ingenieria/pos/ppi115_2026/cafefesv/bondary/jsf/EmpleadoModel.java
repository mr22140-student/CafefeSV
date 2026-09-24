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
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Empleado;

/**
 *
 * @author valen
 */
@Named("empleadoModel")
@ViewScoped
public class EmpleadoModel extends AbstractModel<Empleado, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private EmpleadoDAO empleadoDAO;

    @PostConstruct
    public void init() {
        iniciar();
    }

    @Override
    protected GenericDAO<Empleado, UUID> getDAO() {
        return empleadoDAO;
    }

    @Override
    protected Empleado crearNuevo() {
        Empleado nuevo = new Empleado();
        nuevo.setIdEmpleado(UUID.randomUUID());
        nuevo.setActivo(true);
        return nuevo;
    }
}