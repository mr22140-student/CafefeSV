/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoProducto;

/**
 *
 * @author daniel
 */
public interface TipoProductoDAO extends GenericDAO<TipoProducto, UUID> {

    List<TipoProducto> findByNombre(String nombre);

    List<TipoProducto> findByActivo(Boolean activo);

    List<TipoProducto> findByObservaciones(String observaciones);
}