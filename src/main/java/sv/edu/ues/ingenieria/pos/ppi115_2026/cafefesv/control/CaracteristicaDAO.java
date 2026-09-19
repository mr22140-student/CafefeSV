/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;

/**
 *
 * @author daniel
 */
public interface CaracteristicaDAO extends GenericDAO<Caracteristica, UUID> {

    List<Caracteristica> findByNombre(String nombre);

    List<Caracteristica> findByActivo(Boolean activo);

    List<Caracteristica> findByObservaciones(String observaciones);

    List<Caracteristica> findByTipoCaracteristica(TipoCaracteristica tipoCaracteristica);
}