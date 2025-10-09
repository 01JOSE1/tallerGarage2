<%-- 
    Document   : vehiculo
    Created on : Sep 19, 2025, 1:28:45 PM
    Author     : oljd2
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <title>Lista de Vehículos</title>
        <!-- Bootstrap 5 -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body class="bg-light">

        <div class="container py-5">
            <h1 class="text-center mb-4 text-primary fw-bold">Gestión de Vehículos</h1>


            <!-- Mensajes de éxito -->
            <c:if test="${not empty mensaje}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="bi bi-check-circle-fill"></i> ${mensaje}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <!-- NOTIFICACIÓN ESPECIAL -->
            <c:if test="${not empty notificacion}">
                <div class="alert alert-warning alert-dismissible fade show" role="alert">
                    <i class="bi bi-bell-fill"></i> ${notificacion}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <!-- Mensajes de error -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle-fill"></i> ${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>


            <!-- FORMULARIO (Crear o Editar según el modo) -->
            <div class="card shadow-lg border-0 rounded-4 mb-5">
                <div class="card-body p-4">
                    <!-- Título dinámico según modo edición -->
                    <h2 class="h4 mb-4 text-secondary">
                        <c:choose>
                            <c:when test="${modoEdicion}">Editar Vehículo</c:when>
                            <c:otherwise>Agregar Vehículo</c:otherwise>
                        </c:choose>
                    </h2>

                    <form action="vehiculos" method="post" class="row g-3">
                        <!-- Campo oculto para el ID (solo en modo edición) -->
                        <c:if test="${modoEdicion}">
                            <input type="hidden" name="id" value="${vehiculoEditar.id}"/>
                            <input type="hidden" name="accion" value="actualizar"/>
                        </c:if>

                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Placa</label>
                            <input type="text" name="placa" required class="form-control" 
                                   placeholder="Ej: ABC123" 
                                   value="${modoEdicion ? vehiculoEditar.placa : placa}"/>
                        </div>

                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Marca</label>
                            <input type="text" name="marca" required class="form-control" 
                                   placeholder="Ej: Toyota" 
                                   value="${modoEdicion ? vehiculoEditar.marca : marca}"/>
                        </div>

                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Modelo</label>
                            <input type="text" name="modelo" required class="form-control" 
                                   placeholder="Ej: 2020" 
                                   value="${modoEdicion ? vehiculoEditar.modelo : modelo}"/>
                        </div>

                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Color</label>
                            <input type="text" name="color" class="form-control" 
                                   placeholder="Ej: Rojo" 
                                   value="${modoEdicion ? vehiculoEditar.color : color}"/>
                        </div>

                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Propietario</label>
                            <input type="text" name="propietario" class="form-control" 
                                   placeholder="Ej: Juan Pérez" 
                                   value="${modoEdicion ? vehiculoEditar.propietario : propietario}"/>
                        </div>

                        <div class="col-12 text-end">
                            <!-- Botón de Cancelar (solo en modo edición) -->
                            <c:if test="${modoEdicion}">
                                <a href="vehiculos" class="btn btn-secondary px-4 rounded-pill shadow-sm me-2">
                                    <i class="bi bi-x-circle"></i> Cancelar
                                </a>
                            </c:if>

                            <!-- Botón dinámico según modo -->
                            <button type="submit" class="btn btn-primary px-4 rounded-pill shadow-sm">
                                <c:choose>
                                    <c:when test="${modoEdicion}">
                                        <i class="bi bi-save"></i> Actualizar
                                    </c:when>
                                    <c:otherwise>
                                        <i class="bi bi-plus-circle"></i> Agregar
                                    </c:otherwise>
                                </c:choose>
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- TABLA DE VEHÍCULOS -->
            <div class="card shadow-lg border-0 rounded-4">
                <div class="card-body p-4">
                    <h2 class="h4 mb-4 text-secondary">Lista de Vehículos</h2>
                    <div class="table-responsive">
                        <table class="table table-striped table-hover align-middle">
                            <thead class="table-primary text-center">
                                <tr>
                                    <th>ID</th>
                                    <th>Placa</th>
                                    <th>Marca</th>
                                    <th>Modelo</th>
                                    <th>Color</th>
                                    <th>Propietario</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="vehicle" items="${vehicles}">
                                    <tr class="text-center">
                                        <td>${vehicle.id}</td>
                                        <td>${vehicle.placa}</td>
                                        <td>${vehicle.marca}</td>
                                        <td>${vehicle.modelo}</td>
                                        <td>${vehicle.color}</td>
                                        <td>${vehicle.propietario}</td>
                                        <td>
                                            <!-- Botón Editar: Envía acción "editar" con el ID -->
                                            <form action="vehiculos" method="post" style="display:inline;">
                                                <input type="hidden" name="accion" value="editar">
                                                <input type="hidden" name="id" value="${vehicle.id}">
                                                <button type="submit" class="btn btn-warning btn-sm px-3 rounded-pill shadow-sm">
                                                    <i class="bi bi-pencil-square"></i> Editar
                                                </button>
                                            </form>

                                            <!-- Botón Eliminar: Envía acción "eliminar" con confirmación -->
                                            <form action="vehiculos" method="post" style="display:inline;" 
                                                  onsubmit="return confirm('¿Seguro que deseas eliminar este vehículo?');">
                                                <input type="hidden" name="accion" value="eliminar">
                                                <input type="hidden" name="id" value="${vehicle.id}">
                                                <button type="submit" class="btn btn-danger btn-sm px-3 rounded-pill shadow-sm">
                                                    <i class="bi bi-trash"></i> Eliminar
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <!-- Bootstrap JS + Icons -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    </body>
</html>
