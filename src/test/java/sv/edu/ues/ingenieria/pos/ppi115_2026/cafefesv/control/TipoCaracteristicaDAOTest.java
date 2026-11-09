package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoCaracteristica;

/**
 *
 * @author valen
 */
public class TipoCaracteristicaDAOTest extends AbstractDAOTestBase<TipoCaracteristica, TipoCaracteristicaDAO> {

    @Override
    protected TipoCaracteristicaDAO crearDAO() {
        return new TipoCaracteristicaDAO();
    }

    @Override
    protected TipoCaracteristica crearEntidad() {
        return new TipoCaracteristica();
    }

    // ===================== buscarActivosPorNombre =====================

    @Test
    void buscarActivosPorNombre_filtraPorTextoSinImportarMayusculas() {
        TipoCaracteristica volumen = tipoCaracteristica("Volumen", true, null);
        TipoCaracteristica peso = tipoCaracteristica("Peso", true, null);
        simularConsulta(TipoCaracteristica.class, List.of(volumen, peso));

        assertEquals(List.of(volumen), dao.buscarActivosPorNombre("VOL"));
    }

    @Test
    void buscarActivosPorNombre_sinTexto_devuelveTodosLosActivos() {
        TipoCaracteristica volumen = tipoCaracteristica("Volumen", true, null);
        TipoCaracteristica peso = tipoCaracteristica("Peso", true, null);
        simularConsulta(TipoCaracteristica.class, List.of(volumen, peso));

        assertEquals(List.of(volumen, peso), dao.buscarActivosPorNombre(null));
    }

    // ===================== countCaracteristicasActivas / countCaracteristicas =====================

    @Test
    void countCaracteristicasActivas_devuelveLoQueCuentaLaBase() {
        simularConteo(2);

        assertEquals(2, dao.countCaracteristicasActivas(tipoCaracteristica("Volumen", false, null)));
    }

    @Test
    void countCaracteristicas_devuelveLoQueCuentaLaBase() {
        simularConteo(4);

        assertEquals(4, dao.countCaracteristicas(tipoCaracteristica("Volumen", true, null)));
    }

    // ===================== validarExpresion =====================

    @Test
    void validarExpresion_invalida_devuelveError() {
        // Falta cerrar el paréntesis
        assertNotNull(dao.validarExpresion(tipoCaracteristica("Volumen", true, "^\\d+(")));
    }

    @Test
    void validarExpresion_valida_devuelveNull() {
        assertNull(dao.validarExpresion(tipoCaracteristica("Volumen", true, "^\\d+(\\.\\d{1,2})?$")));
    }

    @Test
    void validarExpresion_vacia_aceptaCualquierValor() {
        assertNull(dao.validarExpresion(tipoCaracteristica("Volumen", true, null)));
        assertNull(dao.validarExpresion(tipoCaracteristica("Volumen", true, "  ")));
    }

    // ===================== validarInactivar =====================

    @Test
    void validarInactivar_conCaracteristicasActivas_devuelveError() {
        simularConteo(1);

        assertNotNull(dao.validarInactivar(tipoCaracteristica("Volumen", false, null)));
    }

    @Test
    void validarInactivar_sinCaracteristicasActivas_devuelveNull() {
        simularConteo(0);

        assertNull(dao.validarInactivar(tipoCaracteristica("Volumen", false, null)));
    }

    @Test
    void validarInactivar_siSigueActivo_noConsultaLaBase() {
        assertNull(dao.validarInactivar(tipoCaracteristica("Volumen", true, null)));
        verifyNoInteractions(em);
    }

    // ===================== validarEliminar =====================

    @Test
    void validarEliminar_conCaracteristicas_devuelveError() {
        simularConteo(2);

        assertNotNull(dao.validarEliminar(tipoCaracteristica("Volumen", true, null)));
    }

    @Test
    void validarEliminar_sinCaracteristicas_devuelveNull() {
        simularConteo(0);

        assertNull(dao.validarEliminar(tipoCaracteristica("Volumen", true, null)));
    }

    // ===================== ayudantes =====================

    private TipoCaracteristica tipoCaracteristica(String nombre, boolean activo, String expresionRegular) {
        TipoCaracteristica tipoCaracteristica = new TipoCaracteristica(UUID.randomUUID());
        tipoCaracteristica.setNombre(nombre);
        tipoCaracteristica.setActivo(activo);
        tipoCaracteristica.setExpresionRegular(expresionRegular);
        return tipoCaracteristica;
    }
}