package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import java.util.UUID;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.AbstractDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoCaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;

/**
 * /resources/v1/tipo_caracteristica
 */
@Path("tipo_caracteristica")
@RequestScoped
public class TipoCaracteristicaResource extends AbstractResource<TipoCaracteristica> {

    @Inject
    TipoCaracteristicaDAO dao;

    public TipoCaracteristicaResource() {
        super(TipoCaracteristica.class);
    }

    @Override
    protected AbstractDAO<TipoCaracteristica, UUID> dao() {
        return dao;
    }

    @Override
    protected UUID getId(TipoCaracteristica registro) {
        return registro.getIdTipoCaracteristica();
    }

    @Override
    protected void setId(TipoCaracteristica registro, UUID id) {
        registro.setIdTipoCaracteristica(id);
    }

    @Override
    protected void prepararNuevo(TipoCaracteristica registro) {
        if (registro.getActivo() == null) {
            registro.setActivo(Boolean.TRUE);
        }
    }

    /** La expresión regular debe compilar. */
    @Override
    protected String validarCrear(TipoCaracteristica nuevo) {
        return dao.validarExpresion(nuevo);
    }

    /** Expresión válida y no desactivar un tipo con características activas. */
    @Override
    protected String validarModificar(TipoCaracteristica nuevo, TipoCaracteristica guardado) {
        return primerError(dao.validarExpresion(nuevo), dao.validarInactivar(nuevo));
    }

    /** Un tipo con características no se puede eliminar. */
    @Override
    protected String validarEliminar(TipoCaracteristica guardado) {
        return dao.validarEliminar(guardado);
    }
}
