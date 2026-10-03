/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.TipoProducto;

/**
 *
 * @author valen
 */
public class TipoProductoDAOTest extends AbstractDAOTestBase<TipoProducto, TipoProductoDAO> {

    @Override
    protected TipoProductoDAO crearDAO() {
        return new TipoProductoDAO();
    }

    @Override
    protected TipoProducto crearEntidad() {
        return new TipoProducto();
    }

}
