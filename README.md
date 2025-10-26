# Feature-2: Project and Task Management System

Feature 2. Gestión de Proyectos y Tareas - Permite a los equipos gestionar los proyectos asignados, organizando tareas, asignando responsables, estableciendo fechas de entrega y controlando el progreso.

## 🚀 Quick Start

### Desarrollo Local

```bash
# Backend (puerto 8082)
cd backend/innosistemas
./mvnw spring-boot:run

# Frontend (puerto 5173)
cd frontend
npm install
npm run dev
```

### Despliegue en Producción

**Lee las guías de despliegue:**
- 📋 [DEPLOYMENT_CHECKLIST.md](./DEPLOYMENT_CHECKLIST.md) - Checklist paso a paso
- ⚡ [DEPLOYMENT_QUICK_START.md](./DEPLOYMENT_QUICK_START.md) - Guía rápida (40 min)
- 📖 [deployment/DEPLOYMENT_GUIDE.md](./deployment/DEPLOYMENT_GUIDE.md) - Guía completa detallada

**Stack de Producción:**
- Frontend: Vercel
- Backend: Render
- Base de Datos: PostgreSQL (Neon)
- CI/CD: GitHub Actions

## 📁 Project Structure

```
├── docs/                  # Documentation and specifications
├── frontend/              # React + Vite + TypeScript (Puerto 5173)
├── backend/               # Spring Boot + Java 21 (Puerto 8082)
├── database/              # PostgreSQL schemas (Neon Cloud)
├── qa/                    # Testing and quality assurance
├── deployment/            # Infrastructure and deployment configs
├── project-management/    # Project planning and tracking
├── integrations/          # External system integrations
└── .github/workflows/     # GitHub Actions CI/CD
```

Each directory contains its own README.md with detailed information about its purpose and structure.

## 🛠️ Tech Stack

### Frontend
- React 18
- TypeScript
- Vite 5.4
- Tailwind CSS
- Axios
- Shadcn/ui Components

### Backend
- Spring Boot 4.0
- Java 21
- PostgreSQL 17.5
- Hibernate ORM
- Maven

### DevOps
- GitHub Actions
- Vercel (Frontend)
- Render (Backend)
- Neon (Database)

## 🌐 URLs

### Desarrollo
- Frontend: http://localhost:5173
- Backend: http://localhost:8082
- API Health: http://localhost:8082/actuator/health

### Producción (después del deploy)
- Frontend: https://tu-app.vercel.app
- Backend: https://tu-backend.onrender.com
- API Health: https://tu-backend.onrender.com/actuator/health

## 📚 Documentación

- **Despliegue**: Ver archivos `DEPLOYMENT_*.md` en la raíz
- **Frontend**: [frontend/README.md](./frontend/README.md)
- **Backend**: [backend/README.md](./backend/README.md)
- **Base de Datos**: [database/README.md](./database/README.md)
- **API**: Backend expone endpoints REST en `/project/*`, `/task/*`, etc.

## 👥 Equipo

InnoSistemas - Fábrica Escuela 2025-2
Universidad de Antioquia 
