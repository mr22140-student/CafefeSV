package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import java.util.Date;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.AbstractDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.DescuentoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoDescuento;

/**
 * /resources/v1/descuento
 */
@Path("descuento")
@RequestScoped
public class DescuentoResource extends AbstractResource<Descuento> {

    @Inject
    DescuentoDAO descuentoDAO;

    public DescuentoResource() {
        super(Descuento.class);
    }

    @Override
    protected AbstractDAO<Descuento, UUID> dao() {
        return descuentoDAO;
    }

    @Override
    protected UUID getId(Descuento registro) {
        return registro.getIdDescuento();
    }

    @Override
    protected void setId(Descuento registro, UUID id) {
        registro.setIdDescuento(id);
    }

    @Override
    protected void resolverRelaciones(Descuento registro) {
        registro.setIdTipoDescuento(referencia(TipoDescuento.class, registro.getIdTipoDescuento(),
                TipoDescuento::getIdTipoDescuento, "idTipoDescuento"));
    }

    /**
     * Sin fecha de inicio, empieza hoy (igual que DescuentoModel).
     */
    @Override
    protected void prepararNuevo(Descuento registro) {
        if (registro.getFechaDesde() == null) {
            registro.setFechaDesde(new Date());
        }
    }

    /**
     * Fecha hasta >= fecha desde y el tipo debe estar activo.
     */
    @Override
    protected String validarCrear(Descuento nuevo) {
        return primerError(descuentoDAO.validarFechas(nuevo), descuentoDAO.validarTipoActivo(nuevo));
    }

    @Override
    protected String validarModificar(Descuento nuevo, Descuento guardado) {
        return primerError(descuentoDAO.validarFechas(nuevo), descuentoDAO.validarTipoActivo(nuevo));
    }

    /**
     * Asignado a productos no se puede eliminar.
     */
    @Override
    protected String validarEliminar(Descuento guardado) {
        return descuentoDAO.validarEliminar(guardado);
    }
}
