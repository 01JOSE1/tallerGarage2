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
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;

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
     * 
     * @return String con mensaje de notificación especial (null si no aplica)
     */
    public String agregar(Vehiculo v) throws SQLException, IllegalArgumentException {

        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);

            // Validar SQL Injection antes que todo
            validarSQLInjection(v);
            
            // Para agregar, no hay placa original que ignorar
            validarDatosVehiculo(v, dao, null);

            dao.agregar(v);
            
            // Notificación simulada para marca Ferrari
            if (v.getMarca() != null && v.getMarca().trim().equalsIgnoreCase("Ferrari")) {
                return "🏎️ ¡NOTIFICACIÓN ESPECIAL! Se ha registrado un vehículo de lujo marca Ferrari. Se ha enviado alerta al departamento de vehículos premium.";
            }
            
            return null; // No hay notificación especial
        }
    }

    /**
     * Actualiza vehículo; incluir reglas de negocio.
     * 
     * @param v Vehículo con los nuevos datos
     * @param placaOriginal Placa original del vehículo (para ignorarla en la validación de duplicados)
     */
    public void actualizar(Vehiculo v, String placaOriginal) throws SQLException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);
            
            // Validar que el vehículo realmente existe antes de actualizar
            Vehiculo vehiculoExistente = dao.buscarPorId(v.getId());
            if (vehiculoExistente == null) {
                throw new IllegalArgumentException("No se puede actualizar: el vehículo con ID " + v.getId() + " no existe");
            }
            
            // Validar SQL Injection
            validarSQLInjection(v);
            
            // Pasar la placa original para que sea ignorada en la validación
            validarDatosVehiculo(v, dao, placaOriginal);
            
            dao.actualizar(v);
        }
    }

    /**
     * Elimina vehículo por id.
     * No se puede eliminar si el propietario es "Administrador".
     */
    public void eliminar(int id) throws SQLException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);
            
            // Buscar el vehículo para verificar el propietario
            Vehiculo vehiculo = dao.buscarPorId(id);
            
            if (vehiculo == null) {
                throw new IllegalArgumentException("No se puede eliminar: el vehículo no existe");
            }
            
            // No permitir eliminar vehículos del propietario "Administrador"
            if (vehiculo.getPropietario() != null && 
                vehiculo.getPropietario().trim().equalsIgnoreCase("Administrador")) {
                throw new IllegalArgumentException("No se puede eliminar un vehículo del 'Administrador'");
            }
            
            dao.eliminar(id);
        }
    }

    /**
     * Validación simulada de SQL Injection.
     * Busca patrones comunes que podrían indicar intentos de inyección SQL.
     */
    private void validarSQLInjection(Vehiculo v) throws IllegalArgumentException {
        // Patrones peligrosos comunes en SQL Injection
        String[] patronesPeligrosos = {
            "'", "\"", ";", "--", "/*", "*/", "xp_", "sp_", 
            "exec", "execute", "select", "insert", "update", 
            "delete", "drop", "create", "alter", "union", "or 1=1", "or '1'='1'"
        };
        
        // Validar cada campo del vehículo
        validarCampo(v.getPlaca(), "Placa", patronesPeligrosos);
        validarCampo(v.getMarca(), "Marca", patronesPeligrosos);
        validarCampo(v.getModelo(), "Modelo", patronesPeligrosos);
        validarCampo(v.getColor(), "Color", patronesPeligrosos);
        validarCampo(v.getPropietario(), "Propietario", patronesPeligrosos);
    }
    
    /**
     * Valida un campo individual contra patrones de SQL Injection
     */
    private void validarCampo(String valor, String nombreCampo, String[] patronesPeligrosos) 
            throws IllegalArgumentException {
        
        if (valor == null || valor.isEmpty()) {
            return; // Campos vacíos se validan en otro lugar
        }
        
        String valorLower = valor.toLowerCase();
        
        for (String patron : patronesPeligrosos) {
            if (valorLower.contains(patron.toLowerCase())) {
                throw new IllegalArgumentException(
                    "El campo '" + nombreCampo + "' contiene caracteres no permitidos que podrían representar un riesgo de seguridad (SQL Injection detectado)"
                );
            }
        }
    }

    /**
     * Implementacion de validaciones:
     *
     * Si me pasas datos incorrectos, te voy a notificar con una
     * IllegalArgumentException. Si hay problemas de base de datos, te voy a
     * notificar con una SQLException.
     * 
     * @param v Vehículo a validar
     * @param dao DAO para consultas a la BD
     * @param placaOriginal Placa original del vehículo (null si es nuevo, o la placa actual si es actualización)
     */
    private void validarDatosVehiculo(Vehiculo v, VehiculoDAO dao, String placaOriginal) 
            throws IllegalArgumentException, SQLException {
        
        if (v == null) {
            throw new IllegalArgumentException("El vehículo no puede ser nulo");
        }

        /**
         * La marca, modelo y placa deben tener al menos 3 caracteres
         */
        if (v.getMarca() == null || v.getMarca().length() <= 3) {
            throw new IllegalArgumentException("La marca debe tener más de 3 caracteres");
        }
        
        if (v.getModelo() == null || v.getModelo().length() <= 3) {
            throw new IllegalArgumentException("El modelo debe tener más de 3 caracteres");
        }
        
        if (v.getPlaca() == null || v.getPlaca().length() <= 3) {
            throw new IllegalArgumentException("La placa debe tener más de 3 caracteres");
        }

        /**
         * No aceptar propietario vacío o con menos de 5 caracteres. 
         */
        if (v.getPropietario() == null || v.getPropietario().trim().isEmpty()) {
            throw new IllegalArgumentException("El campo propietario no puede estar vacío");
        }
        
        if (v.getPropietario().length() < 5) {
            throw new IllegalArgumentException("El campo propietario debe tener al menos 5 caracteres");
        }

        /**
         * Validar colores permitidos
         */
        if (v.getColor() != null && !v.getColor().trim().isEmpty()) {
            switch (v.getColor().trim().toUpperCase()) {
                case "ROJO":
                case "BLANCO":
                case "NEGRO":
                case "AZUL":
                case "GRIS":
                    break;
                default:
                    throw new IllegalArgumentException("Solo se aceptan colores (ROJO, BLANCO, NEGRO, AZUL, GRIS)");
            }
        }

        /**
         * No aceptar vehículos cuyo modelo tenga más de 20 años de antigüedad (por ejemplo, año < actual - 20). 
         */
        try {
            int anioModelo = Integer.parseInt(v.getModelo());
            int anioActual = LocalDate.now().getYear();
            int anioMinimo = anioActual - 20;
            
            if (anioModelo < anioMinimo) {
                throw new IllegalArgumentException("El vehículo tiene más de 20 años de antigüedad (año mínimo permitido: " + anioMinimo + ")");
            }
            
            if (anioModelo > anioActual + 1) {
                throw new IllegalArgumentException("El año del modelo no puede ser mayor al año actual");
            }
            
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El modelo debe ser un año válido (número de 4 dígitos)");
        }

        /**
         * Las placas deben ser únicas en toda la base.
         * IMPORTANTE: Si es una actualización y la placa no cambió, no validar duplicado.
         */
        boolean placaCambio = (placaOriginal == null) || !placaOriginal.equalsIgnoreCase(v.getPlaca());
        
        if (placaCambio && dao.existePlaca(v.getPlaca())) {
            throw new IllegalArgumentException("Esta placa de vehículo ya existe");
        }
    }
}