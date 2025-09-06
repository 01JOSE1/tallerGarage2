/**
 * Fachada que expone los métodos básicos.
 * Sólo incluye el paso directo (sin reglas), para que los estudiantes
 * implementen las reglas de negocio en esta clase.
 */
package com.garage.facade;

import com.garage.model.Vehiculo;
import com.garage.persistence.VehiculoDAO;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Fachada para operaciones sobre vehículos. Deben agregarse reglas de negocio
 * antes de llamar al DAO.
 */
@Stateless
public class VehiculoFacade {

    @Resource(lookup = "jdbc/garageDB")
    private DataSource ds;

    /**
     * Lista todos los vehículos. Debe documentar excepciones si se agregan
     * reglas.
     */
    public List<Vehiculo> listar() throws SQLException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);
            return dao.listar();
        }
    }

    /**
     * Busca vehículo por id. Manejar errores en llamada.
     */
    public Vehiculo buscarPorId(int id) throws SQLException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);
            return dao.buscarPorId(id);
        }
    }

    /**
     * Agrega vehículo. Debe validar con reglas de negocio antes de agregar. Por
     * ejemplo, no agregar si la placa ya existe, si propietario está vacío,
     * etc.
     */
    public void agregar(Vehiculo v) throws SQLException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);
            dao.agregar(v);
        }
    }

    /**
     * Actualiza vehículo; incluir reglas de negocio.
     */
    public void actualizar(Vehiculo v) throws SQLException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);
            dao.actualizar(v);
        }
    }

    /**
     * Elimina vehículo por id.
     */
    public void eliminar(int id) throws SQLException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);
            dao.eliminar(id);
        }
    }
}

