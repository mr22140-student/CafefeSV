package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.DescuentoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.TipoDescuentoDAO;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Descuento;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoDescuento;

/**
 *
 * @author valen
 */
public class DescuentoModelTest extends ModelTestBase {

    @InjectMocks
    private DescuentoModel model;

    @Mock
    private DescuentoDAO descuentoDAO;

    @Mock
    private TipoDescuentoDAO tipoDescuentoDAO;

    @Test
    void noGuardaSiLaFechaHastaEsAnteriorALaFechaDesde() {
        model.setRegistro(descuento(true, enDias(5), enDias(1)));

        model.btnCrearHandler();

        verify(descuentoDAO, never()).save(any());
        assertEquals("Fechas inválidas", ultimoMensaje().getSummary());
    }

    @Test
    void guardaUnDescuentoSinFechaDeFin() {
        Descuento d = descuento(true, enDias(0), null);
        model.setRegistro(d);

        model.btnCrearHandler();

        verify(descuentoDAO).save(d);
    }

    @Test
    void noGuardaConTipoDeDescuentoInactivo() {
        model.setRegistro(descuento(false, enDias(0), enDias(5)));

        model.btnCrearHandler();

        verify(descuentoDAO, never()).save(any());
        assertEquals("Tipo inactivo", ultimoMensaje().getSummary());
    }

    @Test
    void calculaLaVigenciaSegunLaFechaActual() {
        assertEquals("PROGRAMADO", model.vigencia(descuento(true, enDias(2), enDias(5))));
        assertEquals("VIGENTE", model.vigencia(descuento(true, enDias(-1), enDias(5))));
        assertEquals("VENCIDO", model.vigencia(descuento(true, enDias(-5), enDias(-1))));
    }

    private Descuento descuento(boolean tipoActivo, Date desde, Date hasta) {
        TipoDescuento tipo = new TipoDescuento(UUID.randomUUID());
        tipo.setNombre("Temporada");
        tipo.setActivo(tipoActivo);
        tipo.setDescuentoMaximo(30);
        when(tipoDescuentoDAO.findById(tipo.getIdTipoDescuento())).thenReturn(tipo);

        Descuento d = new Descuento(UUID.randomUUID());
        d.setNombre("Navidad");
        d.setIdTipoDescuento(tipo);
        d.setFechaDesde(desde);
        d.setFechaHasta(hasta);
        return d;
    }

}
