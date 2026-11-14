package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.EmpleadoRol;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Rol;

import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Orden;

/**
 *
 * @author valen
 */
public class OrdenDAOTest extends AbstractDAOTestBase<Orden, OrdenDAO> {

    @Override
    protected OrdenDAO crearDAO() {
        return new OrdenDAO();
    }

    @Override
    protected Orden crearEntidad() {
        return new Orden();
    }
    // ===================== validarCamarero =====================

    @Test
    void validarCamarero_conRolCamareroActivo_devuelveNull() {
        assertNull(dao.validarCamarero(asignacion("Camarero", true)));
    }

    @Test
    void validarCamarero_sinImportarMayusculasNiEspacios_devuelveNull() {
        assertNull(dao.validarCamarero(asignacion("  CAMARERO ", true)));
    }

    @Test
    void validarCamarero_conOtroRol_devuelveError() {
        assertNotNull(dao.validarCamarero(asignacion("Cajero", true)));
    }

    @Test
    void validarCamarero_conRolInactivo_devuelveError() {
        assertNotNull(dao.validarCamarero(asignacion("Camarero", false)));
    }

    @Test
    void validarCamarero_sinAsignacion_devuelveError() {
        assertNotNull(dao.validarCamarero(null));
    }

    // ===================== validarOrden =====================
    @Test
    void validarOrden_sinFecha_devuelveError() {
        Orden orden = orden(null);
        orden.setIdEmpleadoRol(asignacion("Camarero", true));

        assertNotNull(dao.validarOrden(orden));
    }

    @Test
    void validarOrden_sinEmpleado_devuelveError() {
        assertNotNull(dao.validarOrden(orden(new Date())));
    }

    @Test
    void validarOrden_conFechaYCamarero_devuelveNull() {
        Orden orden = orden(new Date());
        orden.setIdEmpleadoRol(asignacion("Camarero", true));

        assertNull(dao.validarOrden(orden));
    }

    // ===================== validarEliminar =====================
    @Test
    void validarEliminar_conProductos_devuelveError() {
        simularConteo(2);
        assertNotNull(dao.validarEliminar(orden(new Date())));
    }

    @Test
    void validarEliminar_sinProductos_devuelveNull() {
        simularConteo(0);
        assertNull(dao.validarEliminar(orden(new Date())));
    }

    // ===================== validarDescuentoEnFecha =====================
    @Test
    void validarDescuentoEnFecha_sinDescuento_devuelveError() {
        assertNotNull(dao.validarDescuentoEnFecha(orden(new Date()), null));
    }

    @Test
    void validarDescuentoEnFecha_ordenFueraDelRango_devuelveError() {
        assertNotNull(dao.validarDescuentoEnFecha(orden(enDias(-10)), descuento(enDias(-5), enDias(5))));
    }

    @Test
    void validarDescuentoEnFecha_descuentoAbierto_devuelveNull() {
        assertNull(dao.validarDescuentoEnFecha(orden(new Date()), descuento(enDias(-1), null)));
    }
    // ===================== estado de preparacion =====================

    @Test
    void buscarEstado_ordenSinCambios_devuelveRecibida() {
        assertEquals("RECIBIDA", dao.buscarEstado(UUID.randomUUID()));
    }

    @Test
    void cambiarEstado_guardaElEstadoDeEsaOrden() {
        UUID idOrden = UUID.randomUUID();
        dao.cambiarEstado(idOrden, "PREPARANDO");

        assertEquals("PREPARANDO", dao.buscarEstado(idOrden));
        assertEquals("RECIBIDA", dao.buscarEstado(UUID.randomUUID()));
    }

    @Test
    void validarEstado_estadoQueNoExiste_devuelveError() {
        assertNotNull(dao.validarEstado(UUID.randomUUID(), "COCINANDO"));
        assertNotNull(dao.validarEstado(UUID.randomUUID(), null));
    }

    @Test
    void validarEstado_avanzarOMantener_devuelveNull() {
        UUID idOrden = UUID.randomUUID();
        dao.cambiarEstado(idOrden, "PREPARANDO");

        assertNull(dao.validarEstado(idOrden, "EN_CAMINO"));
        assertNull(dao.validarEstado(idOrden, "PREPARANDO"));
    }

    @Test
    void validarEstado_regresarAUnEstadoAnterior_devuelveError() {
        UUID idOrden = UUID.randomUUID();
        dao.cambiarEstado(idOrden, "ENTREGADA");

        assertNotNull(dao.validarEstado(idOrden, "PREPARANDO"));
    }

    // ===================== ayudantes =====================
    private EmpleadoRol asignacion(String nombreRol, boolean rolActivo) {
        Rol rol = new Rol(UUID.randomUUID());
        rol.setNombre(nombreRol);
        rol.setActivo(rolActivo);
        EmpleadoRol asignacion = new EmpleadoRol(UUID.randomUUID());
        asignacion.setIdEmpleado(UUID.randomUUID());
        asignacion.setIdRol(rol);
        asignacion.setActivo(true);
        return asignacion;
    }

    private Orden orden(Date fechaCreacion) {
        Orden orden = new Orden(UUID.randomUUID());
        orden.setFechaCreacion(fechaCreacion);
        return orden;
    }

    private Descuento descuento(Date fechaDesde, Date fechaHasta) {
        Descuento navidad = new Descuento(UUID.randomUUID());
        navidad.setNombre("Navidad");
        navidad.setFechaDesde(fechaDesde);
        navidad.setFechaHasta(fechaHasta);
        return navidad;
    }

}
