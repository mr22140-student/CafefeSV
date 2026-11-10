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
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.FacturaOrdenProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Factura;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.FacturaOrdenProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.OrdenProducto;

/**
 *
 * @author daniel
 */
@Path("factura_orden_producto")
@RequestScoped
public class FacturaOrdenProductoResource extends AbstractResource<FacturaOrdenProducto> {

    @Inject
    FacturaOrdenProductoDAO facturaOrdenProductoDAO;

    public FacturaOrdenProductoResource() {
        super(FacturaOrdenProducto.class);
    }

    @Override
    protected AbstractDAO<FacturaOrdenProducto, UUID> dao() {
        return facturaOrdenProductoDAO;
    }

    @Override
    protected UUID getId(FacturaOrdenProducto registro) {
        return registro.getIdFacturaOrdenProducto();
    }

    @Override
    protected void setId(FacturaOrdenProducto registro, UUID id) {
        registro.setIdFacturaOrdenProducto(id);
    }

    @Override
    protected void resolverRelaciones(FacturaOrdenProducto registro) {
        registro.setIdFactura(referencia(Factura.class, registro.getIdFactura(), Factura::getIdFactura, "idFactura"));
        registro.setIdOrdenProducto(referencia(OrdenProducto.class, registro.getIdOrdenProducto(),
                OrdenProducto::getIdOrdenProducto, "idOrdenProducto"));
    }

    /**
     * Sin precio, copia el de la orden (ya con descuento, si lo tuvo).
     */
    @Override
    protected void prepararNuevo(FacturaOrdenProducto registro) {
        if (registro.getPrecio() == null && registro.getIdOrdenProducto() != null) {
            registro.setPrecio(registro.getIdOrdenProducto().getPrecio());
        }
    }

    /**
     * Factura PENDIENTE, producto de orden, precio >= 0 y no facturado dos
     * veces.
     */
    @Override
    protected String validarCrear(FacturaOrdenProducto nuevo) {
        return facturaOrdenProductoDAO.validarDetalle(nuevo);
    }

    /**
     * La factura en la que estaba guardado también debe seguir PENDIENTE.
     */
    @Override
    protected String validarModificar(FacturaOrdenProducto nuevo, FacturaOrdenProducto guardado) {
        return primerError(facturaOrdenProductoDAO.validarFacturaEditable(guardado.getIdFactura()), facturaOrdenProductoDAO.validarDetalle(nuevo));
    }

    /**
     * Solo se quitan productos de facturas PENDIENTES.
     */
    @Override
    protected String validarEliminar(FacturaOrdenProducto guardado) {
        return facturaOrdenProductoDAO.validarFacturaEditable(guardado.getIdFactura());
    }

    /**
     * GET /factura_orden_producto/factura/{id}: los productos de una factura.
     */
    @GET
    @Path("factura/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response porFactura(@PathParam("id") String id) {
        return listarPorPadre(id, Factura.class, facturaOrdenProductoDAO::findByFactura);
    }

    /**
     * GET /factura_orden_producto/disponibles: productos de orden que aún no se
     * facturan.
     */
    @GET
    @Path("disponibles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response disponibles() {
        try {
            return Response.ok(facturaOrdenProductoDAO.findOrdenProductoDisponibles(), MediaType.APPLICATION_JSON).build();
        } catch (Exception ex) {
            return manejar(ex, "disponibles");
        }
    }
}
