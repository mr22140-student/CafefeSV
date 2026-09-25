package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

@Stateless
public class FacturaDAO extends AbstractDAO<Factura, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public FacturaDAO() {
        super(Factura.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Factura> findByCliente(String cliente) {
        return em.createNamedQuery(
                "Factura.findByCliente",
                Factura.class
        )
        .setParameter("cliente", cliente)
        .getResultList();
    }

    public List<Factura> findByFechaFacturacion(Date fechaFacturacion) {
        return em.createNamedQuery(
                "Factura.findByFechaFacturacion",
                Factura.class
        )
        .setParameter("fechaFacturacion", fechaFacturacion)
        .getResultList();
    }

    public List<Factura> findByEstado(String estado) {
        return em.createNamedQuery(
                "Factura.findByEstado",
                Factura.class
        )
        .setParameter("estado", estado)
        .getResultList();
    }

    public List<Factura> findByObservaciones(String observaciones) {
        return em.createNamedQuery(
                "Factura.findByObservaciones",
                Factura.class
        )
        .setParameter("observaciones", observaciones)
        .getResultList();
    }

    public List<Factura> findByCaja(Caja caja) {
        return em.createQuery(
                "SELECT x FROM Factura x "
                + "WHERE x.idCaja = :caja",
                Factura.class
        )
        .setParameter("caja", caja)
        .getResultList();
    }

    public List<Factura> findByEmpleadoRol(EmpleadoRol empleadoRol) {
        return em.createQuery(
                "SELECT x FROM Factura x "
                + "WHERE x.idEmpleadoRol = :empleadoRol",
                Factura.class
        )
        .setParameter("empleadoRol", empleadoRol)
        .getResultList();
    }
}