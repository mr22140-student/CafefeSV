package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.ProductoCaracteristica;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Caracteristica;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Producto;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;

/**
 *
 * @author valen
 */
public class ProductoCaracteristicaDAOTest extends AbstractDAOTestBase<ProductoCaracteristica, ProductoCaracteristicaDAO> {

    private static final String SOLO_NUMEROS = "^\\d+(\\.\\d{1,2})?$";

    @Override
    protected ProductoCaracteristicaDAO crearDAO() {
        return new ProductoCaracteristicaDAO();
    }

    @Override
    protected ProductoCaracteristica crearEntidad() {
        return new ProductoCaracteristica();
    }
    // ===================== validarCaracteristica =====================

    @Test
    void validarCaracteristica_sinCaracteristica_devuelveError() {
        assertNotNull(dao.validarCaracteristica(asignacion(producto(), null, "250")));
    }

    @Test
    void validarCaracteristica_inactiva_devuelveError() {
        Caracteristica vasoGrande = caracteristica(false, tipoCaracteristica(true, SOLO_NUMEROS));
        when(em.find(Caracteristica.class, vasoGrande.getIdCaracteristica())).thenReturn(vasoGrande);

        assertNotNull(dao.validarCaracteristica(asignacion(producto(), vasoGrande, "250")));
    }

    @Test
    void validarCaracteristica_conTipoInactivo_devuelveError() {
        Caracteristica vasoGrande = caracteristica(true, tipoCaracteristica(false, SOLO_NUMEROS));
        when(em.find(Caracteristica.class, vasoGrande.getIdCaracteristica())).thenReturn(vasoGrande);

        assertNotNull(dao.validarCaracteristica(asignacion(producto(), vasoGrande, "250")));
    }

    @Test
    void validarCaracteristica_activaConTipoActivo_devuelveNull() {
        Caracteristica vasoGrande = caracteristica(true, tipoCaracteristica(true, SOLO_NUMEROS));
        when(em.find(Caracteristica.class, vasoGrande.getIdCaracteristica())).thenReturn(vasoGrande);

        assertNull(dao.validarCaracteristica(asignacion(producto(), vasoGrande, "250")));
    }

    // ===================== validarValor =====================
    @Test
    void validarValor_vacio_devuelveError() {
        Caracteristica vasoGrande = caracteristica(true, tipoCaracteristica(true, SOLO_NUMEROS));
        assertNotNull(dao.validarValor(asignacion(producto(), vasoGrande, "   ")));
    }

    @Test
    void validarValor_queNoCumpleLaExpresion_devuelveError() {
        Caracteristica vasoGrande = caracteristica(true, tipoCaracteristica(true, SOLO_NUMEROS));
        assertNotNull(dao.validarValor(asignacion(producto(), vasoGrande, "mucho")));
    }

    @Test
    void validarValor_queCumpleLaExpresion_devuelveNull() {
        Caracteristica vasoGrande = caracteristica(true, tipoCaracteristica(true, SOLO_NUMEROS));
        assertNull(dao.validarValor(asignacion(producto(), vasoGrande, "8.50")));
    }

    @Test
    void validarValor_tipoSinExpresion_aceptaCualquierValor() {
        Caracteristica vasoGrande = caracteristica(true, tipoCaracteristica(true, null));
        assertNull(dao.validarValor(asignacion(producto(), vasoGrande, "lo que sea")));
    }

    @Test
    void validarValor_conExpresionMalConfigurada_devuelveError() {
        Caracteristica vasoGrande = caracteristica(true, tipoCaracteristica(true, "^\\d+("));
        assertNotNull(dao.validarValor(asignacion(producto(), vasoGrande, "250")));
    }

    // ===================== validarDuplicado =====================
    @Test
    void validarDuplicado_caracteristicaYaAsignada_devuelveError() {
        Producto producto = producto();
        Caracteristica vasoGrande = caracteristica(true, tipoCaracteristica(true, SOLO_NUMEROS));
        simularConsulta(ProductoCaracteristica.class, List.of(asignacion(producto, vasoGrande, "350")));

        assertNotNull(dao.validarDuplicado(asignacion(producto, vasoGrande, "250")));
    }

    @Test
    void validarDuplicado_caracteristicaNueva_devuelveNull() {
        Producto producto = producto();
        TipoCaracteristica volumen = tipoCaracteristica(true, SOLO_NUMEROS);
        simularConsulta(ProductoCaracteristica.class, List.of(asignacion(producto, caracteristica(true, volumen), "350")));

        assertNull(dao.validarDuplicado(asignacion(producto, caracteristica(true, volumen), "250")));
    }

    // ===================== ayudantes =====================
    private Producto producto() {
        Producto producto = new Producto(UUID.randomUUID());
        producto.setNombre("Café latte");
        return producto;
    }

    private TipoCaracteristica tipoCaracteristica(boolean activo, String expresionRegular) {
        TipoCaracteristica tipoCaracteristica = new TipoCaracteristica(UUID.randomUUID());
        tipoCaracteristica.setNombre("Volumen");
        tipoCaracteristica.setActivo(activo);
        tipoCaracteristica.setExpresionRegular(expresionRegular);
        return tipoCaracteristica;
    }

    private Caracteristica caracteristica(boolean activa, TipoCaracteristica tipoCaracteristica) {
        Caracteristica caracteristica = new Caracteristica(UUID.randomUUID());
        caracteristica.setNombre("Vaso grande");
        caracteristica.setActivo(activa);
        caracteristica.setIdTipoCaracteristica(tipoCaracteristica);
        return caracteristica;
    }

    private ProductoCaracteristica asignacion(Producto producto, Caracteristica caracteristica, String valor) {
        ProductoCaracteristica asignacion = new ProductoCaracteristica(UUID.randomUUID());
        asignacion.setIdProducto(producto);
        asignacion.setIdCaracteristica(caracteristica);
        asignacion.setValor(valor);
        return asignacion;
    }

}
