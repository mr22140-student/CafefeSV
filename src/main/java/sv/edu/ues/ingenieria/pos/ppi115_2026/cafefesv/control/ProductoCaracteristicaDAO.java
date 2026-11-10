package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoCaracteristica;

/**
 *
 * @author daniel
 */
@Stateless
public class ProductoCaracteristicaDAO extends AbstractDAO<ProductoCaracteristica, UUID> {

    @PersistenceContext(unitName = "CafefePU")
    private EntityManager em;

    public ProductoCaracteristicaDAO() {
        super(ProductoCaracteristica.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ProductoCaracteristica> findByProducto(Producto producto) {
        return em.createQuery(
                "SELECT asignacion FROM ProductoCaracteristica asignacion "
                + "WHERE asignacion.idProducto = :producto",
                ProductoCaracteristica.class
        )
                .setParameter("producto", producto)
                .getResultList();
    }

    /**
     * La asignación debe tener una característica ACTIVA cuyo tipo también esté
     * activo. La característica se lee de la base para revisar su estado
     * actual.
     *
     * @return el mensaje de error, o null si la característica está bien
     */
    public String validarCaracteristica(ProductoCaracteristica asignacion) {
        if (asignacion == null || asignacion.getIdCaracteristica() == null
                || asignacion.getIdCaracteristica().getIdCaracteristica() == null) {
            return "Seleccione una característica.";
        }
        Caracteristica caracteristicaEnLaBase = em.find(Caracteristica.class,
                asignacion.getIdCaracteristica().getIdCaracteristica());
        if (caracteristicaEnLaBase == null || !Boolean.TRUE.equals(caracteristicaEnLaBase.getActivo())) {
            return "\"" + asignacion.getIdCaracteristica().getNombre() + "\" está inactiva. Seleccione otra.";
        }
        TipoCaracteristica tipoCaracteristica = caracteristicaEnLaBase.getIdTipoCaracteristica();
        if (tipoCaracteristica != null && !Boolean.TRUE.equals(tipoCaracteristica.getActivo())) {
            return "El tipo \"" + tipoCaracteristica.getNombre() + "\" está inactivo. Seleccione otra característica.";
        }
        return null;
    }

    /**
     * El valor es obligatorio y debe cumplir la expresión regular del tipo de
     * la característica (si el tipo no tiene expresión, acepta cualquier
     * valor).
     *
     * @return el mensaje de error, o null si el valor está bien
     */
    public String validarValor(ProductoCaracteristica asignacion) {
        String valor = (asignacion == null || asignacion.getValor() == null) ? "" : asignacion.getValor().trim();
        if (valor.isEmpty()) {
            return "Escriba el valor de la característica.";
        }
        TipoCaracteristica tipoCaracteristica = asignacion.getIdCaracteristica() == null
                ? null : asignacion.getIdCaracteristica().getIdTipoCaracteristica();
        if (tipoCaracteristica == null || tipoCaracteristica.getExpresionRegular() == null
                || tipoCaracteristica.getExpresionRegular().isBlank()) {
            return null;
        }
        String expresionRegular = tipoCaracteristica.getExpresionRegular().trim();
        try {
            if (!Pattern.matches(expresionRegular, valor)) {
                return "El valor \"" + valor + "\" no cumple el formato de "
                        + tipoCaracteristica.getNombre() + ": " + expresionRegular;
            }
            return null;
        } catch (PatternSyntaxException error) {
            return "La expresión regular del tipo " + tipoCaracteristica.getNombre() + " está mal configurada.";
        }
    }

    /**
     * Un producto no puede tener la misma característica dos veces.
     *
     * @return el mensaje de error, o null si no está repetida
     */
    public String validarDuplicado(ProductoCaracteristica asignacion) {
        if (asignacion == null || asignacion.getIdProducto() == null || asignacion.getIdCaracteristica() == null) {
            return null;
        }
        boolean repetida = findByProducto(asignacion.getIdProducto()).stream()
                .anyMatch(otraAsignacion -> !otraAsignacion.getIdProductoCaracteristica().equals(asignacion.getIdProductoCaracteristica())
                        && asignacion.getIdCaracteristica().equals(otraAsignacion.getIdCaracteristica()));
        return repetida ? "El producto ya tiene esa característica." : null;
    }
}
