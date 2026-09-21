/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;


/**
 *
 * @author daniel
 */
@Stateless
public class DescuentoDAOImpl extends AbstractDAO<Descuento, UUID> implements DescuentoDAO {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public DescuentoDAOImpl() {
        super(Descuento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<Descuento> findByNombre(String nombre) {
        return em.createNamedQuery("Descuento.findByNombre", Descuento.class)
                .setParameter("nombre", nombre)
                .getResultList();
    }

    @Override
    public List<Descuento> findByFechaDesde(Date fechaDesde) {
        return em.createNamedQuery("Descuento.findByFechaDesde", Descuento.class)
                .setParameter("fechaDesde", fechaDesde)
                .getResultList();
    }

    @Override
    public List<Descuento> findByFechaHasta(Date fechaHasta) {
        return em.createNamedQuery("Descuento.findByFechaHasta", Descuento.class)
                .setParameter("fechaHasta", fechaHasta)
                .getResultList();
    }

    @Override
    public List<Descuento> findByObservaciones(String observaciones) {
        return em.createNamedQuery("Descuento.findByObservaciones", Descuento.class)
                .setParameter("observaciones", observaciones)
                .getResultList();
    }

    @Override
    public List<Descuento> findVigentes(Date fecha) {
        return em.createQuery(
                "SELECT d FROM Descuento d WHERE :fecha BETWEEN d.fechaDesde AND d.fechaHasta",
                Descuento.class)
                .setParameter("fecha", fecha)
                .getResultList();
    }
}