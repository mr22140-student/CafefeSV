/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Empleado;

/**
 *
 * @author daniel
 */
public interface EmpleadoDAO extends GenericDAO<Empleado, UUID> {

    List<Empleado> findByNombre(String nombre);

    List<Empleado> findByApellido(String apellido);

    List<Empleado> findByActivo(Boolean activo);

    List<Empleado> findByComentarios(String comentarios);
    
   Empleado obtenerPorId(UUID id);

}