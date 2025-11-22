# 🧪 Guía de Testing Local - Feature-2

Esta guía te ayudará a probar todo el sistema localmente antes de hacer commit.

## 📋 Pre-requisitos

Antes de comenzar, asegúrate de tener:

- ✅ Docker Desktop instalado y corriendo
- ✅ Java 21+ instalado
- ✅ Node.js 18+ instalado
- ✅ Maven (incluido en `mvnw`)

## 🚀 Testing Rápido (Todo en Uno)

```powershell
# Ejecutar script de validación completo
.\test-local.ps1
```

Este script automáticamente:
1. ✅ Verifica pre-requisitos
2. ✅ Limpia ambiente previo
3. ✅ Construye servicios backend
4. ✅ Construye imágenes Docker
5. ✅ Levanta todos los servicios
6. ✅ Verifica health checks
7. ✅ Prueba conectividad
8. ✅ Construye frontend
9. ✅ Muestra resumen de resultados

### Opciones del Script

```powershell
# Saltar build de Maven (usar JARs existentes)
.\test-local.ps1 -SkipBuild

# Saltar tests unitarios (build más rápido)
.\test-local.ps1 -SkipTests

# Modo verbose
.\test-local.ps1 -Verbose

# Combinar opciones
.\test-local.ps1 -SkipTests -SkipBuild
```

## 🔧 Testing Manual (Paso a Paso)

### 1️⃣ Backend (Docker)

```powershell
# Ir a la carpeta backend
cd backend

# Limpiar ambiente previo
docker-compose down -v

# Construir servicios (opcional si ya están construidos)
cd authenticator
.\mvnw.cmd clean package -DskipTests
cd ..\gateway
.\mvnw.cmd clean package -DskipTests
cd ..\innosistemas
.\mvnw.cmd clean package -DskipTests
cd ..

# Construir imágenes Docker
docker-compose build

# Iniciar servicios
docker-compose up -d

# Ver logs en tiempo real
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f gateway
```

### 2️⃣ Verificar Health Checks

```powershell
# Gateway (debe responder con {"status":"UP"})
curl http://localhost:8080/actuator/health

# Authenticator
curl http://localhost:8081/actuator/health

# Innosistemas
curl http://localhost:8082/actuator/health
```

O abre en el navegador:
- http://localhost:8080/actuator/health
- http://localhost:8081/actuator/health
- http://localhost:8082/actuator/health

### 3️⃣ Frontend

```powershell
# Ir a la carpeta frontend
cd frontend

# Instalar dependencias (solo la primera vez)
npm install

# Build de producción
npm run build

# O iniciar en modo desarrollo
npm run dev
```

Abre en el navegador: http://localhost:5173

## 🔍 Verificaciones Detalladas

### Verificar Contenedores Corriendo

```powershell
docker ps
```

Deberías ver 4 contenedores:
- `backend-gateway-1`
- `backend-authenticator-1`
- `backend-innosistemas-1`
- `backend-postgres-1`

### Verificar Logs de un Servicio

```powershell
# Ver últimas 50 líneas
docker-compose logs --tail=50 gateway

# Ver logs en tiempo real
docker-compose logs -f authenticator

# Ver todos los logs
docker-compose logs
```

### Verificar Conectividad entre Servicios

```powershell
# Probar login a través del gateway
curl -X POST http://localhost:8080/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{"username":"test@example.com","password":"test123"}'

# Probar endpoint de proyectos
curl http://localhost:8080/api/projects
```

### Verificar Base de Datos

```powershell
# Entrar al contenedor de PostgreSQL
docker exec -it backend-postgres-1 psql -U postgres -d innosistemas

# Dentro de PostgreSQL
\dt                    # Ver tablas
\d users              # Ver estructura de tabla users
SELECT * FROM users;  # Ver datos
\q                    # Salir
```

## 🐛 Troubleshooting

### Error: "Puerto ya en uso"

```powershell
# Ver qué está usando el puerto
netstat -ano | findstr ":8080"
netstat -ano | findstr ":8081"
netstat -ano | findstr ":8082"
netstat -ano | findstr ":5432"

# Detener servicios anteriores
cd backend
docker-compose down -v
```

### Error: "Cannot connect to Docker daemon"

```powershell
# Verificar que Docker Desktop está corriendo
docker ps

# Si no funciona, reinicia Docker Desktop
```

### Error: "Build failed" en Maven

```powershell
# Limpiar caché de Maven
cd backend/[servicio]
.\mvnw.cmd clean

# Reinstalar dependencias
.\mvnw.cmd dependency:purge-local-repository
.\mvnw.cmd clean install
```

### Error: "Container keeps restarting"

```powershell
# Ver logs del contenedor problemático
docker-compose logs gateway

# Reconstruir sin caché
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

### Error: "Frontend no conecta con backend"

1. Verifica que el backend esté corriendo:
   ```powershell
   curl http://localhost:8080/actuator/health
   ```

2. Verifica la configuración en `frontend/.env.development`:
   ```
   VITE_API_GATEWAY_URL=http://localhost:8080
   ```

3. Reinicia el servidor de desarrollo:
   ```powershell
   # En frontend/
   npm run dev
   ```

## ✅ Checklist Pre-Commit

Antes de hacer commit, verifica:

- [ ] `.\test-local.ps1` pasa todos los tests
- [ ] Todos los health checks responden `{"status":"UP"}`
- [ ] Frontend construye sin errores (`npm run build`)
- [ ] Frontend conecta correctamente con backend
- [ ] No hay errores en los logs de Docker
- [ ] Tests unitarios pasan (`mvnw test`)

## 📊 Scripts Útiles

### Reiniciar Todo

```powershell
# Detener, limpiar y reiniciar
cd backend
docker-compose down -v
docker-compose up -d --build
```

### Ver Uso de Recursos

```powershell
# Ver CPU, memoria, red
docker stats

# Ver solo ciertos contenedores
docker stats backend-gateway-1 backend-postgres-1
```

### Limpiar Espacio en Disco

```powershell
# Eliminar contenedores detenidos
docker container prune

# Eliminar imágenes sin usar
docker image prune

# Limpiar todo (cuidado!)
docker system prune -a
```

## 🎯 Flujo de Trabajo Recomendado

1. **Antes de empezar a trabajar:**
   ```powershell
   git pull
   cd backend
   docker-compose up -d
   ```

2. **Durante el desarrollo:**
   - Backend: Los cambios requieren rebuild
     ```powershell
     docker-compose restart [servicio]
     ```
   - Frontend: Hot reload automático con `npm run dev`

3. **Antes de commit:**
   ```powershell
   .\test-local.ps1
   # Si pasa, entonces:
   git add .
   git commit -m "tu mensaje"
   git push
   ```

4. **Al terminar:**
   ```powershell
   cd backend
   docker-compose down
   ```

## 📚 Recursos Adicionales

- **Docker Compose**: `docker-compose --help`
- **Maven**: `.\mvnw.cmd --help`
- **npm**: `npm run` (ver scripts disponibles)
- **Health Checks**: `/actuator/health` en cada servicio

## 🆘 Ayuda

Si encuentras problemas que no están en esta guía:

1. Revisa los logs: `docker-compose logs`
2. Verifica el estado: `docker ps -a`
3. Consulta la documentación oficial de Docker/Spring Boot
4. Pregunta al equipo

---

¡Listo! Con esta guía deberías poder probar todo localmente sin problemas. 🚀
