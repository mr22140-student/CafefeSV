package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    void noGuardaSiElDaoRechazaLaOrden() {
        model.btnNuevoHandler();
        Orden orden = model.getRegistro();
        orden.setIdEmpleadoRol(asignacion(empleado("Ana", true), "Cajero", true, true));
        when(ordenDAO.validarOrden(orden)).thenReturn("Solo un empleado con rol camarero puede tomar la orden.");

        model.btnCrearHandler();

        verify(ordenDAO, never()).save(any());
        assertEquals("Orden no válida", ultimoMensaje().getSummary());
    }

    @Test
    void guardaSiElDaoNoEncuentraErrores() {
        model.btnNuevoHandler();
        Orden orden = model.getRegistro();
        orden.setIdEmpleadoRol(asignacion(empleado("Ana", true), "Camarero", true, true));

        model.btnCrearHandler();

        verify(ordenDAO).save(orden);
    }

    @Test
    void noEliminaSiElDaoLaRechaza() {
        Orden orden = orden(new Date());
        when(ordenDAO.validarEliminar(orden)).thenReturn("La orden tiene productos.");
        model.seleccionarRegistro(orden);

        model.btnEliminarHandler();

        verify(ordenDAO, never()).delete(any());
        assertEquals("No se puede eliminar", ultimoMensaje().getSummary());
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
        when(ordenDAO.validarCamarero(cajero)).thenReturn("Solo un empleado con rol camarero puede tomar la orden.");
        model.init();

        assertEquals(List.of(camarero), model.completarEmpleadoRol(""));
    }

    @Test
    void elBuscador_encuentraSinImportarTildesNiMayusculas() {
        Empleado jose = empleado("José", true);
        EmpleadoRol camarero = asignacion(jose, "Camarero", true, true);
        when(empleadoDAO.findRange(0, 1000)).thenReturn(List.of(jose));
        when(empleadoRolDAO.findRange(0, 1000)).thenReturn(List.of(camarero));
        model.init();

        assertEquals(List.of(camarero), model.completarEmpleadoRol("JOSE"));
        assertEquals("José Pérez (Camarero)", model.obtenerTextoEmpleadoRol(camarero));
    }

    @Test
    void elConverterDelBuscador_convierteIdYEntidad() {
        Empleado jose = empleado("José", true);
        EmpleadoRol camarero = asignacion(jose, "Camarero", true, true);
        when(empleadoDAO.findRange(0, 1000)).thenReturn(List.of(jose));
        when(empleadoRolDAO.findRange(0, 1000)).thenReturn(List.of(camarero));
        model.init();
        Converter<EmpleadoRol> converter = model.getEmpleadoRolConverter();

        String id = converter.getAsString(null, null, camarero);

        assertEquals(camarero.getIdEmpleadoRol().toString(), id);
        assertEquals(camarero, converter.getAsObject(null, null, id));
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
    void agregarProducto_siElDaoLoRechaza_noGuarda() {
        model.seleccionarRegistro(orden(new Date()));
        when(ordenProductoDAO.validarProductoElegido(null)).thenReturn("Seleccione un producto.");

        model.btnAgregarProductoHandler();

        verify(ordenProductoDAO, never()).save(any());
        assertEquals("Producto no válido", ultimoMensaje().getSummary());
    }

    @Test
    void laListaDeProductos_laTraeElDao() {
        List<Producto> activos = List.of(producto("Café", "2.50", true));
        when(productoDAO.findByActivo(Boolean.TRUE)).thenReturn(activos);

        assertEquals(activos, model.getListaProducto());
    }

    // ===================== Descuentos =====================
    @Test
    void siElDaoRechazaElDescuentoPorFecha_noSeAplicaANingunProducto() {
        Orden orden = orden(enDias(-10));
        OrdenProducto detalle = detalle(orden, producto("Café", "10.00", true), "10.00");
        Descuento navidad = descuento("Navidad", enDias(-5), enDias(5));
        when(ordenProductoDAO.findByOrden(orden)).thenReturn(List.of(detalle));
        when(ordenDAO.validarDescuentoEnFecha(orden, navidad)).thenReturn("El descuento Navidad no aplica.");
        model.seleccionarRegistro(orden);
        model.setDescuentoSeleccionado(navidad);

        model.btnAplicarDescuentoHandler();

        verify(ordenProductoDAO, never()).update(any());
        assertEquals(new BigDecimal("10.00"), detalle.getPrecio());
        assertEquals("Descuento no aplicado", ultimoMensaje().getSummary());
    }

    @Test
    void aplicaElDescuentoSoloAlProductoAutorizado_conMontoYComentario() {
        Orden orden = orden(new Date());
        Producto cafe = producto("Café", "10.00", true);
        Producto te = producto("Té", "5.00", true);
        OrdenProducto conDescuento = detalle(orden, cafe, "10.00");
        OrdenProducto sinDescuento = detalle(orden, te, "5.00");
        Descuento navidad = descuento("Navidad", enDias(-1), null);
        when(ordenProductoDAO.findByOrden(orden)).thenReturn(List.of(conDescuento, sinDescuento));
        when(descuentoProductoDAO.buscarAutorizacion(navidad, cafe, orden.getFechaCreacion()))
                .thenReturn(autorizacion(navidad, cafe, 20, enDias(-1), null));
        when(ordenProductoDAO.calcularMontoDescuento(new BigDecimal("10.00"), 20)).thenReturn(new BigDecimal("2.00"));
        model.seleccionarRegistro(orden);
        model.setDescuentoSeleccionado(navidad);

        model.btnAplicarDescuentoHandler();

        assertEquals(new BigDecimal("8.00"), conDescuento.getPrecio());
        assertEquals("Descuento Navidad (20%): -$2.00 (precio original $10.00)", conDescuento.getObservaciones());
        verify(ordenProductoDAO).update(conDescuento);
        verify(ordenProductoDAO, never()).update(sinDescuento);
        assertEquals(new BigDecimal("5.00"), sinDescuento.getPrecio());
        assertEquals("Descuento Navidad aplicado a 1 producto(s).", ultimoMensaje().getSummary());
    }

    @Test
    void siNingunProductoEstaAutorizado_avisa() {
        Orden orden = orden(new Date());
        Descuento navidad = descuento("Navidad", enDias(-1), null);
        when(ordenProductoDAO.findByOrden(orden)).thenReturn(List.of(detalle(orden, producto("Café", "10.00", true), "10.00")));
        model.seleccionarRegistro(orden);
        model.setDescuentoSeleccionado(navidad);

        model.btnAplicarDescuentoHandler();

        verify(ordenProductoDAO, never()).update(any());
        assertEquals("El descuento Navidad no aplica a ningún producto de esta orden.", ultimoMensaje().getSummary());
    }

    @Test
    void siElDaoDiceQueYaTieneDescuento_noAplicaOtro() {
        Orden orden = orden(new Date());
        Producto cafe = producto("Café", "10.00", true);
        OrdenProducto detalle = detalle(orden, cafe, "8.00");
        Descuento navidad = descuento("Navidad", enDias(-1), null);
        when(ordenProductoDAO.findByOrden(orden)).thenReturn(List.of(detalle));
        when(descuentoProductoDAO.buscarAutorizacion(navidad, cafe, orden.getFechaCreacion()))
                .thenReturn(autorizacion(navidad, cafe, 10, enDias(-1), null));
        when(ordenProductoDAO.validarSinDescuento(detalle)).thenReturn("El producto ya tiene un descuento aplicado.");
        model.seleccionarRegistro(orden);
        model.setDescuentoSeleccionado(navidad);

        model.btnAplicarDescuentoHandler();

        verify(ordenProductoDAO, never()).update(any());
        assertEquals("Los productos de este descuento ya tienen un descuento aplicado.", ultimoMensaje().getSummary());
    }

    @Test
    void siElDaoDiceQueYaEstaFacturado_noAplicaElDescuento() {
        Orden orden = orden(new Date());
        Producto cafe = producto("Café", "10.00", true);
        OrdenProducto detalle = detalle(orden, cafe, "10.00");
        Descuento navidad = descuento("Navidad", enDias(-1), null);
        when(ordenProductoDAO.findByOrden(orden)).thenReturn(List.of(detalle));
        when(descuentoProductoDAO.buscarAutorizacion(navidad, cafe, orden.getFechaCreacion()))
                .thenReturn(autorizacion(navidad, cafe, 20, enDias(-1), null));
        when(ordenProductoDAO.validarNoFacturado(detalle)).thenReturn("Este producto ya está facturado.");
        model.seleccionarRegistro(orden);
        model.setDescuentoSeleccionado(navidad);

        model.btnAplicarDescuentoHandler();

        verify(ordenProductoDAO, never()).update(any());
    }

    @Test
    void laListaDeDescuentos_laPideAlDaoConLaFechaDeLaOrden() {
        Orden orden = orden(new Date());
        List<Descuento> vigentes = List.of(descuento("Abierto", enDias(-1), null));
        when(descuentoDAO.buscarVigentesEn(orden.getFechaCreacion())).thenReturn(vigentes);
        model.seleccionarRegistro(orden);

        assertEquals(vigentes, model.getListaDescuento());
    }

    @Test
    void elTotal_loCalculaElDao() {
        Orden orden = orden(new Date());
        List<OrdenProducto> detalles = List.of(detalle(orden, producto("Café", "2.50", true), "2.50"));
        when(ordenProductoDAO.findByOrden(orden)).thenReturn(detalles);
        when(ordenProductoDAO.calcularTotal(detalles)).thenReturn(new BigDecimal("2.50"));
        model.seleccionarRegistro(orden);

        assertEquals(new BigDecimal("2.50"), model.getTotal());
    }

    // ===================== ayudantes =====================
    private Empleado empleado(String nombre, boolean activo) {
        Empleado empleado = new Empleado(UUID.randomUUID());
        empleado.setNombre(nombre);
        empleado.setApellido("Pérez");
        empleado.setActivo(activo);
        return empleado;
    }

    private EmpleadoRol asignacion(Empleado empleado, String nombreRol, boolean rolActivo, boolean asignacionActiva) {
        Rol rol = new Rol(UUID.randomUUID());
        rol.setNombre(nombreRol);
        rol.setActivo(rolActivo);
        EmpleadoRol asignacion = new EmpleadoRol(UUID.randomUUID());
        asignacion.setIdEmpleado(empleado.getIdEmpleado());
        asignacion.setIdRol(rol);
        asignacion.setActivo(asignacionActiva);
        return asignacion;
    }

    private Orden orden(Date fechaCreacion) {
        Orden orden = new Orden(UUID.randomUUID());
        orden.setFechaCreacion(fechaCreacion);
        return orden;
    }

    private Producto producto(String nombre, String precio, boolean activo) {
        Producto producto = new Producto(UUID.randomUUID());
        producto.setNombre(nombre);
        producto.setPrecioSugerido(new BigDecimal(precio));
        producto.setActivo(activo);
        return producto;
    }

    private OrdenProducto detalle(Orden orden, Producto producto, String precio) {
        OrdenProducto detalle = new OrdenProducto(UUID.randomUUID());
        detalle.setIdOrden(orden);
        detalle.setIdProducto(producto);
        detalle.setPrecio(new BigDecimal(precio));
        return detalle;
    }

    private Descuento descuento(String nombre, Date fechaDesde, Date fechaHasta) {
        Descuento descuento = new Descuento(UUID.randomUUID());
        descuento.setNombre(nombre);
        descuento.setFechaDesde(fechaDesde);
        descuento.setFechaHasta(fechaHasta);
        return descuento;
    }

    private DescuentoProducto autorizacion(Descuento descuento, Producto producto, int porcentaje, Date fechaDesde, Date fechaHasta) {
        DescuentoProducto autorizacion = new DescuentoProducto(UUID.randomUUID());
        autorizacion.setIdDescuento(descuento);
        autorizacion.setIdProducto(producto);
        autorizacion.setValor(porcentaje);
        autorizacion.setFechaDesde(fechaDesde);
        autorizacion.setFechaHasta(fechaHasta);
        return autorizacion;
    }
}