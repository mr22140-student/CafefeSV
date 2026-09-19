/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;

/**
 *
 * @author daniel
 */
public interface DescuentoDAO extends GenericDAO<Descuento, UUID> {

    List<Descuento> findByNombre(String nombre);

    List<Descuento> findByFechaDesde(Date fechaDesde);

    List<Descuento> findByFechaHasta(Date fechaHasta);

    List<Descuento> findByObservaciones(String observaciones);

    /**
     * Descuentos vigentes en la fecha indicada, es decir aquellos donde
     * {@code fecha} cae entre {@code fechaDesde} y {@code fechaHasta}.
     */
    List<Descuento> findVigentes(Date fecha);
}