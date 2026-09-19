/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoDescuento;

/**
 *
 * @author valen
 */

public interface TipoDescuentoDAO extends GenericDAO<TipoDescuento, UUID> {

    List<TipoDescuento> findByNombre(String nombre);

    List<TipoDescuento> findByActivo(Boolean activo);

    List<TipoDescuento> findByDescuentoMaximo(Integer descuentoMaximo);

    List<TipoDescuento> findByObservaciones(String observaciones);
}