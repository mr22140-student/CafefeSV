package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Pago;

@Stateless
public class PagoDAO extends AbstractDAO<Pago, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public PagoDAO() {
        super(Pago.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Pago> findByEstado(String estado) {
        return em.createNamedQuery(
                "Pago.findByEstado",
                Pago.class
        )
        .setParameter("estado", estado)
        .getResultList();
    }

    public List<Pago> findByObservaciones(String observaciones) {
        return em.createNamedQuery(
                "Pago.findByObservaciones",
                Pago.class
        )
        .setParameter("observaciones", observaciones)
        .getResultList();
    }

    public List<Pago> findByFactura(Factura factura) {
        return em.createQuery(
                "SELECT x FROM Pago x "
                + "WHERE x.idFactura = :factura",
                Pago.class
        )
        .setParameter("factura", factura)
        .getResultList();
    }
}