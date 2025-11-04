# 🎯 Guía Rápida: Cómo Usar tus Usuarios Existentes

## ✅ Estado Actual

**Todos los servicios están corriendo:**
- ✅ Gateway: `http://localhost:8080`
- ✅ Authenticator: `http://localhost:8081`
- ✅ InnoSistemas: `http://localhost:8082`
- ✅ Frontend: `http://localhost:5174` ⚠️ **NOTA: Ahora está en puerto 5174**

**Integración completada:**
- ✅ AuthContext actualizado para usar API real
- ✅ JWT token automático en todas las peticiones
- ✅ Login funcional con backend

---

## 🚀 Opción 1: Usar la Interfaz Gráfica (RECOMENDADO)

### Paso 1: Abrir la Aplicación

1. Abre tu navegador
2. Ve a: `http://localhost:5174`
3. Verás la pantalla de login

### Paso 2: Iniciar Sesión

Ingresa las credenciales de **TUS** usuarios que ya existen en la base de datos:

**Ejemplo:**
- **Email:** `estudiante@test.com` (o el email que tengas)
- **Password:** `password123` (o tu contraseña)

### Paso 3: ¡Listo!

Si las credenciales son correctas:
- ✅ Verás mensaje "Bienvenido, [nombre]"
- ✅ Serás redirigido al Dashboard
- ✅ El token JWT se guardará automáticamente
- ✅ Todas las llamadas a la API incluirán tu token

---

## 🧪 Opción 2: Probar desde la Consola del Navegador

Si prefieres probar primero desde la consola:

### Paso 1: Abrir Consola

1. Ve a `http://localhost:5174`
2. Presiona `F12` → pestaña "Console"

### Paso 2: Probar Login

```javascript
// Reemplaza con TUS credenciales reales
const resultado = await window.ejemplosApi.ejemploLogin('TU_EMAIL@ejemplo.com', 'TU_PASSWORD');
console.log('✅ Login exitoso:', resultado);
```

### Paso 3: Verificar Token

```javascript
// Ver el token guardado
console.log('Token:', localStorage.getItem('authToken'));

// Ver usuario actual
console.log('Usuario:', JSON.parse(localStorage.getItem('currentUser')));
```

### Paso 4: Probar Endpoints

```javascript
// Listar proyectos
const proyectos = await window.ejemplosApi.ejemploListarProyectos();
console.log('📋 Proyectos:', proyectos);

// Crear un proyecto
const nuevoProyecto = await window.ejemplosApi.ejemploCrearProyecto();
console.log('🆕 Proyecto creado:', nuevoProyecto);
```

---

## 📋 ¿Qué Credenciales Usar?

### Si no recuerdas tus usuarios, verifica en la base de datos:

#### Opción A: DBeaver/pgAdmin

Conecta a tu base de datos Neon y ejecuta:

```sql
-- Ver todos los usuarios
SELECT id, email, role 
FROM authentication.person
ORDER BY role, email;
```

#### Opción B: psql (PowerShell)

```powershell
$env:PGPASSWORD="7zLUWHv1BRF0"
psql -h ep-round-rain-ado66wet-pooler.c-2.us-east-1.aws.neon.tech -U neondb_owner -d neondb -c "SELECT id, email, role FROM authentication.person;"
```

---

## 🔐 Estructura de la Autenticación

### Lo que sucede al hacer login:

```
1. Usuario ingresa email y password
   ↓
2. Frontend llama → POST /authenticator/person/authenticate
   ↓
3. Backend valida credenciales contra PostgreSQL
   ↓
4. Backend genera JWT token firmado
   ↓
5. Frontend recibe: { token, email, role }
   ↓
6. Frontend guarda token en localStorage
   ↓
7. Todas las peticiones subsecuentes incluyen:
   Authorization: Bearer <token>
```

### Respuesta del Login:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "estudiante@test.com",
  "role": "estudiante"
}
```

---

## 🛠️ Solución de Problemas

### ❌ Error: "Correo no encontrado"

**Causa:** El email no existe en la base de datos

**Solución:** Verifica el email en la base de datos:
```sql
SELECT email FROM authentication.person WHERE email = 'tu_email@test.com';
```

### ❌ Error: "Contraseña incorrecta"

**Causa:** La contraseña no coincide con el hash BCrypt almacenado

**Solución:** 
1. Verifica que estás usando la contraseña correcta
2. Si olvidaste la contraseña, puedes generar un nuevo hash:
   - Online: https://bcrypt-generator.com/ (rounds: 10)
   - Actualiza en la DB:
     ```sql
     UPDATE authentication.person 
     SET password = '$2a$10$NUEVO_HASH_AQUI'
     WHERE email = 'tu_email@test.com';
     ```

### ❌ Error: "Usuario ya tiene una sesión activa"

**Causa:** El sistema permite solo una sesión activa por usuario

**Solución:** Elimina la sesión anterior:
```sql
DELETE FROM authentication.active_sessions 
WHERE person_id = (
  SELECT id FROM authentication.person 
  WHERE email = 'tu_email@test.com'
);
```

### ❌ Error: "Usuario bloqueado"

**Causa:** Múltiples intentos fallidos de login

**Solución:**
```sql
DELETE FROM authentication.login_attempts 
WHERE person_id = (
  SELECT id FROM authentication.person 
  WHERE email = 'tu_email@test.com'
);
```

### ❌ Error: 401 Unauthorized en peticiones

**Causa:** Token expirado o inválido

**Solución:** Volver a hacer login:
```javascript
await window.ejemplosApi.ejemploLogin('tu_email@test.com', 'tu_password');
```

### ❌ Error: 403 Forbidden

**Causa:** Usuario sin permisos para ese endpoint

**Configuración de permisos:**
- `/project/project/listAll` → Solo `profesor`
- Todos los demás endpoints `/project/**` → `estudiante`

**Solución:** Usa un usuario con el rol correcto

---

## 📊 Endpoints Disponibles

### 🔓 Públicos (Sin JWT)

```javascript
POST /authenticator/person/authenticate
Body: { "email": "user@test.com", "password": "pass123" }
Response: { "token": "...", "email": "...", "role": "..." }
```

### 🔒 Protegidos (Requieren JWT)

#### Proyectos (estudiante)
```javascript
GET    /project/project/listAll    // Solo profesor
POST   /project/project             // Crear
GET    /project/project/{id}        // Ver uno
PUT    /project/project/{id}        // Actualizar
DELETE /project/project/{id}        // Eliminar
```

#### Tareas (estudiante)
```javascript
GET    /project/task/listAll
POST   /project/task
GET    /project/task/{id}
PUT    /project/task/{id}
DELETE /project/task/{id}
```

#### Estados (estudiante)
```javascript
GET    /project/state/listAll
POST   /project/state
GET    /project/state/{id}
```

#### Equipos (estudiante)
```javascript
GET    /project/team/listAll
POST   /project/team
GET    /project/team/{id}
```

---

## ✅ Checklist de Verificación

- [ ] Todos los servicios corriendo (8080, 8081, 8082, 5174)
- [ ] Conozco al menos un email/password de mi base de datos
- [ ] Puedo hacer login en `http://localhost:5174`
- [ ] Login exitoso muestra mensaje de bienvenida
- [ ] Soy redirigido al Dashboard
- [ ] Token guardado en localStorage
- [ ] Puedo listar proyectos/tareas sin error 401

---

## 🎯 Próximos Pasos

Una vez que el login funcione correctamente:

1. ✅ **Crear más usuarios** (si es necesario)
2. ✅ **Probar todos los endpoints** desde la interfaz
3. ✅ **Implementar logout**
4. ✅ **Agregar refresh tokens**
5. ✅ **Crear página de registro**
6. ✅ **Añadir recuperación de contraseña**
7. ✅ **Guards de ruta por roles**

---

## 💡 Tips Importantes

1. **No accedas directamente a `localhost:8080`** → Solo el frontend debe comunicarse con el Gateway

2. **Token se guarda automáticamente** → No necesitas manejarlo manualmente

3. **Roles importan** → Un `estudiante` no puede acceder a endpoints de `profesor`

4. **CORS configurado** → Frontend puede comunicarse con todos los microservicios

5. **Session timeout** → 15 minutos de inactividad cierra sesión automáticamente

---

## 📞 ¿Necesitas Ayuda?

Si algo no funciona:

1. **Verifica logs del Gateway** → `backend/gateway/target/...`
2. **Verifica logs del Authenticator** → `backend/authenticator/target/...`
3. **Revisa consola del navegador** → Presiona F12
4. **Verifica Network tab** → Ve las peticiones HTTP y respuestas

---

**¡Tu sistema está listo para usarse! 🚀**

Ahora solo necesitas ingresar con las credenciales de tus usuarios existentes en `http://localhost:5174`
