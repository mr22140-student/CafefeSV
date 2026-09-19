/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Pago;

/**
 *
 * @author daniel
 */
public interface PagoDAO extends GenericDAO<Pago, UUID> {

    List<Pago> findByEstado(String estado);

    List<Pago> findByObservaciones(String observaciones);

    List<Pago> findByFactura(Factura factura);
}