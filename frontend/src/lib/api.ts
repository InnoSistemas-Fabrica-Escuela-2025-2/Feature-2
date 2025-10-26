import axios from 'axios';

// Configuración base de Axios
const api = axios.create({
  baseURL: 'http://localhost:8080', // URL del backend
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // Permite enviar cookies si es necesario
});

// Interceptor para agregar el token de autenticación si existe
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor para manejar errores de respuesta
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Si el token expiró o no es válido, redirigir al login
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// ==================== PROJECTS ====================
export const projectsApi = {
  // Obtener todos los proyectos
  getAll: () => api.get('/api/projects'),
  
  // Obtener un proyecto por ID
  getById: (id: number) => api.get(`/api/projects/${id}`),
  
  // Crear un nuevo proyecto
  create: (projectData: any) => api.post('/api/projects', projectData),
  
  // Actualizar un proyecto
  update: (id: number, projectData: any) => api.put(`/api/projects/${id}`, projectData),
  
  // Eliminar un proyecto
  delete: (id: number) => api.delete(`/api/projects/${id}`),
};

// ==================== TASKS ====================
export const tasksApi = {
  // Obtener todas las tareas
  getAll: () => api.get('/api/tasks'),
  
  // Obtener una tarea por ID
  getById: (id: number) => api.get(`/api/tasks/${id}`),
  
  // Obtener tareas por proyecto
  getByProject: (projectId: number) => api.get(`/api/tasks/project/${projectId}`),
  
  // Crear una nueva tarea
  create: (taskData: any) => api.post('/api/tasks', taskData),
  
  // Actualizar una tarea
  update: (id: number, taskData: any) => api.put(`/api/tasks/${id}`, taskData),
  
  // Eliminar una tarea
  delete: (id: number) => api.delete(`/api/tasks/${id}`),
};

export default api;
