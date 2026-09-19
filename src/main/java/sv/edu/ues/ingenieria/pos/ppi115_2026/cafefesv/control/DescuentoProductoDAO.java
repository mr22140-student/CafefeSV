/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.DescuentoProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;

/**
 *
 * @author daniel
 */
public interface DescuentoProductoDAO extends GenericDAO<DescuentoProducto, UUID> {

    List<DescuentoProducto> findByFechaDesde(Date fechaDesde);

    List<DescuentoProducto> findByFechaHasta(Date fechaHasta);

    List<DescuentoProducto> findByValor(Integer valor);

    List<DescuentoProducto> findByObservaciones(String observaciones);

    List<DescuentoProducto> findByDescuento(Descuento descuento);

    List<DescuentoProducto> findByProducto(Producto producto);
}