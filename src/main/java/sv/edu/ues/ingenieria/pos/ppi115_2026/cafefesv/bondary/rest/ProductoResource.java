package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.AbstractDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;

/**
 * /resources/v1/producto
 */
@Path("producto")
@RequestScoped
public class ProductoResource extends AbstractResource<Producto> {

    @Inject
    ProductoDAO productoDAO;

    public ProductoResource() {
        super(Producto.class);
    }

    @Override
    protected AbstractDAO<Producto, UUID> dao() {
        return productoDAO;
    }

    @Override
    protected UUID getId(Producto registro) {
        return registro.getIdProducto();
    }

    @Override
    protected void setId(Producto registro, UUID id) {
        registro.setIdProducto(id);
    }

    /**
     * Si no viene, el producto se crea activo.
     */
    @Override
    protected void prepararNuevo(Producto registro) {
        if (registro.getActivo() == null) {
            registro.setActivo(Boolean.TRUE);
        }
    }

    /**
     * El precio sugerido debe ser válido.
     */
    @Override
    protected String validarCrear(Producto nuevo) {
        return productoDAO.validarPrecio(nuevo);
    }

    @Override
    protected String validarModificar(Producto nuevo, Producto guardado) {
        return productoDAO.validarPrecio(nuevo);
    }

    /**
     * Con órdenes, características o descuentos no se puede eliminar.
     */
    @Override
    protected String validarEliminar(Producto guardado) {
        return productoDAO.validarEliminar(guardado);
    }
}
