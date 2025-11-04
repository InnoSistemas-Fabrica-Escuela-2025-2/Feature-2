# 🚀 Prueba INMEDIATA con tu Usuario: camilo@udea.edu.co

## ✅ Tu Usuario Encontrado

```json
{
    "email": "camilo@udea.edu.co",
    "password": "camilo"
}
```

---

## 🎯 Opción 1: Probar desde la Interfaz de Login (MÁS FÁCIL)

### Paso 1: Abrir la Aplicación

1. Abre tu navegador (Chrome/Edge)
2. Ve a: **`http://localhost:5173`**
3. Verás la pantalla de login

### Paso 2: Ingresar Credenciales

En el formulario de login, ingresa:

- **Correo electrónico:** `camilo@udea.edu.co`
- **Contraseña:** `camilo`

### Paso 3: Click en "Iniciar Sesión"

Si todo funciona correctamente:
- ✅ Verás mensaje: "Bienvenido, camilo"
- ✅ Serás redirigido al Dashboard
- ✅ El token JWT se guardará automáticamente en localStorage
- ✅ Ya puedes usar toda la aplicación

---

## 🧪 Opción 2: Probar desde la Consola del Navegador

Si prefieres verificar primero que todo funcione:

### Paso 1: Abrir Consola

1. Ve a `http://localhost:5173`
2. Presiona `F12` para abrir DevTools
3. Ve a la pestaña **"Console"**

### Paso 2: Ejecutar el Login

Copia y pega este comando en la consola:

```javascript
const resultado = await window.ejemplosApi.ejemploLogin('camilo@udea.edu.co', 'camilo');
console.log('✅ Login exitoso:', resultado);
```

**Resultado esperado:**

```javascript
{
  token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJjYW1pbG9AdWRlYS5lZHUuY28iLCJyb2xlIjoiZXN0dWRpYW50ZSIsImlhdCI6MTczMDY3MDAwMCwiZXhwIjoxNzMwNzU2NDAwfQ...",
  email: "camilo@udea.edu.co",
  role: "estudiante"  // o "profesor", depende de tu usuario
}
```

### Paso 3: Verificar que el Token se Guardó

```javascript
// Ver el token guardado automáticamente
console.log('Token:', localStorage.getItem('authToken'));

// Ver los datos del usuario
console.log('Usuario:', JSON.parse(localStorage.getItem('currentUser')));
```

**Deberías ver algo como:**

```javascript
Token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

Usuario: {
  id: "camilo@udea.edu.co",
  nombre: "camilo",
  correo: "camilo@udea.edu.co",
  rol: "estudiante",
  fechaRegistro: "2025-11-03T..."
}
```

### Paso 4: Probar Endpoints Protegidos

Ahora que tienes el token, puedes acceder a los endpoints protegidos:

```javascript
// Listar todos los proyectos
const proyectos = await window.ejemplosApi.ejemploListarProyectos();
console.log('📋 Proyectos:', proyectos);

// Listar tareas
const tareas = await window.ejemplosApi.ejemploListarTareas();
console.log('✅ Tareas:', tareas);

// Crear un proyecto de prueba
const nuevoProyecto = await window.ejemplosApi.ejemploCrearProyecto();
console.log('🆕 Proyecto creado:', nuevoProyecto);
```

---

## 🔍 ¿Qué está Pasando por Detrás?

### Flujo Completo del Login:

```
1. Usuario ingresa: camilo@udea.edu.co / camilo
   ↓
2. Frontend → POST http://localhost:8080/authenticator/person/authenticate
   Body: { "email": "camilo@udea.edu.co", "password": "camilo" }
   ↓
3. Gateway (8080) → Reenvía a → Authenticator (8081)
   ↓
4. Authenticator valida contra PostgreSQL
   - Busca usuario por email
   - Verifica password con BCrypt
   - Genera token JWT firmado
   ↓
5. Authenticator → Responde con:
   {
     "token": "eyJhbGci...",
     "email": "camilo@udea.edu.co",
     "role": "estudiante"
   }
   ↓
6. Frontend (AuthContext) recibe respuesta:
   - Guarda token en localStorage: authToken
   - Guarda usuario en localStorage: currentUser
   - Actualiza estado de React
   - Redirige al Dashboard
   ↓
7. Para TODAS las peticiones siguientes:
   - Axios interceptor lee el token de localStorage
   - Añade header: Authorization: Bearer <token>
   - Gateway valida el JWT
   - Si es válido, permite acceso
```

---

## 🔐 Estructura del Token JWT

Tu token contiene (decodificado):

```json
{
  "sub": "camilo@udea.edu.co",  // Email del usuario
  "role": "estudiante",          // Rol del usuario
  "userId": 123,                 // ID en la base de datos
  "iat": 1730670000,             // Issued at (timestamp)
  "exp": 1730756400              // Expiration (timestamp)
}
```

El token está **firmado** con la clave secreta: `b7XfP9aQ2rL0sZ8wV3nC6mJ1yT4dE5kR`

---

## 📂 ¿Dónde se Guarda el Token?

### localStorage del Navegador

El token se guarda automáticamente en 2 lugares:

1. **`authToken`** → El token JWT completo
   ```javascript
   localStorage.getItem('authToken')
   // "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
   ```

2. **`currentUser`** → Datos del usuario
   ```javascript
   JSON.parse(localStorage.getItem('currentUser'))
   // { id, nombre, correo, rol, fechaRegistro }
   ```

### Interceptor de Axios

En `frontend/src/lib/api.ts`, el interceptor automáticamente:

```typescript
// Request interceptor - AÑADE el token a TODAS las peticiones
apiGateway.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

**Esto significa que NO necesitas enviar el token manualmente en cada petición.**

---

## ✅ Verificación Completa

### 1. Verificar que el Login funciona

```javascript
// En la consola del navegador
const login = await window.ejemplosApi.ejemploLogin('camilo@udea.edu.co', 'camilo');
console.log('Login:', login.token ? '✅ ÉXITO' : '❌ FALLÓ');
```

### 2. Verificar que el token se guarda

```javascript
const token = localStorage.getItem('authToken');
const user = JSON.parse(localStorage.getItem('currentUser'));
console.log('Token guardado:', token ? '✅ SÍ' : '❌ NO');
console.log('Usuario guardado:', user ? '✅ SÍ' : '❌ NO');
```

### 3. Verificar que el interceptor funciona

```javascript
// Hacer una petición a endpoint protegido
const proyectos = await window.ejemplosApi.ejemploListarProyectos();
console.log('Proyectos:', proyectos.length >= 0 ? '✅ FUNCIONA' : '❌ ERROR');
```

### 4. Verificar headers en Network Tab

1. Abre DevTools → Pestaña **"Network"**
2. Ejecuta: `await window.ejemplosApi.ejemploListarProyectos()`
3. Busca la petición a `/project/project/listAll`
4. Click derecho → **"Headers"**
5. Verifica que en **Request Headers** aparece:
   ```
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

---

## 🐛 Solución de Problemas

### ❌ Error: "Correo no encontrado"

**Causa:** El usuario no existe en la base de datos

**Solución:** Verifica en la base de datos:
```sql
SELECT * FROM authentication.person WHERE email = 'camilo@udea.edu.co';
```

### ❌ Error: "Contraseña incorrecta"

**Causa:** La contraseña en la base de datos está hasheada y no coincide

**Solución:** 
1. Verifica que la contraseña en test.rest sea la correcta
2. Revisa el hash en la base de datos

### ❌ Error: "Usuario ya tiene una sesión activa"

**Solución:** Elimina la sesión anterior:
```sql
DELETE FROM authentication.active_sessions 
WHERE person_id = (SELECT id FROM authentication.person WHERE email = 'camilo@udea.edu.co');
```

### ❌ Error: 401 Unauthorized en peticiones posteriores

**Causa:** Token expirado o no se está enviando

**Verificar:**
```javascript
// 1. ¿Hay token?
console.log('Token:', localStorage.getItem('authToken'));

// 2. ¿El interceptor está funcionando?
// Ve a Network tab y verifica el header Authorization
```

**Solución:** Re-login
```javascript
await window.ejemplosApi.ejemploLogin('camilo@udea.edu.co', 'camilo');
```

---

## 🎯 Prueba Completa Paso a Paso

### Copia y pega este bloque completo en la consola:

```javascript
console.clear();
console.log('🚀 Iniciando prueba completa de autenticación...\n');

// 1. Login
console.log('1️⃣ Intentando login...');
const loginResult = await window.ejemplosApi.ejemploLogin('camilo@udea.edu.co', 'camilo');
console.log('   ✅ Login exitoso:', loginResult);
console.log('   📧 Email:', loginResult.email);
console.log('   👤 Rol:', loginResult.role);
console.log('   🔑 Token:', loginResult.token.substring(0, 50) + '...\n');

// 2. Verificar almacenamiento
console.log('2️⃣ Verificando localStorage...');
const storedToken = localStorage.getItem('authToken');
const storedUser = JSON.parse(localStorage.getItem('currentUser'));
console.log('   ✅ Token guardado:', storedToken ? 'SÍ (' + storedToken.length + ' caracteres)' : 'NO');
console.log('   ✅ Usuario guardado:', storedUser ? 'SÍ' : 'NO');
console.log('   👤 Usuario:', storedUser, '\n');

// 3. Probar endpoints protegidos
console.log('3️⃣ Probando endpoints protegidos...');
try {
  const proyectos = await window.ejemplosApi.ejemploListarProyectos();
  console.log('   ✅ Proyectos obtenidos:', proyectos.length, 'proyectos');
} catch (error) {
  console.log('   ❌ Error al obtener proyectos:', error.message);
}

try {
  const tareas = await window.ejemplosApi.ejemploListarTareas();
  console.log('   ✅ Tareas obtenidas:', tareas.length, 'tareas');
} catch (error) {
  console.log('   ❌ Error al obtener tareas:', error.message);
}

console.log('\n🎉 ¡Prueba completa finalizada!');
console.log('📝 Revisa la pestaña Network para ver las peticiones HTTP con el header Authorization');
```

---

## 📊 Resumen

**Tu usuario:**
- Email: `camilo@udea.edu.co`
- Password: `camilo`

**URLs:**
- Frontend: `http://localhost:5173`
- Gateway: `http://localhost:8080`
- Authenticator: `http://localhost:8081`
- InnoSistemas: `http://localhost:8082`

**Lo que se hace automáticamente:**
1. ✅ Login llama a `/authenticator/person/authenticate`
2. ✅ Token se guarda en `localStorage.authToken`
3. ✅ Usuario se guarda en `localStorage.currentUser`
4. ✅ Interceptor añade `Authorization: Bearer <token>` a TODAS las peticiones
5. ✅ Gateway valida el token en cada petición
6. ✅ Si el token es válido, permite el acceso

**Tu trabajo:**
1. Solo ingresa las credenciales en el login
2. ¡Eso es todo! El resto es automático 🎉

---

**¿Listo para probar? Ve a `http://localhost:5173` e ingresa las credenciales! 🚀**
