/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.FacturaOrdenProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.OrdenProducto;

/**
 *
 * @author daniel
 */
public interface FacturaOrdenProductoDAO extends GenericDAO<FacturaOrdenProducto, UUID> {

    List<FacturaOrdenProducto> findByPrecio(BigDecimal precio);

    List<FacturaOrdenProducto> findByObservaciones(String observaciones);

    List<FacturaOrdenProducto> findByFactura(Factura factura);

    List<FacturaOrdenProducto> findByOrdenProducto(OrdenProducto ordenProducto);
}