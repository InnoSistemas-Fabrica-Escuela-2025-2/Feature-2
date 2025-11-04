# Copilot Instructions for InnoSistemas Project Management System

## Project Architecture Overview

This is a project and task management system with a clear separation between frontend and backend:

### Frontend (React + TypeScript + Vite)
- Located in `/frontend`
- Uses React Router for navigation
- State management via Context API (`ProjectContext`, `AuthContext`)
- UI components from shadcn/ui library
- API integration through axios with interceptors for auth

Key patterns:
```typescript
// API client setup with auth interceptors
const api = axios.create({
  baseURL: 'http://localhost:8080',
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true
});
```

### Backend (Spring Boot)
- Located in `/backend/{authenticator,innosistemas}`
- RESTful APIs with standard CRUD operations
- JWT-based authentication
- Project and task management services

## Development Workflow

1. Start Backend:
```powershell
cd backend/innosistemas
./mvnw spring-boot:run
```

2. Start Frontend:
```powershell
cd frontend
npm install
npm run dev
```

3. Test Connection:
- Check `/frontend/src/lib/testConnection.ts`
- Endpoints should respond at `http://localhost:8080`

## Key Design Patterns

### Data Flow
1. Components use React contexts for state:
   - `ProjectContext` for project/task management
   - `AuthContext` for user authentication
2. API calls through centralized `/frontend/src/lib/api.ts`
3. Type definitions in `/frontend/src/types/index.ts`

### Component Structure
- Pages in `/frontend/src/pages/`
- Reusable components in `/frontend/src/components/`
- UI components in `/frontend/src/components/ui/`
- Business logic in context providers

### Backend Integration Points
1. Project Management:
   - CRUD operations via `/project` endpoint
   - Task management through `/task` endpoint
2. Authentication:
   - JWT-based auth with token refresh
   - Role-based access control (estudiante/profesor)

## Common Patterns

### Task Status Flow
```typescript
type TaskStatus = 'pendiente' | 'en-progreso' | 'finalizado';
```

### Project Structure
```typescript
interface Project {
  id: string;
  nombre: string;
  descripcion: string;
  objetivos: string;
  fechaEntrega: Date;
  fechaCreacion: Date;
  creadorId: string;
  miembros: string[];
  progreso: number;
}
```

## Important Files to Reference

- API Integration: `/frontend/src/lib/api.ts`
- Type Definitions: `/frontend/src/types/index.ts`
- Project Context: `/frontend/src/context/ProjectContext.tsx`
- Backend Controllers: `/backend/innosistemas/src/main/java/.../controller/`

## Testing and Validation

1. Frontend:
   - Use mock data from `/frontend/src/data/mockData.ts` for development
   - Validation utils in `/frontend/src/utils/validation.ts`

2. Backend:
   - Integration tests in `/backend/*/src/test/`
   - Schema validations in database layer

## Common Tasks

### Adding New Features
1. Define types in `/frontend/src/types/`
2. Add API endpoints in `/frontend/src/lib/api.ts`
3. Update context if needed
4. Create/update components
5. Add corresponding backend endpoints

### Error Handling
- Frontend uses toast notifications for user feedback
- Backend returns appropriate HTTP status codes
- API client includes error interceptors for 401/auth failures