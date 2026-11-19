MPAZ Pañol · Microservicios

Backend de MPAZ Pañol, la app móvil para gestionar el inventario, la reserva y el préstamo de equipos tecnológicos de la Escuela Escritora Marcela Paz.

Proyecto de la asignatura DSY1105 · Desarrollo de Aplicaciones Móviles. La app Android está en otro repositorio: MPAZ-EP2-Moviles.

Integrantes
Nombre	GitHub
Benjamin Aedo	@beaedov
Thomas Huenchulao	@thuenchulao
Problema que resuelve

Hoy los docentes piden equipos (proyectores, notebooks, kits de robótica) por correo al encargado de TI. Esto provoca reservas duplicadas, solicitudes perdidas y falta de control sobre quién tiene cada equipo.

El backend controla el stock de cada equipo, gestiona el ciclo de vida de las solicitudes y deja una bitácora de cada cambio de estado.

Arquitectura

Tres microservicios en Spring Boot con una única base de datos PostgreSQL, donde cada servicio administra sus propias tablas.

                  ┌──────────────────────┐
                  │   App Android        │
                  │ (Retrofit, Kotlin)   │
                  └───┬──────┬──────┬────┘
                      │      │      │
            ┌─────────▼┐ ┌───▼──────┴───┐ ┌▼───────────────┐
            │inventario│ │ solicitudes  │ │ trazabilidad   │
            │  :8081   │◄┤    :8082     ├►│     :8083      │
            └────┬─────┘ └──────┬───────┘ └───────┬────────┘
                 └──────────────┼─────────────────┘
                                ▼
                     ┌─────────────────────┐
                     │ PostgreSQL (mpazdb) │
                     │ Docker · puerto 5434│
                     └─────────────────────┘
Microservicio	Puerto	Responsabilidad	Tablas
inventario-service	8081	Catálogo de equipos y control de stock	equipos
solicitudes-service	8082	Solicitudes de préstamo y sus estados	solicitudes, solicitud_items
trazabilidad-service	8083	Bitácora de cambios de estado	eventos_solicitud

El login y el registro de usuarios no pasan por el backend: se mantienen en SQLite (Room) dentro de la app.

Comunicación entre servicios

solicitudes-service consume por HTTP (con WebClient) a los otros dos:

Acción en solicitudes	Llamada que realiza
Crear	Evento Pendiente en trazabilidad
Aceptar	PATCH /descontar en inventario por cada equipo, y evento Aceptada
Rechazar	Evento Rechazada con la justificación
Entregar	Evento Entregada
Devolver	PATCH /reponer en inventario por cada equipo, y evento Devuelta
Inventario es obligatorio: si no hay stock o el equipo no existe, la solicitud no se acepta (400).
Compensación: si falla el descuento de un equipo, se reponen los ya descontados para no dejar el stock a medias.
Trazabilidad es opcional: si está caída, solo se deja un aviso en el log y la solicitud igual se guarda.
Endpoints
inventario-service · http://localhost:8081
Método	Ruta	Descripción	Respuestas
GET	/api/equipos	Lista equipos (filtros opcionales ?categoria= y ?estado=)	200
GET	/api/equipos/{id}	Obtiene un equipo	200, 404
POST	/api/equipos	Crea un equipo	201
PUT	/api/equipos/{id}	Reemplaza los datos de un equipo	200, 404
PATCH	/api/equipos/{id}/descontar?cantidad=	Descuenta stock disponible	200, 400, 404
PATCH	/api/equipos/{id}/reponer?cantidad=	Repone stock (sin superar el total)	200, 404

Al iniciar por primera vez carga 10 equipos de ejemplo si la tabla está vacía.

solicitudes-service · http://localhost:8082
Método	Ruta	Descripción	Respuestas
GET	/api/solicitudes	Lista solicitudes (filtros opcionales ?email= y ?estado=)	200
GET	/api/solicitudes/{id}	Obtiene una solicitud	200, 404
POST	/api/solicitudes	Crea una solicitud (queda Pendiente)	201
PATCH	/api/solicitudes/{id}/aceptar	Pendiente → Aceptada (descuenta stock)	200, 400, 404
PATCH	/api/solicitudes/{id}/rechazar?comentario=	Pendiente → Rechazada (justificación obligatoria)	200, 400, 404
PATCH	/api/solicitudes/{id}/entregar	Aceptada → Entregada	200, 400, 404
PATCH	/api/solicitudes/{id}/devolver	Entregada → Devuelta (repone stock)	200, 400, 404

Ciclo de estados: Pendiente → Aceptada → Entregada → Devuelta, o Pendiente → Rechazada.

Ejemplo de body para POST /api/solicitudes:

json
{
  "solicitanteNombre": "Docente Prueba",
  "solicitanteEmail": "docente@mpaz.cl",
  "equipos": "Proyector Epson x2",
  "sala": "Sala 5",
  "fecha": "20/11/2026",
  "horaInicio": "10:00",
  "horaDevolucion": "11:00",
  "items": [ { "equipoId": 1, "cantidad": 2 } ]
}

equipos es el resumen que se muestra al usuario; items indica qué equipo y cuántas unidades se descuentan del inventario.

trazabilidad-service · http://localhost:8083
Método	Ruta	Descripción	Respuestas
POST	/api/eventos	Registra un evento de una solicitud	201, 400
GET	/api/eventos	Lista eventos (filtro opcional ?estado=)	200
GET	/api/eventos/solicitud/{solicitudId}	Historial de una solicitud	200

Estados válidos: Pendiente, Aceptada, Rechazada, Entregada, Devuelta. Rechazada exige un comentario.

Modelo de datos
Tabla	Campos principales
equipos	id, nombre, marca, modelo, categoria, stockTotal, stockDisponible, estado, ubicacion
solicitudes	id, solicitanteNombre, solicitanteEmail, equipos, sala, fecha, horaInicio, horaDevolucion, estado, fechaCreacion, comentario
solicitud_items	solicitud_id, equipoId, cantidad
eventos_solicitud	id, solicitudId, estado, fechaHora, actor, comentario
Tecnologías

Java 21 · Spring Boot 4.1 (Web, Data JPA, WebFlux para WebClient) · PostgreSQL 18 · Docker Compose · Lombok · JUnit 5 y Mockito · Maven

Estructura
Mpaz-panol_Microservicios_Aedo_Huenchulao
├── docker-compose.yml        → PostgreSQL
├── inventario-service
├── solicitudes-service
└── trazabilidad-service

com.mpaz.<servicio>
├── config        → Configuración (datos iniciales, WebClient)
├── controller    → Endpoints REST
├── service       → Lógica de negocio (interfaz + implementación)
├── repository    → Acceso a la base de datos (Spring Data JPA)
└── entity        → Entidades que se guardan como tablas
Cómo ejecutar

Requisitos: JDK 21, Maven (o el mvnw incluido) y Docker Desktop.

Clonar el repositorio:
bash
git clone https://github.com/thuenchulao/Mpaz-panol_Microservicios_Aedo_Huenchulao.git
cd Mpaz-panol_Microservicios_Aedo_Huenchulao
Crear un archivo .env en la raíz, junto a docker-compose.yml (no se sube a GitHub; son credenciales solo para desarrollo local):
DB_USER=postgres
DB_PASSWORD=postgres
Levantar la base de datos (crea mpazdb en el puerto 5434):
bash
docker compose up -d
Levantar cada microservicio, desde IntelliJ (clase ...Application) o desde la terminal (en Windows usa mvnw.cmd):
bash
cd inventario-service && ./mvnw spring-boot:run
cd solicitudes-service && ./mvnw spring-boot:run
cd trazabilidad-service && ./mvnw spring-boot:run

Las tablas se crean solas al iniciar (ddl-auto: update).

Variables de configuración (todas con valor por defecto para desarrollo local): PORT (8081 / 8082 / 8083), DB_HOST (localhost), DB_PORT (5434), DB_NAME (mpazdb), DB_USER y DB_PASSWORD (postgres), DDL_AUTO (update) y SHOW_SQL (true).

Cómo probar (Postman)

Con la base y los tres servicios levantados:

POST http://localhost:8082/api/solicitudes con el body de ejemplo.
GET http://localhost:8081/api/equipos/1 y anota el stockDisponible.
PATCH http://localhost:8082/api/solicitudes/1/aceptar → estado Aceptada, y el stock baja.
PATCH .../1/entregar y luego .../1/devolver → el stock vuelve a subir.
GET http://localhost:8083/api/eventos/solicitud/1 → aparecen todos los estados registrados.

Casos de error: aceptar con más unidades de las disponibles (400, el stock no cambia), rechazar sin ?comentario= (400), entregar una solicitud no Aceptada (400) y consultar un id inexistente (404).

Pruebas unitarias

Con JUnit 5 y Mockito (@Mock, @InjectMocks, patrón Arrange-Act-Assert). Los repositorios y las llamadas a otros servicios se simulan, por lo que no necesitan base de datos ni servicios levantados.

Servicio	Clase	Pruebas
inventario-service	EquipoServiceImplTest	6
solicitudes-service	SolicitudServiceImplTest	11
trazabilidad-service	EventoServiceImplTest	7

Validan el descuento y la reposición de stock, el ciclo de estados, la justificación obligatoria al rechazar, el fallo de inventario sin guardar cambios y las validaciones del registro de eventos. Se ejecutan con ./mvnw test dentro de cada servicio, o desde IntelliJ (clic derecho en la clase → Run).

Flujo de trabajo y planificación
main: versión estable para entregar.
Ramas feature/...: nacen de main y vuelven mediante Pull Request.
Tablero de Trello: https://trello.com/b/0l3Gx364/mpazpanol-ep2

Todos los datos del sistema (equipos, solicitantes, correos) son ficticios, según la Ley de protección de datos personales.
Planificación

Tablero de Trello: https://trello.com/b/0l3Gx364/mpazpanol-ep2

Todos los datos del sistema (equipos, solicitantes, correos) son ficticios, según la Ley de protección de datos personales.
