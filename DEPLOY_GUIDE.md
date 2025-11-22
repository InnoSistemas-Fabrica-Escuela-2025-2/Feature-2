# 🚀 Guía Rápida: Deploy en Render con Neon

## ✅ Ya Configurado

- ✅ Base de datos Neon PostgreSQL
- ✅ Dockerfiles para todos los servicios
- ✅ Variables de entorno preparadas
- ✅ Health checks configurados

## 📋 Pasos para Deploy

### 1. Crear Servicios en Render (uno por uno)

#### 🔐 A. Authenticator Service

1. **Dashboard Render** → **New +** → **Web Service**
2. **Conectar repositorio**: `InnoSistemas-Fabrica-Escuela-2025-2/Feature-2`
3. **Configuración**:
   ```
   Name: innosistemas-authenticator
   Region: Oregon (US West) o el más cercano
   Branch: integracios2_docker
   Root Directory: backend/authenticator
   Environment: Docker
   Dockerfile Path: backend/authenticator/Dockerfile
   Plan: Free
   ```
4. **Environment Variables** (copiar de `RENDER_ENV_VARS.txt`):
   ```
   SPRING_DATASOURCE_URL=jdbc:postgresql://ep-round-rain-ado66wet-pooler.c-2.us-east-1.aws.neon.tech:5432/neondb?sslmode=require&channel_binding=require
   SPRING_DATASOURCE_USERNAME=neondb_owner
   SPRING_DATASOURCE_PASSWORD=npg_NilC90RWPxTz
   SERVER_PORT=8081
   JWT_SECRET=b7XfP9aQ2rL0sZ8wV3nC6mJ1yT4dE5kR
   ```
5. **Advanced** → **Health Check Path**: `/actuator/health`
6. **Create Web Service**
7. ⏳ **Espera** a que el estado sea **"Live"** (~5-10 min primera vez)
8. 📝 **Guarda la URL**: `https://innosistemas-authenticator.onrender.com`

---

#### 📊 B. Innosistemas Service

1. **New +** → **Web Service**
2. **Mismo repo**: `Feature-2`, branch `integracios2_docker`
3. **Configuración**:
   ```
   Name: innosistemas-core
   Region: Same as Authenticator
   Branch: integracios2_docker
   Root Directory: backend/innosistemas
   Environment: Docker
   Dockerfile Path: backend/innosistemas/Dockerfile
   Plan: Free
   ```
4. **Environment Variables**:
   ```
   SPRING_DATASOURCE_URL=jdbc:postgresql://ep-round-rain-ado66wet-pooler.c-2.us-east-1.aws.neon.tech:5432/neondb?sslmode=require&channel_binding=require
   SPRING_DATASOURCE_USERNAME=neondb_owner
   SPRING_DATASOURCE_PASSWORD=npg_NilC90RWPxTz
   SERVER_PORT=8082
   ```
5. **Health Check Path**: `/actuator/health`
6. **Create Web Service**
7. ⏳ **Espera** a que esté **"Live"**
8. 📝 **Guarda la URL**: `https://innosistemas-core.onrender.com`

---

#### 🌐 C. Gateway Service

1. **New +** → **Web Service**
2. **Mismo repo**: `Feature-2`, branch `integracios2_docker`
3. **Configuración**:
   ```
   Name: innosistemas-gateway
   Region: Same as others
   Branch: integracios2_docker
   Root Directory: backend/gateway
   Environment: Docker
   Dockerfile Path: backend/gateway/Dockerfile
   Plan: Free
   ```
4. **Environment Variables** (⚠️ usa las URLs reales de arriba):
   ```
   SERVER_PORT=8080
   AUTHENTICATOR_URL=https://innosistemas-authenticator.onrender.com
   INNOSISTEMAS_URL=https://innosistemas-core.onrender.com
   ALLOWED_ORIGINS=https://tu-frontend.vercel.app,http://localhost:5173
   ```
5. **Health Check Path**: `/actuator/health`
6. **Create Web Service**
7. ⏳ **Espera** a que esté **"Live"**
8. 📝 **Guarda la URL**: `https://innosistemas-gateway.onrender.com`

---

### 2. Verificar Servicios

```powershell
# Gateway
curl https://innosistemas-gateway.onrender.com/actuator/health

# Authenticator (dará 403 - correcto por seguridad)
curl https://innosistemas-authenticator.onrender.com/actuator/health

# Innosistemas
curl https://innosistemas-core.onrender.com/actuator/health
```

✅ Todos deben responder (el authenticator con 403 es correcto)

---

### 3. Deploy Frontend en Vercel

1. **Vercel Dashboard** → **Add New** → **Project**
2. **Import Git Repository**: `Feature-2`
3. **Configuración**:
   ```
   Framework Preset: Vite
   Root Directory: frontend
   Build Command: npm run build
   Output Directory: dist
   ```
4. **Environment Variables**:
   ```
   VITE_API_GATEWAY_URL=https://innosistemas-gateway.onrender.com
   ```
5. **Deploy**
6. 📝 **Guarda la URL**: `https://tu-frontend.vercel.app`

---

### 4. Actualizar CORS en Gateway

1. Ve a **Gateway Service** en Render
2. **Environment** → **Edit**
3. Actualiza `ALLOWED_ORIGINS`:
   ```
   ALLOWED_ORIGINS=https://tu-frontend.vercel.app
   ```
4. **Save Changes** (redesplegará automáticamente)

---

## 🧪 Testing Completo

### 1. Health Checks
```powershell
curl https://innosistemas-gateway.onrender.com/actuator/health
# Esperado: {"status":"UP"}
```

### 2. Test Frontend
1. Abre `https://tu-frontend.vercel.app`
2. Intenta hacer login
3. Verifica que puedas ver proyectos/tareas

### 3. Ver Logs (si algo falla)
- Render: Service → Logs → Live Logs
- Vercel: Deployments → [tu deploy] → View Function Logs

---

## ⚠️ Importante: Free Tier

**Render Free Tier**:
- Los servicios se duermen después de **15 min sin uso**
- Primera petición tarda **~30-60 segundos** en despertar
- 750 horas/mes por servicio (suficiente para 1 servicio 24/7)

**Solución**: Si necesitas que estén siempre activos, considera:
1. Usar un servicio de "ping" cada 10 min
2. O upgrade a plan pagado ($7/mes por servicio)

**Neon Free Tier**:
- 0.5 GB storage
- Sin límite de tiempo ✅
- No se duerme ✅

---

## 🔐 Seguridad en Producción

**ANTES DE IR A PRODUCCIÓN REAL**:

1. Genera un JWT_SECRET nuevo:
   ```powershell
   $bytes = New-Object byte[] 32
   [System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
   Write-Host ([Convert]::ToBase64String($bytes))
   ```

2. Actualiza en Render:
   - Authenticator → Environment → JWT_SECRET

3. Cambia password de Neon (opcional pero recomendado)

---

## 📱 URLs Finales

Después del deployment, tendrás:

```
Backend Gateway:     https://innosistemas-gateway.onrender.com
Backend Auth:        https://innosistemas-authenticator.onrender.com
Backend Core:        https://innosistemas-core.onrender.com
Frontend:            https://tu-frontend.vercel.app
Database:            Neon (sin cambios)
```

---

## 🆘 Troubleshooting

### Service no inicia
- Revisa **Logs** en Render
- Verifica que las **Environment Variables** estén correctas
- Confirma que el **Dockerfile Path** sea correcto

### Database connection error
- Verifica que la URL incluya `?sslmode=require&channel_binding=require`
- Confirma username/password de Neon
- Checa en Neon Console que la DB esté activa

### CORS error en frontend
- Verifica `ALLOWED_ORIGINS` en Gateway
- Debe incluir la URL exacta de Vercel (con https://)

### 404 en API calls
- Confirma que Gateway tenga las URLs correctas de los otros servicios
- Verifica que todos los servicios estén "Live"

---

¡Listo para deployar! 🚀
