# Guía de Configuración en Render

## 🎯 Resumen de Servicios a Desplegar

Actualmente tienes **4 microservicios** que necesitan ser desplegados en Render:

1. **Gateway** (Puerto 8080) - Spring Cloud Gateway
2. **Authenticator** (Puerto 8081) - Servicio de autenticación JWT
3. **Innosistemas** (Puerto 8082) - Lógica de negocio (Proyectos, Tareas)
4. **Notifications** (Puerto 8083) - Servicio de notificaciones (Kafka + Email)

---

## 📋 Pasos de Configuración en Render

### OPCIÓN 1: Usar render.yaml (Recomendado - Automático)

Si ya tienes el repositorio conectado a Render:

1. **Hacer commit del render.yaml actualizado**
   ```bash
   git add render.yaml
   git commit -m "feat: Add notifications service to render.yaml"
   git push origin main
   ```

2. **En Render Dashboard:**
   - Ve a tu proyecto existente
   - Render detectará automáticamente los cambios en `render.yaml`
   - Aprobará el nuevo servicio `innosistemas-notifications`

3. **Configurar variables de entorno adicionales:**
   - `KAFKA_BOOTSTRAP_SERVERS` - URL de tu servidor Kafka (ver opciones abajo)
   - `SENDGRID_API_KEY` - API Key de SendGrid para envío de emails

---

### OPCIÓN 2: Crear Servicio Manualmente

Si prefieres crear el servicio desde el dashboard:

1. **Ir a Render Dashboard** → New Web Service

2. **Conectar Repositorio:**
   - Selecciona: `InnoSistemas-Fabrica-Escuela-2025-2/Feature-2`
   - Branch: `main`

3. **Configurar Servicio Notifications:**
   - **Name:** `innosistemas-notifications`
   - **Runtime:** Docker
   - **Dockerfile Path:** `./backend/notifications/Dockerfile`
   - **Docker Context:** `./backend/notifications`
   - **Instance Type:** Free (o Starter según necesites)

4. **Variables de Entorno:**
   ```
   PORT=8083
   SPRING_DATASOURCE_URL=<Connection String de tu DB>
   SPRING_DATASOURCE_USERNAME=<DB User>
   SPRING_DATASOURCE_PASSWORD=<DB Password>
   KAFKA_BOOTSTRAP_SERVERS=<Kafka URL>
   SENDGRID_API_KEY=<Tu API Key>
   SPRING_PROFILES_ACTIVE=prod
   ```

5. **Health Check Path:** `/actuator/health`

---

## 🔧 Configuración de Kafka

Tienes 3 opciones para Kafka en producción:

### Opción 1: CloudKarafka (Free Tier Disponible)
- **URL:** https://www.cloudkarafka.com/
- **Plan Free:** 5 MB storage, 10 topics
- **Setup:**
  1. Crear cuenta
  2. Crear instancia
  3. Obtener KAFKA_URL (formato: `kafka+ssl://usuario:password@servidor:9094`)
  4. Configurar en ambos servicios (Innosistemas y Notifications)

### Opción 2: Upstash Kafka (Recomendado - Serverless)
- **URL:** https://upstash.com/
- **Plan Free:** 10,000 mensajes/día
- **Setup:**
  1. Crear cuenta en Upstash
  2. Crear Kafka cluster
  3. Crear topic: `notification-events`
  4. Copiar KAFKA_URL
  5. Configurar en servicios

### Opción 3: Confluent Cloud (Más robusto)
- **URL:** https://confluent.cloud/
- **Plan Free:** $400 en créditos
- **Setup:**
  1. Crear cuenta
  2. Crear cluster básico
  3. Crear topic `notification-events`
  4. Generar API Keys
  5. Configurar BOOTSTRAP_SERVERS + SASL

---

## 📧 Configuración de SendGrid

1. **Crear cuenta en SendGrid:**
   - URL: https://sendgrid.com/
   - Plan Free: 100 emails/día

2. **Generar API Key:**
   - Settings → API Keys → Create API Key
   - Name: `InnoSistemas Notifications`
   - Permissions: Full Access (o Mail Send)
   - **Copiar la API Key** (solo se muestra una vez)

3. **Verificar Sender Email:**
   - Settings → Sender Authentication
   - Verificar email que usarás como remitente
   - Confirma email de verificación

4. **Configurar en Render:**
   ```
   SENDGRID_API_KEY=SG.xxxxxxxxxxxxxxxxxxxxxxxxx
   ```

---

## 🗄️ Actualización de Base de Datos

Tu base de datos PostgreSQL actual necesita incluir el schema de notifications:

### Verificar Schema:
```sql
-- Conectar a tu DB de Render
SELECT schema_name FROM information_schema.schemata;
```

### Crear Schema si no existe:
```sql
CREATE SCHEMA IF NOT EXISTS notification;

-- El servicio creará las tablas automáticamente con JPA
```

---

## 🌐 Actualización del Gateway

Si ya tienes el Gateway desplegado, necesitas agregar la ruta a Notifications:

### Variables de Entorno del Gateway (Actualizar):
```
NOTIFICATIONS_URL=https://innosistemas-notifications.onrender.com
```

### Verificar application.yml del Gateway:
Debe tener la ruta configurada para `/notifications/**`

---

## 🔗 Orden de Despliegue Recomendado

1. ✅ **Base de Datos** (ya existe)
2. ✅ **Authenticator** (ya desplegado)
3. ✅ **Innosistemas** (ya desplegado)
4. 🆕 **Notifications** (nuevo - desplegar ahora)
5. 🔄 **Gateway** (actualizar variable NOTIFICATIONS_URL)

---

## ✅ Checklist de Configuración

### Pre-Deploy:
- [ ] render.yaml actualizado con notifications service
- [ ] Kafka configurado (CloudKarafka, Upstash o Confluent)
- [ ] SendGrid API Key obtenida
- [ ] Sender email verificado en SendGrid

### Deploy:
- [ ] Servicio Notifications creado en Render
- [ ] Variables de entorno configuradas
- [ ] Health check pasando (`/actuator/health`)
- [ ] Logs sin errores de conexión

### Post-Deploy:
- [ ] Gateway actualizado con NOTIFICATIONS_URL
- [ ] Frontend actualizado con URL del Gateway
- [ ] Prueba de notificación end-to-end
- [ ] Verificar email enviado correctamente

---

## 🧪 Testing de Notifications

### 1. Health Check:
```bash
curl https://innosistemas-notifications.onrender.com/actuator/health
```

### 2. Verificar conexión Kafka en logs:
```
✅ Kafka consumer started successfully
✅ Subscribed to topics: [notification-events]
```

### 3. Test end-to-end:
1. Crear una tarea en el frontend
2. Verificar en logs de Innosistemas que se envió mensaje a Kafka
3. Verificar en logs de Notifications que se recibió mensaje
4. Verificar que el email fue enviado
5. Consultar endpoint GET /notifications/listAll/{studentId}

---

## 📊 Monitoreo

### Logs a Revisar:
```bash
# En Render Dashboard, revisar logs de:
1. Innosistemas: "Notification sent to Kafka"
2. Notifications: "Notification received from Kafka"
3. Notifications: "Email sent successfully"
```

### Métricas Importantes:
- **Kafka Lag:** Debe ser 0 (mensajes procesados inmediatamente)
- **Email Delivery Rate:** 100% en SendGrid dashboard
- **Service Health:** Todos en "Healthy"

---

## ⚠️ Troubleshooting Común

### Error: "Unable to connect to Kafka"
- Verificar KAFKA_BOOTSTRAP_SERVERS correcto
- Verificar que topic `notification-events` existe
- Revisar credenciales SASL si aplica

### Error: "SendGrid authentication failed"
- Verificar SENDGRID_API_KEY válida
- Verificar que API Key tiene permisos Mail Send
- Regenerar API Key si es necesaria

### Error: "Schema notification does not exist"
- Conectar a DB y crear schema manualmente
- Verificar que el usuario tiene permisos CREATE SCHEMA

### Service crashea al iniciar:
- Revisar logs completos en Render
- Verificar que todas las variables de entorno están configuradas
- Verificar conexión a base de datos

---

## 💰 Estimación de Costos (Plan Free)

| Servicio | Plan | Costo |
|----------|------|-------|
| Render - 4 Web Services | Free | $0 |
| PostgreSQL (Render) | Free (1GB) | $0 |
| Kafka (Upstash) | Free | $0 |
| SendGrid | Free (100 emails/día) | $0 |
| **TOTAL** | | **$0/mes** |

**Nota:** Con plan Free de Render, los servicios se suspenden después de 15 min de inactividad.

Para producción seria, considera:
- Render Starter ($7/servicio) = $28/mes para 4 servicios
- PostgreSQL Standard (10GB) = $7/mes
- Kafka Pro (Upstash) = $10/mes
- SendGrid Essentials = $15/mes
- **Total Producción:** ~$60/mes

---

## 🚀 Siguiente Paso Inmediato

1. **Configurar Kafka** (recomiendo Upstash por simplicidad)
2. **Obtener SendGrid API Key**
3. **Commit y push del render.yaml actualizado**
4. **Esperar deploy automático o crear servicio manualmente**
5. **Verificar health checks**
6. **Probar flujo completo**

---

**Fecha:** 2025-11-23  
**Última actualización:** Después del merge a main  
**Arquitectura:** 4 microservicios + PostgreSQL + Kafka + SendGrid
