package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.CaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoCaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoCaracteristica;

/**
 *
 * @author valen
 */
@Named("productoCaracteristicaModel")
@ViewScoped
public class ProductoCaracteristicaModel extends AbstractModel<ProductoCaracteristica, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProductoCaracteristicaDAO pcDAO;

    @Inject
    private ProductoDAO productoDAO;

    @Inject
    private CaracteristicaDAO caracteristicaDAO;

    private List<Producto> listaProducto;
    private List<Caracteristica> listaCaracteristica;

    @PostConstruct
    public void init() {
        iniciar();
        listaProducto = productoDAO.findAll();
        listaCaracteristica = caracteristicaDAO.findAll();
    }

    @Override
    protected GenericDAO<ProductoCaracteristica, UUID> getDAO() {
        return pcDAO;
    }

    @Override
    protected ProductoCaracteristica crearNuevo() {
        return new ProductoCaracteristica(UUID.randomUUID());
    }

    @Override
    public void btnCrearHandler() {
        if (valorValido() && !existeDuplicado()) {
            super.btnCrearHandler();
        }
    }

    @Override
    public void btnModificarHandler() {
        if (valorValido() && !existeDuplicado()) {
            super.btnModificarHandler();
        }
    }

    /**
     * Un producto no debe tener la misma caracteristica repetida.
     */
    private boolean existeDuplicado() {
        if (registro == null || registro.getIdProducto() == null || registro.getIdCaracteristica() == null) {
            return false;
        }
        boolean duplicado = pcDAO.findByProducto(registro.getIdProducto()).stream()
                .anyMatch(x -> !x.getIdProductoCaracteristica().equals(registro.getIdProductoCaracteristica())
                        && registro.getIdCaracteristica().equals(x.getIdCaracteristica()));
        if (duplicado) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Registro duplicado", "El producto ya tiene asignada esa característica."));
        }
        return duplicado;
    }

    /**
     * El valor debe cumplir la expresión regular del tipo de la característica.
     */
    private boolean valorValido() {
        if (registro == null || registro.getIdCaracteristica() == null) {
            return true;
        }
        TipoCaracteristica tipo = registro.getIdCaracteristica().getIdTipoCaracteristica();
        if (tipo == null || tipo.getExpresionRegular() == null || tipo.getExpresionRegular().isBlank()) {
            return true;
        }
        String valor = registro.getValor() == null ? "" : registro.getValor().trim();
        try {
            if (!Pattern.matches(tipo.getExpresionRegular(), valor)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "El valor no es válido",
                        "El valor no cumple la validación de " + tipo.getNombre() + ": " + tipo.getExpresionRegular()));
                return false;
            }
            registro.setValor(valor);
            return true;
        } catch (PatternSyntaxException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Expresión regular mal configurada",
                    "Revise la expresión regular del tipo " + tipo.getNombre() + "."));
            return false;
        }
    }

    /**
     * Texto de ayuda con el formato esperado, para mostrarlo junto al campo
     * Valor.
     */
    public String getFormatoValor() {
        if (registro == null || registro.getIdCaracteristica() == null
                || registro.getIdCaracteristica().getIdTipoCaracteristica() == null) {
            return "";
        }
        String regex = registro.getIdCaracteristica().getIdTipoCaracteristica().getExpresionRegular();
        return (regex == null || regex.isBlank()) ? "" : "Formato: " + regex;
    }

    public List<Producto> getListaProducto() {
        return listaProducto;
    }

    public List<Caracteristica> getListaCaracteristica() {
        return listaCaracteristica;
    }
}
