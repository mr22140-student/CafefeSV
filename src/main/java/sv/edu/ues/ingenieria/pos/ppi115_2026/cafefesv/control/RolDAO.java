/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Rol;

/**
 *
 * @author daniel
 */
public interface RolDAO extends GenericDAO<Rol, UUID> {

    List<Rol> findByNombre(String nombre);

    List<Rol> findByActivo(Boolean activo);

    List<Rol> findByObservaciones(String observaciones);
}