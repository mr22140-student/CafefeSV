/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.Rol;

/**
 *
 * @author valen
 */
public class RolDAOTest extends AbstractDAOTestBase<Rol, RolDAO> {

    @Override
    protected RolDAO crearDAO() {
        return new RolDAO();
    }

    @Override
    protected Rol crearEntidad() {
        return new Rol();
    }
    // ===================== validarInactivar =====================

    @Test
    void validarInactivar_rolActivo_noConsultaYDevuelveNull() {
        assertNull(dao.validarInactivar(rol(true)));
        verifyNoInteractions(em);
    }

    @Test
    void validarInactivar_conEmpleados_devuelveError() {
        simularConteo(2);
        assertNotNull(dao.validarInactivar(rol(false)));
    }

    @Test
    void validarInactivar_sinEmpleados_devuelveNull() {
        simularConteo(0);
        assertNull(dao.validarInactivar(rol(false)));
    }

    // ===================== validarEliminar =====================
    @Test
    void validarEliminar_conEmpleados_devuelveError() {
        simularConteo(1);
        assertNotNull(dao.validarEliminar(rol(true)));
    }

    @Test
    void validarEliminar_sinEmpleados_devuelveNull() {
        simularConteo(0);
        assertNull(dao.validarEliminar(rol(true)));
    }

    // ===================== buscarActivosIncluyendo =====================
    @Test
    void buscarActivosIncluyendo_agregaElRolActualAunqueEsteInactivo() {
        Rol cajero = rol(true);
        Rol meseroInactivo = rol(false);
        simularConsulta(Rol.class, List.of(cajero));

        assertEquals(List.of(cajero, meseroInactivo), dao.buscarActivosIncluyendo(meseroInactivo));
    }

    @Test
    void buscarActivosIncluyendo_noRepiteElRolActualSiYaEstaActivo() {
        Rol cajero = rol(true);
        simularConsulta(Rol.class, List.of(cajero));

        assertEquals(List.of(cajero), dao.buscarActivosIncluyendo(cajero));
    }

    // ===================== ayudantes =====================
    private Rol rol(boolean activo) {
        Rol rol = new Rol(UUID.randomUUID());
        rol.setNombre(activo ? "Cajero" : "Mesero");
        rol.setActivo(activo);
        return rol;
    }
}
