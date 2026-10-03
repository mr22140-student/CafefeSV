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
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoCaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoCaracteristicaDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoCaracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;

/**
 *
 * @author valen
 */
public class CaracteristicaModelTest extends ModelTestBase  {
    @InjectMocks
    private CaracteristicaModel model;

    @Mock
    private CaracteristicaDAO caracteristicaDAO;

    @Mock
    private TipoCaracteristicaDAO tipoCaracteristicaDAO;

    @Mock
    private ProductoCaracteristicaDAO pcDAO;

    @Test
    void elBuscadorSoloMuestraTiposActivos() {
        TipoCaracteristica activo = tipo(true);
        TipoCaracteristica inactivo = tipo(false);
        when(tipoCaracteristicaDAO.findAll()).thenReturn(List.of(activo, inactivo));
        model.setListaTipoCaracteristica(List.of(activo, inactivo));

        List<TipoCaracteristica> resultado = model.completarTipoCaracteristica("");

        assertEquals(List.of(activo), resultado);
    }

    @Test
    void noGuardaCaracteristicaActivaConTipoInactivo() {
        model.setRegistro(caracteristica(true, tipo(false)));

        model.btnCrearHandler();

        verify(caracteristicaDAO, never()).save(any());
        assertEquals("Tipo inactivo", ultimoMensaje().getSummary());
    }

    @Test
    void permiteInactivarUnaCaracteristicaAunqueSuTipoEsteInactivo() {
        Caracteristica c = caracteristica(false, tipo(false));
        model.setRegistro(c);

        model.btnModificarHandler();

        verify(caracteristicaDAO).update(c);
    }

    @Test
    void noInactivaSiUnProductoActivoLaUsa() {
        Caracteristica c = caracteristica(false, tipo(true));
        Producto producto = new Producto(UUID.randomUUID());
        producto.setActivo(true);
        ProductoCaracteristica pc = new ProductoCaracteristica(UUID.randomUUID());
        pc.setIdProducto(producto);
        pc.setIdCaracteristica(c);
        when(pcDAO.findByCaracteristica(c)).thenReturn(List.of(pc));
        model.setRegistro(c);

        model.btnModificarHandler();

        verify(caracteristicaDAO, never()).update(any());
        assertEquals("No se puede inactivar", ultimoMensaje().getSummary());
    }

    private TipoCaracteristica tipo(boolean activo) {
        TipoCaracteristica t = new TipoCaracteristica();
        t.setIdTipoCaracteristica(UUID.randomUUID());
        t.setNombre(activo ? "Volumen" : "Colores");
        t.setActivo(activo);
        when(tipoCaracteristicaDAO.findById(t.getIdTipoCaracteristica())).thenReturn(t);
        return t;
    }

    private Caracteristica caracteristica(boolean activa, TipoCaracteristica tipo) {
        Caracteristica c = new Caracteristica();
        c.setIdCaracteristica(UUID.randomUUID());
        c.setNombre("Onzas");
        c.setActivo(activa);
        c.setIdTipoCaracteristica(tipo);
        return c;
    }
    
}
