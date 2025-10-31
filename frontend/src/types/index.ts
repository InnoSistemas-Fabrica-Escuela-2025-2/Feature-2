// Core domain types for the academic project management system

export type UserRole = 'estudiante' | 'profesor';

export type TaskStatus = 'pendiente' | 'en-progreso' | 'finalizado';

export interface User {
  id: string;
  nombre: string;
  correo: string;
  rol: UserRole;
  fechaRegistro: Date;
}

export interface Team {
  id: string;
  nombre: string;
  miembros: string[]; // Array of user IDs
}

export interface Project {
  id: string;
  nombre: string;
  descripcion: string;
  objetivos: string;
  fechaEntrega: Date;
  fechaCreacion: Date;
  creadorId: string;
  equipoId?: string; // Team assigned to project
  miembros: string[]; // Array of user IDs
  progreso: number; // 0-100
}

export interface Task {
  id: string;
  proyectoId: string;
  titulo: string;
  descripcion: string;
  fechaEntrega: Date;
  fechaCreacion: Date;
  responsableId: string;
  estado: TaskStatus;
  prioridad?: 'baja' | 'media' | 'alta';
}

export interface Notification {
  id: string;
  userId: string;
  tipo: 'tarea-proxima' | 'tarea-vencida' | 'proyecto-actualizado' | 'asignacion-nueva';
  mensaje: string;
  leida: boolean;
  fecha: Date;
  relacionadoId?: string; // ID of related task or project
}

export interface AuthCredentials {
  correo: string;
  contrasena: string;
}

export interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}
