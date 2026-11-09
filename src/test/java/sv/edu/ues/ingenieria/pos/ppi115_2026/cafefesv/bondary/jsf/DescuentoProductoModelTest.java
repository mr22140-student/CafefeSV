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
    private DescuentoProductoDAO descuentoProductoDAO;
    @Mock
    private DescuentoDAO descuentoDAO;
    @Mock
    private ProductoDAO productoDAO;

    @Test
    void noGuardaSiElDaoRechazaElDescuento() {
        DescuentoProducto asignacion = registro(descuento(30, enDias(-1), enDias(10)), 10, enDias(0), enDias(5));
        when(descuentoProductoDAO.validarDescuento(asignacion)).thenReturn("El descuento terminó.");
        model.setRegistro(asignacion);

        model.btnCrearHandler();

        verify(descuentoProductoDAO, never()).save(any());
        assertEquals("Descuento no válido", ultimoMensaje().getSummary());
    }

    @Test
    void noGuardaSiElDaoRechazaElPorcentaje() {
        DescuentoProducto asignacion = registro(descuento(30, enDias(-1), enDias(10)), 40, enDias(0), enDias(5));
        when(descuentoProductoDAO.validarValor(asignacion)).thenReturn("Supera el máximo.");
        model.setRegistro(asignacion);

        model.btnCrearHandler();

        verify(descuentoProductoDAO, never()).save(any());
        assertEquals("Porcentaje no válido", ultimoMensaje().getSummary());
    }

    @Test
    void noGuardaSiElDaoRechazaLasFechas() {
        DescuentoProducto asignacion = registro(descuento(30, enDias(-1), null), 10, enDias(5), enDias(1));
        when(descuentoProductoDAO.validarFechas(asignacion)).thenReturn("La fecha final es anterior a la inicial.");
        model.setRegistro(asignacion);

        model.btnCrearHandler();

        verify(descuentoProductoDAO, never()).save(any());
        assertEquals("Fechas no válidas", ultimoMensaje().getSummary());
    }

    @Test
    void noGuardaSiElDaoEncuentraElDescuentoRepetido() {
        DescuentoProducto asignacion = registro(descuento(30, enDias(-1), null), 10, enDias(0), null);
        when(descuentoProductoDAO.validarDuplicado(asignacion)).thenReturn("El producto ya tiene asignado ese descuento.");
        model.setRegistro(asignacion);

        model.btnCrearHandler();

        verify(descuentoProductoDAO, never()).save(any());
        assertEquals("Registro duplicado", ultimoMensaje().getSummary());
    }

    @Test
    void guardaSiElDaoNoEncuentraErrores() {
        DescuentoProducto asignacion = registro(descuento(30, enDias(-1), null), 10, enDias(0), null);
        model.setRegistro(asignacion);

        model.btnCrearHandler();

        verify(descuentoProductoDAO).save(asignacion);
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

    private Descuento descuento(int descuentoMaximo, Date fechaDesde, Date fechaHasta) {
        TipoDescuento temporada = new TipoDescuento(UUID.randomUUID());
        temporada.setActivo(true);
        temporada.setDescuentoMaximo(descuentoMaximo);
        Descuento navidad = new Descuento(UUID.randomUUID());
        navidad.setNombre("Navidad");
        navidad.setIdTipoDescuento(temporada);
        navidad.setFechaDesde(fechaDesde);
        navidad.setFechaHasta(fechaHasta);
        return navidad;
    }

    private DescuentoProducto registro(Descuento descuento, Integer porcentaje, Date fechaDesde, Date fechaHasta) {
        DescuentoProducto asignacion = new DescuentoProducto(UUID.randomUUID());
        asignacion.setIdProducto(new Producto(UUID.randomUUID()));
        asignacion.setIdDescuento(descuento);
        asignacion.setValor(porcentaje);
        asignacion.setFechaDesde(fechaDesde);
        asignacion.setFechaHasta(fechaHasta);
        return asignacion;
    }

}
