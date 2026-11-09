package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;

/**
 *
 * @author daniel
 */
@Stateless
public class TipoCaracteristicaDAO extends AbstractDAO<TipoCaracteristica, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public TipoCaracteristicaDAO() {
        super(TipoCaracteristica.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<TipoCaracteristica> findByActivo(Boolean activo) {
        return em.createNamedQuery("TipoCaracteristica.findByActivo", TipoCaracteristica.class)
                .setParameter("activo", activo)
                .getResultList();
    }

    /**
     * Tipos ACTIVOS cuyo nombre contiene el texto, sin importar mayúsculas. Lo
     * usa el buscador (autoComplete) de Características.
     */
    public List<TipoCaracteristica> buscarActivosPorNombre(String textoBuscado) {
        String filtro = textoBuscado == null ? "" : textoBuscado.toLowerCase().trim();
        return findByActivo(Boolean.TRUE).stream()
                .filter(tipo -> tipo.getNombre() != null && tipo.getNombre().toLowerCase().contains(filtro))
                .collect(Collectors.toList());
    }

    /**
     * Cuenta las características ACTIVAS de este tipo.
     */
    public long countCaracteristicasActivas(TipoCaracteristica tipoCaracteristica) {
        return em.createQuery(
                "SELECT COUNT(caracteristica) FROM Caracteristica caracteristica "
                + "WHERE caracteristica.idTipoCaracteristica = :tipoCaracteristica AND caracteristica.activo = true",
                Long.class)
                .setParameter("tipoCaracteristica", tipoCaracteristica)
                .getSingleResult();
    }

    /**
     * Cuenta las características (activas o no) de este tipo.
     */
    public long countCaracteristicas(TipoCaracteristica tipoCaracteristica) {
        return em.createQuery(
                "SELECT COUNT(caracteristica) FROM Caracteristica caracteristica "
                + "WHERE caracteristica.idTipoCaracteristica = :tipoCaracteristica",
                Long.class)
                .setParameter("tipoCaracteristica", tipoCaracteristica)
                .getSingleResult();
    }

    /**
     * La expresión regular debe ser válida en Java. Vacía significa que el tipo
     * acepta cualquier valor.
     *
     * @return el mensaje de error, o null si la expresión es válida
     */
    public String validarExpresion(TipoCaracteristica tipoCaracteristica) {
        if (tipoCaracteristica == null || tipoCaracteristica.getExpresionRegular() == null
                || tipoCaracteristica.getExpresionRegular().isBlank()) {
            return null;
        }
        try {
            Pattern.compile(tipoCaracteristica.getExpresionRegular().trim());
            return null;
        } catch (PatternSyntaxException error) {
            return error.getDescription() + ". Para aceptar cualquier valor use .* o deje el campo vacío.";
        }
    }

    /**
     * Un tipo no se puede inactivar mientras tenga características ACTIVAS.
     *
     * @return el mensaje de error, o null si se puede inactivar
     */
    public String validarInactivar(TipoCaracteristica tipoCaracteristica) {
        if (tipoCaracteristica == null || Boolean.TRUE.equals(tipoCaracteristica.getActivo())) {
            return null;
        }
        long caracteristicasActivas = countCaracteristicasActivas(tipoCaracteristica);
        if (caracteristicasActivas > 0) {
            return "Hay " + caracteristicasActivas + " característica(s) activa(s) con este tipo. "
                    + "Inactívelas o cámbielas de tipo primero.";
        }
        return null;
    }

    /**
     * Un tipo no se puede eliminar si alguna característica lo usa.
     *
     * @return el mensaje de error, o null si se puede eliminar
     */
    public String validarEliminar(TipoCaracteristica tipoCaracteristica) {
        if (tipoCaracteristica != null && countCaracteristicas(tipoCaracteristica) > 0) {
            return "Hay características que usan este tipo. Cámbielas de tipo o elimínelas primero.";
        }
        return null;
    }

}
