package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.DescuentoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.DescuentoProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.DescuentoProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoDescuento;

/**
 *
 * @author valen
 */
public class DescuentoProductoModelTest extends ModelTestBase {

    @InjectMocks
    private DescuentoProductoModel model;

    @Mock
    private DescuentoProductoDAO dpDAO;
    @Mock
    private DescuentoDAO descuentoDAO;
    @Mock
    private ProductoDAO productoDAO;

    @Test
    void rechazaUnValorMayorAlMaximoDelTipo() {
        model.setRegistro(registro(descuento(30, enDias(-1), enDias(10)), 40, enDias(0), enDias(5)));

        model.btnCrearHandler();

        verify(dpDAO, never()).save(any());
        assertEquals("Valor inválido", ultimoMensaje().getSummary());
    }

    @Test
    void rechazaFechaHastaAnteriorAFechaDesde() {
        model.setRegistro(registro(descuento(30, enDias(-1), null), 10, enDias(5), enDias(1)));

        model.btnCrearHandler();

        verify(dpDAO, never()).save(any());
        assertEquals("Fechas inválidas", ultimoMensaje().getSummary());
    }

    @Test
    void noGuardaElMismoDescuentoDosVeces() {
        Descuento d = descuento(30, enDias(-1), null);
        DescuentoProducto existente = registro(d, 5, enDias(0), null);
        when(dpDAO.findByProducto(existente.getIdProducto())).thenReturn(List.of(existente));
        DescuentoProducto nuevo = registro(d, 10, enDias(0), null);
        nuevo.setIdProducto(existente.getIdProducto());
        model.setRegistro(nuevo);

        model.btnCrearHandler();

        verify(dpDAO, never()).save(any());
        assertEquals("Registro duplicado", ultimoMensaje().getSummary());
    }

    @Test
    void guardaUnDescuentoValido() {
        DescuentoProducto dp = registro(descuento(30, enDias(-1), null), 10, enDias(0), null);
        model.setRegistro(dp);

        model.btnCrearHandler();

        verify(dpDAO).save(dp);
    }

    @Test
    void alElegirUnDescuentoFuturo_proponeSusFechas() {
        Date inicio = enDias(3);
        Date fin = enDias(10);
        model.btnNuevoHandler();
        model.getRegistro().setIdDescuento(descuento(30, inicio, fin));

        model.onDescuentoChange();

        assertEquals(inicio, model.getRegistro().getFechaDesde());
        assertEquals(fin, model.getRegistro().getFechaHasta());
    }

    @Test
    void alElegirUnDescuentoVencido_avisa() {
        model.btnNuevoHandler();
        model.getRegistro().setIdDescuento(descuento(30, enDias(-10), enDias(-1)));

        model.onDescuentoChange();

        assertEquals("Descuento vencido", ultimoMensaje().getSummary());
    }

    private Descuento descuento(int maximo, Date desde, Date hasta) {
        TipoDescuento tipo = new TipoDescuento(UUID.randomUUID());
        tipo.setActivo(true);
        tipo.setDescuentoMaximo(maximo);
        Descuento d = new Descuento(UUID.randomUUID());
        d.setNombre("Navidad");
        d.setIdTipoDescuento(tipo);
        d.setFechaDesde(desde);
        d.setFechaHasta(hasta);
        return d;
    }

    private DescuentoProducto registro(Descuento d, Integer valor, Date desde, Date hasta) {
        DescuentoProducto dp = new DescuentoProducto(UUID.randomUUID());
        dp.setIdProducto(new Producto(UUID.randomUUID()));
        dp.setIdDescuento(d);
        dp.setValor(valor);
        dp.setFechaDesde(desde);
        dp.setFechaHasta(hasta);
        return dp;
    }

}
