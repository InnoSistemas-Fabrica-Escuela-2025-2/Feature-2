<!-- 
  📋 CHECKLIST DE DESPLIEGUE - INNOSISTEMAS
  Marca cada item cuando lo completes
-->

# ✅ Checklist de Despliegue

## 📦 Pre-requisitos
- [ ] Cuenta creada en [Render.com](https://render.com)
- [ ] Cuenta creada en [Vercel.com](https://vercel.com)
- [ ] Acceso al repositorio en GitHub
- [ ] Archivos de configuración revisados

---

## 🔧 Fase 1: Backend en Render

### Crear Servicio
- [ ] Acceder a Render Dashboard
- [ ] Click "New" → "Web Service"
- [ ] Conectar repositorio GitHub: `Feature-2`
- [ ] Configurar nombre: `innosistemas-backend`
- [ ] Seleccionar región: Oregon (US West)
- [ ] Branch: `main` (o `testeo` para pruebas)
- [ ] Root Directory: `backend/innosistemas`
- [ ] Environment: `Docker` (Render auto-detecta Java)
- [ ] Build Command: `chmod +x ./mvnw && ./mvnw clean package -DskipTests`
- [ ] Start Command: `java -jar target/innosistemas-0.0.1-SNAPSHOT.jar`

### Variables de Entorno
- [ ] `JAVA_OPTS=-Xmx512m -Xms256m`
- [ ] `SPRING_PROFILES_ACTIVE=prod`
- [ ] `DATABASE_URL=jdbc:postgresql://ep-round-rain-ado66wet-pooler.c-2.us-east-1.aws.neon.tech/neondb?sslmode=require`
- [ ] `DB_USERNAME=neondb_owner`
- [ ] `DB_PASSWORD=npg_NilC90RWPxTz`
- [ ] `PORT=8080`
- [ ] `CORS_ALLOWED_ORIGINS=http://localhost:5173` (actualizar después)

### Health Check
- [ ] Health Check Path: `/actuator/health`
- [ ] Health Check Response: `200`

### Deploy
- [ ] Click "Create Web Service"
- [ ] Esperar despliegue (5-10 min)
- [ ] ✏️ **Anotar URL**: `https://_______________________.onrender.com`
- [ ] Verificar health check: abrir URL + `/actuator/health`

---

## 🎨 Fase 2: Frontend en Vercel

### Crear Proyecto
- [ ] Acceder a Vercel Dashboard
- [ ] Click "Add New..." → "Project"
- [ ] Importar repositorio: `Feature-2`
- [ ] Framework Preset: `Vite`
- [ ] Root Directory: `frontend`
- [ ] Build Command: `npm run build`
- [ ] Output Directory: `dist`
- [ ] Install Command: `npm install`

### Variables de Entorno
- [ ] `VITE_API_URL` = URL del backend de Render (de Fase 1)
- [ ] Verificar que la URL sea HTTPS

### Deploy
- [ ] Click "Deploy"
- [ ] Esperar despliegue (2-3 min)
- [ ] ✏️ **Anotar URL**: `https://_______________________.vercel.app`
- [ ] Verificar que la app carga

---

## 🔗 Fase 3: Conectar Frontend y Backend

### Actualizar CORS
- [ ] Volver a Render Dashboard
- [ ] Ir a servicio backend → Environment
- [ ] Editar `CORS_ALLOWED_ORIGINS`
- [ ] Actualizar con: `https://tu-app.vercel.app,http://localhost:5173`
- [ ] Guardar cambios
- [ ] Esperar redeploy (2-3 min)

### Probar Conexión
- [ ] Abrir app en Vercel
- [ ] Abrir DevTools (F12) → Network
- [ ] Navegar a "Proyectos"
- [ ] Verificar peticiones al backend
- [ ] Confirmar que no hay errores CORS
- [ ] Probar crear un proyecto
- [ ] ✅ **Verificar que funciona correctamente**

---

## 🤖 Fase 4: GitHub Actions

### Obtener Tokens de Vercel
- [ ] Ir a [Vercel Tokens](https://vercel.com/account/tokens)
- [ ] Crear nuevo token
- [ ] ✏️ **Guardar token**: `vercel_______________________`
- [ ] Ir a proyecto Vercel → Settings → General
- [ ] ✏️ **Copiar Project ID**: `prj_______________________`
- [ ] ✏️ **Copiar Team ID**: `team_______________________`

### Obtener Tokens de Render
- [ ] Ir a [Render API Keys](https://dashboard.render.com/u/settings/api-keys)
- [ ] Crear nuevo API Key
- [ ] ✏️ **Guardar API Key**: `rnd_______________________`
- [ ] Ir a servicio → Settings
- [ ] ✏️ **Copiar Service ID** (en URL): `srv_______________________`

### Configurar Secrets en GitHub
- [ ] Ir a repo GitHub → Settings → Secrets and variables → Actions
- [ ] Verificar secrets existentes de Vercel (ya deberías tenerlos):
  - [ ] `VERCEL_TOKEN` ✅
  - [ ] `VERCEL_ORG_ID` ✅
  - [ ] `VERCEL_PROJECT_ID` ✅
- [ ] New repository secret: `RENDER_API_KEY`
- [ ] New repository secret: `RENDER_SERVICE_ID`
- [ ] New repository secret: `VITE_API_URL`

### Verificar Secrets
- [ ] Total de 6 secrets agregados (3 ya existían de Vercel, 3 nuevos)
- [ ] Todos los valores correctos sin espacios extras
- [ ] URLs con https:// (no http://)
- [ ] Tokens de Vercel funcionando (ya los usabas antes)
---

## 🚀 Fase 5: Primer Deploy Automático

### Hacer Commit
```bash
git add .
git commit -m "feat: add deployment configuration"
git push origin testeo
```

- [ ] Archivos commiteados
- [ ] Push exitoso a GitHub
- [ ] Ir a GitHub → Actions tab
- [ ] Ver workflows ejecutándose
- [ ] ✅ Workflow `CI - Build and Test` exitoso (verde)
- [ ] ✅ Workflow `Deploy Frontend to Vercel` exitoso (verde)
- [ ] ✅ Workflow `Deploy Backend to Render` exitoso (verde)

---

## ✅ Fase 6: Verificación Final

### Tests Funcionales
- [ ] Backend health check responde: `{"status":"UP"}`
- [ ] Frontend carga correctamente
- [ ] Login funciona
- [ ] Puede crear proyectos
- [ ] Puede listar proyectos
- [ ] No hay errores CORS en consola
- [ ] Datos se guardan en base de datos

### Tests de Performance
- [ ] Backend responde en menos de 2 segundos
- [ ] Frontend carga en menos de 3 segundos
- [ ] Navegación entre páginas es fluida

### Documentación
- [ ] URLs de producción documentadas
- [ ] Secrets guardados de forma segura
- [ ] Checklist completado
- [ ] Equipo notificado del deploy

---

## 📝 URLs Finales (completa después del deploy)

```
Backend Producción:   https://_______________________.onrender.com
Frontend Producción:  https://_______________________.vercel.app
Base de Datos:        Neon PostgreSQL (ya configurado)
Repositorio GitHub:   https://github.com/InnoSistemas-Fabrica-Escuela-2025-2/Feature-2
```

---

## 🎉 ¡DESPLIEGUE COMPLETADO!

Fecha: ____ / ____ / ________
Desplegado por: _______________________
Duración total: ________ minutos

### Próximos Pasos
- [ ] Notificar a profesores
- [ ] Compartir URLs con equipo
- [ ] Configurar monitoreo (opcional)
- [ ] Documentar accesos y credenciales

---

## 🆘 En caso de problemas

**Error CORS**: Verifica `CORS_ALLOWED_ORIGINS` en Render
**Error 500**: Revisa logs en Render Dashboard
**Frontend no actualiza**: Redeploy en Vercel
**GitHub Actions falla**: Verifica secrets

**Documentación completa**: `deployment/DEPLOYMENT_GUIDE.md`
**Guía rápida**: `DEPLOYMENT_QUICK_START.md`

---

**Buena suerte con tu despliegue! 🚀**
