# Environment Variables - Production

Este archivo contiene todas las variables de entorno necesarias para desplegar la aplicación en producción.

## 🔧 Render (Backend)

Variables de entorno para configurar en Render Dashboard:

```bash
# Java Configuration
JAVA_OPTS=-Xmx512m -Xms256m

# Spring Profile
SPRING_PROFILES_ACTIVE=prod

# Database Configuration (tu Neon actual)
DATABASE_URL=jdbc:postgresql://ep-round-rain-ado66wet-pooler.c-2.us-east-1.aws.neon.tech/neondb?sslmode=require
DB_USERNAME=neondb_owner
DB_PASSWORD=npg_NilC90RWPxTz

# Server Configuration
PORT=8080

# CORS Configuration (actualizar con tu URL de Vercel)
CORS_ALLOWED_ORIGINS=https://tu-app.vercel.app,http://localhost:5173
```

### 📝 Notas:
- Render asigna automáticamente la variable `PORT`
- `CORS_ALLOWED_ORIGINS` debe incluir la URL exacta de tu frontend en Vercel
- El formato de `DATABASE_URL` debe incluir `?sslmode=require` para Neon
- Puedes usar múltiples orígenes separados por coma

---

## 🎨 Vercel (Frontend)

Variables de entorno para configurar en Vercel Dashboard:

```bash
# Backend API URL (actualizar con tu URL de Render)
VITE_API_URL=https://tu-backend.onrender.com
```

### 📝 Notas:
- La URL debe ser HTTPS (no HTTP)
- No incluir slash final en la URL
- Vercel requiere prefijo `VITE_` para variables accesibles en el cliente

---

## 🔑 GitHub Secrets

Secrets para configurar en GitHub Repository Settings → Secrets and variables → Actions:

```bash
# Vercel Secrets
VERCEL_TOKEN=vercel_xxxxxxxxxxxxxxxxxxxxxxxxxxxxx
VERCEL_ORG_ID=team_xxxxxxxxxxxxxxxxxxxxxxxxxxxxx
VERCEL_PROJECT_ID=prj_xxxxxxxxxxxxxxxxxxxxxxxxxxxxx

# Render Secrets
RENDER_API_KEY=rnd_xxxxxxxxxxxxxxxxxxxxxxxxxxxxx
RENDER_SERVICE_ID=srv-xxxxxxxxxxxxxxxxxxxxxxxxxxxxx

# Environment Variables
VITE_API_URL=https://tu-backend.onrender.com
```

### 📝 Cómo obtener cada valor:

#### VERCEL_TOKEN
1. Ve a: https://vercel.com/account/tokens
2. Click "Create Token"
3. Dale un nombre (ej: "GitHub Actions")
4. Copia el token generado

#### VERCEL_ORG_ID
1. Ve a tu proyecto en Vercel
2. Settings → General
3. Busca "Team ID" o "Organization ID"
4. Copia el valor (comienza con `team_`)

#### VERCEL_PROJECT_ID
1. Ve a tu proyecto en Vercel
2. Settings → General
3. Busca "Project ID"
4. Copia el valor (comienza con `prj_`)

#### RENDER_API_KEY
1. Ve a: https://dashboard.render.com/u/settings/api-keys
2. Click "Create API Key"
3. Dale un nombre (ej: "GitHub Actions")
4. Copia la key generada (comienza con `rnd_`)

#### RENDER_SERVICE_ID
1. Ve a tu servicio en Render
2. Mira la URL del navegador
3. El Service ID está en la URL: `https://dashboard.render.com/web/srv-XXXXX`
4. Copia solo la parte `srv-XXXXX`

---

## 🧪 Variables para Desarrollo Local

### Frontend (`.env.local`)

```bash
VITE_API_URL=http://localhost:8082
```

### Backend (`application.properties`)

```bash
server.port=8082
spring.datasource.url=jdbc:postgresql://ep-round-rain-ado66wet-pooler.c-2.us-east-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require
spring.datasource.username=neondb_owner
spring.datasource.password=npg_NilC90RWPxTz
cors.allowed.origins=http://localhost:5173
```

---

## 🔒 Seguridad

### ⚠️ IMPORTANTE:

- ❌ **NUNCA** commitear archivos `.env` con valores reales a Git
- ✅ Usar `.env.example` para documentar variables necesarias
- ✅ `.env.local` está en `.gitignore` (seguro para desarrollo local)
- ✅ Usar GitHub Secrets para valores sensibles en CI/CD
- ✅ Cambiar credenciales de base de datos en producción
- ✅ Rotar tokens regularmente (cada 3-6 meses)

### Archivos que NO deben tener valores reales:
- `.env.example` ✅ (plantilla)
- Este archivo ✅ (documentación)

### Archivos que SÍ pueden tener valores reales (pero no se suben a Git):
- `.env.local` ✅ (está en .gitignore)
- Configuración local de IDE ✅

---

## 📋 Checklist de Variables

Antes de desplegar, verifica que tengas configuradas:

### Render:
- [ ] `JAVA_OPTS`
- [ ] `SPRING_PROFILES_ACTIVE`
- [ ] `DATABASE_URL`
- [ ] `DB_USERNAME`
- [ ] `DB_PASSWORD`
- [ ] `PORT`
- [ ] `CORS_ALLOWED_ORIGINS`

### Vercel:
- [ ] `VITE_API_URL`

### GitHub:
- [ ] `VERCEL_TOKEN`
- [ ] `VERCEL_ORG_ID`
- [ ] `VERCEL_PROJECT_ID`
- [ ] `RENDER_API_KEY`
- [ ] `RENDER_SERVICE_ID`
- [ ] `VITE_API_URL`

---

## 🆘 Troubleshooting

### Error: "CORS_ALLOWED_ORIGINS not set"
**Solución**: Agrega la variable en Render con tu URL de Vercel

### Error: "Cannot connect to database"
**Solución**: Verifica `DATABASE_URL`, `DB_USERNAME` y `DB_PASSWORD`

### Error: "VITE_API_URL is undefined"
**Solución**: Agrega la variable en Vercel y redeploy

### Error: "Unauthorized" en GitHub Actions
**Solución**: Verifica que los tokens en GitHub Secrets sean correctos

---

## 📚 Referencias

- [Render Environment Variables](https://render.com/docs/environment-variables)
- [Vercel Environment Variables](https://vercel.com/docs/projects/environment-variables)
- [GitHub Encrypted Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [Vite Environment Variables](https://vitejs.dev/guide/env-and-mode.html)

---

**Última actualización**: 2025-10-25
