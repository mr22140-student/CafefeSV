/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;

/**
 *
 * @author daniel
 */
public interface ProductoDAO extends GenericDAO<Producto, UUID> {

    List<Producto> findByNombre(String nombre);

    List<Producto> findByActivo(Boolean activo);

    List<Producto> findByComentarios(String comentarios);
}