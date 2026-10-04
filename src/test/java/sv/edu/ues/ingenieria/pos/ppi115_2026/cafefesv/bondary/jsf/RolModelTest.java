package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoRolDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.RolDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
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
    @Mock
    private EmpleadoRolDAO empleadoRolDAO;

    @Test
    void nuevoRol_empiezaActivo() {
        model.btnNuevoHandler();

        assertEquals(AbstractModel.ESTADO_CREAR, model.getEstado());
        assertTrue(model.getRegistro().getActivo());
    }

    @Test
    void crearRol_loGuarda() {
        model.btnNuevoHandler();
        Rol rol = model.getRegistro();
        rol.setNombre("Cajero");

        model.btnCrearHandler();

        verify(rolDAO).save(rol);
    }

    @Test
    void noDesactivaUnRolAsignadoAEmpleados() {
        Rol rol = rol(false);
        when(empleadoRolDAO.findByRol(rol)).thenReturn(List.of(new EmpleadoRol(UUID.randomUUID())));
        model.seleccionarRegistro(rol);

        model.btnModificarHandler();

        verify(rolDAO, never()).update(any());
        assertTrue(rol.getActivo(), "El check de activo debe regresar a marcado");
        assertTrue(ultimoMensaje().getSummary().startsWith("No se puede desactivar"));
    }

    @Test
    void desactivaUnRolQueNadieTiene() {
        Rol rol = rol(false);
        model.seleccionarRegistro(rol);

        model.btnModificarHandler();

        verify(rolDAO).update(rol);
    }

    @Test
    void noEliminaUnRolAsignadoAEmpleados() {
        Rol rol = rol(true);
        when(empleadoRolDAO.findByRol(rol)).thenReturn(List.of(new EmpleadoRol(UUID.randomUUID())));
        model.seleccionarRegistro(rol);

        model.btnEliminarHandler();

        verify(rolDAO, never()).delete(any());
        assertTrue(ultimoMensaje().getSummary().startsWith("No se puede eliminar"));
    }

    private Rol rol(boolean activo) {
        Rol r = new Rol(UUID.randomUUID());
        r.setNombre("Barista");
        r.setActivo(activo);
        return r;
    }

}
