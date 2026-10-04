/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.entity.FacturaOrdenProducto;

/**
 *
 * @author valen
 */
public class FacturaOrdenProductoDAOTest extends AbstractDAOTestBase<FacturaOrdenProducto, FacturaOrdenProductoDAO> {

    @Override
    protected FacturaOrdenProductoDAO crearDAO() {
        return new FacturaOrdenProductoDAO();
    }

    @Override
    protected FacturaOrdenProducto crearEntidad() {
        return new FacturaOrdenProducto();
    }

}
