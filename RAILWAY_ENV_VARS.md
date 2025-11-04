# Configuración de Variables de Entorno en Railway

## 🎯 URLs de tus servicios:
- Gateway: `https://gateway-production-5a82.up.railway.app`
- Authenticator: `https://authenticator-production-d23b.up.railway.app`
- InnoSistemas: `https://innosistemas-production.up.railway.app`
- Frontend Vercel: `https://feature-2.vercel.app`

---

## 📝 Variables a configurar en Railway:

### 🔷 Gateway (gateway-production-5a82)

Ve a Settings → Variables y agrega:

```
PORT=8080
JWT_SECRET=b7XfP9aQ2rL0sZ8wV3nC6mJ1yT4dE5kR
AUTHENTICATOR_URL=https://authenticator-production-d23b.up.railway.app
INNOSISTEMAS_URL=https://innosistemas-production.up.railway.app
FRONTEND_URL=https://feature-2.vercel.app
```

---

### 🔷 Authenticator (authenticator-production-d23b)

Ve a Settings → Variables y agrega:

```
PORT=8081
JWT_SECRET=b7XfP9aQ2rL0sZ8wV3nC6mJ1yT4dE5kR
AUTH_URL=jdbc:postgresql://ep-round-rain-ado66wet-pooler.c-2.us-east-1.aws.neon.tech/neondb?sslmode=require
AUTH_USERNAME=neondb_owner
AUTH_PASSWORD=npg_NilC90RWPxTz
```

---

### 🔷 InnoSistemas (innosistemas-production)

Ve a Settings → Variables y agrega:

```
PORT=8082
SPRING_DATASOURCE_URL=jdbc:postgresql://ep-round-rain-ado66wet-pooler.c-2.us-east-1.aws.neon.tech/neondb?sslmode=require
SPRING_DATASOURCE_USERNAME=neondb_owner
SPRING_DATASOURCE_PASSWORD=npg_NilC90RWPxTz
```

---

## ⚡ Después de configurar las variables:

1. **En cada servicio**: Settings → Redeploy
2. **Espera** a que los 3 servicios se desplieguen correctamente (logs en verde ✅)
3. **Verifica** que estén funcionando:
   - Gateway: https://gateway-production-5a82.up.railway.app/actuator/health
   - Authenticator: https://authenticator-production-d23b.up.railway.app/actuator/health
   - InnoSistemas: https://innosistemas-production.up.railway.app/project/project/message

---

## 🌐 En Vercel (Frontend):

**No necesitas configurar nada** - El archivo `.env.production` ya tiene las URLs correctas y Vercel lo usará automáticamente.

Solo haz:
```bash
git add .
git commit -m "Config: Actualizar URLs de producción para Railway"
git push origin main
```

Vercel detectará el push y redesplegará automáticamente con las nuevas URLs. ✅

---

## 🧪 Prueba Final:

1. Abre: https://feature-2.vercel.app
2. Intenta login con: `camilo@udea.edu.co / camilo`
3. Deberías poder ver proyectos y tareas
4. Intenta crear un proyecto nuevo

Si todo funciona, ¡listo! 🎉

---

## ⚠️ Notas importantes:

- **NO uses Base de Datos MySQL** - Ya tienes PostgreSQL en Neon configurado ✅
- **NO necesitas CorsConfig.java** - El CORS ya está en `application.yml` del Gateway ✅
- **El frontend SOLO habla con el Gateway** - Los microservicios son internos ✅
