# 🚀 Guía Rápida de Inicio - InnoSistemas

## ⚡ Inicio Rápido (Recomendado)

### Windows PowerShell
```powershell
# Iniciar todos los microservicios automáticamente
./start-all-services.ps1

# Esperar 45 segundos a que todos los servicios inicien

# En otra terminal, iniciar el frontend
cd frontend
npm install
npm run dev
```

### Detener todos los servicios
```powershell
./stop-all-services.ps1
```

## 📋 Inicio Manual (Paso a Paso)

### 1. Iniciar Backend (en orden)

#### Terminal 1 - Gateway (Puerto 8080)
```powershell
cd backend/gateway
./mvnw spring-boot:run
```
✅ Espera hasta ver: `Started GatewayApplication`

#### Terminal 2 - Authenticator (Puerto 8081)
```powershell
cd backend/authenticator
./mvnw spring-boot:run
```
✅ Espera hasta ver: `Started AuthenticatorApplication`

#### Terminal 3 - InnoSistemas (Puerto 8082)
```powershell
cd backend/innosistemas
./mvnw spring-boot:run
```
✅ Espera hasta ver: `Started InnosistemasApplication`

### 2. Iniciar Frontend

#### Terminal 4 - Frontend (Puerto 5173)
```powershell
cd frontend
npm install
npm run dev
```
✅ Abre el navegador en: `http://localhost:5173`

## 🧪 Verificar Integración

### Desde el Navegador
1. Abre `http://localhost:5173`
2. Presiona `F12` para abrir la consola
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

### Desde PowerShell

#### Verificar Gateway:
```powershell
curl http://localhost:8080/actuator/health
```

#### Verificar Authenticator:
```powershell
curl http://localhost:8080/authenticator/person/message
```

#### Verificar InnoSistemas:
```powershell
curl http://localhost:8080/project/project/message
```

## 🏗️ Arquitectura

```
Frontend (React + Vite)
     ↓ http://localhost:5173
     ↓
API Gateway (Spring Cloud Gateway)
     ↓ http://localhost:8080
     ├─→ Authenticator (http://localhost:8081)
     │   └─ /authenticator/**
     └─→ InnoSistemas (http://localhost:8082)
         └─ /project/**
```

## 📦 Puertos Utilizados

| Servicio | Puerto | Estado |
|----------|--------|--------|
| Frontend | 5173 | 🌐 |
| Gateway | 8080 | 🚪 |
| Authenticator | 8081 | 🔐 |
| InnoSistemas | 8082 | 📊 |

## 🛠️ Solución de Problemas

### Error: Puerto ya en uso

```powershell
# Ver qué está usando el puerto
netstat -ano | findstr :8080

# Detener el proceso (reemplaza PID)
taskkill /PID <PID> /F
```

### Error: "No se pudo conectar"

1. Verifica que todos los servicios estén corriendo
2. Espera 15-20 segundos después de iniciar cada servicio
3. Revisa los logs en la terminal de cada servicio

### Error: CORS

✅ Ya está configurado correctamente en todos los servicios

## 📚 Documentación Completa

- [INTEGRATION_GUIDE.md](./INTEGRATION_GUIDE.md) - Guía detallada de integración
- [Frontend README](./frontend/README.md) - Documentación del frontend
- [Backend README](./backend/README.md) - Documentación del backend

## 🎯 Endpoints Principales

### Autenticación
- `POST /authenticator/person/authenticate` - Login

### Proyectos
- `GET /project/project/listAll` - Listar proyectos
- `POST /project/project/save` - Crear proyecto
- `PUT /project/project/update` - Actualizar proyecto
- `DELETE /project/project/delete/{id}` - Eliminar proyecto

### Tareas
- `GET /project/task/listAll` - Listar tareas
- `POST /project/task/save` - Crear tarea
- `PUT /project/task/update` - Actualizar tarea
- `DELETE /project/task/delete/{id}` - Eliminar tarea

## 👥 Equipo de Desarrollo

- **Branch**: `pruebas_integracion`
- **Repositorio**: InnoSistemas-Fabrica-Escuela-2025-2/Feature-2

## 📝 Notas Importantes

1. **Orden de inicio**: Siempre inicia Gateway primero, luego los microservicios
2. **Tiempo de espera**: Espera 15-20 segundos entre cada servicio
3. **Variables de entorno**: Configuradas automáticamente en `.env.local`
4. **CORS**: Configurado para `http://localhost:5173`

---

**¿Problemas?** Revisa [INTEGRATION_GUIDE.md](./INTEGRATION_GUIDE.md) para más detalles.
