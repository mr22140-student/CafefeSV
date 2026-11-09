package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.RolDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Rol;

/**
 *
 * @author valen
 */
public class RolModelTest extends ModelTestBase {

    @InjectMocks
    private RolModel model;

    @Mock
    private RolDAO rolDAO;

    @Test
    void nuevoRol_empiezaActivo() {
        model.btnNuevoHandler();

        assertEquals(AbstractModel.ESTADO_CREAR, model.getEstado());
        assertTrue(model.getRegistro().getActivo());
    }

    @Test
    void crearRol_loGuarda() {
        model.btnNuevoHandler();
        Rol cajero = model.getRegistro();
        cajero.setNombre("Cajero");

        model.btnCrearHandler();

        verify(rolDAO).save(cajero);
    }

    @Test
    void noDesactivaSiElDaoLoRechaza_yRegresaElCheck() {
        Rol barista = rol(false);
        when(rolDAO.validarInactivar(barista)).thenReturn("El rol está asignado a 1 empleado(s).");
        model.seleccionarRegistro(barista);

        model.btnModificarHandler();

        verify(rolDAO, never()).update(any());
        assertTrue(barista.getActivo(), "El check de activo debe regresar a marcado");
        assertEquals("No se puede desactivar", ultimoMensaje().getSummary());
    }

    @Test
    void desactivaSiElDaoNoEncuentraErrores() {
        Rol barista = rol(false);
        model.seleccionarRegistro(barista);

        model.btnModificarHandler();

        verify(rolDAO).update(barista);
    }

    @Test
    void noEliminaSiElDaoLoRechaza() {
        Rol barista = rol(true);
        when(rolDAO.validarEliminar(barista)).thenReturn("El rol está asignado a uno o más empleados.");
        model.seleccionarRegistro(barista);

        model.btnEliminarHandler();

        verify(rolDAO, never()).delete(any());
        assertEquals("No se puede eliminar", ultimoMensaje().getSummary());
    }

    private Rol rol(boolean activo) {
        Rol barista = new Rol(UUID.randomUUID());
        barista.setNombre("Barista");
        barista.setActivo(activo);
        return barista;
    }
}