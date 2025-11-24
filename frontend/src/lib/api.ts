import axios, { AxiosError } from 'axios';

// ==================== CONFIGURACIÓN DE CLIENTES API ====================

/**
 * Cliente principal - API Gateway (Spring Cloud Gateway)
 * Puerto: 8080
 * Enruta las peticiones a los microservicios
 */
const apiGateway = axios.create({
  baseURL: import.meta.env.VITE_API_GATEWAY_URL || 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
  withCredentials: true,
  timeout: Number.parseInt(import.meta.env.VITE_API_TIMEOUT || '30000'),
});

/**
 * Cliente para el microservicio de Autenticación
 * Puerto: 8081
 * Gestiona autenticación y autorización
 */
const authenticatorClient = axios.create({
  baseURL: import.meta.env.VITE_AUTHENTICATOR_URL || 'http://localhost:8081',
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
  withCredentials: true,
  timeout: Number.parseInt(import.meta.env.VITE_API_TIMEOUT || '30000'),
});

/**
 * Cliente para el microservicio de InnoSistemas
 * Puerto: 8082
 * Gestiona proyectos, tareas, equipos y estados
 */
const innosistemasClient = axios.create({
  baseURL: import.meta.env.VITE_INNOSISTEMAS_URL || 'http://localhost:8082',
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
  withCredentials: true,
  timeout: Number.parseInt(import.meta.env.VITE_API_TIMEOUT || '30000'),
});

// ==================== INTERCEPTORES ====================

/**
 * Interceptor de Request - Logging y trazabilidad
 */
const requestInterceptor = (config: any) => {
  // Log para debugging (solo en desarrollo)
  if (import.meta.env.DEV) {
    console.log(`🔵 ${config.method?.toUpperCase()} ${config.baseURL}${config.url}`, config.data || '');
  }
  
  return config;
};

/**
 * Interceptor de Response - Maneja respuestas exitosas y errores
 */
const responseInterceptor = (response: any) => {
  // Log para debugging (solo en desarrollo)
  if (import.meta.env.DEV) {
    console.log(`✅ ${response.config.method?.toUpperCase()} ${response.config.url}`, response.data);
  }
  return response;
};

/**
 * Interceptor de Error - Maneja errores de autenticación y red
 */
const errorInterceptor = async (error: AxiosError) => {
  const originalRequest = error.config as any;

  // Manejo de error 401 (no autorizado)
  if (error.response?.status === 401 && !originalRequest._retry) {
    originalRequest._retry = true;
    
    // Solo redirigir si NO estamos ya en la página de login
    const currentPath = globalThis.location?.pathname;
    if (currentPath !== '/login' && currentPath !== '/') {
      console.log('⚠️ Token inválido o expirado, redirigiendo a login...');
      // Limpiar token y redirigir a login
      localStorage.removeItem('user');
      globalThis.location.href = '/login';
    }
    throw error;
  }

  // Manejo de errores de red
  if (!error.response) {
    console.error('❌ Error de red - Backend no disponible:', error.message);
    let serviceName = 'Backend';
    if (error.config?.baseURL?.includes('8080')) {
      serviceName = 'Gateway';
    } else if (error.config?.baseURL?.includes('8081')) {
      serviceName = 'Authenticator';
    } else if (error.config?.baseURL?.includes('8082')) {
      serviceName = 'InnoSistemas';
    }
    throw new Error(`No se pudo conectar con el servicio ${serviceName}. Verifica que esté ejecutándose.`);
  }

  // Log de errores (solo en desarrollo)
  if (import.meta.env.DEV) {
    console.error(`❌ ${error.config?.method?.toUpperCase()} ${error.config?.url}`, {
      status: error.response?.status,
      data: error.response?.data
    });
  }

  throw error;
};

// Aplicar interceptores a todos los clientes
const clients = [apiGateway, authenticatorClient, innosistemasClient];
for (const client of clients) {
  client.interceptors.request.use(requestInterceptor, (error) => { throw error; });
  client.interceptors.response.use(responseInterceptor, errorInterceptor);
}

// ==================== API DE AUTENTICACIÓN ====================
// Usa el microservicio de Authenticator (puerto 8081) a través del Gateway

export const authApi = {
  /**
   * Autenticar usuario
   * POST /authenticator/person/authenticate
   */
  login: (credentials: { email: string; password: string }) => 
    apiGateway.post('/authenticator/person/authenticate', credentials),
  
  /**
   * Verificar estado del servicio de autenticación
   * GET /authenticator/person/message
   */
  checkService: () => 
    apiGateway.get('/authenticator/person/message'),
};

// ==================== API DE PROYECTOS ====================
// Usa el microservicio de InnoSistemas (puerto 8082) a través del Gateway

export const projectsApi = {
  /**
   * Obtener todos los proyectos
   * GET /project/project/listAll
   */
  getAll: () => 
    apiGateway.get('/project/project/listAll'),
  
  /**
   * Obtener proyectos asociados a un estudiante específico
   * GET /project/project/listAllById/{id}
   */
  getByStudentId: (studentId: number) => 
    apiGateway.get(`/project/project/listAllById/${studentId}`),
  
  /**
   * Crear un nuevo proyecto
   * POST /project/project/save
   */
  create: (projectData: any) => 
    apiGateway.post('/project/project/save', projectData),
  
  /**
   * Verificar estado del servicio
   * GET /project/project/message
   */
  checkService: () => 
    apiGateway.get('/project/project/message'),
};

// ==================== API DE TAREAS ====================
// Usa el microservicio de InnoSistemas (puerto 8082) a través del Gateway

export const tasksApi = {
  /**
   * Obtener todas las tareas
   * GET /project/task/listAll
   */
  getAll: () => 
    apiGateway.get('/project/task/listAll'),
  
  /**
   * Crear una nueva tarea
   * POST /project/task/save
   */
  create: (taskData: any) => 
    apiGateway.post('/project/task/save', taskData),
  
  /**
   * Actualizar una tarea
   * PUT /project/task/update
   */
  update: (taskData: any) => 
    apiGateway.put('/project/task/update', taskData),
  
  /**
   * Eliminar una tarea
   * DELETE /project/task/delete/{id}
   */
  delete: (id: number) => 
    apiGateway.delete(`/project/task/delete/${id}`),
  
  /**
   * Actualizar estado de una tarea
   * PUT /project/task/updateState/{id_task}/{id_state}
   */
  updateState: (taskId: number, stateId: number) => 
    apiGateway.put(`/project/task/updateState/${taskId}/${stateId}`),
};

// ==================== API DE EQUIPOS ====================
// Usa el microservicio de InnoSistemas (puerto 8082) a través del Gateway

export const teamsApi = {
  /**
   * Obtener nombres de estudiantes por ID de equipo
   * GET /project/team/getStudentsName/{id}
   */
  getStudentsNames: (teamId: number) => 
    apiGateway.get(`/project/team/getStudentsName/${teamId}`),
};

// ==================== API DE ESTADOS ====================
// Usa el microservicio de InnoSistemas (puerto 8082) a través del Gateway

export const statesApi = {
  /**
   * Obtener todos los estados
   * GET /project/state/listAll
   */
  getAll: () => 
    apiGateway.get('/project/state/listAll'),
};

// ==================== API DE NOTIFICACIONES ====================
// Usa el microservicio de Notifications (puerto 8083) a través del Gateway

export const notificationsApi = {
  /**
   * Obtener todas las notificaciones de un estudiante
   * GET /notifications/listAll/{id}
   */
  getByStudentId: (studentId: number) => 
    apiGateway.get(`/notifications/listAll/${studentId}`),
  
  /**
   * Eliminar (marcar como leída) una notificación
   * PUT /notifications/delete/{id}
   */
  delete: (notificationId: number) => 
    apiGateway.put(`/notifications/delete/${notificationId}`),
};

// ==================== FUNCIONES DE UTILIDAD ====================

/**
 * Verifica la conectividad con todos los microservicios
 */
export const checkAllServices = async () => {
  const results = {
    gateway: false,
    authenticator: false,
    innosistemas: false,
  };

  try {
    // Verificar Gateway
    await apiGateway.get('/actuator/health').catch(() => {
      console.warn('⚠️ Gateway health endpoint no disponible');
    });
    results.gateway = true;
    console.log('✅ Gateway (8080) - Conectado');
  } catch (error: unknown) {
    console.error('❌ Gateway (8080) - No disponible', error);
  }

  try {
    // Verificar Authenticator a través del Gateway
    await authApi.checkService();
    results.authenticator = true;
    console.log('✅ Authenticator (8081) - Conectado');
  } catch (error: unknown) {
    console.error('❌ Authenticator (8081) - No disponible', error);
  }

  try {
    // Verificar InnoSistemas a través del Gateway
    await projectsApi.checkService();
    results.innosistemas = true;
    console.log('✅ InnoSistemas (8082) - Conectado');
  } catch (error: unknown) {
    console.error('❌ InnoSistemas (8082) - No disponible', error);
  }

  return results;
};

// Exportar cliente principal por defecto (Gateway)
export default apiGateway;
