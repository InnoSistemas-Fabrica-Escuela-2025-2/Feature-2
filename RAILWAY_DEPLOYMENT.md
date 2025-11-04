# Guía de Despliegue en Railway - Arquitectura de Microservicios

## 📋 Prerequisitos
- Cuenta en Railway (https://railway.app)
- Repositorio GitHub conectado
- Base de datos PostgreSQL en Neon (ya configurada)

## 🚀 Despliegue de los 3 Servicios

### Servicio 1: Gateway (Puerto 8080)

1. **Crear nuevo servicio en Railway**
   - Click en "New Project" → "Deploy from GitHub repo"
   - Selecciona: `Feature-2`
   - En "Root Directory": `backend/gateway`

2. **Configurar Variables de Entorno**
   ```
   PORT=8080
   JWT_SECRET=b7XfP9aQ2rL0sZ8wV3nC6mJ1yT4dE5kR
   AUTHENTICATOR_URL=https://authenticator-production-xxxx.up.railway.app
   INNOSISTEMAS_URL=https://innosistemas-production-xxxx.up.railway.app
   FRONTEND_URL=https://tu-app.vercel.app
   ```
   
   ⚠️ **IMPORTANTE**: Las URLs de AUTHENTICATOR_URL e INNOSISTEMAS_URL las obtendrás después de desplegar esos servicios.

3. **Configurar Build**
   - Railway detectará automáticamente que es un proyecto Maven
   - Seleccionará Nixpacks como builder
   - Compilará automáticamente con: `mvn clean package -DskipTests`
   - ✅ No necesitas crear ningún archivo de configuración

4. **Exponer Puerto Público**
   - En Settings → Networking → Generate Domain
   - Anota la URL generada (ej: `gateway-production-xxxx.up.railway.app`)

---

### Servicio 2: Authenticator (Puerto 8081)

1. **Crear nuevo servicio en Railway**
   - En el mismo proyecto, click "New Service"
   - Deploy from GitHub repo → Selecciona: `Feature-2`
   - En "Root Directory": `backend/authenticator`

2. **Configurar Variables de Entorno**
   ```
   PORT=8081
   JWT_SECRET=b7XfP9aQ2rL0sZ8wV3nC6mJ1yT4dE5kR
   AUTH_URL=jdbc:postgresql://ep-round-rain-ado66wet-pooler.c-2.us-east-1.aws.neon.tech/neondb?sslmode=require
   AUTH_USERNAME=neondb_owner
   AUTH_PASSWORD=npg_NilC90RWPxTz
   ```

3. **Exponer Puerto Público**
   - Settings → Networking → Generate Domain
   - Anota la URL (ej: `authenticator-production-xxxx.up.railway.app`)
   - **VOLVER AL GATEWAY y actualizar `AUTHENTICATOR_URL` con esta URL**

---

### Servicio 3: Innosistemas (Puerto 8082)

1. **Crear nuevo servicio en Railway** (si no está ya desplegado)
   - En el mismo proyecto, click "New Service"
   - Deploy from GitHub repo → Selecciona: `Feature-2`
   - En "Root Directory": `backend/innosistemas`

2. **Configurar Variables de Entorno**
   ```
   PORT=8082
   SPRING_DATASOURCE_URL=jdbc:postgresql://ep-round-rain-ado66wet-pooler.c-2.us-east-1.aws.neon.tech/neondb?sslmode=require
   SPRING_DATASOURCE_USERNAME=neondb_owner
   SPRING_DATASOURCE_PASSWORD=npg_NilC90RWPxTz
   ```

3. **Exponer Puerto Público**
   - Settings → Networking → Generate Domain
   - Anota la URL (ej: `innosistemas-production-xxxx.up.railway.app`)
   - **VOLVER AL GATEWAY y actualizar `INNOSISTEMAS_URL` con esta URL**

---

## 🔄 Configuración Final

### 1. Actualizar Gateway con URLs correctas

Una vez que tengas las 3 URLs, actualiza las variables de entorno del **Gateway**:

```
AUTHENTICATOR_URL=https://authenticator-production-[TU-ID].up.railway.app
INNOSISTEMAS_URL=https://innosistemas-production-[TU-ID].up.railway.app
FRONTEND_URL=https://[TU-APP].vercel.app
```

Luego, en Railway → Gateway → Settings → Redeploy

### 2. Actualizar Frontend en Vercel

En tu proyecto de Vercel, actualiza la variable de entorno:

```
VITE_API_URL=https://gateway-production-[TU-ID].up.railway.app
```

O edita `frontend/src/lib/api.ts`:

```typescript
const api = axios.create({
  baseURL: 'https://gateway-production-[TU-ID].up.railway.app',
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true
});
```

Luego, haz commit y push para que Vercel redeploy automáticamente.

---

## ✅ Verificación

1. **Gateway**: https://gateway-production-xxxx.up.railway.app/actuator/health
2. **Authenticator**: https://authenticator-production-xxxx.up.railway.app/actuator/health
3. **Innosistemas**: https://innosistemas-production-xxxx.up.railway.app/project/project/message

Deberías recibir respuestas 200 OK.

4. **Prueba de Login desde Frontend**:
   - Abre tu app en Vercel
   - Intenta login con: `camilo@udea.edu.co / camilo`
   - Debería funcionar correctamente

---

## 🐛 Troubleshooting

### Problema: "Cannot find pom.xml" o "Build failed"
**Solución**: 
1. Verifica que el "Root Directory" esté configurado correctamente:
   - Gateway: `backend/gateway`
   - Authenticator: `backend/authenticator`
   - Innosistemas: `backend/innosistemas`
2. En Railway Settings → General → Root Directory

### Problema: "Builder: Nixpacks - Deprecated"
**Solución**: Puedes ignorar este warning. Nixpacks sigue funcionando perfectamente y Railway lo detectará automáticamente para proyectos Java/Maven.

### Problema: "503 Service Unavailable" en Gateway
**Solución**: Verifica que AUTHENTICATOR_URL e INNOSISTEMAS_URL estén correctas en las variables de entorno del Gateway.

### Problema: "CORS Error" en Frontend
**Solución**: Asegúrate de que FRONTEND_URL en Gateway incluya tu dominio de Vercel exacto.

### Problema: "Cannot connect to database"
**Solución**: Verifica las credenciales de PostgreSQL en las variables de entorno.

### Problema: "Build Failed - Lombok"
**Solución**: Ya está solucionado con los getters/setters explícitos en el último commit.

---

## 💰 Costos

Railway ofrece $5 USD gratis/mes:
- 3 servicios pequeños (~512MB RAM cada uno) = ~$3-4 USD/mes
- Debería caber en el plan gratuito si no tienes mucho tráfico

---

## 📊 Arquitectura Final

```
[Frontend Vercel]
       ↓
[Gateway Railway :8080]
       ↓
    ├──→ [Authenticator Railway :8081] → [PostgreSQL Neon]
    └──→ [Innosistemas Railway :8082] → [PostgreSQL Neon]
```

---

## 🔐 Seguridad

⚠️ **IMPORTANTE**: Las credenciales de base de datos y JWT_SECRET están actualmente en texto plano.

**Para producción real, deberías**:
1. Usar Railway's Secret Management
2. Rotar el JWT_SECRET
3. Crear un usuario de DB específico con permisos limitados
4. Habilitar IP Whitelist en Neon
