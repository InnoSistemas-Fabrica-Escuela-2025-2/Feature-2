import { User, Project, Task, Notification } from '@/types';

// Mock users for authentication
export const mockUsers: User[] = [
  {
    id: '1',
    nombre: 'María García',
    correo: 'maria.garcia@universidad.edu',
    rol: 'estudiante',
    fechaRegistro: new Date('2024-01-15'),
  },
  {
    id: '2',
    nombre: 'Dr. Carlos Rodríguez',
    correo: 'carlos.rodriguez@universidad.edu',
    rol: 'profesor',
    fechaRegistro: new Date('2023-08-01'),
  },
  {
    id: '3',
    nombre: 'Ana Martínez',
    correo: 'ana.martinez@universidad.edu',
    rol: 'estudiante',
    fechaRegistro: new Date('2024-01-15'),
  },
  {
    id: '4',
    nombre: 'Juan Pérez',
    correo: 'juan.perez@universidad.edu',
    rol: 'estudiante',
    fechaRegistro: new Date('2024-01-15'),
  },
];

// Mock projects
export const mockProjects: Project[] = [
  {
    id: 'p1',
    nombre: 'Sistema de Gestión de Biblioteca',
    descripcion: 'Desarrollo de un sistema web para la gestión automatizada de préstamos y devoluciones de libros',
    objetivos: 'Implementar un sistema CRUD completo con autenticación de usuarios y reportes estadísticos',
    fechaEntrega: new Date('2025-12-15'),
    fechaCreacion: new Date('2025-09-01'),
    creadorId: '1',
    miembros: ['1', '3', '4'],
    progreso: 45,
  },
  {
    id: 'p2',
    nombre: 'Aplicación Móvil de Nutrición',
    descripcion: 'App para seguimiento de hábitos alimenticios y cálculo de calorías',
    objetivos: 'Crear una aplicación móvil multiplataforma con base de datos de alimentos y planes personalizados',
    fechaEntrega: new Date('2025-11-30'),
    fechaCreacion: new Date('2025-08-15'),
    creadorId: '3',
    miembros: ['1', '3'],
    progreso: 60,
  },
];

// Mock tasks
export const mockTasks: Task[] = [
  {
    id: 't1',
    proyectoId: 'p1',
    titulo: 'Diseñar base de datos',
    descripcion: 'Crear el modelo entidad-relación y normalizar hasta 3FN',
    fechaEntrega: new Date('2025-10-20'),
    fechaCreacion: new Date('2025-09-01'),
    responsableId: '1',
    estado: 'finalizado',
    prioridad: 'alta',
  },
  {
    id: 't2',
    proyectoId: 'p1',
    titulo: 'Implementar autenticación',
    descripcion: 'Desarrollar sistema de login con JWT y roles de usuario',
    fechaEntrega: new Date('2025-10-25'),
    fechaCreacion: new Date('2025-09-01'),
    responsableId: '3',
    estado: 'en-progreso',
    prioridad: 'alta',
  },
  {
    id: 't3',
    proyectoId: 'p1',
    titulo: 'Crear interfaz de préstamos',
    descripcion: 'Diseñar y desarrollar la interfaz para registrar préstamos de libros',
    fechaEntrega: new Date('2025-11-05'),
    fechaCreacion: new Date('2025-09-15'),
    responsableId: '4',
    estado: 'pendiente',
    prioridad: 'media',
  },
  {
    id: 't4',
    proyectoId: 'p2',
    titulo: 'API de alimentos',
    descripcion: 'Integrar API externa de información nutricional',
    fechaEntrega: new Date('2025-10-18'),
    fechaCreacion: new Date('2025-08-15'),
    responsableId: '1',
    estado: 'en-progreso',
    prioridad: 'alta',
  },
  {
    id: 't5',
    proyectoId: 'p2',
    titulo: 'Pantalla de estadísticas',
    descripcion: 'Crear gráficos de seguimiento de consumo calórico',
    fechaEntrega: new Date('2025-10-30'),
    fechaCreacion: new Date('2025-09-01'),
    responsableId: '3',
    estado: 'pendiente',
    prioridad: 'media',
  },
];

// Mock notifications
export const mockNotifications: Notification[] = [
  {
    id: 'n1',
    userId: '1',
    tipo: 'tarea-proxima',
    mensaje: 'La tarea "API de alimentos" vence en 3 días',
    leida: false,
    fecha: new Date(),
    relacionadoId: 't4',
  },
  {
    id: 'n2',
    userId: '1',
    tipo: 'asignacion-nueva',
    mensaje: 'Te han asignado una nueva tarea en "Sistema de Gestión de Biblioteca"',
    leida: false,
    fecha: new Date(Date.now() - 86400000),
    relacionadoId: 't1',
  },
  {
    id: 'n3',
    userId: '3',
    tipo: 'tarea-proxima',
    mensaje: 'La tarea "Implementar autenticación" vence mañana',
    leida: true,
    fecha: new Date(Date.now() - 172800000),
    relacionadoId: 't2',
  },
];
