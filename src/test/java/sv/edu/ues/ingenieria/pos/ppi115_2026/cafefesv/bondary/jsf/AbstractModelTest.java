package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.faces.application.FacesMessage;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.GenericDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoProducto;


/**
 *
 * @author valen
 */

// Pruebas del comportamiento común de AbstractModel (estados y botones CRUD), usando una implementación mínima con TipoProducto y un DAO falso.

public class AbstractModelTest extends ModelTestBase {
     @Mock
    private GenericDAO<TipoProducto, UUID> dao;

    private AbstractModel<TipoProducto, UUID> model;

    @BeforeEach
    void crearModel() {
        model = new AbstractModel<>() {
            @Override
            protected GenericDAO<TipoProducto, UUID> getDAO() {
                return dao;
            }

            @Override
            protected TipoProducto crearNuevo() {
                return new TipoProducto(UUID.randomUUID());
            }
        };
    }

    // ===================== iniciar / nuevo / seleccionar / cancelar =====================

    @Test
    void iniciar_cargaLosRegistrosYDejaElEstadoEnNinguno() {
        List<TipoProducto> lista = List.of(tipo());
        when(dao.findRange(0, 1000)).thenReturn(lista);
        model.setEstado(AbstractModel.ESTADO_CREAR);

        model.iniciar();

        assertSame(lista, model.getRegistros());
        assertEquals(AbstractModel.ESTADO_NINGUNO, model.getEstado());
    }

    @Test
    void btnNuevo_creaUnRegistroYCambiaAEstadoCrear() {
        model.btnNuevoHandler();

        assertNotNull(model.getRegistro());
        assertEquals(AbstractModel.ESTADO_CREAR, model.getEstado());
    }

    @Test
    void btnNuevo_conActionEvent_haceLoMismo() {
        model.btnNuevoHandler(null);

        assertEquals(AbstractModel.ESTADO_CREAR, model.getEstado());
    }

    @Test
    void seleccionarRegistro_cambiaAEstadoModificar() {
        TipoProducto t = tipo();

        model.seleccionarRegistro(t);

        assertSame(t, model.getRegistro());
        assertEquals(AbstractModel.ESTADO_MODIFICAR, model.getEstado());
    }

    @Test
    void seleccionarRegistro_conNull_noCambiaElEstado() {
        model.seleccionarRegistro(null);

        assertEquals(AbstractModel.ESTADO_NINGUNO, model.getEstado());
    }

    @Test
    void btnCancelar_limpiaElRegistroYRecargaLaLista() {
        model.setRegistro(tipo());
        model.setEstado(AbstractModel.ESTADO_MODIFICAR);

        model.btnCancelarHandler();

        assertNull(model.getRegistro());
        assertEquals(AbstractModel.ESTADO_NINGUNO, model.getEstado());
        verify(dao).findRange(0, 1000);
    }

    // ===================== crear =====================

    @Test
    void btnCrear_exitoso_guardaYVuelveAEstadoNinguno() {
        model.btnNuevoHandler();
        TipoProducto nuevo = model.getRegistro();

        model.btnCrearHandler();

        verify(dao).save(nuevo);
        assertNull(model.getRegistro());
        assertEquals(AbstractModel.ESTADO_NINGUNO, model.getEstado());
        assertEquals(FacesMessage.SEVERITY_INFO, ultimoMensaje().getSeverity());
        assertEquals("Registro creado con éxito", ultimoMensaje().getSummary());
    }

    @Test
    void btnCrear_siElDaoFalla_muestraErrorYSeQuedaEnCrear() {
        model.btnNuevoHandler();
        doThrow(new IllegalStateException("falla simulada")).when(dao).save(any());

        model.btnCrearHandler();

        assertNotNull(model.getRegistro());
        assertEquals(AbstractModel.ESTADO_CREAR, model.getEstado());
        assertEquals(FacesMessage.SEVERITY_ERROR, ultimoMensaje().getSeverity());
        assertEquals("No se pudo crear el registro", ultimoMensaje().getSummary());
    }

    @Test
    void btnCrear_sinRegistro_muestraAdvertenciaYNoGuarda() {
        model.btnCrearHandler();

        verify(dao, never()).save(any());
        assertEquals(FacesMessage.SEVERITY_WARN, ultimoMensaje().getSeverity());
    }

    // ===================== modificar =====================

    @Test
    void btnModificar_exitoso_actualizaYVuelveAEstadoNinguno() {
        TipoProducto t = tipo();
        model.seleccionarRegistro(t);

        model.btnModificarHandler();

        verify(dao).update(t);
        assertNull(model.getRegistro());
        assertEquals(AbstractModel.ESTADO_NINGUNO, model.getEstado());
        assertEquals("Registro modificado con éxito", ultimoMensaje().getSummary());
    }

    @Test
    void btnModificar_siElDaoFalla_muestraErrorYSeQuedaEnModificar() {
        model.seleccionarRegistro(tipo());
        when(dao.update(any())).thenThrow(new IllegalStateException("falla simulada"));

        model.btnModificarHandler();

        assertEquals(AbstractModel.ESTADO_MODIFICAR, model.getEstado());
        assertEquals("No se pudo modificar el registro", ultimoMensaje().getSummary());
    }

    @Test
    void btnModificar_sinRegistro_muestraAdvertenciaYNoActualiza() {
        model.btnModificarHandler();

        verify(dao, never()).update(any());
        assertEquals(FacesMessage.SEVERITY_WARN, ultimoMensaje().getSeverity());
    }

    // ===================== eliminar =====================

    @Test
    void btnEliminar_exitoso_eliminaYVuelveAEstadoNinguno() {
        TipoProducto t = tipo();
        model.seleccionarRegistro(t);

        model.btnEliminarHandler();

        verify(dao).delete(t);
        assertNull(model.getRegistro());
        assertEquals(AbstractModel.ESTADO_NINGUNO, model.getEstado());
        assertEquals("Registro eliminado con éxito", ultimoMensaje().getSummary());
    }

    @Test
    void btnEliminar_siElDaoFalla_muestraError() {
        model.seleccionarRegistro(tipo());
        doThrow(new IllegalStateException("falla simulada")).when(dao).delete(any());

        model.btnEliminarHandler();

        assertEquals("No se pudo eliminar el registro", ultimoMensaje().getSummary());
    }

    @Test
    void btnEliminar_sinRegistro_muestraAdvertenciaYNoElimina() {
        model.btnEliminarHandler();

        verify(dao, never()).delete(any());
        assertEquals(FacesMessage.SEVERITY_WARN, ultimoMensaje().getSeverity());
    }

    private TipoProducto tipo() {
        TipoProducto t = new TipoProducto(UUID.randomUUID());
        t.setNombre("Bebidas");
        return t;
    }
    
}
