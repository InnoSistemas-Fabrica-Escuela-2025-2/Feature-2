# 🚀 Guía Rápida de Despliegue - InnoSistemas

## ✅ Archivos Listos para Desplegar

Tu proyecto ya está configurado con todos los archivos necesarios:

### ✨ Nuevos Archivos Creados:
```
✅ .github/workflows/
   ├── ci.yml                    # CI automático
   ├── frontend-deploy.yml       # Deploy Vercel
   └── backend-deploy.yml        # Deploy Render

✅ backend/innosistemas/
   ├── render.yaml               # Config Render
   └── src/main/resources/
       └── application-prod.properties

✅ frontend/
   ├── vercel.json               # Config Vercel
   ├── .env.example              # Ejemplo
   └── .env.local                # Local (no se sube)

✅ deployment/
   ├── DEPLOYMENT_GUIDE.md       # Guía completa
   └── README.md                 # Resumen
```

---

## 🎯 Paso a Paso (40 minutos)

### 1️⃣ Backend en Render (15 min)

**URL**: https://dashboard.render.com/

1. New → Web Service → Conecta GitHub
2. Selecciona tu repo: `Feature-2`
3. **Configuración (campos que aparecen)**:
   ```
   Name: innosistemas-backend
   Language: Node (selecciona "Node" para que aparezcan Build y Start Command)
   Branch: main
   Region: Oregon (US West)
   Root Directory: backend/innosistemas
   Dockerfile Path: (déjalo vacío)
   ```

4. **Ahora aparecen los campos Build & Deploy**:
   - **Build Command**: `chmod +x ./mvnw && ./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/innosistemas-0.0.1-SNAPSHOT.jar`

5. **Environment Variables** (agregar todas estas):
   ```bash
   JAVA_OPTS=-Xmx512m -Xms256m
   SPRING_PROFILES_ACTIVE=prod
   DATABASE_URL=jdbc:postgresql://ep-round-rain-ado66wet-pooler.c-2.us-east-1.aws.neon.tech/neondb?sslmode=require
   DB_USERNAME=neondb_owner
   DB_PASSWORD=npg_NilC90RWPxTz
   PORT=8080
   CORS_ALLOWED_ORIGINS=http://localhost:5173
   ```

6. **Scroll down** y configura:
   - **Health Check Path**: `/actuator/health`

7. Click **"Create Web Service"**
8. **⚠️ GUARDA LA URL**: `https://innosistemas-backend-XXXX.onrender.com`

---

### 2️⃣ Frontend en Vercel (10 min)

**URL**: https://vercel.com/new

1. Add New → Project → Import from GitHub
2. Selecciona tu repo: `Feature-2`
3. **Configuración**:
   ```
   Framework Preset: Vite
   Root Directory: frontend
   Build Command: npm run build
   Output Directory: dist
   Install Command: npm install
   ```

4. **Environment Variables**:
   ```bash
   VITE_API_URL=https://innosistemas-backend-XXXX.onrender.com
   ```
   ⚠️ Usa la URL que guardaste del paso 1

5. Click "Deploy"
6. **⚠️ GUARDA LA URL**: `https://innosistemas-XXXX.vercel.app`

---

### 3️⃣ Actualizar CORS (5 min)

1. Ve a Render → Tu servicio backend
2. Environment → Editar `CORS_ALLOWED_ORIGINS`
3. Actualiza con:
   ```
   https://innosistemas-XXXX.vercel.app,http://localhost:5173
   ```
   ⚠️ Usa la URL que guardaste del paso 2

4. Save → Espera redeploy (2-3 min)

---

### 4️⃣ GitHub Actions (10 min)

**URL**: https://github.com/InnoSistemas-Fabrica-Escuela-2025-2/Feature-2/settings/secrets/actions

#### A. Obtener Tokens de Vercel:
1. Ve a: https://vercel.com/account/tokens
2. Create Token → Guarda: `vercel_XXXXXXX`
3. Ve a tu proyecto → Settings → General
4. Guarda: Project ID y Team ID (Org ID)

#### B. Obtener Tokens de Render:
1. Ve a: https://dashboard.render.com/u/settings/api-keys
2. Create API Key → Guarda: `rnd_XXXXXXX`
3. Ve a tu servicio → Settings
4. Guarda: Service ID (en la URL: `srv-XXXXXXX`)

#### C. Agregar Secrets en GitHub:

Ve a: Settings → Secrets and variables → Actions → New repository secret

**⚠️ IMPORTANTE: Estos secrets ya deberían existir si ya desplegaste en Vercel antes:**

```
✅ VERCEL_TOKEN=vercel_XXXXXXX (ya existe)
✅ VERCEL_ORG_ID=team_XXXXXXX (ya existe)
✅ VERCEL_PROJECT_ID=prj_XXXXXXX (ya existe)

🆕 RENDER_API_KEY=rnd_XXXXXXX (nuevo - agregar)
🆕 RENDER_SERVICE_ID=srv-XXXXXXX (nuevo - agregar)
🆕 VITE_API_URL=https://innosistemas-backend-XXXX.onrender.com (nuevo - agregar)
```

**Solo necesitas agregar los 3 secrets nuevos de Render y la variable de entorno.**

---

### 5️⃣ Subir Cambios y Desplegar (5 min)

```bash
# Verifica que estés en la rama correcta
git status

# Agrega todos los archivos nuevos
git add .

# Commit
git commit -m "feat: add deployment configuration for Vercel and Render with GitHub Actions"

# Push
git push origin testeo

# Ver los workflows ejecutándose
# Ve a: https://github.com/tu-usuario/Feature-2/actions
```

---

## 🧪 Probar que Todo Funciona

### ✅ Test 1: Health Check Backend
```
https://innosistemas-backend-XXXX.onrender.com/actuator/health
```
Debe responder: `{"status":"UP"}`

### ✅ Test 2: Frontend Carga
```
https://innosistemas-XXXX.vercel.app
```
Debe mostrar tu aplicación

### ✅ Test 3: Comunicación Frontend-Backend
1. Abre tu app en Vercel
2. Abre DevTools (F12) → Network
3. Ve a "Proyectos"
4. Debes ver peticiones a tu backend en Render
5. ✅ Si carga proyectos: **¡FUNCIONA!**
6. ❌ Si hay error CORS: revisa paso 3

---

## 🎉 ¡Listo!

Tu aplicación está en producción:
- **Frontend**: https://innosistemas-XXXX.vercel.app
- **Backend**: https://innosistemas-backend-XXXX.onrender.com
- **GitHub Actions**: Configurado para CI/CD automático

### 🔄 Próximos deploys:
```bash
git add .
git commit -m "feat: nueva característica"
git push origin main  # ← Deploy automático
```

---

## 🆘 Problemas Comunes

### ❌ Error: CORS blocked
**Solución**: Verifica que `CORS_ALLOWED_ORIGINS` en Render tenga tu URL de Vercel

### ❌ Backend responde 500
**Solución**: Ve a Render Dashboard → Tu servicio → Logs

### ❌ Frontend no se actualiza
**Solución**: Vercel Dashboard → Tu proyecto → Deployments → Redeploy

### ❌ GitHub Actions falla
**Solución**: Verifica que todos los secrets estén bien escritos

### ⏱️ Backend tarda mucho en responder
**Nota**: El plan gratuito de Render "duerme" el servicio después de 15 min de inactividad. La primera petición puede tardar 30-60 segundos.

---

## 📚 Más Información

Lee `deployment/DEPLOYMENT_GUIDE.md` para detalles completos.

---

**¿Necesitas ayuda?** Contacta a tus profesores o revisa los logs en Render/Vercel.

¡Buena suerte! 🚀
