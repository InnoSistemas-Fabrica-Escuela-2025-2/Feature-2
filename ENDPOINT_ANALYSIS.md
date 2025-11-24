# Análisis de Endpoints: Frontend vs Backend

## ❌ PROBLEMAS ENCONTRADOS - 5 ENDPOINTS FALTANTES

### 1. AUTHENTICATOR SERVICE

#### ✅ Endpoints que SÍ existen:
- `POST /authenticator/person/authenticate` ✅
- `GET /authenticator/person/message` ✅

#### ❌ Endpoints que NO existen en backend pero frontend los llama:
- **`GET /authenticator/person/me`** ❌
  - Frontend: `authApi.me()`
  - Backend: **NO IMPLEMENTADO**
  - Uso: Obtener perfil del usuario autenticado
  
- **`POST /authenticator/person/logout`** ❌
  - Frontend: `authApi.logout()`
  - Backend: **NO IMPLEMENTADO**
  - Uso: Cerrar sesión del usuario

---

### 2. INNOSISTEMAS - PROJECTS

#### ✅ Endpoints que SÍ existen:
- `POST /project/project/save` ✅
- `GET /project/project/listAll` ✅
- `GET /project/project/listAllById/{id}` ✅
- `GET /project/project/message` ✅

#### ❌ Endpoints que NO existen en backend pero frontend los llama:
- **`PUT /project/project/update`** ❌
  - Frontend: `projectsApi.update(projectData)`
  - Backend: **NO IMPLEMENTADO**
  - Uso: Actualizar un proyecto existente
  
- **`DELETE /project/project/delete/{id}`** ❌
  - Frontend: `projectsApi.delete(id)`
  - Backend: **NO IMPLEMENTADO**
  - Uso: Eliminar un proyecto

---

### 3. INNOSISTEMAS - TASKS ✅

#### ✅ TODOS los endpoints existen:
- `GET /project/task/listAll` ✅
- `POST /project/task/save` ✅
- `PUT /project/task/update` ✅
- `DELETE /project/task/delete/{id}` ✅
- `PUT /project/task/updateState/{id_task}/{id_state}` ✅

**STATUS: COMPLETO** ✅

---

### 4. INNOSISTEMAS - TEAMS

#### ✅ Endpoints que SÍ existen:
- `GET /project/team/getStudentsName/{id}` ✅

#### ❌ Endpoints que NO existen en backend pero frontend los llama:
- **`GET /project/team/listAll`** ❌
  - Frontend: `teamsApi.getAll()`
  - Backend: **NO IMPLEMENTADO**
  - Uso: Listar todos los equipos

---

### 5. INNOSISTEMAS - STATES ✅

#### ✅ TODOS los endpoints existen:
- `GET /project/state/listAll` ✅

**STATUS: COMPLETO** ✅

---

### 6. NOTIFICATIONS SERVICE ✅

#### ✅ TODOS los endpoints existen:
- `GET /notifications/listAll/{id}` ✅
- `PUT /notifications/delete/{id}` ✅

**STATUS: COMPLETO** ✅

---

## 📊 RESUMEN FINAL

### Servicios Completos (3):
1. ✅ **Tasks** - 5/5 endpoints implementados
2. ✅ **States** - 1/1 endpoint implementado
3. ✅ **Notifications** - 2/2 endpoints implementados

### Servicios Incompletos (3):
1. ❌ **Authenticator** - 2/4 endpoints (faltan 2)
2. ❌ **Projects** - 4/6 endpoints (faltan 2)
3. ❌ **Teams** - 1/2 endpoints (falta 1)

---

## 🔧 ACCIONES REQUERIDAS

### Prioridad ALTA (críticos para funcionalidad básica):

1. **`PUT /project/project/update`**
   - Ubicación: `ProjectController.java`
   - Implementación sugerida:
   ```java
   @PutMapping("/update")
   public ResponseEntity<Project> updateProject(@RequestBody Project project) {
       try {
           Project updated = projectService.saveProject(project);
           return ResponseEntity.ok(updated);
       } catch (Exception e) {
           return ResponseEntity.internalServerError().build();
       }
   }
   ```

2. **`DELETE /project/project/delete/{id}`**
   - Ubicación: `ProjectController.java`
   - Implementación sugerida:
   ```java
   @DeleteMapping("/delete/{id}")
   public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
       projectService.deleteProject(id);
       return ResponseEntity.noContent().build();
   }
   ```

3. **`GET /project/team/listAll`**
   - Ubicación: `TeamController.java`
   - Implementación sugerida:
   ```java
   @GetMapping("/listAll")
   public ResponseEntity<List<Team>> listAllTeams() {
       return ResponseEntity.ok(teamService.listAllTeams());
   }
   ```

### Prioridad MEDIA (mejoran UX):

4. **`GET /authenticator/person/me`**
   - Ubicación: `AuthenticatorController.java`
   - Requiere: Obtener usuario de JWT token
   - Implementación sugerida:
   ```java
   @GetMapping("/me")
   public ResponseEntity<PersonResponse> getCurrentUser(Authentication authentication) {
       // Extraer email del token JWT
       String email = authentication.getName();
       PersonResponse user = authenticatorService.getUserByEmail(email);
       return ResponseEntity.ok(user);
   }
   ```

5. **`POST /authenticator/person/logout`**
   - Ubicación: `AuthenticatorController.java`
   - Nota: Con JWT stateless, logout es manejado en frontend (limpiar token)
   - Implementación sugerida (opcional):
   ```java
   @PostMapping("/logout")
   public ResponseEntity<Void> logout() {
       // Si se implementa blacklist de tokens, agregar token a blacklist
       return ResponseEntity.noContent().build();
   }
   ```

---

## 🎯 IMPACTO EN FUNCIONALIDAD

### Funcionalidades que NO funcionarán:
- ❌ Editar proyectos existentes
- ❌ Eliminar proyectos
- ❌ Listar equipos en interfaz
- ❌ Obtener perfil de usuario
- ❌ Cerrar sesión (frontend puede manejar localmente)

### Funcionalidades que SÍ funcionarán:
- ✅ Login
- ✅ Crear proyectos
- ✅ Listar proyectos
- ✅ Crear, editar, eliminar tareas
- ✅ Cambiar estados de tareas
- ✅ Ver notificaciones
- ✅ Marcar notificaciones como leídas

---

## 📝 RECOMENDACIONES

1. **Implementar los 5 endpoints faltantes** antes de hacer merge a main
2. **Crear tests unitarios** para los nuevos endpoints
3. **Actualizar documentación** con los nuevos endpoints
4. **Verificar métodos en services** (deleteProject, listAllTeams, getUserByEmail)

---

## ✅ SOLUCIONES IMPLEMENTADAS EN FRONTEND

### 1. AuthContext - Manejo de Sesión Local
- **Problema**: `authApi.me()` y `authApi.logout()` no existen
- **Solución**: 
  - Sesión manejada con **localStorage**
  - Al login: guardar usuario en localStorage
  - Al cargar app: restaurar usuario desde localStorage
  - Al logout: limpiar localStorage localmente
  - JWT es stateless, no requiere endpoint de logout en backend

### 2. CreateProjectDialog - Teams Hardcoded
- **Problema**: `teamsApi.getAll()` no existe
- **Solución**:
  - Lista predefinida de 5 equipos (id: 1-5)
  - Los equipos deben existir en la base de datos
  - Backend solo expone `getStudentsName/{id}` para equipos

### 3. Projects Update - Usar Save Endpoint
- **Problema**: `PUT /project/project/update` no existe
- **Solución**:
  - Usar `POST /project/project/save` con ID incluido
  - JPA detecta ID existente y hace UPDATE automáticamente
  - Actualizado en `apiExamples.ts`

### 4. Projects Delete - No Disponible
- **Problema**: `DELETE /project/project/delete/{id}` no existe
- **Solución**:
  - Función marcada como no implementada
  - Lanza error explicativo
  - Requiere implementación futura en backend si se necesita

### Archivos Modificados:
1. ✅ `frontend/src/lib/api.ts` - Removidos endpoints inexistentes
2. ✅ `frontend/src/contexts/AuthContext.tsx` - Sesión con localStorage
3. ✅ `frontend/src/components/projects/CreateProjectDialog.tsx` - Teams hardcoded
4. ✅ `frontend/src/lib/apiExamples.ts` - Ejemplos actualizados

### Estado Final:
- **Compilación**: ✅ Sin errores de TypeScript
- **Funcionalidad**: ✅ Login/logout funcional localmente
- **Proyectos**: ✅ Crear y actualizar (usando save)
- **Equipos**: ✅ Lista predefinida funcional
- **Limitación**: ❌ No se pueden eliminar proyectos (requiere backend)

---

**Fecha de análisis:** 2025-11-23  
**Fecha de implementación:** 2025-11-23  
**Branch:** integration-sprint2-sprint3  
**Analista:** GitHub Copilot
