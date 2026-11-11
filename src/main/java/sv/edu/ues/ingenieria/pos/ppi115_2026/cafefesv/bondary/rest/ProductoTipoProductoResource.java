package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Date;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.AbstractDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoTipoProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoTipoProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoProducto;

/**
 * /resources/v1/producto_tipo_producto
 */
@Path("producto_tipo_producto")
@RequestScoped
public class ProductoTipoProductoResource extends AbstractResource<ProductoTipoProducto> {

    @Inject
    ProductoTipoProductoDAO productoTipoProductoDAO;

    public ProductoTipoProductoResource() {
        super(ProductoTipoProducto.class);
    }

    @Override
    protected AbstractDAO<ProductoTipoProducto, UUID> dao() {
        return productoTipoProductoDAO;
    }

    @Override
    protected UUID getId(ProductoTipoProducto registro) {
        return registro.getIdProductoTipoProducto();
    }

    @Override
    protected void setId(ProductoTipoProducto registro, UUID id) {
        registro.setIdProductoTipoProducto(id);
    }

    /** OJO: en esta entidad la relación con Producto se llama "producto", no "idProducto". */
    @Override
    protected void resolverRelaciones(ProductoTipoProducto registro) {
        registro.setProducto(referencia(Producto.class, registro.getProducto(), Producto::getIdProducto, "producto"));
        registro.setIdTipoProducto(referencia(TipoProducto.class, registro.getIdTipoProducto(),
                TipoProducto::getIdTipoProducto, "idTipoProducto"));
    }

    @Override
    protected void prepararNuevo(ProductoTipoProducto registro) {
        if (registro.getFechaCreacion() == null) {
            registro.setFechaCreacion(new Date());
        }
    }

    /** El tipo debe ser válido y el producto no puede tener ese tipo dos veces. */
    @Override
    protected String validarCrear(ProductoTipoProducto nuevo) {
        return primerError(productoTipoProductoDAO.validarTipo(nuevo), productoTipoProductoDAO.validarDuplicado(nuevo));
    }

    @Override
    protected String validarModificar(ProductoTipoProducto nuevo, ProductoTipoProducto guardado) {
        return primerError(productoTipoProductoDAO.validarTipo(nuevo), productoTipoProductoDAO.validarDuplicado(nuevo));
    }

    /** GET /producto_tipo_producto/producto/{id}: los tipos de un producto. */
    @GET
    @Path("producto/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response porProducto(@PathParam("id") String id) {
        return listarPorPadre(id, Producto.class, productoTipoProductoDAO::findByProducto);
    }

    /** GET /producto_tipo_producto/tipo_producto/{id}: los productos de una categoría. */
    @GET
    @Path("tipo_producto/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response porTipoProducto(@PathParam("id") String id) {
        return listarPorPadre(id, TipoProducto.class, productoTipoProductoDAO::findByTipoProducto);
    }
}
