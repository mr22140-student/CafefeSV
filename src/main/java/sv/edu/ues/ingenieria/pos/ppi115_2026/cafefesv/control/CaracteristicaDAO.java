package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;

/**
 *
 * @author daniel
 */
@Stateless
public class CaracteristicaDAO extends AbstractDAO<Caracteristica, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public CaracteristicaDAO() {
        super(Caracteristica.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Caracteristica> findByTipoCaracteristica(TipoCaracteristica tipoCaracteristica) {
        if (tipoCaracteristica == null) {
            return Collections.emptyList();
        }
        return em.createQuery(
                "SELECT caracteristica FROM Caracteristica caracteristica WHERE caracteristica.idTipoCaracteristica = :tipoCaracteristica",
                Caracteristica.class)
                .setParameter("tipoCaracteristica", tipoCaracteristica)
                .getResultList();
    }

    /**
     * Cuenta los productos que tienen esta característica.
     */
    public long countProductos(Caracteristica caracteristica) {
        return em.createQuery(
                "SELECT COUNT(asignacion) FROM ProductoCaracteristica asignacion "
                + "WHERE asignacion.idCaracteristica = :caracteristica",
                Long.class)
                .setParameter("caracteristica", caracteristica)
                .getSingleResult();
    }

    /**
     * Una característica ACTIVA no puede tener un tipo inactivo. El tipo se lee
     * de la base para revisar su estado actual.
     *
     * @return el mensaje de error, o null si el tipo está bien
     */
    public String validarTipoActivo(Caracteristica caracteristica) {
        if (caracteristica == null || !Boolean.TRUE.equals(caracteristica.getActivo())
                || caracteristica.getIdTipoCaracteristica() == null
                || caracteristica.getIdTipoCaracteristica().getIdTipoCaracteristica() == null) {
            return null;
        }
        TipoCaracteristica tipoEnLaBase = em.find(TipoCaracteristica.class,
                caracteristica.getIdTipoCaracteristica().getIdTipoCaracteristica());
        if (tipoEnLaBase == null || !Boolean.TRUE.equals(tipoEnLaBase.getActivo())) {
            return "El tipo \"" + caracteristica.getIdTipoCaracteristica().getNombre()
                    + "\" está inactivo. Seleccione un tipo activo.";
        }
        return null;
    }

    /**
     * Una característica no se puede eliminar si algún producto la tiene.
     *
     * @return el mensaje de error, o null si se puede eliminar
     */
    public String validarEliminar(Caracteristica caracteristica) {
        if (caracteristica != null && countProductos(caracteristica) > 0) {
            return "Hay productos que tienen esta característica. Quítesela primero.";
        }
        return null;
    }

    /**
     * Características ACTIVAS de un tipo. Llena el segundo combo del diálogo de
     * características de Producto.
     */
    public List<Caracteristica> buscarActivasPorTipo(TipoCaracteristica tipoCaracteristica) {
        return findByTipoCaracteristica(tipoCaracteristica).stream()
                .filter(caracteristica -> Boolean.TRUE.equals(caracteristica.getActivo()))
                .collect(Collectors.toList());
    }
}
