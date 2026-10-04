package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.faces.convert.Converter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.DescuentoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.DescuentoProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.EmpleadoRolDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.OrdenDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.OrdenProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.ProductoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.DescuentoProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Empleado;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Orden;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.OrdenProducto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Rol;

/**
 *
 * @author valen
 */
public class OrdenModelTest extends ModelTestBase {

    private static final String SOLO_CAMARERO = "Solo un empleado con rol camarero puede tomar la orden.";

    @InjectMocks
    private OrdenModel model;

    @Mock
    private OrdenDAO ordenDAO;
    @Mock
    private EmpleadoRolDAO empleadoRolDAO;
    @Mock
    private EmpleadoDAO empleadoDAO;
    @Mock
    private OrdenProductoDAO ordenProductoDAO;
    @Mock
    private ProductoDAO productoDAO;
    @Mock
    private DescuentoDAO descuentoDAO;
    @Mock
    private DescuentoProductoDAO descuentoProductoDAO;

    // ===================== Datos de la orden =====================
    @Test
    void nuevaOrden_traeLaFechaActual() {
        model.btnNuevoHandler();

        assertNotNull(model.getRegistro().getFechaCreacion());
    }

    @Test
    void noGuardaUnaOrdenSinEmpleado() {
        model.btnNuevoHandler();

        model.btnCrearHandler();

        verify(ordenDAO, never()).save(any());
        assertEquals(SOLO_CAMARERO, ultimoMensaje().getSummary());
    }

    @Test
    void noGuardaUnaOrdenTomadaPorUnCajero() {
        model.btnNuevoHandler();
        model.getRegistro().setIdEmpleadoRol(asignacion(empleado("Ana", true), "Cajero", true, true));

        model.btnCrearHandler();

        verify(ordenDAO, never()).save(any());
        assertEquals(SOLO_CAMARERO, ultimoMensaje().getSummary());
    }

    @Test
    void noGuardaSiElRolCamareroEstaInactivo() {
        model.btnNuevoHandler();
        model.getRegistro().setIdEmpleadoRol(asignacion(empleado("Ana", true), "Camarero", false, true));

        model.btnCrearHandler();

        verify(ordenDAO, never()).save(any());
    }

    @Test
    void guardaUnaOrdenTomadaPorUnCamarero() {
        model.btnNuevoHandler();
        Orden orden = model.getRegistro();
        orden.setIdEmpleadoRol(asignacion(empleado("Ana", true), "Camarero", true, true));

        model.btnCrearHandler();

        verify(ordenDAO).save(orden);
    }

    @Test
    void noEliminaUnaOrdenConProductos() {
        Orden orden = orden(new Date());
        when(ordenProductoDAO.countByOrden(orden)).thenReturn(2L);
        model.seleccionarRegistro(orden);

        model.btnEliminarHandler();

        verify(ordenDAO, never()).delete(any());
    }

    // ===================== Buscador de empleados =====================
    @Test
    void elBuscador_soloMuestraCamarerosActivos() {
        Empleado activo = empleado("José", true);
        Empleado inactivo = empleado("Beto", false);
        EmpleadoRol camarero = asignacion(activo, "Camarero", true, true);
        EmpleadoRol cajero = asignacion(activo, "Cajero", true, true);
        EmpleadoRol camareroInactivo = asignacion(inactivo, "Camarero", true, true);
        EmpleadoRol asignacionInactiva = asignacion(activo, "Camarero", true, false);
        when(empleadoDAO.findRange(0, 1000)).thenReturn(List.of(activo, inactivo));
        when(empleadoRolDAO.findRange(0, 1000))
                .thenReturn(List.of(camarero, cajero, camareroInactivo, asignacionInactiva));
        model.init();

        assertEquals(List.of(camarero), model.completarEmpleadoRol(""));
    }

    @Test
    void elBuscador_encuentraSinImportarTildesNiMayusculas() {
        Empleado jose = empleado("José", true);
        EmpleadoRol er = asignacion(jose, "Camarero", true, true);
        when(empleadoDAO.findRange(0, 1000)).thenReturn(List.of(jose));
        when(empleadoRolDAO.findRange(0, 1000)).thenReturn(List.of(er));
        model.init();

        assertEquals(List.of(er), model.completarEmpleadoRol("JOSE"));
        assertEquals("José Pérez (Camarero)", model.obtenerTextoEmpleadoRol(er));
    }

    @Test
    void elConverterDelBuscador_convierteIdYEntidad() {
        Empleado jose = empleado("José", true);
        EmpleadoRol er = asignacion(jose, "Camarero", true, true);
        when(empleadoDAO.findRange(0, 1000)).thenReturn(List.of(jose));
        when(empleadoRolDAO.findRange(0, 1000)).thenReturn(List.of(er));
        model.init();
        Converter<EmpleadoRol> converter = model.getEmpleadoRolConverter();

        String id = converter.getAsString(null, null, er);

        assertEquals(er.getIdEmpleadoRol().toString(), id);
        assertEquals(er, converter.getAsObject(null, null, id));
        assertNull(converter.getAsObject(null, null, "no-es-un-uuid"));
    }

    // ===================== Productos de la orden =====================
    @Test
    void agregarProducto_usaElPrecioSugerido() {
        model.seleccionarRegistro(orden(new Date()));
        model.setProductoSeleccionado(producto("Café", "2.50", true));

        model.btnAgregarProductoHandler();

        ArgumentCaptor<OrdenProducto> captor = ArgumentCaptor.forClass(OrdenProducto.class);
        verify(ordenProductoDAO).save(captor.capture());
        assertEquals(new BigDecimal("2.50"), captor.getValue().getPrecio());
        assertEquals("Producto Café agregado.", ultimoMensaje().getSummary());
    }

    @Test
    void agregarProducto_sinElegir_avisa() {
        model.seleccionarRegistro(orden(new Date()));

        model.btnAgregarProductoHandler();

        verify(ordenProductoDAO, never()).save(any());
        assertEquals("Seleccione un producto.", ultimoMensaje().getSummary());
    }

    @Test
    void laListaDeProductos_soloTraeActivos() {
        Producto activo = producto("Café", "2.50", true);
        when(productoDAO.findRange(0, 1000)).thenReturn(List.of(activo, producto("Té", "1.00", false)));

        assertEquals(List.of(activo), model.getListaProducto());
    }

    // ===================== Descuentos =====================
    @Test
    void descuentoPorFechas_siLaOrdenEstaFueraDelRango_noSeAplicaANingunProducto() {
        Orden orden = orden(enDias(-10));
        Producto cafe = producto("Café", "10.00", true);
        OrdenProducto op = renglon(orden, cafe, "10.00");
        when(ordenProductoDAO.findByOrden(orden)).thenReturn(List.of(op));
        model.seleccionarRegistro(orden);
        model.setDescuentoSeleccionado(descuento("Navidad", enDias(-5), enDias(5)));

        model.btnAplicarDescuentoHandler();

        verify(ordenProductoDAO, never()).update(any());
        assertEquals(new BigDecimal("10.00"), op.getPrecio());
        assertTrue(ultimoMensaje().getSummary().startsWith("El descuento Navidad no aplica: la fecha de la orden"));
    }

    @Test
    void descuentoAbierto_seAplicaSoloAlProductoAutorizado_conMontoYComentario() {
        Orden orden = orden(new Date());
        Producto cafe = producto("Café", "10.00", true);
        Producto te = producto("Té", "5.00", true);
        OrdenProducto conDescuento = renglon(orden, cafe, "10.00");
        OrdenProducto sinDescuento = renglon(orden, te, "5.00");
        Descuento abierto = descuento("Navidad", enDias(-1), null);
        when(ordenProductoDAO.findByOrden(orden)).thenReturn(List.of(conDescuento, sinDescuento));
        when(descuentoProductoDAO.findByDescuento(abierto))
                .thenReturn(List.of(autorizacion(abierto, cafe, 20, enDias(-1), null)));
        model.seleccionarRegistro(orden);
        model.setDescuentoSeleccionado(abierto);

        model.btnAplicarDescuentoHandler();

        assertEquals(new BigDecimal("8.00"), conDescuento.getPrecio());
        assertEquals("Descuento Navidad (20%): -$2.00 (precio original $10.00)", conDescuento.getObservaciones());
        verify(ordenProductoDAO).update(conDescuento);
        verify(ordenProductoDAO, never()).update(sinDescuento);
        assertEquals(new BigDecimal("5.00"), sinDescuento.getPrecio());
        assertEquals("Descuento Navidad aplicado a 1 producto(s).", ultimoMensaje().getSummary());
    }

    @Test
    void siLaAutorizacionDelProductoNoCubreLaFecha_noSeAplica() {
        Orden orden = orden(new Date());
        Producto cafe = producto("Café", "10.00", true);
        Descuento d = descuento("Navidad", enDias(-1), null);
        when(ordenProductoDAO.findByOrden(orden)).thenReturn(List.of(renglon(orden, cafe, "10.00")));
        when(descuentoProductoDAO.findByDescuento(d))
                .thenReturn(List.of(autorizacion(d, cafe, 20, enDias(2), enDias(5))));
        model.seleccionarRegistro(orden);
        model.setDescuentoSeleccionado(d);

        model.btnAplicarDescuentoHandler();

        verify(ordenProductoDAO, never()).update(any());
        assertEquals("El descuento Navidad no aplica a ningún producto de esta orden.", ultimoMensaje().getSummary());
    }

    @Test
    void noAplicaDosDescuentosAlMismoProducto() {
        Orden orden = orden(new Date());
        Producto cafe = producto("Café", "10.00", true);
        OrdenProducto op = renglon(orden, cafe, "8.00");
        op.setObservaciones("Descuento Anterior (20%): -$2.00 (precio original $10.00)");
        Descuento d = descuento("Navidad", enDias(-1), null);
        when(ordenProductoDAO.findByOrden(orden)).thenReturn(List.of(op));
        when(descuentoProductoDAO.findByDescuento(d)).thenReturn(List.of(autorizacion(d, cafe, 10, enDias(-1), null)));
        model.seleccionarRegistro(orden);
        model.setDescuentoSeleccionado(d);

        model.btnAplicarDescuentoHandler();

        verify(ordenProductoDAO, never()).update(any());
        assertEquals("Los productos de este descuento ya tienen un descuento aplicado.", ultimoMensaje().getSummary());
    }

    @Test
    void noAplicaDescuentoAUnProductoYaFacturado() {
        Orden orden = orden(new Date());
        Producto cafe = producto("Café", "10.00", true);
        OrdenProducto op = renglon(orden, cafe, "10.00");
        Descuento d = descuento("Navidad", enDias(-1), null);
        when(ordenProductoDAO.findByOrden(orden)).thenReturn(List.of(op));
        when(ordenProductoDAO.countFacturas(op)).thenReturn(1L);
        when(descuentoProductoDAO.findByDescuento(d)).thenReturn(List.of(autorizacion(d, cafe, 20, enDias(-1), null)));
        model.seleccionarRegistro(orden);
        model.setDescuentoSeleccionado(d);

        model.btnAplicarDescuentoHandler();

        verify(ordenProductoDAO, never()).update(any());
    }

    @Test
    void laListaDeDescuentos_soloTraeLosVigentesEnLaFechaDeLaOrden() {
        Descuento vigente = descuento("Abierto", enDias(-1), null);
        Descuento vencido = descuento("Vencido", enDias(-10), enDias(-5));
        when(descuentoDAO.findRange(0, 1000)).thenReturn(List.of(vigente, vencido));
        model.seleccionarRegistro(orden(new Date()));

        assertEquals(List.of(vigente), model.getListaDescuento());
    }

    @Test
    void elTotal_sumaLosPreciosDeLaOrden() {
        Orden orden = orden(new Date());
        when(ordenProductoDAO.findByOrden(orden)).thenReturn(List.of(
                renglon(orden, producto("Café", "2.50", true), "2.50"),
                renglon(orden, producto("Té", "1.00", true), "1.00")));
        model.seleccionarRegistro(orden);

        assertEquals(new BigDecimal("3.50"), model.getTotal());
    }

    // ===================== ayudantes =====================
    private Empleado empleado(String nombre, boolean activo) {
        Empleado e = new Empleado(UUID.randomUUID());
        e.setNombre(nombre);
        e.setApellido("Pérez");
        e.setActivo(activo);
        return e;
    }

    private EmpleadoRol asignacion(Empleado e, String nombreRol, boolean rolActivo, boolean asignacionActiva) {
        Rol r = new Rol(UUID.randomUUID());
        r.setNombre(nombreRol);
        r.setActivo(rolActivo);
        EmpleadoRol er = new EmpleadoRol(UUID.randomUUID());
        er.setIdEmpleado(e.getIdEmpleado());
        er.setIdRol(r);
        er.setActivo(asignacionActiva);
        return er;
    }

    private Orden orden(Date fecha) {
        Orden o = new Orden(UUID.randomUUID());
        o.setFechaCreacion(fecha);
        return o;
    }

    private Producto producto(String nombre, String precio, boolean activo) {
        Producto p = new Producto(UUID.randomUUID());
        p.setNombre(nombre);
        p.setPrecioSugerido(new BigDecimal(precio));
        p.setActivo(activo);
        return p;
    }

    private OrdenProducto renglon(Orden o, Producto p, String precio) {
        OrdenProducto op = new OrdenProducto(UUID.randomUUID());
        op.setIdOrden(o);
        op.setIdProducto(p);
        op.setPrecio(new BigDecimal(precio));
        return op;
    }

    private Descuento descuento(String nombre, Date desde, Date hasta) {
        Descuento d = new Descuento(UUID.randomUUID());
        d.setNombre(nombre);
        d.setFechaDesde(desde);
        d.setFechaHasta(hasta);
        return d;
    }

    private DescuentoProducto autorizacion(Descuento d, Producto p, int valor, Date desde, Date hasta) {
        DescuentoProducto dp = new DescuentoProducto(UUID.randomUUID());
        dp.setIdDescuento(d);
        dp.setIdProducto(p);
        dp.setValor(valor);
        dp.setFechaDesde(desde);
        dp.setFechaHasta(hasta);
        return dp;
    }
}
