# 🧪 Lista de Verificación de Integración

Usa esta lista para verificar que todo está funcionando correctamente después de la integración.

## ✅ Pre-requisitos

- [ ] Node.js instalado (v16+)
- [ ] Java 17+ instalado
- [ ] Maven instalado
- [ ] Git instalado
- [ ] PowerShell disponible

---

## 🔧 Verificación de Archivos

### Variables de Entorno
- [ ] Archivo `frontend/.env.local` existe
- [ ] Archivo `frontend/.env.development` existe
- [ ] Archivo `frontend/.env.production` existe
- [ ] Variables `VITE_API_GATEWAY_URL` configuradas correctamente

### Cliente API
- [ ] Archivo `frontend/src/lib/api.ts` actualizado
- [ ] Sin errores de TypeScript en el archivo
- [ ] Exporta: `authApi`, `projectsApi`, `tasksApi`, `teamsApi`, `statesApi`

### Test y Ejemplos
- [ ] Archivo `frontend/src/lib/testConnection.ts` creado
- [ ] Archivo `frontend/src/lib/apiExamples.ts` actualizado
- [ ] Sin errores de compilación

### Scripts
- [ ] Archivo `start-all-services.ps1` existe
- [ ] Archivo `stop-all-services.ps1` existe
- [ ] Scripts tienen permisos de ejecución

### Documentación
- [ ] `INTEGRATION_GUIDE.md` creado
- [ ] `QUICKSTART.md` creado
- [ ] `INTEGRATION_SUMMARY.md` creado

---

## 🚀 Verificación de Backend

### 1. Gateway (Puerto 8080)
```powershell
cd backend/gateway
./mvnw spring-boot:run
```

**Verificar:**
- [ ] Inicia sin errores
- [ ] Log muestra: `Started GatewayApplication`
- [ ] Endpoint responde: `curl http://localhost:8080/actuator/health`

### 2. Authenticator (Puerto 8081)
```powershell
cd backend/authenticator
./mvnw spring-boot:run
```

**Verificar:**
- [ ] Inicia sin errores
- [ ] Log muestra: `Started AuthenticatorApplication`
- [ ] Endpoint responde: `curl http://localhost:8081/authenticator/person/message`
- [ ] A través del Gateway: `curl http://localhost:8080/authenticator/person/message`

### 3. InnoSistemas (Puerto 8082)
```powershell
cd backend/innosistemas
./mvnw spring-boot:run
```

**Verificar:**
- [ ] Inicia sin errores
- [ ] Log muestra: `Started InnosistemasApplication`
- [ ] Endpoint responde: `curl http://localhost:8082/project/project/message`
- [ ] A través del Gateway: `curl http://localhost:8080/project/project/message`

---

## 🌐 Verificación de Frontend

### Instalación y Build
```powershell
cd frontend
npm install
npm run build
```

**Verificar:**
- [ ] `npm install` completa sin errores
- [ ] `npm run build` genera carpeta `dist`
- [ ] Sin errores de TypeScript

### Desarrollo
```powershell
npm run dev
```

**Verificar:**
- [ ] Inicia en `http://localhost:5173`
- [ ] Página carga correctamente
- [ ] No hay errores en consola del navegador
- [ ] Variables de entorno se cargan: `import.meta.env.VITE_API_GATEWAY_URL`

---

## 🧪 Verificación de Conectividad

### Test desde Navegador

1. Abrir: `http://localhost:5173`
2. Abrir consola (F12)
3. Ejecutar:

```javascript
// Test de conexión
await window.testConnection()
```

**Resultado esperado:**
```
✅ Gateway (8080) - Conectado
✅ Authenticator (8081) - Conectado
✅ InnoSistemas (8082) - Conectado
✨ Todos los servicios están operativos
```

### Test de Endpoints Individuales

```javascript
// 1. Verificar servicio de autenticación
const authResponse = await window.ejemplosApi.ejemploVerificarAuth()
console.log('Auth service:', authResponse)

// 2. Obtener proyectos
const projects = await window.ejemplosApi.fetchProjects()
console.log('Proyectos:', projects)

// 3. Obtener tareas
const tasks = await window.ejemplosApi.fetchTasks()
console.log('Tareas:', tasks)

// 4. Obtener estados
const states = await window.ejemplosApi.fetchStates()
console.log('Estados:', states)
```

---

## 🔐 Verificación de Autenticación

### Test de Login (si tienes usuario de prueba)

```javascript
// Reemplaza con credenciales reales
const loginResponse = await window.ejemplosApi.ejemploLogin()
console.log('Login:', loginResponse)

// Verificar que el token se guardó
console.log('Token guardado:', localStorage.getItem('token'))
```

**Verificar:**
- [ ] Login devuelve token JWT
- [ ] Token se guarda en localStorage
- [ ] Peticiones posteriores incluyen token en headers

---

## 🔌 Verificación de CORS

### Test desde Frontend
```javascript
// Debería funcionar sin errores de CORS
const response = await fetch('http://localhost:8080/project/project/listAll', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${localStorage.getItem('token')}`
  },
  credentials: 'include'
})

console.log('CORS test:', response.status)
```

**Verificar:**
- [ ] No hay error: "CORS policy: No 'Access-Control-Allow-Origin'"
- [ ] Respuesta exitosa (status 200 o 401)

---

## 🎯 Verificación de Endpoints

### Authenticator
```javascript
// POST /authenticator/person/authenticate
const authResult = await fetch('http://localhost:8080/authenticator/person/authenticate', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'test@example.com',
    password: 'password123'
  })
})
console.log('Auth result:', await authResult.json())
```

- [ ] Endpoint responde
- [ ] Devuelve token o error apropiado

### Proyectos
```javascript
// GET /project/project/listAll
const projects = await window.ejemplosApi.fetchProjects()
```

- [ ] Lista proyectos o array vacío
- [ ] Sin errores 500

### Tareas
```javascript
// GET /project/task/listAll
const tasks = await window.ejemplosApi.fetchTasks()
```

- [ ] Lista tareas o array vacío
- [ ] Sin errores 500

### Estados
```javascript
// GET /project/state/listAll
const states = await window.ejemplosApi.fetchStates()
```

- [ ] Lista estados
- [ ] Array con al menos 1 estado

---

## 🛠️ Verificación de Manejo de Errores

### Test de Error 401
```javascript
// Limpiar token
localStorage.removeItem('token')

// Intentar acceder a endpoint protegido
try {
  await window.ejemplosApi.fetchProjects()
} catch (error) {
  console.log('Error capturado correctamente:', error)
}
```

**Verificar:**
- [ ] Error 401 es capturado
- [ ] Redirige a página de login (o muestra mensaje)

### Test de Servicio No Disponible
```powershell
# Detener InnoSistemas
# Luego en el navegador:
```

```javascript
try {
  await window.ejemplosApi.fetchProjects()
} catch (error) {
  console.log('Error de conexión:', error.message)
}
```

**Verificar:**
- [ ] Mensaje de error claro
- [ ] No rompe la aplicación

---

## 📊 Verificación de Flujo Completo

### Crear Proyecto con Tareas
```javascript
await window.ejemplosApi.ejemploFlujoCompleto()
```

**Verificar:**
- [ ] Proyecto se crea correctamente
- [ ] 3 tareas se crean para el proyecto
- [ ] No hay errores en el proceso
- [ ] Log muestra progreso:
  - `1️⃣ Obteniendo estados...`
  - `2️⃣ Creando proyecto...`
  - `3️⃣ Creando tareas...`
  - `🎉 Flujo completado exitosamente!`

---

## 🔄 Verificación de Operaciones CRUD

### Proyectos

```javascript
// CREATE
const newProject = await window.ejemplosApi.createProject({
  name: "Test Project",
  description: "Test Description",
  teamId: 1
})
console.log('Proyecto creado:', newProject)

// READ
const allProjects = await window.ejemplosApi.fetchProjects()
console.log('Todos los proyectos:', allProjects)

// UPDATE
const updated = await window.ejemplosApi.updateProject({
  id: newProject.id,
  name: "Test Project Updated",
  description: "Updated Description",
  teamId: 1
})
console.log('Proyecto actualizado:', updated)

// DELETE
await window.ejemplosApi.deleteProject(newProject.id)
console.log('Proyecto eliminado')
```

**Verificar:**
- [ ] CREATE funciona
- [ ] READ funciona
- [ ] UPDATE funciona
- [ ] DELETE funciona

### Tareas

```javascript
// CREATE
const newTask = await window.ejemplosApi.createTask({
  name: "Test Task",
  description: "Test Description",
  projectId: 1, // Usar ID de proyecto existente
  stateId: 1
})
console.log('Tarea creada:', newTask)

// READ
const allTasks = await window.ejemplosApi.fetchTasks()
console.log('Todas las tareas:', allTasks)

// UPDATE
const updatedTask = await window.ejemplosApi.updateTask({
  id: newTask.id,
  name: "Test Task Updated",
  description: "Updated Description",
  projectId: 1,
  stateId: 2
})
console.log('Tarea actualizada:', updatedTask)

// UPDATE STATE
await window.ejemplosApi.updateTaskState(newTask.id, 3)
console.log('Estado actualizado')

// DELETE
await window.ejemplosApi.deleteTask(newTask.id)
console.log('Tarea eliminada')
```

**Verificar:**
- [ ] CREATE funciona
- [ ] READ funciona
- [ ] UPDATE funciona
- [ ] UPDATE STATE funciona
- [ ] DELETE funciona

---

## 🎯 Checklist Final

### Backend
- [ ] Gateway corriendo en 8080
- [ ] Authenticator corriendo en 8081
- [ ] InnoSistemas corriendo en 8082
- [ ] Todos los servicios responden a health checks
- [ ] CORS configurado correctamente
- [ ] Logs no muestran errores

### Frontend
- [ ] Aplicación inicia en 5173
- [ ] Variables de entorno cargadas
- [ ] Cliente API configurado
- [ ] Sin errores de TypeScript
- [ ] Sin errores en consola del navegador

### Integración
- [ ] Gateway enruta correctamente a microservicios
- [ ] Authenticator accesible a través del Gateway
- [ ] InnoSistemas accesible a través del Gateway
- [ ] Test de conexión exitoso
- [ ] Todos los endpoints responden

### Funcionalidad
- [ ] Login funciona (si hay usuario de prueba)
- [ ] Listar proyectos funciona
- [ ] Crear proyecto funciona
- [ ] Actualizar proyecto funciona
- [ ] Eliminar proyecto funciona
- [ ] Listar tareas funciona
- [ ] Crear tarea funciona
- [ ] Actualizar tarea funciona
- [ ] Actualizar estado de tarea funciona
- [ ] Eliminar tarea funciona

### Seguridad
- [ ] JWT se guarda correctamente
- [ ] JWT se envía en cada petición
- [ ] Error 401 maneja correctamente
- [ ] CORS permite solo orígenes permitidos

---

## 📝 Notas de Verificación

Fecha: ___________
Verificado por: ___________

### Problemas encontrados:
```
[Espacio para notas]
```

### Soluciones aplicadas:
```
[Espacio para notas]
```

---

## 🆘 Si algo falla

1. **Revisar logs de servicios** - Cada servicio muestra logs en su terminal
2. **Verificar puertos** - `netstat -ano | findstr :<puerto>`
3. **Reiniciar servicios** - `./stop-all-services.ps1` y luego `./start-all-services.ps1`
4. **Limpiar caché** - `Ctrl+Shift+R` en el navegador
5. **Verificar base de datos** - Asegúrate de que las credenciales sean correctas
6. **Consultar documentación** - Ver `INTEGRATION_GUIDE.md` para detalles

---

**¿Todo funcionando?** ✅ 
¡Excelente! La integración está completa.

**¿Algo no funciona?** ❌  
Revisa la sección "Solución de Problemas" en `INTEGRATION_GUIDE.md`
