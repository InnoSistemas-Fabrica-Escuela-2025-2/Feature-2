# Deployment Guide - InnoSistemas

Esta guía te ayudará a desplegar tu aplicación **Frontend en Vercel** y **Backend en Render** con integración de GitHub Actions.

---

## 📋 Pre-requisitos

- Cuenta en [Vercel](https://vercel.com)
- Cuenta en [Render](https://render.com)
- Cuenta en GitHub (ya tienes el repositorio)
- Base de datos PostgreSQL (ya tienes Neon configurado)

---

## 🎯 Parte 1: Desplegar Backend en Render

### 1.1 Crear servicio en Render

1. Ve a [Render Dashboard](https://dashboard.render.com/)
2. Click en **"New +"** → **"Web Service"**
3. Conecta tu repositorio de GitHub: `InnoSistemas-Fabrica-Escuela-2025-2/Feature-2`
4. Configuración del servicio:
   - **Name**: `innosistemas-backend`
   - **Region**: `Oregon (US West)`
   - **Branch**: `main` (o `testeo` para pruebas)
   - **Root Directory**: `backend/innosistemas`
   - **Environment**: `Docker` (Render detectará Java automáticamente por el pom.xml)
   - **Build Command**: `chmod +x ./mvnw && ./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/innosistemas-0.0.1-SNAPSHOT.jar`

### 1.2 Variables de entorno en Render

En la sección **"Environment"**, agrega estas variables:

```bash
# Java configuration
JAVA_OPTS=-Xmx512m -Xms256m
SPRING_PROFILES_ACTIVE=prod

# Database configuration (tu Neon actual)
DATABASE_URL=jdbc:postgresql://ep-round-rain-ado66wet-pooler.c-2.us-east-1.aws.neon.tech/neondb?sslmode=require
DB_USERNAME=neondb_owner
DB_PASSWORD=npg_NilC90RWPxTz

# Server configuration
PORT=8080

# CORS - Actualiza después de tener la URL de Vercel
CORS_ALLOWED_ORIGINS=https://tu-app.vercel.app,http://localhost:5173
```

### 1.3 Configurar Health Check

- **Health Check Path**: `/actuator/health`
- **Health Check Response**: `200`

### 1.4 Deploy

Click en **"Create Web Service"** y espera a que termine el despliegue (toma 5-10 minutos).

Tu backend estará disponible en: `https://innosistemas-backend.onrender.com`

---

## 🚀 Parte 2: Desplegar Frontend en Vercel

### 2.1 Crear proyecto en Vercel

1. Ve a [Vercel Dashboard](https://vercel.com/dashboard)
2. Click en **"Add New..."** → **"Project"**
3. Importa tu repositorio: `InnoSistemas-Fabrica-Escuela-2025-2/Feature-2`
4. Configuración del proyecto:
   - **Framework Preset**: `Vite`
   - **Root Directory**: `frontend`
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
   - **Install Command**: `npm install`

### 2.2 Variables de entorno en Vercel

En **"Environment Variables"**, agrega:

```bash
VITE_API_URL=https://innosistemas-backend.onrender.com
```

**Importante**: Reemplaza `innosistemas-backend.onrender.com` con la URL real que te dio Render.

### 2.3 Deploy

Click en **"Deploy"** y espera a que termine (toma 2-3 minutos).

Tu frontend estará disponible en: `https://innosistemas.vercel.app` (o el dominio que Vercel te asigne)

### 2.4 Actualizar CORS en Render

1. Ve de nuevo a tu servicio en Render
2. Actualiza la variable `CORS_ALLOWED_ORIGINS` con la URL real de Vercel:
   ```bash
   CORS_ALLOWED_ORIGINS=https://innosistemas.vercel.app,http://localhost:5173
   ```
3. Guarda y espera a que se reinicie el servicio

---

## ⚙️ Parte 3: Configurar GitHub Actions

### 3.1 Obtener tokens necesarios

#### **Para Vercel:**

1. Ve a [Vercel Settings](https://vercel.com/account/tokens)
2. Crea un nuevo token y guárdalo
3. Ve a tu proyecto → Settings → General
4. Copia el **Project ID** y **Team ID** (Org ID)

#### **Para Render:**

1. Ve a [Render API Keys](https://dashboard.render.com/u/settings/api-keys)
2. Crea un nuevo API Key y guárdalo
3. Ve a tu servicio → Settings → General
4. Copia el **Service ID** (está en la URL: `srv-xxxxx`)

### 3.2 Agregar Secrets a GitHub

1. Ve a tu repositorio en GitHub
2. Settings → Secrets and variables → Actions
3. Click en **"New repository secret"** y agrega:

```bash
# Vercel Secrets
VERCEL_TOKEN=<tu-token-de-vercel>
VERCEL_ORG_ID=<tu-team-id-de-vercel>
VERCEL_PROJECT_ID=<tu-project-id-de-vercel>

# Render Secrets
RENDER_API_KEY=<tu-api-key-de-render>
RENDER_SERVICE_ID=<tu-service-id-de-render>

# Environment Variables
VITE_API_URL=https://innosistemas-backend.onrender.com
```

### 3.3 Probar GitHub Actions

Los workflows ya están configurados en `.github/workflows/`:
- `ci.yml` - Se ejecuta en cada PR
- `frontend-deploy.yml` - Despliega frontend a Vercel
- `backend-deploy.yml` - Despliega backend a Render

Para probar:

```bash
git add .
git commit -m "feat: add deployment configuration"
git push origin testeo
```

Verás los workflows ejecutándose en: **Actions** tab de GitHub.

---

## 🧪 Parte 4: Probar el Despliegue

### 4.1 Verificar Backend

Abre en tu navegador:
```
https://innosistemas-backend.onrender.com/actuator/health
```

Deberías ver:
```json
{
  "status": "UP"
}
```

### 4.2 Verificar Frontend

Abre tu app en:
```
https://innosistemas.vercel.app
```

Deberías poder:
- Ver la página de login
- Crear proyectos
- Ver la lista de proyectos

### 4.3 Verificar Comunicación

1. Abre DevTools (F12) en el navegador
2. Ve a la pestaña **Network**
3. Intenta crear un proyecto
4. Deberías ver peticiones a `https://innosistemas-backend.onrender.com/project/save`
5. Si ves errores CORS, verifica que hayas actualizado la variable `CORS_ALLOWED_ORIGINS` en Render

---

## 🐛 Troubleshooting

### Error: CORS blocked

**Solución**: Actualiza `CORS_ALLOWED_ORIGINS` en Render con la URL correcta de Vercel.

### Error: 500 Internal Server Error en backend

**Solución**: Revisa los logs en Render Dashboard → tu servicio → Logs.

### Error: Cannot connect to database

**Solución**: Verifica que las variables `DATABASE_URL`, `DB_USERNAME`, `DB_PASSWORD` estén correctas en Render.

### Frontend no se actualiza después de hacer push

**Solución**: 
1. Ve a Vercel Dashboard → tu proyecto → Deployments
2. Click en los 3 puntos → Redeploy

### Backend tarda mucho en responder la primera vez

**Nota**: Render (plan gratuito) pone tu servicio en "sleep" después de 15 minutos de inactividad. La primera petición puede tardar 30-60 segundos en "despertar" el servicio.

---

## 📝 Comandos útiles para desarrollo local

```bash
# Frontend
cd frontend
npm install
npm run dev

# Backend
cd backend/innosistemas
./mvnw spring-boot:run

# Ver logs de GitHub Actions
gh run list
gh run view <run-id>
```

---

## 🔄 Workflow de Desarrollo

1. **Desarrollo local**: Trabaja en rama `testeo`
2. **Push a GitHub**: Los workflows de CI se ejecutan automáticamente
3. **Merge a main**: Se despliega automáticamente a producción
4. **Verifica**: Revisa que todo funcione en las URLs de producción

---

## ✅ Checklist de Despliegue

- [ ] Backend desplegado en Render
- [ ] Frontend desplegado en Vercel
- [ ] Variables de entorno configuradas
- [ ] CORS actualizado con URL de producción
- [ ] GitHub Secrets agregados
- [ ] Health check funcionando
- [ ] Comunicación frontend-backend funcionando
- [ ] GitHub Actions ejecutándose correctamente

---

¡Tu aplicación ya está en producción! 🎉
