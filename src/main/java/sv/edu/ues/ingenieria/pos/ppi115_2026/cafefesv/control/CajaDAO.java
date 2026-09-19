/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caja;

/**
 *
 * @author daniel
 */

public interface CajaDAO extends GenericDAO<Caja, UUID> {

    List<Caja> findByNombre(String nombre);

    List<Caja> findByActivo(Boolean activo);

    List<Caja> findByObservaciones(String observaciones);
}