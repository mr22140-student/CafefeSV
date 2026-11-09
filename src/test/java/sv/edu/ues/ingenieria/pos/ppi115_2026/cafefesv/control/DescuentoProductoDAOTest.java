package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.DescuentoProducto;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoDescuento;

/**
 *
 * @author valen
 */
public class DescuentoProductoDAOTest extends AbstractDAOTestBase<DescuentoProducto, DescuentoProductoDAO> {

    @Override
    protected DescuentoProductoDAO crearDAO() {
        return new DescuentoProductoDAO();
    }

    @Override
    protected DescuentoProducto crearEntidad() {
        return new DescuentoProducto();
    }
    // ===================== validarDescuento =====================

    @Test
    void validarDescuento_sinDescuento_devuelveError() {
        assertNotNull(dao.validarDescuento(asignacion(null, 10, enDias(0), null)));
    }

    @Test
    void validarDescuento_conTipoInactivo_devuelveError() {
        Descuento navidad = descuento(tipoDescuento(false, 30), enDias(-1), enDias(10));
        when(em.find(Descuento.class, navidad.getIdDescuento())).thenReturn(navidad);

        assertNotNull(dao.validarDescuento(asignacion(navidad, 10, enDias(0), enDias(5))));
    }

    @Test
    void validarDescuento_vencido_devuelveError() {
        Descuento navidad = descuento(tipoDescuento(true, 30), enDias(-10), enDias(-1));
        when(em.find(Descuento.class, navidad.getIdDescuento())).thenReturn(navidad);

        assertNotNull(dao.validarDescuento(asignacion(navidad, 10, enDias(0), null)));
    }

    @Test
    void validarDescuento_vigenteConTipoActivo_devuelveNull() {
        Descuento navidad = descuento(tipoDescuento(true, 30), enDias(-1), enDias(10));
        when(em.find(Descuento.class, navidad.getIdDescuento())).thenReturn(navidad);

        assertNull(dao.validarDescuento(asignacion(navidad, 10, enDias(0), enDias(5))));
    }

    // ===================== validarValor =====================
    @Test
    void validarValor_cero_devuelveError() {
        Descuento navidad = descuento(tipoDescuento(true, 30), enDias(-1), enDias(10));
        assertNotNull(dao.validarValor(asignacion(navidad, 0, enDias(0), enDias(5))));
    }

    @Test
    void validarValor_mayorAlMaximoDelTipo_devuelveError() {
        Descuento navidad = descuento(tipoDescuento(true, 30), enDias(-1), enDias(10));
        assertNotNull(dao.validarValor(asignacion(navidad, 40, enDias(0), enDias(5))));
    }

    @Test
    void validarValor_dentroDelMaximo_devuelveNull() {
        Descuento navidad = descuento(tipoDescuento(true, 30), enDias(-1), enDias(10));
        assertNull(dao.validarValor(asignacion(navidad, 20, enDias(0), enDias(5))));
    }

    // ===================== validarFechas =====================
    @Test
    void validarFechas_sinFechaDeInicio_devuelveError() {
        Descuento navidad = descuento(tipoDescuento(true, 30), enDias(-1), enDias(10));
        assertNotNull(dao.validarFechas(asignacion(navidad, 10, null, enDias(5))));
    }

    @Test
    void validarFechas_inicioAntesDeQueEmpieceElDescuento_devuelveError() {
        Descuento navidad = descuento(tipoDescuento(true, 30), enDias(2), enDias(10));
        assertNotNull(dao.validarFechas(asignacion(navidad, 10, enDias(1), enDias(5))));
    }

    @Test
    void validarFechas_inicioDespuesDelFinDelDescuento_devuelveError() {
        Descuento navidad = descuento(tipoDescuento(true, 30), enDias(-1), enDias(10));
        assertNotNull(dao.validarFechas(asignacion(navidad, 10, enDias(11), enDias(12))));
    }

    @Test
    void validarFechas_descuentoConFinYAsignacionSinFin_devuelveError() {
        Descuento navidad = descuento(tipoDescuento(true, 30), enDias(-1), enDias(10));
        assertNotNull(dao.validarFechas(asignacion(navidad, 10, enDias(0), null)));
    }

    @Test
    void validarFechas_finAntesDelInicio_devuelveError() {
        Descuento navidad = descuento(tipoDescuento(true, 30), enDias(-1), enDias(10));
        assertNotNull(dao.validarFechas(asignacion(navidad, 10, enDias(5), enDias(2))));
    }

    @Test
    void validarFechas_finDespuesDelFinDelDescuento_devuelveError() {
        Descuento navidad = descuento(tipoDescuento(true, 30), enDias(-1), enDias(10));
        assertNotNull(dao.validarFechas(asignacion(navidad, 10, enDias(0), enDias(15))));
    }

    @Test
    void validarFechas_descuentoAbiertoSinFin_devuelveNull() {
        Descuento frecuente = descuento(tipoDescuento(true, 15), enDias(-1), null);
        assertNull(dao.validarFechas(asignacion(frecuente, 10, enDias(0), null)));
    }

    @Test
    void validarFechas_dentroDeLaVigencia_devuelveNull() {
        Descuento navidad = descuento(tipoDescuento(true, 30), enDias(-1), enDias(10));
        assertNull(dao.validarFechas(asignacion(navidad, 10, enDias(0), enDias(5))));
    }

    // ===================== validarDuplicado =====================
    @Test
    void validarDuplicado_descuentoYaAsignado_devuelveError() {
        Descuento navidad = descuento(tipoDescuento(true, 30), enDias(-1), enDias(10));
        DescuentoProducto guardada = asignacion(navidad, 10, enDias(0), enDias(5));
        simularConsulta(DescuentoProducto.class, List.of(guardada));

        DescuentoProducto nueva = asignacion(navidad, 20, enDias(0), enDias(5));
        nueva.setIdProducto(guardada.getIdProducto());

        assertNotNull(dao.validarDuplicado(nueva));
    }

    @Test
    void validarDuplicado_descuentoNuevo_devuelveNull() {
        DescuentoProducto guardada = asignacion(descuento(tipoDescuento(true, 30), enDias(-1), enDias(10)), 10, enDias(0), enDias(5));
        simularConsulta(DescuentoProducto.class, List.of(guardada));

        DescuentoProducto nueva = asignacion(descuento(tipoDescuento(true, 15), enDias(-1), null), 10, enDias(0), null);
        nueva.setIdProducto(guardada.getIdProducto());

        assertNull(dao.validarDuplicado(nueva));
    }
    // ===================== buscarAutorizacion =====================

    @Test
    void buscarAutorizacion_productoAutorizadoEnLaFecha_laDevuelve() {
        Descuento navidad = descuento(tipoDescuento(true, 30), enDias(-1), null);
        DescuentoProducto autorizacion = asignacion(navidad, 20, enDias(-1), null);
        simularConsulta(DescuentoProducto.class, List.of(autorizacion));

        assertEquals(autorizacion, dao.buscarAutorizacion(navidad, autorizacion.getIdProducto(), new Date()));
    }

    @Test
    void buscarAutorizacion_siNoCubreLaFecha_devuelveNull() {
        Descuento navidad = descuento(tipoDescuento(true, 30), enDias(-1), null);
        DescuentoProducto autorizacion = asignacion(navidad, 20, enDias(2), enDias(5));
        simularConsulta(DescuentoProducto.class, List.of(autorizacion));

        assertNull(dao.buscarAutorizacion(navidad, autorizacion.getIdProducto(), new Date()));
    }

    @Test
    void buscarAutorizacion_deOtroProducto_devuelveNull() {
        Descuento navidad = descuento(tipoDescuento(true, 30), enDias(-1), null);
        simularConsulta(DescuentoProducto.class, List.of(asignacion(navidad, 20, enDias(-1), null)));

        assertNull(dao.buscarAutorizacion(navidad, new Producto(UUID.randomUUID()), new Date()));
    }

    // ===================== ayudantes =====================
    private TipoDescuento tipoDescuento(boolean activo, int descuentoMaximo) {
        TipoDescuento tipoDescuento = new TipoDescuento(UUID.randomUUID());
        tipoDescuento.setNombre("Temporada");
        tipoDescuento.setActivo(activo);
        tipoDescuento.setDescuentoMaximo(descuentoMaximo);
        return tipoDescuento;
    }

    private Descuento descuento(TipoDescuento tipoDescuento, Date fechaDesde, Date fechaHasta) {
        Descuento descuento = new Descuento(UUID.randomUUID());
        descuento.setNombre("Navidad");
        descuento.setIdTipoDescuento(tipoDescuento);
        descuento.setFechaDesde(fechaDesde);
        descuento.setFechaHasta(fechaHasta);
        return descuento;
    }

    private DescuentoProducto asignacion(Descuento descuento, Integer porcentaje, Date fechaDesde, Date fechaHasta) {
        Producto producto = new Producto(UUID.randomUUID());
        producto.setNombre("Café latte");
        DescuentoProducto asignacion = new DescuentoProducto(UUID.randomUUID());
        asignacion.setIdProducto(producto);
        asignacion.setIdDescuento(descuento);
        asignacion.setValor(porcentaje);
        asignacion.setFechaDesde(fechaDesde);
        asignacion.setFechaHasta(fechaHasta);
        return asignacion;
    }

}
