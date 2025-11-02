import axios from 'axios';

// Configuración base de Axios
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8082', // URL del backend desde variable de entorno
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
  getAll: () => api.get('/project/project/listAll'),
  
  // Obtener un proyecto por ID
  getById: (id: number) => api.get(`/project/project/${id}`),
  
  // Crear un nuevo proyecto
  create: (projectData: any) => api.post('/project/project/save', projectData),
  
  // Actualizar un proyecto
  update: (id: number, projectData: any) => api.put(`/project/project/update`, projectData),
  
  // Eliminar un proyecto
  delete: (id: number) => api.delete(`/project/project/delete/${id}`),
};

// ==================== TASKS ====================
export const tasksApi = {
  // Obtener todas las tareas
  getAll: () => api.get('/project/task/listAll'),
  
  // Obtener una tarea por ID
  getById: (id: number) => api.get(`/project/task/${id}`),
  
  // Obtener tareas por proyecto
  getByProject: (projectId: number) => api.get(`/project/task/project/${projectId}`),
  
  // Crear una nueva tarea
  create: (taskData: any) => api.post('/project/task/save', taskData),
  
  // Actualizar una tarea
  update: (id: number, taskData: any) => api.put(`/project/task/update`, taskData),
  
  // Eliminar una tarea
  delete: (id: number) => api.delete(`/project/task/delete/${id}`),
};

export default api;
