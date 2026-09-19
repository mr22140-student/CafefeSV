/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;

/**
 *
 * @author daniel
 */
public interface TipoCaracteristicaDAO extends GenericDAO<TipoCaracteristica, UUID> {

    List<TipoCaracteristica> findByNombre(String nombre);

    List<TipoCaracteristica> findByExpresionRegular(String expresionRegular);

    List<TipoCaracteristica> findByActivo(Boolean activo);

    List<TipoCaracteristica> findByObservaciones(String observaciones);
}