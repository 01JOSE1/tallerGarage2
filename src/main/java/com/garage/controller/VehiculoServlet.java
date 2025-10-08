/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.garage.controller;

import com.garage.facade.VehiculoFacade;
import com.garage.model.Vehiculo;
import jakarta.ejb.EJB;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador web para la gestión de vehículos. Recibe peticiones HTTP y las
 * traduce en operaciones CRUD. Debe mostrar mensajes claros en error de
 * negocio.
 */
@WebServlet("/vehiculos")
public class VehiculoServlet extends HttpServlet {

    /**
     * No se puede crear manualmente la instancia del facade ya que entonces el
     * contenedor(Glasfish) no inyectaria las dependencias(@Stateless,
     *
     * @Resource) y no funcionaria facade.ds = null (nunca se inyectó) y el
     * ds.getConnection() → NullPointerException
     *
     * @EJB = Le pides al servidor que te inyecte un Enterprise JavaBean
     * (componente de negocio), en lugar de instanciarlo tú mismo.
     */
    @EJB
    private VehiculoFacade vehiculoFacade;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtener la acción solicitada (crear, editar o eliminar)
        String accion = request.getParameter("accion");

        // Si no hay acción, es una creación por defecto
        if (accion == null || accion.isEmpty()) {
            crearVehiculo(request, response);
        } else {
            switch (accion) {
                case "editar":
                    // Cargar datos del vehículo para editar
                    cargarVehiculoParaEditar(request, response);
                    break;
                case "actualizar":
                    // Actualizar vehículo existente
                    actualizarVehiculo(request, response);
                    break;
                case "eliminar":
                    // Eliminar vehículo
                    eliminarVehiculo(request, response);
                    break;
                default:
                    crearVehiculo(request, response);
                    break;
            }
        }
    }

    /**
     * Método para crear un nuevo vehículo
     */
    private void crearVehiculo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String placa = request.getParameter("placa");
        String marca = request.getParameter("marca");
        String modelo = request.getParameter("modelo");
        String color = request.getParameter("color");
        String propietario = request.getParameter("propietario");

        try {
            Vehiculo nuevoVehiculo = new Vehiculo(placa, marca, modelo, color, propietario);
            
            // agregar() ahora retorna una notificación especial si aplica
            String notificacion = vehiculoFacade.agregar(nuevoVehiculo);
            
            request.setAttribute("mensaje", "Vehículo agregado exitosamente");
            
            // Si hay notificación especial (ej: Ferrari), mostrarla también
            if (notificacion != null) {
                request.setAttribute("notificacion", notificacion);
            }

            // Manejar TODAS las excepciones del EJB (validación, BD, etc.)
        } catch (jakarta.ejb.EJBException e) {

            Throwable causa = e.getCause();

            if (causa instanceof IllegalArgumentException) {
                request.setAttribute("error", causa.getMessage());
                request.setAttribute("placa", placa);
                request.setAttribute("marca", marca);
                request.setAttribute("modelo", modelo);
                request.setAttribute("color", color);
                request.setAttribute("propietario", propietario);

            } else if (causa instanceof SQLException) {
                request.setAttribute("error", "Error de base de datos: " + causa.getMessage());

            } else {
                request.setAttribute("error", "Error del sistema: "
                        + (causa != null ? causa.getMessage() : "Error interno"));
            }

        } catch (Exception e) {
            // Solo para errores NO relacionados con EJB
            request.setAttribute("error", "Error inesperado del sistema");
        }

        // Cargar lista de vehículos y redirigir
        cargarListaVehiculos(request, response);
    }

    /**
     * Método para cargar los datos de un vehículo para editar
     */
    private void cargarVehiculoParaEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");

        try {
            int id = Integer.parseInt(idStr);
            Vehiculo vehiculo = vehiculoFacade.buscarPorId(id);

            if (vehiculo != null) {
                // Pasar datos del vehículo al formulario
                request.setAttribute("vehiculoEditar", vehiculo);
                request.setAttribute("modoEdicion", true);
            } else {
                request.setAttribute("error", "Vehículo no encontrado");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de vehículo inválido");
        } catch (Exception e) {
            request.setAttribute("error", "Error al cargar el vehículo: " + e.getMessage());
        }

        // Cargar lista de vehículos y redirigir
        cargarListaVehiculos(request, response);
    }

    /**
     * Método para actualizar un vehículo existente
     */
    private void actualizarVehiculo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        String placa = request.getParameter("placa");
        String marca = request.getParameter("marca");
        String modelo = request.getParameter("modelo");
        String color = request.getParameter("color");
        String propietario = request.getParameter("propietario");

        try {
            int id = Integer.parseInt(idStr);
            
            // Obtener la placa original del vehículo antes de actualizar
            Vehiculo vehiculoOriginal = vehiculoFacade.buscarPorId(id);
            String placaOriginal = (vehiculoOriginal != null) ? vehiculoOriginal.getPlaca() : null;
            
            Vehiculo vehiculo = new Vehiculo(placa, marca, modelo, color, propietario);
            vehiculo.setId(id);

            // Pasar la placa original para que la validación la ignore
            vehiculoFacade.actualizar(vehiculo, placaOriginal);
            request.setAttribute("mensaje", "Vehículo actualizado exitosamente");

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de vehículo inválido");

        } catch (jakarta.ejb.EJBException e) {
            Throwable causa = e.getCause();

            if (causa instanceof IllegalArgumentException) {
                request.setAttribute("error", causa.getMessage());
                // Mantener datos en el formulario
                Vehiculo vehiculoTemp = new Vehiculo(placa, marca, modelo, color, propietario);
                vehiculoTemp.setId(Integer.parseInt(idStr));
                request.setAttribute("vehiculoEditar", vehiculoTemp);
                request.setAttribute("modoEdicion", true);

            } else if (causa instanceof SQLException) {
                request.setAttribute("error", "Error de base de datos: " + causa.getMessage());

            } else {
                request.setAttribute("error", "Error del sistema: "
                        + (causa != null ? causa.getMessage() : "Error interno"));
            }

        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado al actualizar: " + e.getMessage());
        }

        // Cargar lista de vehículos y redirigir
        cargarListaVehiculos(request, response);
    }

    /**
     * Método para eliminar un vehículo
     */
    private void eliminarVehiculo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");

        try {
            int id = Integer.parseInt(idStr);
            vehiculoFacade.eliminar(id);
            request.setAttribute("mensaje", "Vehículo eliminado exitosamente");

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de vehículo inválido");

        } catch (jakarta.ejb.EJBException e) {
            Throwable causa = e.getCause();

            if (causa instanceof IllegalArgumentException) {
                // Capturar mensajes de validación (ej: no eliminar Administrador)
                request.setAttribute("error", causa.getMessage());
                
            } else if (causa instanceof SQLException) {
                request.setAttribute("error", "Error de base de datos al eliminar: " + causa.getMessage());
                
            } else {
                request.setAttribute("error", "Error al eliminar vehículo: "
                        + (causa != null ? causa.getMessage() : "Error interno"));
            }

        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado al eliminar: " + e.getMessage());
        }

        // Cargar lista de vehículos y redirigir
        cargarListaVehiculos(request, response);
    }

    /**
     * Método auxiliar para cargar la lista de vehículos y redirigir al JSP
     */
    private void cargarListaVehiculos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Vehiculo> vehiculos = vehiculoFacade.listar();
            request.setAttribute("vehicles", vehiculos);
        } catch (Exception e) {
            request.setAttribute("vehicles", new ArrayList<>());
        }

        request.getRequestDispatcher("vehiculo.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Vehiculo> vehiculos = vehiculoFacade.listar();

            request.setAttribute("vehicles", vehiculos);

            request.getRequestDispatcher("/vehiculo.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al cargar la lista de vehículos: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ocurrió un error inesperado: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}