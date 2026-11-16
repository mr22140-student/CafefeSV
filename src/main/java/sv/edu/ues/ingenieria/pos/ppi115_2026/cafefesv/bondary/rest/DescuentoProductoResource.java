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
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.DescuentoProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.DescuentoProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;

/**
 *
 * @author daniel
 */
@Path("descuento_producto")
@RequestScoped
public class DescuentoProductoResource extends AbstractResource<DescuentoProducto> {

    @Inject
    DescuentoProductoDAO descuentoProductoDAO;

    public DescuentoProductoResource() {
        super(DescuentoProducto.class);
    }

    @Override
    protected AbstractDAO<DescuentoProducto, UUID> dao() {
        return descuentoProductoDAO;
    }

    @Override
    protected UUID getId(DescuentoProducto registro) {
        return registro.getIdDescuentoProducto();
    }

    @Override
    protected void setId(DescuentoProducto registro, UUID id) {
        registro.setIdDescuentoProducto(id);
    }

    @Override
    protected void resolverRelaciones(DescuentoProducto registro) {
        registro.setIdDescuento(referencia(Descuento.class, registro.getIdDescuento(), Descuento::getIdDescuento, "idDescuento"));
        registro.setIdProducto(referencia(Producto.class, registro.getIdProducto(), Producto::getIdProducto, "idProducto"));
    }

    /** Sin fecha de inicio, empieza hoy (igual que DescuentoProductoModel). */
    @Override
    protected void prepararNuevo(DescuentoProducto registro) {
        if (registro.getFechaDesde() == null) {
            registro.setFechaDesde(new Date());
        }
    }

    /** Descuento vigente, porcentaje dentro del máximo, fechas válidas y sin duplicados. */
    @Override
    protected String validarCrear(DescuentoProducto nuevo) {
        return primerError(descuentoProductoDAO.validarDescuento(nuevo), descuentoProductoDAO.validarValor(nuevo),
                descuentoProductoDAO.validarFechas(nuevo), descuentoProductoDAO.validarDuplicado(nuevo));
    }

    @Override
    protected String validarModificar(DescuentoProducto nuevo, DescuentoProducto guardado) {
        return primerError(descuentoProductoDAO.validarDescuento(nuevo), descuentoProductoDAO.validarValor(nuevo),
                descuentoProductoDAO.validarFechas(nuevo), descuentoProductoDAO.validarDuplicado(nuevo));
    }

    /** GET /descuento_producto/descuento/{id}: los productos de un descuento. */
    @GET
    @Path("descuento/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response porDescuento(@PathParam("id") String id) {
        return listarPorPadre(id, Descuento.class, descuentoProductoDAO::findByDescuento);
    }

    /** GET /descuento_producto/producto/{id}: los descuentos de un producto. */
    @GET
    @Path("producto/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response porProducto(@PathParam("id") String id) {
        return listarPorPadre(id, Producto.class, descuentoProductoDAO::findByProducto);
    }
}
