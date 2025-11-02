# Deployment

Esta carpeta contiene las configuraciones de despliegue para la aplicación InnoSistemas.

## 📚 Documentación

- **[DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md)** - Guía completa paso a paso para desplegar en Vercel y Render

## 🎯 Configuración Actual

### Frontend (Vercel)
- Framework: Vite + React + TypeScript
- Puerto local: 5173
- Build output: `dist/`
- Configuración: `frontend/vercel.json`

### Backend (Render)
- Framework: Spring Boot 4.0 + Java 21
- Puerto producción: 8080
- Puerto local: 8082
- Configuración: `backend/innosistemas/render.yaml`
- Perfil producción: `application-prod.properties`

### Base de Datos
- PostgreSQL en Neon Cloud
- Conexión ya configurada
- Sin migraciones automáticas (tablas ya creadas)

### CI/CD (GitHub Actions)
- Workflow CI: `.github/workflows/ci.yml`
- Deploy Frontend: `.github/workflows/frontend-deploy.yml`
- Deploy Backend: `.github/workflows/backend-deploy.yml`

## 🚀 Quick Start

1. **Lee primero**: `DEPLOYMENT_GUIDE.md` para instrucciones completas
2. **Requisitos**: Cuentas en Vercel, Render y GitHub
3. **Tiempo estimado**: 40-50 minutos para setup completo

## 📝 Resumen de Pasos

1. Desplegar backend en Render (15 min)
2. Desplegar frontend en Vercel (10 min)
3. Configurar CORS (5 min)
4. Configurar GitHub Actions (10 min)
5. Probar integración (5 min)

## ⚡ Archivos Importantes

```
deployment/
├── DEPLOYMENT_GUIDE.md       # ← EMPIEZA AQUÍ
├── README.md                  # ← Estás aquí

backend/innosistemas/
├── render.yaml                # Config Render
└── src/main/resources/
    ├── application.properties         # Config local
    └── application-prod.properties    # Config producción

frontend/
├── vercel.json               # Config Vercel
├── .env.example              # Ejemplo variables
└── .env.local                # Variables locales

.github/workflows/
├── ci.yml                    # CI para PRs
├── frontend-deploy.yml       # Deploy frontend
└── backend-deploy.yml        # Deploy backend
```

## 🆘 Soporte

Si encuentras problemas durante el despliegue, revisa la sección **Troubleshooting** en `DEPLOYMENT_GUIDE.md`.

---

**¡Listo para desplegar! Lee `DEPLOYMENT_GUIDE.md` para empezar. 🚀**
