# Garage
Taller numero 2 garage 
Integrantes: JOSE LUIS ORDOÑEZ DIAZ / JUAN MANUEL HERNANDEZ SANCHEZ
Grupo: E191P

Estructura

├── controller → Controladores (Servlets)
│ └── VehiculoServlet.java
├── facade → Lógica de negocio / reglas (EJB)
│ └── VehiculoFacade.java
├── model → Clases de entidad (POJOs)
│ └── Vehiculo.java
├── persistence → Acceso a datos (DAO)
│ └── VehiculoDAO.java
└── webapp
└── vehiculo.jsp → Vista principal (Bootstrap + JSTL)

 /*Capas principales*/

- **Modelo (`model`)**: Define la estructura de datos (clase `Vehiculo`).
- **DAO (`persistence`)**: Encapsula el acceso a la base de datos mediante JDBC y consultas SQL parametrizadas.
- **Fachada (`facade`)**: Implementa las **reglas de negocio y validaciones**, como:
  - Validación contra SQL Injection.  
  - Unicidad de placas.  
  - Validación de modelo (máx. 20 años de antigüedad).  
  - Restricción de eliminación para vehículos del propietario “Administrador”.  
- **Controlador (`controller`)**: Gestiona las peticiones HTTP desde el JSP y delega las operaciones a la fachada.
- **Vista (`vehiculo.jsp`)**: Interfaz web con **Bootstrap 5** y **JSTL**, permite agregar, editar, listar y eliminar vehículos.

/*Principales Características*/

- CRUD completo de vehículos (crear, leer, actualizar y eliminar).  
- Validaciones de negocio (EJB).  
- Detección de intentos de SQL Injection.  
- Notificación especial para vehículos de lujo (marca “Ferrari”).  
- Interfaz  Bootstrap 5 y Bootstrap Icons.  
- Tablas dinámicas y formularios responsivos.  

Convenciones: 

| Elemento              | Convención           | Ejemplo                  |
|-----------------------|---------------------|--------------------------|
| Clases                | `PascalCase`        | `VehiculoFacade`         |
| Métodos y variables   | `camelCase`         | `buscarPorId`, `listaVehiculos` |
| Paquetes              | `minúsculas`        | `com.garage.facade`      |
| JSP / HTML / CSS      | nombres descriptivos | `vehiculo.jsp`, `main.css` |

El proyecto se gestiona con Git y GitHub, siguiendo buenas prácticas de control de versiones.

### Flujo recomendado
- `main`→ Rama de producción.  
- `feature`/→ Ramas para nuevas funcionalidades.

# Clonar el repositorio
git clone https://github.com/01JOSE1/tallerGarage2

Herramientas utilizadas para el desarrollo del proyecto: 

NetBeans 20.
JDK 17.
Servidor GlassFish 7 o Apache Tomcat 10.
Base de datos MySQL.
Driver JDBC configurado.





