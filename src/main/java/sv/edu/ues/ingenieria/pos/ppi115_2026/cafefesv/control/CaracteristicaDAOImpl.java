/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;

/**
 *
 * @author daniel
 */

@Stateless
public class CaracteristicaDAOImpl extends AbstractDAO<Caracteristica, UUID> implements CaracteristicaDAO {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public CaracteristicaDAOImpl() {
        super(Caracteristica.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<Caracteristica> findByNombre(String nombre) {
        return em.createNamedQuery("Caracteristica.findByNombre", Caracteristica.class)
                .setParameter("nombre", nombre)
                .getResultList();
    }

    @Override
    public List<Caracteristica> findByActivo(Boolean activo) {
        return em.createNamedQuery("Caracteristica.findByActivo", Caracteristica.class)
                .setParameter("activo", activo)
                .getResultList();
    }

    @Override
    public List<Caracteristica> findByObservaciones(String observaciones) {
        return em.createNamedQuery("Caracteristica.findByObservaciones", Caracteristica.class)
                .setParameter("observaciones", observaciones)
                .getResultList();
    }

    @Override
    public List<Caracteristica> findByTipoCaracteristica(TipoCaracteristica tipoCaracteristica) {
        return em.createQuery(
                "SELECT x FROM Caracteristica x WHERE x.idTipoCaracteristica = :tipoCaracteristica",
                Caracteristica.class)
                .setParameter("tipoCaracteristica", tipoCaracteristica)
                .getResultList();
    }
}