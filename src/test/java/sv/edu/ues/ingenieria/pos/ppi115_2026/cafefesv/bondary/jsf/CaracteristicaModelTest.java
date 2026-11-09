package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.CaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoCaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;

/**
 * El model solo llama a las reglas del DAO. Las reglas en sí se prueban en
 * CaracteristicaDAOTest y TipoCaracteristicaDAOTest.
 *
 * @author valen
 */
public class CaracteristicaModelTest extends ModelTestBase {

    @InjectMocks
    private CaracteristicaModel model;

    @Mock
    private CaracteristicaDAO caracteristicaDAO;

    @Mock
    private TipoCaracteristicaDAO tipoCaracteristicaDAO;

    @Test
    void elBuscadorDevuelveLoQueEncuentraElDao() {
        TipoCaracteristica volumen = new TipoCaracteristica(UUID.randomUUID());
        when(tipoCaracteristicaDAO.buscarActivosPorNombre("vol")).thenReturn(List.of(volumen));

        assertEquals(List.of(volumen), model.completarTipoCaracteristica("vol"));
    }

    @Test
    void noCreaSiElTipoEstaInactivo() {
        Caracteristica caracteristica = caracteristica();
        when(caracteristicaDAO.validarTipoActivo(caracteristica)).thenReturn("El tipo está inactivo.");
        model.setRegistro(caracteristica);

        model.btnCrearHandler();

        verify(caracteristicaDAO, never()).save(any());
        assertEquals("Tipo inactivo", ultimoMensaje().getSummary());
    }

    @Test
    void modificaSiElTipoEstaActivo() {
        Caracteristica caracteristica = caracteristica();
        when(caracteristicaDAO.validarTipoActivo(caracteristica)).thenReturn(null);
        model.setRegistro(caracteristica);

        model.btnModificarHandler();

        verify(caracteristicaDAO).update(caracteristica);
    }

    @Test
    void noEliminaSiUnProductoLaTiene() {
        Caracteristica caracteristica = caracteristica();
        when(caracteristicaDAO.validarEliminar(caracteristica)).thenReturn("Hay productos que tienen esta característica.");
        model.setRegistro(caracteristica);

        model.btnEliminarHandler();

        verify(caracteristicaDAO, never()).delete(any());
        assertEquals("No se puede eliminar", ultimoMensaje().getSummary());
    }

    private Caracteristica caracteristica() {
        Caracteristica caracteristica = new Caracteristica(UUID.randomUUID());
        caracteristica.setNombre("Vaso grande");
        caracteristica.setActivo(true);
        return caracteristica;
    }
}