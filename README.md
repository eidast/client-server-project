# FidESPN United 2026

## Resumen

FidESPN United 2026 es una aplicación de gestión de partidos de fútbol desarrollada en Java Swing para el Mundial United 2026. El sistema permite a diferentes tipos de usuarios (Administradores, Corresponsales y Fanáticos) interactuar con partidos en tiempo real, incluyendo reportes de eventos, chat en vivo y gestión de equipos favoritos.

La aplicación utiliza una arquitectura MVC (Model-View-Controller) con persistencia de datos mediante serialización Java, proporcionando una experiencia completa para la gestión y seguimiento de partidos de fútbol.

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

La aplicación incluye usuarios de demostración preconfigurados:

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| `admin` | `admin123` | Administrador |
| `corresponsal1` | `pass123` | Corresponsal |
| `fanatico1` | `pass123` | Fanático |
| `fanatico2` | `pass123` | Fanático |

## Características Implementadas

### ✅ Funcionalidades Completadas

#### **Sistema de Autenticación**
- Login con validación de credenciales
- Manejo de diferentes tipos de usuario
- Interfaz gráfica moderna y responsive

#### **Gestión de Usuarios**
- **Administrador**: Control total del sistema, gestión de usuarios y partidos
- **Corresponsal**: Reportes en tiempo real, gestión de eventos de partido
- **Fanático**: Seguimiento de equipos favoritos, chat en vivo

#### **Gestión de Partidos**
- Creación y gestión de partidos
- Actualización de marcadores en tiempo real
- Sistema de eventos (goles, tarjetas, etc.)
- Estados de partido (programado, en vivo, finalizado)

#### **Sistema de Equipos**
- Gestión de equipos participantes
- Asignación de equipos favoritos a fanáticos
- Información detallada de equipos

#### **Chat en Tiempo Real**
- Chat específico para cada partido
- Mensajería entre usuarios
- Persistencia de conversaciones

#### **Persistencia de Datos**
- Serialización automática de datos
- Archivos de datos: `users.ser`, `matches.ser`, `teams.ser`, `chats.ser`
- Carga automática al iniciar la aplicación

### 🔄 Funcionalidades en Desarrollo

- Registro de nuevos usuarios
- Recuperación de contraseñas
- Notificaciones push
- Estadísticas avanzadas
- Exportación de reportes

## Estructura de Carpetas

```
Semana9/
├── .gitignore                 # Archivo de exclusión para Git
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
│               └── LoginFrame.java
├── chats.ser                  # Datos de chats (generado automáticamente)
├── matches.ser                # Datos de partidos (generado automáticamente)
├── teams.ser                  # Datos de equipos (generado automáticamente)
└── users.ser                  # Datos de usuarios (generado automáticamente)
```

### Descripción de Paquetes

#### **`main`**
Contiene la clase principal `MainApp.java` que inicia la aplicación y configura los datos de prueba.

#### **`model`**
Define todas las entidades del sistema:
- **User**: Clase abstracta base para todos los usuarios
- **Administrator/Correspondent/Fanatic**: Implementaciones específicas de usuario
- **Match**: Representa un partido con todos sus datos
- **Team**: Información de equipos participantes
- **Chat/ChatMessage**: Sistema de mensajería
- **MatchEvent**: Eventos durante un partido (goles, tarjetas)

#### **`service`**
Contiene la lógica de negocio:
- **UserManager**: Gestión de usuarios, autenticación, registro
- **MatchManager**: Gestión de partidos, equipos, eventos y chats
- **exceptions**: Excepciones personalizadas para manejo de errores

#### **`view`**
Interfaces gráficas de usuario:
- **LoginFrame**: Pantalla de inicio de sesión
- **AdminDashboardFrame**: Dashboard para administradores
- **CorrespondentDashboardFrame**: Dashboard para corresponsales
- **FanaticDashboardFrame**: Dashboard para fanáticos

## Tecnologías Utilizadas

- **Java 8+**: Lenguaje de programación principal
- **Java Swing**: Framework para interfaz gráfica
- **Java Serialization**: Persistencia de datos
- **Arquitectura MVC**: Separación de responsabilidades
- **Git**: Control de versiones

## Contribución

Para contribuir al proyecto:

1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -am 'Agrega nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crea un Pull Request

## Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## Contacto

Para preguntas o soporte, contacta al equipo de desarrollo de FidESPN United 2026. 