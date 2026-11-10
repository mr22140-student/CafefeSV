package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.AbstractDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.OrdenProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Orden;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.OrdenProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;

/**
 *
 * @author daniel
 */
@Path("orden_producto")
@RequestScoped
public class OrdenProductoResource extends AbstractResource<OrdenProducto> {

    @Inject
    OrdenProductoDAO ordenProductoDAO;

    public OrdenProductoResource() {
        super(OrdenProducto.class);
    }

    @Override
    protected AbstractDAO<OrdenProducto, UUID> dao() {
        return ordenProductoDAO;
    }

    @Override
    protected UUID getId(OrdenProducto registro) {
        return registro.getIdOrdenProducto();
    }

    @Override
    protected void setId(OrdenProducto registro, UUID id) {
        registro.setIdOrdenProducto(id);
    }

    @Override
    protected void resolverRelaciones(OrdenProducto registro) {
        registro.setIdOrden(referencia(Orden.class, registro.getIdOrden(), Orden::getIdOrden, "idOrden"));
        registro.setIdProducto(referencia(Producto.class, registro.getIdProducto(), Producto::getIdProducto, "idProducto"));
    }

    /** Sin precio, toma el precio sugerido del producto (igual que en JSF). */
    @Override
    protected void prepararNuevo(OrdenProducto registro) {
        if (registro.getPrecio() == null && registro.getIdProducto() != null) {
            registro.setPrecio(registro.getIdProducto().getPrecioSugerido());
        }
    }

    /** Orden, producto y precio obligatorios; el producto debe estar activo. */
    @Override
    protected String validarCrear(OrdenProducto nuevo) {
        return primerError(ordenProductoDAO.validarDetalle(nuevo), ordenProductoDAO.validarProductoElegido(nuevo.getIdProducto()));
    }

    /** Un producto ya facturado no se modifica. */
    @Override
    protected String validarModificar(OrdenProducto nuevo, OrdenProducto guardado) {
        return primerError(ordenProductoDAO.validarNoFacturado(guardado), ordenProductoDAO.validarDetalle(nuevo));
    }

    /** Un producto ya facturado no se elimina. */
    @Override
    protected String validarEliminar(OrdenProducto guardado) {
        return ordenProductoDAO.validarNoFacturado(guardado);
    }

    /** GET /orden_producto/orden/{id}: los productos de una orden. */
    @GET
    @Path("orden/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response porOrden(@PathParam("id") String id) {
        return listarPorPadre(id, Orden.class, ordenProductoDAO::findByOrden);
    }
}
