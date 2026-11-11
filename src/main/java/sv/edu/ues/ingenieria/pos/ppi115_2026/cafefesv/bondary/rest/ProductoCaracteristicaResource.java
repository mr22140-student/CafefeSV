package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.AbstractDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoCaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoCaracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;

/**
 * /resources/v1/producto_caracteristica
 */
@Path("producto_caracteristica")
@RequestScoped
public class ProductoCaracteristicaResource extends AbstractResource<ProductoCaracteristica> {

    @Inject
    ProductoCaracteristicaDAO dao;

    public ProductoCaracteristicaResource() {
        super(ProductoCaracteristica.class);
    }

    @Override
    protected AbstractDAO<ProductoCaracteristica, UUID> dao() {
        return dao;
    }

    @Override
    protected UUID getId(ProductoCaracteristica registro) {
        return registro.getIdProductoCaracteristica();
    }

    @Override
    protected void setId(ProductoCaracteristica registro, UUID id) {
        registro.setIdProductoCaracteristica(id);
    }

    @Override
    protected void resolverRelaciones(ProductoCaracteristica registro) {
        registro.setIdProducto(referencia(Producto.class, registro.getIdProducto(), Producto::getIdProducto, "idProducto"));
        registro.setIdCaracteristica(referencia(Caracteristica.class, registro.getIdCaracteristica(),
                Caracteristica::getIdCaracteristica, "idCaracteristica"));
    }

    /** Característica válida, valor que cumpla la expresión regular y sin duplicados. */
    @Override
    protected String validarCrear(ProductoCaracteristica nuevo) {
        return primerError(dao.validarCaracteristica(nuevo), dao.validarValor(nuevo), dao.validarDuplicado(nuevo));
    }

    @Override
    protected String validarModificar(ProductoCaracteristica nuevo, ProductoCaracteristica guardado) {
        return primerError(dao.validarCaracteristica(nuevo), dao.validarValor(nuevo), dao.validarDuplicado(nuevo));
    }

    /** GET /producto_caracteristica/producto/{id}: las características de un producto. */
    @GET
    @Path("producto/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response porProducto(@PathParam("id") String id) {
        return listarPorPadre(id, Producto.class, dao::findByProducto);
    }
}
