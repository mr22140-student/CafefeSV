package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoTipoProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoTipoProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoProducto;
/**
 *
 * @author valen
 */
@Named("productoTipoProductoModel")
@ViewScoped
public class ProductoTipoProductoModel extends AbstractModel<ProductoTipoProducto, UUID> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProductoTipoProductoDAO productoTipoProductoDAO;

    @Inject
    private ProductoDAO productoDAO;

    @Inject
    private TipoProductoDAO tipoProductoDAO;

    private List<Producto> listaProducto;
    private List<TipoProducto> listaTipoProducto;

    @PostConstruct
    public void init() {
        iniciar();
        listaProducto = productoDAO.findAll();
        listaTipoProducto = tipoProductoDAO.findAll();
    }

    @Override
    protected GenericDAO<ProductoTipoProducto, UUID> getDAO() {
        return productoTipoProductoDAO;
    }

    @Override
    protected ProductoTipoProducto crearNuevo() {
        ProductoTipoProducto nuevo = new ProductoTipoProducto(UUID.randomUUID());
        nuevo.setFechaCreacion(new Date());
        return nuevo;
    }

    @Override
    public void btnCrearHandler() {
        if (asignacionValida()) {
            super.btnCrearHandler();
        }
    }

    @Override
    public void btnModificarHandler() {
        if (asignacionValida()) {
            super.btnModificarHandler();
        }
    }

      // Mismas reglas que la pestaña Tipos de Producto de Producto.
    private boolean asignacionValida() {
        return sinError("Tipo no válido", productoTipoProductoDAO.validarTipo(registro))
                && sinError("Registro duplicado", productoTipoProductoDAO.validarDuplicado(registro));
    }

    public List<Producto> getListaProducto() {
        return listaProducto;
    }

    public List<TipoProducto> getListaTipoProducto() {
        return listaTipoProducto;
    }
}