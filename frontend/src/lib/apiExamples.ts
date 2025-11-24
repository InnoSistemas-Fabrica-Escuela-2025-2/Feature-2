/**
 * 📚 EJEMPLOS DE USO DE LA API INTEGRADA
 * 
 * Este archivo muestra cómo usar las APIs con la arquitectura de microservicios
 * Gateway (8080) → Authenticator (8081) + InnoSistemas (8082)
 */

import { authApi, projectsApi, tasksApi, teamsApi, statesApi } from './api';

console.log('✅ apiExamples.ts cargado correctamente');

// ==================== AUTENTICACIÓN ====================

/**
 * Ejemplo 1: Login de usuario
 */
export const ejemploLogin = async () => {
  try {
    const response = await authApi.login({
      email: "estudiante@ejemplo.com",
      password: "password123"
    });
    
    console.log('✅ Login exitoso:', response.data);
    return response.data;
  } catch (error: any) {
    console.error('❌ Error en login:', error.response?.data);
    throw error;
  }
};

/**
 * Ejemplo 2: Verificar servicio de autenticación
 */
export const ejemploVerificarAuth = async () => {
  try {
    const response = await authApi.checkService();
    console.log('✅ Servicio de autenticación:', response.data);
    return response.data;
  } catch (error) {
    console.error('❌ Servicio no disponible:', error);
    throw error;
  }
};

// ==================== PROYECTOS ====================

/**
 * Ejemplo 3: Obtener todos los proyectos
 */
export const fetchProjects = async () => {
  try {
    const response = await projectsApi.getAll();
    console.log('✅ Proyectos obtenidos:', response.data);
    return response.data;
  } catch (error) {
    console.error('❌ Error al obtener proyectos:', error);
    throw error;
  }
};

/**
 * Ejemplo 4: Crear un nuevo proyecto
 */
export const createProject = async (projectData: any) => {
  try {
    const response = await projectsApi.create({
      name: projectData.name,
      description: projectData.description,
      team: {
        id: projectData.teamId || 1
      }
    });
    console.log('✅ Proyecto creado:', response.data);
    return response.data;
  } catch (error: any) {
    console.error('❌ Error al crear proyecto:', error.response?.data);
    throw error;
  }
};

/**
 * Ejemplo 5: Actualizar un proyecto
 * NOTA: Este endpoint NO está implementado en el backend actualmente.
 * El backend solo tiene POST /project/project/save para crear.
 * Para actualizar, usaríamos el mismo endpoint save con un ID existente.
 */
export const updateProject = async (projectData: any) => {
  try {
    // Usar el endpoint save para actualizar (debe incluir id)
    const response = await projectsApi.create({
      id: projectData.id,
      name: projectData.name,
      description: projectData.description,
      team: {
        id: projectData.teamId
      }
    });
    console.log('✅ Proyecto actualizado:', response.data);
    return response.data;
  } catch (error: any) {
    console.error('❌ Error al actualizar proyecto:', error.response?.data);
    throw error;
  }
};

/**
 * Ejemplo 6: Eliminar un proyecto
 * NOTA: Este endpoint NO está implementado en el backend actualmente.
 * El backend no tiene DELETE /project/project/delete/{id}
 */
export const deleteProject = async (projectId: number) => {
  console.warn('⚠️ Endpoint DELETE /project/project/delete/{id} no implementado en backend');
  console.log('Para eliminar proyectos, se requiere implementar el endpoint en ProjectController.java');
  throw new Error('Delete project endpoint not implemented in backend');
  
  // Código comentado hasta que el backend implemente el endpoint:
  // try {
  //   await projectsApi.delete(projectId);
  //   console.log('✅ Proyecto eliminado:', projectId);
  // } catch (error: any) {
  //   console.error('❌ Error al eliminar proyecto:', error.response?.data);
  //   throw error;
  // }
};

// ==================== TAREAS ====================

/**
 * Ejemplo 7: Obtener todas las tareas
 */
export const fetchTasks = async () => {
  try {
    const response = await tasksApi.getAll();
    console.log('✅ Tareas obtenidas:', response.data);
    return response.data;
  } catch (error) {
    console.error('❌ Error al obtener tareas:', error);
    throw error;
  }
};

/**
 * Ejemplo 8: Crear una nueva tarea
 */
export const createTask = async (taskData: any) => {
  try {
    const response = await tasksApi.create({
      name: taskData.name,
      description: taskData.description,
      project: {
        id: taskData.projectId
      },
      state: {
        id: taskData.stateId || 1
      }
    });
    console.log('✅ Tarea creada:', response.data);
    return response.data;
  } catch (error: any) {
    console.error('❌ Error al crear tarea:', error.response?.data);
    throw error;
  }
};

/**
 * Ejemplo 9: Actualizar una tarea
 */
export const updateTask = async (taskData: any) => {
  try {
    const response = await tasksApi.update({
      id: taskData.id,
      name: taskData.name,
      description: taskData.description,
      project: {
        id: taskData.projectId
      },
      state: {
        id: taskData.stateId
      }
    });
    console.log('✅ Tarea actualizada:', response.data);
    return response.data;
  } catch (error: any) {
    console.error('❌ Error al actualizar tarea:', error.response?.data);
    throw error;
  }
};

/**
 * Ejemplo 10: Eliminar una tarea
 */
export const deleteTask = async (taskId: number) => {
  try {
    await tasksApi.delete(taskId);
    console.log('✅ Tarea eliminada:', taskId);
  } catch (error: any) {
    console.error('❌ Error al eliminar tarea:', error.response?.data);
    throw error;
  }
};

/**
 * Ejemplo 11: Actualizar estado de una tarea
 */
export const updateTaskState = async (taskId: number, stateId: number) => {
  try {
    await tasksApi.updateState(taskId, stateId);
    console.log(`✅ Estado de tarea ${taskId} actualizado al estado ${stateId}`);
  } catch (error: any) {
    console.error('❌ Error al actualizar estado:', error.response?.data);
    throw error;
  }
};

// ==================== EQUIPOS ====================

/**
 * Ejemplo 12: Obtener nombres de estudiantes de un equipo
 */
export const fetchStudentsNames = async (teamId: number) => {
  try {
    const response = await teamsApi.getStudentsNames(teamId);
    console.log(`✅ Estudiantes del equipo ${teamId}:`, response.data);
    return response.data;
  } catch (error) {
    console.error('❌ Error al obtener estudiantes:', error);
    throw error;
  }
};

// ==================== ESTADOS ====================

/**
 * Ejemplo 13: Obtener todos los estados
 */
export const fetchStates = async () => {
  try {
    const response = await statesApi.getAll();
    console.log('✅ Estados disponibles:', response.data);
    return response.data;
  } catch (error) {
    console.error('❌ Error al obtener estados:', error);
    throw error;
  }
};

// ==================== FLUJOS COMPLETOS ====================

/**
 * Ejemplo 14: Flujo completo - Crear proyecto con tareas
 */
export const ejemploFlujoCompleto = async () => {
  try {
    console.log('📋 Iniciando flujo completo...\n');
    
    // 1. Obtener estados disponibles
    console.log('1️⃣ Obteniendo estados...');
    const estados = await fetchStates();
    console.log(`   ✅ ${estados.length} estados encontrados\n`);
    
    // 2. Crear proyecto
    console.log('2️⃣ Creando proyecto...');
    const proyecto = await createProject({
      name: "Proyecto de Ejemplo",
      description: "Proyecto creado por flujo completo",
      teamId: 1
    });
    console.log(`   ✅ Proyecto creado: ${proyecto.name}\n`);
    
    // 3. Crear tareas para el proyecto
    console.log('3️⃣ Creando tareas...');
    const tareasData = [
      { name: "Análisis de requisitos", description: "Documentar requisitos" },
      { name: "Diseño", description: "Diseñar arquitectura" },
      { name: "Implementación", description: "Desarrollar funcionalidades" }
    ];
    
    for (const tareaData of tareasData) {
      const tarea = await createTask({
        ...tareaData,
        projectId: proyecto.id,
        stateId: estados[0].id
      });
      console.log(`   ✅ Tarea creada: ${tarea.name}`);
    }
    
    console.log('\n🎉 Flujo completado exitosamente!');
    
  } catch (error) {
    console.error('❌ Error en flujo completo:', error);
    throw error;
  }
};

// Exportar para usar en consola del navegador
if (globalThis.window !== undefined) {
  (globalThis.window as any).ejemplosApi = {
    ejemploLogin,
    ejemploVerificarAuth,
    fetchProjects,
    createProject,
    updateProject,
    deleteProject,
    fetchTasks,
    createTask,
    updateTask,
    deleteTask,
    updateTaskState,
    fetchStudentsNames,
    fetchStates,
    ejemploFlujoCompleto
  };
}
