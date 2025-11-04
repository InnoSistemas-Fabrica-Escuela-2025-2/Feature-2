# ✅ Solución Completa: Error 403 CORS en Gateway

## Cambios Implementados

### 1. **application.yml** - Añadido `add-to-simple-url-handler-mapping: true`
Este es el cambio **CRÍTICO** que permite que el Gateway maneje correctamente las peticiones OPTIONS antes de que lleguen a Spring Security.

```yaml
globalcors:
    add-to-simple-url-handler-mapping: true  # ← NUEVO: Maneja OPTIONS correctamente
    corsConfigurations:
        '[/**]':
            allowedOriginPatterns: 
                - "http://localhost:*"
                - "https://*.vercel.app"
                - "https://feature-2.vercel.app"
            allowedMethods:
                - GET
                - POST
                - PUT
                - DELETE
                - OPTIONS  # ← Incluido explícitamente
                - PATCH
```

### 2. **SecurityConfig.java** - Actualizado para incluir Vercel
Ahora permite peticiones desde tu frontend en Vercel.

### 3. **CorsConfig.java** - ELIMINADO
Este archivo causaba conflicto con la configuración en SecurityConfig. Solo debe haber UNA configuración CORS.

## Variables de Entorno en Railway

Verifica que estas variables estén configuradas **exactamente así** en el servicio Gateway:

```env
# Gateway Service
AUTHENTICATOR_URL=https://authenticator-production-d23b.up.railway.app
INNOSISTEMAS_URL=https://innosistemas-production.up.railway.app
JWT_SECRET=b7XfP9aQ2rL0sZ8wV3nC6mJ1yT4dE5kR
PORT=8080
```

⚠️ **IMPORTANTE**: Las URLs deben incluir `https://` al inicio.

## Pasos para Deployar

### 1. Commit y Push
```powershell
git add .
git commit -m "Fix: Configurar CORS correctamente con add-to-simple-url-handler-mapping para OPTIONS"
git push origin main
```

### 2. Verificar Deployment en Railway
- Ve a Railway → Gateway service
- Espera a que el deployment termine (status: "Active")
- Verifica los logs para confirmar que inició correctamente

### 3. Probar con cURL

**Probar preflight request (OPTIONS):**
```powershell
curl -X OPTIONS `
  https://gateway-production-5a82.up.railway.app/authenticator/person/authenticate `
  -H "Origin: https://feature-2.vercel.app" `
  -H "Access-Control-Request-Method: POST" `
  -H "Access-Control-Request-Headers: content-type" `
  -v
```

**Respuesta esperada:**
```
< HTTP/1.1 200 OK
< Access-Control-Allow-Origin: https://feature-2.vercel.app
< Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS,PATCH
< Access-Control-Allow-Credentials: true
< Access-Control-Max-Age: 3600
```

**Probar petición POST real:**
```powershell
curl -X POST `
  https://gateway-production-5a82.up.railway.app/authenticator/person/authenticate `
  -H "Origin: https://feature-2.vercel.app" `
  -H "Content-Type: application/json" `
  -d '{"email":"camilo@udea.edu.co","password":"camilo"}' `
  -v
```

## Frontend: Verificar Configuración

Tu frontend en Vercel debe hacer peticiones así:

```typescript
// frontend/src/lib/api.ts
const API_URL = 'https://gateway-production-5a82.up.railway.app';

export const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,  // ← IMPORTANTE: Incluir credenciales
});
```

## Troubleshooting

### Si el error persiste:

**1. Limpia caché del navegador:**
```
Chrome: Ctrl+Shift+Delete → Borrar caché
```

**2. Verifica logs del Gateway en Railway:**
Busca estas líneas:
```
CorsConfiguration: Adding CORS mappings
Loaded RoutePredicateFactory [Path]
```

**3. Verifica que no haya otros archivos CORS:**
```powershell
cd backend/gateway
Get-ChildItem -Recurse -Filter "*Cors*.java"
```

Solo debería haber:
- `security/SecurityConfig.java` (contiene CORS)

**4. Reinicia completamente Railway:**
- Gateway service → Settings → Restart

### ¿Cómo verificar en el navegador?

1. Abre DevTools (F12)
2. Ve a Network tab
3. Filtra por "Fetch/XHR"
4. Intenta hacer login desde tu frontend
5. Deberías ver:
   - Primera petición: `OPTIONS` → Status `200 OK` ✅
   - Segunda petición: `POST` → Status `200 OK` ✅

### Si ves "net::ERR_CERT_AUTHORITY_INVALID"

Esto es diferente al error CORS. Significa problema con certificado SSL. Railway maneja esto automáticamente, pero verifica:
- La URL es `https://` (no `http://`)
- El dominio de Railway está correcto

## Próximos Pasos

Una vez que el Gateway esté funcionando:

1. ✅ Verificar que Authenticator está corriendo y responde
2. ✅ Verificar que InnoSistemas está corriendo y responde
3. ✅ Probar flujo completo de login desde Vercel
4. ✅ Probar operaciones CRUD de proyectos

## Documentación de Referencia

- [Spring Cloud Gateway CORS](https://www.baeldung.com/spring-cloud-gateaway-configure-cors-policy)
- [Spring Security WebFlux CORS](https://docs.spring.io/spring-security/reference/reactive/integrations/cors.html)
- [Railway Environment Variables](https://docs.railway.com/guides/variables)
