/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caja;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;

/**
 *
 * @author daniel
 */
public interface FacturaDAO extends GenericDAO<Factura, UUID> {

    List<Factura> findByCliente(String cliente);

    List<Factura> findByFechaFacturacion(Date fechaFacturacion);

    List<Factura> findByEstado(String estado);

    List<Factura> findByObservaciones(String observaciones);

    List<Factura> findByCaja(Caja caja);

    List<Factura> findByEmpleadoRol(EmpleadoRol empleadoRol);
}