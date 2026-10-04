/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

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

}
