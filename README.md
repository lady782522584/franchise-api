# Franchise API

API REST reactiva para gestionar franquicias, sus sucursales y los productos de cada sucursal. Proyecto universitario.

**Stack:** Java 17, Spring Boot 4.1.2, Spring WebFlux (reactivo), MongoDB (Atlas), Maven, Swagger (springdoc), JUnit 5 + StepVerifier.

## Modelo

Una **franquicia** tiene un nombre y una lista de **sucursales**. Cada sucursal tiene un nombre y una lista de **productos**. Cada producto tiene un nombre y un stock. Se guarda como un único documento anidado en MongoDB (colección `franchises`).

## Requisitos

- Java 17
- Una base de datos MongoDB (se usó MongoDB Atlas, plan gratuito M0)
- No hace falta instalar Maven: el proyecto incluye `mvnw`

## Ejecutar en local

1. Clonar el repositorio:

```bash
git clone https://github.com/lady782522584/franchise-api.git
cd franchise-api
```

2. Definir la cadena de conexión en la variable de entorno `MONGODB_URI` (la contraseña nunca se escribe en el código).

PowerShell:

```powershell
$env:MONGODB_URI = 'mongodb+srv://USUARIO:CONTRASEÑA@TU_CLUSTER.mongodb.net/franchise_db?retryWrites=true&w=majority'
```

Linux / Mac:

```bash
export MONGODB_URI='mongodb+srv://USUARIO:CONTRASEÑA@TU_CLUSTER.mongodb.net/franchise_db?retryWrites=true&w=majority'
```

Si no se define, la app intenta conectarse a `mongodb://localhost:27017/franchise_db`.

3. Arrancar:

```bash
./mvnw spring-boot:run        # Linux / Mac
.\mvnw.cmd spring-boot:run    # Windows
```

La API queda en `http://localhost:8080`. En Atlas hay que permitir la IP desde la que se ejecuta (Network Access).

## Documentación interactiva (Swagger)

Con la app corriendo: http://localhost:8080/swagger-ui.html

## Endpoints

Base: `/api/franchises`

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/franchises` | Agregar franquicia |
| POST | `/api/franchises/{franchiseId}/branches` | Agregar sucursal a una franquicia |
| POST | `/api/franchises/{franchiseId}/branches/{branchId}/products` | Agregar producto a una sucursal |
| DELETE | `/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}` | Eliminar producto de una sucursal |
| PATCH | `/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock` | Modificar el stock de un producto |
| GET | `/api/franchises/{franchiseId}/top-products` | Producto con más stock por sucursal de una franquicia |
| PATCH | `/api/franchises/{franchiseId}` | Renombrar franquicia (plus) |
| PATCH | `/api/franchises/{franchiseId}/branches/{branchId}` | Renombrar sucursal (plus) |
| PATCH | `/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}` | Renombrar producto (plus) |

Cuerpos de ejemplo:

- Franquicia, sucursal o renombrar: `{"name": "Mi Franquicia"}`
- Producto: `{"name": "Hamburguesa", "stock": 25}`
- Stock: `{"stock": 100}`

Errores: 404 si la franquicia, sucursal o producto no existe, y 400 si los datos son inválidos (formato RFC 7807).

## Tests

```bash
./mvnw test -Dtest=FranchiseServiceTest
```

Pruebas unitarias reactivas con Mockito y StepVerifier (no requieren base de datos).

## Decisiones de diseño

- **WebFlux + Reactive MongoDB** para programación reactiva de extremo a extremo.
- **Documento anidado**: franquicia → sucursales → productos en un solo documento, sin joins.
- **Validación** con Bean Validation y manejo global de errores con `ProblemDetail`.
- **Configuración por variables de entorno** (`MONGODB_URI`, `PORT`), sin secretos en el repositorio.

## Uso de IA

(si utilice ia principalmente para el readme considero que es muy buena para documentaciony y la utilizo para validar fallas en mis proyectos solo si son errores conocidos en esta caso solo la utilice para el readme por que tenia solido conocimiento y no se me presento ningun dificultad, Yo ejecuté y probé cada endpoint, y corregí problemas como la propiedad de conexión de MongoDB en Spring Boot 4 y la autenticación con Atlas.")

## Autora

Leidy Mahecha