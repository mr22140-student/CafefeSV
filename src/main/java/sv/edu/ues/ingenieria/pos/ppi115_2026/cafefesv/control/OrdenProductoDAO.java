/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Orden;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.OrdenProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;

/**
 *
 * @author daniel
 */
public interface OrdenProductoDAO extends GenericDAO<OrdenProducto, UUID> {

    List<OrdenProducto> findByPrecio(BigDecimal precio);

    List<OrdenProducto> findByObservaciones(String observaciones);

    List<OrdenProducto> findByOrden(Orden orden);

    List<OrdenProducto> findByProducto(Producto producto);
}