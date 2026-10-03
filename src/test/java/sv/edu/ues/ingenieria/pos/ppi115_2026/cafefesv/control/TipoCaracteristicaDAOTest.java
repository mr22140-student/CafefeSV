/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control;

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

}
