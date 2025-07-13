# FidESPN United 2026

## Resumen

FidESPN United 2026 es una aplicación de gestión de partidos de fútbol desarrollada en Java Swing para el Mundial United 2026. El sistema permite a diferentes tipos de usuarios (Administradores, Corresponsales y Fanáticos) interactuar con partidos en tiempo real, incluyendo reportes de eventos, chat en vivo, gestión de equipos favoritos y seguimiento de partidos en vivo.

La aplicación utiliza una arquitectura MVC (Model-View-Controller) con persistencia de datos mediante serialización Java, proporcionando una experiencia completa para la gestión y seguimiento de partidos de fútbol con una interfaz moderna y responsive.

## Requisitos

### Requisitos del Sistema
- **Java Development Kit (JDK)**: Versión 8 o superior
- **Sistema Operativo**: Windows, macOS o Linux
- **Memoria RAM**: Mínimo 512MB (recomendado 1GB)
- **Espacio en Disco**: 50MB de espacio libre

### Dependencias
- **Java Swing**: Incluido en el JDK estándar
- **Java Serialization**: Para persistencia de datos
- **Java Collections Framework**: Para estructuras de datos
- **Java Timer**: Para actualizaciones en tiempo real

## Cómo Ejecutar

### Opción 1: Desde la línea de comandos

1. **Compilar el proyecto:**
   ```bash
   javac -d bin src/com/fidespn/main/*.java src/com/fidespn/model/*.java src/com/fidespn/service/*.java src/com/fidespn/service/exceptions/*.java src/com/fidespn/view/*.java
   ```

2. **Ejecutar la aplicación:**
   ```bash
   java -cp bin com.fidespn.main.MainApp
   ```

### Opción 2: Desde un IDE

1. **Abrir el proyecto** en tu IDE preferido (IntelliJ IDEA, Eclipse, NetBeans)
2. **Navegar** a `src/com/fidespn/main/MainApp.java`
3. **Ejecutar** la clase `MainApp`

### Usuarios de Prueba

La aplicación incluye usuarios de demostración preconfigurados que se crean automáticamente en la primera ejecución:

| Usuario | Contraseña | Rol | Funcionalidades |
|---------|------------|-----|-----------------|
| `admin` | `admin123` | Administrador | Gestión completa del sistema |
| `corresponsal1` | `pass123` | Corresponsal | Reportes en tiempo real |
| `fanatico1` | `pass123` | Fanático | Seguimiento de equipos favoritos |
| `fanatico2` | `pass123` | Fanático | Chat en vivo y partidos |

## Características Implementadas

### ✅ Funcionalidades Completadas

#### **Sistema de Autenticación **
- Login con validación de credenciales
- Manejo de diferentes tipos de usuario con roles específicos
- Interfaz gráfica moderna con diseño responsive
- Manejo de excepciones personalizadas (InvalidCredentialsException, UserNotFoundException)

#### **Gestión de Usuarios Completa**
- **Administrador**: Control total del sistema, gestión de usuarios y partidos
  - Dashboard completo con estadísticas
  - Gestión de usuarios (crear, editar, eliminar)
  - Supervisión de partidos y eventos
- **Corresponsal**: Reportes en tiempo real, gestión de eventos de partido
  - Dashboard especializado para reportes
  - Gestión de eventos de partido (goles, tarjetas, etc.)
  - Actualización de marcadores en tiempo real
- **Fanático**: Seguimiento de equipos favoritos, chat en vivo
  - Dashboard personalizado con equipos favoritos
  - Gestión de equipos favoritos
  - Visualización de partidos en vivo

#### **Gestión de Partidos Avanzada**
- Creación y gestión completa de partidos
- Actualización de marcadores en tiempo real
- Sistema de eventos detallado (goles, tarjetas, faltas, etc.)
- Estados de partido (programado, en vivo, finalizado)
- Asignación de corresponsales a partidos
- Alineaciones de equipos

#### **Sistema de Equipos Completo**
- Gestión de 20+ equipos participantes del Mundial 2026
- Asignación de equipos favoritos a fanáticos
- Información detallada de equipos con banderas
- Sistema de gestión de equipos favoritos con interfaz gráfica

#### **Chat en Tiempo Real**
- Chat específico para cada partido
- Mensajería entre usuarios con persistencia
- Interfaz de chat integrada en los dashboards
- Historial de mensajes por partido

#### **Sistema de Partidos en Vivo**
- Ventana dedicada para seguimiento de partidos en vivo
- Actualización automática cada 30 segundos
- Visualización de eventos en tiempo real
- Marcadores actualizados dinámicamente

#### **Persistencia de Datos **
- Serialización automática de datos
- Archivos de datos: `users.ser`, `matches.ser`, `teams.ser`, `chats.ser`
- Carga automática al iniciar la aplicación
- Manejo de errores en la persistencia

#### **Interfaz de Usuario Moderna**
- Diseño responsive con colores modernos
- Tipografía Inter para mejor legibilidad
- Iconografía con emojis de banderas
- Navegación intuitiva entre ventanas
- Botones estilizados y efectos visuales

### 🔄 Funcionalidades en Desarrollo

- Registro de nuevos usuarios desde la interfaz
- Recuperación de contraseñas
- Notificaciones push
- Estadísticas avanzadas de jugadores
- Exportación de reportes en PDF
- Sistema de notificaciones en tiempo real

## Estructura de Carpetas

```
Semana9/
├── .gitignore                 # Archivo de exclusión para Git
├── LICENSE.md                 # Licencia del proyecto
├── README.md                  # Documentación del proyecto
├── src/                       # Código fuente principal
│   └── com/
│       └── fidespn/
│           ├── main/          # Punto de entrada de la aplicación
│           │   └── MainApp.java
│           ├── model/         # Modelos de datos
│           │   ├── Administrator.java
│           │   ├── Chat.java
│           │   ├── ChatMessage.java
│           │   ├── Correspondent.java
│           │   ├── Fanatic.java
│           │   ├── Match.java
│           │   ├── MatchEvent.java
│           │   ├── Player.java
│           │   ├── Team.java
│           │   └── User.java
│           ├── service/       # Lógica de negocio
│           │   ├── exceptions/
│           │   │   ├── DuplicateUsernameException.java
│           │   │   ├── InvalidCredentialsException.java
│           │   │   ├── MatchNotFoundException.java
│           │   │   ├── TeamNotFoundException.java
│           │   │   └── UserNotFoundException.java
│           │   ├── MatchManager.java
│           │   └── UserManager.java
│           └── view/          # Interfaces de usuario
│               ├── AdminDashboardFrame.java
│               ├── CorrespondentDashboardFrame.java
│               ├── FanaticDashboardFrame.java
│               ├── LiveMatchFrame.java
│               ├── LoginFrame.java
│               └── ManageFavoriteTeamsFrame.java
├── chats.ser                  # Datos de chats (generado automáticamente)
├── matches.ser                # Datos de partidos (generado automáticamente)
├── teams.ser                  # Datos de equipos (generado automáticamente)
└── users.ser                  # Datos de usuarios (generado automáticamente)
```

### Descripción de Paquetes

#### **`main`**
Contiene la clase principal `MainApp.java` que:
- Inicia la aplicación en el Event Dispatch Thread (EDT)
- Configura los managers de usuarios y partidos
- Crea usuarios de prueba automáticamente
- Inicializa datos de demostración

#### **`model`**
Define todas las entidades del sistema:
- **User**: Clase abstracta base para todos los usuarios
- **Administrator/Correspondent/Fanatic**: Implementaciones específicas de usuario
- **Match**: Representa un partido con todos sus datos y eventos
- **Team**: Información de equipos participantes con banderas
- **Chat/ChatMessage**: Sistema de mensajería en tiempo real
- **MatchEvent**: Eventos durante un partido (goles, tarjetas, faltas)
- **Player**: Información de jugadores

#### **`service`**
Contiene la lógica de negocio:
- **UserManager**: Gestión completa de usuarios, autenticación, registro, actualización
- **MatchManager**: Gestión de partidos, equipos, eventos, chats y persistencia
- **exceptions**: Excepciones personalizadas para manejo de errores específicos

#### **`view`**
Interfaces gráficas de usuario modernas:
- **LoginFrame**: Pantalla de inicio de sesión con diseño moderno
- **AdminDashboardFrame**: Dashboard completo para administradores
- **CorrespondentDashboardFrame**: Dashboard especializado para corresponsales
- **FanaticDashboardFrame**: Dashboard personalizado para fanáticos
- **LiveMatchFrame**: Ventana para seguimiento de partidos en vivo
- **ManageFavoriteTeamsFrame**: Gestión de equipos favoritos

## Características Técnicas

### **Arquitectura MVC**
- **Model**: Entidades de datos en el paquete `model`
- **View**: Interfaces gráficas en el paquete `view`
- **Controller**: Lógica de negocio en el paquete `service`

### **Persistencia de Datos**
- Serialización Java para almacenamiento local
- Archivos `.ser` para cada tipo de entidad
- Carga automática al inicio de la aplicación
- Guardado automático tras cada operación

### **Manejo de Excepciones**
- Excepciones personalizadas para cada tipo de error
- Validación de datos de entrada
- Mensajes de error informativos para el usuario

### **Interfaz de Usuario**
- Diseño moderno con colores profesionales
- Tipografía Inter para mejor legibilidad
- Componentes Swing personalizados
- Navegación intuitiva entre ventanas

### **Tiempo Real**
- Timer para actualizaciones automáticas
- Chat en vivo por partido
- Actualización de marcadores dinámica
- Eventos de partido en tiempo real

## Tecnologías Utilizadas

- **Java 8+**: Lenguaje de programación principal
- **Java Swing**: Framework para interfaz gráfica
- **Java Serialization**: Persistencia de datos
- **Java Timer**: Actualizaciones en tiempo real
- **Arquitectura MVC**: Separación de responsabilidades
- **Git**: Control de versiones

## Flujo de Uso

### **Para Administradores:**
1. Iniciar sesión con credenciales de administrador
2. Acceder al dashboard de administración
3. Gestionar usuarios, partidos y equipos
4. Supervisar eventos y reportes

### **Para Corresponsales:**
1. Iniciar sesión con credenciales de corresponsal
2. Acceder al dashboard de corresponsal
3. Crear y gestionar partidos
4. Reportar eventos en tiempo real
5. Actualizar marcadores y estadísticas

### **Para Fanáticos:**
1. Iniciar sesión con credenciales de fanático
2. Acceder al dashboard personalizado
3. Gestionar equipos favoritos
4. Ver partidos en vivo
5. Participar en chats de partido

## Contribución

Para contribuir al proyecto:

1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -am 'Agrega nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crea un Pull Request

## Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE.md` para más detalles.

## Contacto

Para preguntas o soporte, contacta al equipo de desarrollo de FidESPN United 2026.

---

**Versión**: 2.0  
**Última actualización**: Julio 2025  
**Estado**: Funcionalidades principales completadas 