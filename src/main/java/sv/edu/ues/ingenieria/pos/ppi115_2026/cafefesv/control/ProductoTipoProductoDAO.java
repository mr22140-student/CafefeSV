/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import java.util.Date;
import java.util.List;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoTipoProducto;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoProducto;

/**
 *
 * @author daniel
 */
  public interface ProductoTipoProductoDAO extends GenericDAO<ProductoTipoProducto, UUID> {

    List<ProductoTipoProducto> findByFechaCreacion(Date fechaCreacion);

    List<ProductoTipoProducto> findByObservaciones(String observaciones);

    List<ProductoTipoProducto> findByProducto(Producto producto);

    List<ProductoTipoProducto> findByTipoProducto(TipoProducto tipoProducto);
}