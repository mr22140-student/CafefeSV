package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import java.math.BigDecimal;
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
    ProductoDAO dao;

    public ProductoResource() {
        super(Producto.class);
    }

    @Override
    protected AbstractDAO<Producto, UUID> dao() {
        return dao;
    }

    @Override
    protected UUID getId(Producto registro) {
        return registro.getIdProducto();
    }

    @Override
    protected void setId(Producto registro, UUID id) {
        registro.setIdProducto(id);
    }

    /** Igual que ProductoModel: activo y precio sugerido 0 si no vienen. */
    @Override
    protected void prepararNuevo(Producto registro) {
        if (registro.getActivo() == null) {
            registro.setActivo(Boolean.TRUE);
        }
        if (registro.getPrecioSugerido() == null) {
            registro.setPrecioSugerido(BigDecimal.ZERO);
        }
    }

    /** El precio sugerido debe ser válido. */
    @Override
    protected String validarCrear(Producto nuevo) {
        return dao.validarPrecio(nuevo);
    }

    @Override
    protected String validarModificar(Producto nuevo, Producto guardado) {
        return dao.validarPrecio(nuevo);
    }
}
