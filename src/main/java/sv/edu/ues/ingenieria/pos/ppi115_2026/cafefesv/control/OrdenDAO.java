/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Orden;

/**
 *
 * @author daniel
 */
public interface OrdenDAO extends GenericDAO<Orden, UUID> {

    List<Orden> findByFechaCreacion(Date fechaCreacion);

    List<Orden> findByEmpleadoRol(EmpleadoRol empleadoRol);
}