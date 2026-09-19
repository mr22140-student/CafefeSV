/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Rol;

/**
 *
 * @author daniel
 */
public interface EmpleadoRolDAO extends GenericDAO<EmpleadoRol, UUID> {

    List<EmpleadoRol> findByActivo(Boolean activo);

    List<EmpleadoRol> findByObservaciones(String observaciones);

    List<EmpleadoRol> findByRol(Rol rol);
}
