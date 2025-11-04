# 🏗️ Integración Frontend-Backend con Arquitectura de Microservicios

## 📋 Arquitectura del Sistema

El sistema está construido con una arquitectura de microservicios donde el frontend se comunica con un **API Gateway** que enruta las peticiones a los diferentes microservicios:

```
Frontend (React + Vite)
     ↓ Puerto 5173
     ↓
API Gateway (Spring Cloud Gateway)
     ↓ Puerto 8080
     ├─→ Authenticator Service (Puerto 8081)
     │   └─ /authenticator/**
     └─→ InnoSistemas Service (Puerto 8082)
         └─ /project/**
```

## 🔌 Configuración de Puertos

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| **Frontend** | 5173 | Aplicación React con Vite |
| **Gateway** | 8080 | API Gateway principal (punto de entrada) |
| **Authenticator** | 8081 | Microservicio de autenticación |
| **InnoSistemas** | 8082 | Microservicio de proyectos y tareas |

## 📦 Microservicios

### 1. Gateway (Puerto 8080)
**Archivo**: `backend/gateway/application.yml`

Enrutamiento configurado:
- `/authenticator/**` → `http://localhost:8081`
- `/project/**` → `http://localhost:8082`

### 2. Authenticator (Puerto 8081)
**Archivo**: `backend/authenticator/src/main/resources/application.properties`

**Endpoints disponibles**:
- `POST /authenticator/person/authenticate` - Autenticación de usuarios
- `GET /authenticator/person/message` - Health check

### 3. InnoSistemas (Puerto 8082)
**Archivo**: `backend/innosistemas/src/main/resources/application-prod.properties`

**Endpoints disponibles**:

#### Proyectos
- `GET /project/project/listAll` - Listar todos los proyectos
- `GET /project/project/listAllById/{id}` - Listar proyectos por equipo
- `POST /project/project/save` - Crear proyecto
- `PUT /project/project/update` - Actualizar proyecto
- `DELETE /project/project/delete/{id}` - Eliminar proyecto
- `GET /project/project/message` - Health check

#### Tareas
- `GET /project/task/listAll` - Listar todas las tareas
- `POST /project/task/save` - Crear tarea
- `PUT /project/task/update` - Actualizar tarea
- `DELETE /project/task/delete/{id}` - Eliminar tarea
- `PUT /project/task/updateState/{id_task}/{id_state}` - Actualizar estado

#### Equipos
- `GET /project/team/getStudentsName/{id}` - Obtener nombres de estudiantes

#### Estados
- `GET /project/state/listAll` - Listar todos los estados

## 🔧 Configuración del Frontend

### Variables de Entorno

El frontend usa variables de entorno para configurar las URLs de los microservicios:

**Archivo**: `.env.local` (para desarrollo local)
```env
VITE_API_GATEWAY_URL=http://localhost:8080
VITE_AUTHENTICATOR_URL=http://localhost:8081
VITE_INNOSISTEMAS_URL=http://localhost:8082
VITE_API_TIMEOUT=30000
VITE_ENV=local
```

**Archivo**: `.env.production` (para producción)
```env
VITE_API_GATEWAY_URL=https://api.innosistemas.com
VITE_AUTHENTICATOR_URL=https://auth.innosistemas.com
VITE_INNOSISTEMAS_URL=https://projects.innosistemas.com
VITE_API_TIMEOUT=30000
VITE_ENV=production
```

### Cliente API

**Archivo**: `frontend/src/lib/api.ts`

El archivo contiene:
1. **Clientes Axios** para cada microservicio
2. **Interceptores** para autenticación automática
3. **APIs organizadas** por funcionalidad:
   - `authApi` - Autenticación
   - `projectsApi` - Proyectos
   - `tasksApi` - Tareas
   - `teamsApi` - Equipos
   - `statesApi` - Estados

## 🚀 Cómo Iniciar el Sistema

### 1. Iniciar Backend (en orden)

#### a) Iniciar Gateway
```powershell
cd backend/gateway
./mvnw spring-boot:run
```
Espera a ver: `Started GatewayApplication in X seconds`

#### b) Iniciar Authenticator
```powershell
cd backend/authenticator
./mvnw spring-boot:run
```
Espera a ver: `Started AuthenticatorApplication in X seconds`

#### c) Iniciar InnoSistemas
```powershell
cd backend/innosistemas
./mvnw spring-boot:run
```
Espera a ver: `Started InnosistemasApplication in X seconds`

### 2. Iniciar Frontend

```powershell
cd frontend
npm install
npm run dev
```

El frontend estará disponible en: `http://localhost:5173`

## 🧪 Probar la Integración

### Opción 1: Test desde el navegador

1. Abre el navegador en `http://localhost:5173`
2. Abre la consola del navegador (F12)
3. Ejecuta:
```javascript
window.testConnection()
```

Deberías ver:
```
✅ Gateway (8080) - Conectado
✅ Authenticator (8081) - Conectado
✅ InnoSistemas (8082) - Conectado
✨ Todos los servicios están operativos
```

### Opción 2: Test manual de endpoints

#### Probar Gateway:
```powershell
curl http://localhost:8080/actuator/health
```

#### Probar Authenticator:
```powershell
curl http://localhost:8080/authenticator/person/message
```

#### Probar InnoSistemas:
```powershell
curl http://localhost:8080/project/project/message
```

## 🔐 Flujo de Autenticación

1. **Usuario inicia sesión** → Frontend envía credenciales
2. **Gateway recibe** → Enruta a `/authenticator/person/authenticate`
3. **Authenticator valida** → Genera token JWT
4. **Token guardado** → `localStorage.setItem('token', token)`
5. **Peticiones futuras** → Interceptor agrega `Authorization: Bearer {token}`

## 📊 Manejo de Errores

El sistema maneja automáticamente:

- **401 Unauthorized**: Redirige al login y limpia tokens
- **Network Errors**: Muestra qué servicio no está disponible
- **Timeout**: 30 segundos por defecto (configurable)

## 🐛 Solución de Problemas

### Error: "No se pudo conectar con el servicio Gateway"

**Solución**:
1. Verifica que el Gateway esté corriendo en el puerto 8080
2. Ejecuta: `cd backend/gateway && ./mvnw spring-boot:run`

### Error: "No se pudo conectar con el servicio Authenticator"

**Solución**:
1. Verifica que Authenticator esté corriendo en el puerto 8081
2. Ejecuta: `cd backend/authenticator && ./mvnw spring-boot:run`

### Error: "No se pudo conectar con el servicio InnoSistemas"

**Solución**:
1. Verifica que InnoSistemas esté corriendo en el puerto 8082
2. Ejecuta: `cd backend/innosistemas && ./mvnw spring-boot:run`

### Error: "CORS policy"

**Solución**:
1. Verifica la configuración CORS en `backend/innosistemas/src/main/resources/application-prod.properties`
2. Asegúrate de que incluya: `cors.allowed.origins=http://localhost:5173`

### Los puertos están ocupados

**Solución**:
```powershell
# Ver qué proceso está usando el puerto
netstat -ano | findstr :8080
netstat -ano | findstr :8081
netstat -ano | findstr :8082

# Detener el proceso (reemplaza PID con el número del proceso)
taskkill /PID <PID> /F
```

## 📚 Ejemplos de Uso

### Crear un Proyecto

```typescript
import { projectsApi } from './lib/api';

const nuevoProyecto = {
  name: "Mi Proyecto",
  description: "Descripción del proyecto",
  team: { id: 1 } // ID del equipo
};

const response = await projectsApi.create(nuevoProyecto);
console.log('Proyecto creado:', response.data);
```

### Crear una Tarea

```typescript
import { tasksApi } from './lib/api';

const nuevaTarea = {
  name: "Mi Tarea",
  description: "Descripción de la tarea",
  project: { id: 1 }, // ID del proyecto
  state: { id: 1 } // ID del estado
};

const response = await tasksApi.create(nuevaTarea);
console.log('Tarea creada:', response.data);
```

### Autenticación

```typescript
import { authApi, saveToken } from './lib/api';

const credentials = {
  email: "usuario@ejemplo.com",
  password: "contraseña123"
};

const response = await authApi.login(credentials);
saveToken(response.data.token);
console.log('Autenticado:', response.data);
```

## 🎯 Checklist de Integración

- [ ] Gateway corriendo en puerto 8080
- [ ] Authenticator corriendo en puerto 8081
- [ ] InnoSistemas corriendo en puerto 8082
- [ ] Frontend corriendo en puerto 5173
- [ ] Archivo `.env.local` creado con las URLs correctas
- [ ] CORS configurado correctamente en el backend
- [ ] Test de conexión exitoso (`window.testConnection()`)
- [ ] Login funcional
- [ ] Endpoints de proyectos funcionando
- [ ] Endpoints de tareas funcionando

## 📖 Recursos Adicionales

- [Documentación de Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
- [Documentación de Axios](https://axios-http.com/)
- [Documentación de Vite](https://vitejs.dev/)

---

**Última actualización**: 3 de noviembre de 2025
**Autor**: Equipo de Desarrollo InnoSistemas
