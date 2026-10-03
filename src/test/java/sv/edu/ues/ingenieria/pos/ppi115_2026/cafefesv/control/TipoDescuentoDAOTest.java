/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoDescuento;

/**
 *
 * @author valen
 */
public class TipoDescuentoDAOTest extends AbstractDAOTestBase<TipoDescuento, TipoDescuentoDAO> {

    @Override
    protected TipoDescuentoDAO crearDAO() {
        return new TipoDescuentoDAO();
    }

    @Override
    protected TipoDescuento crearEntidad() {
        return new TipoDescuento();
    }

}
