/**
 * 📚 EJEMPLO DE USO DEL API
 * 
 * Este archivo muestra cómo usar las funciones del API en tus componentes React
 */

import { projectsApi, tasksApi } from './api';

// ==================== EJEMPLOS DE USO ====================

/**
 * Ejemplo 1: Obtener todos los proyectos
 */
export const fetchProjects = async () => {
  try {
    const response = await projectsApi.getAll();
    console.log('Proyectos:', response.data);
    return response.data;
  } catch (error) {
    console.error('Error al obtener proyectos:', error);
    throw error;
  }
};

/**
 * Ejemplo 2: Crear un nuevo proyecto
 */
export const createProject = async (projectData: any) => {
  try {
    const response = await projectsApi.create({
      name: projectData.name,
      description: projectData.description,
      startDate: projectData.startDate,
      endDate: projectData.endDate,
      status: projectData.status || 'ACTIVE'
    });
    console.log('Proyecto creado:', response.data);
    return response.data;
  } catch (error) {
    console.error('Error al crear proyecto:', error);
    throw error;
  }
};

/**
 * Ejemplo 3: Obtener todas las tareas
 */
export const fetchTasks = async () => {
  try {
    const response = await tasksApi.getAll();
    console.log('Tareas:', response.data);
    return response.data;
  } catch (error) {
    console.error('Error al obtener tareas:', error);
    throw error;
  }
};

/**
 * Ejemplo 4: Crear una nueva tarea
 */
export const createTask = async (taskData: any) => {
  try {
    const response = await tasksApi.create({
      title: taskData.title,
      description: taskData.description,
      status: taskData.status || 'TODO',
      priority: taskData.priority || 'MEDIUM',
      dueDate: taskData.dueDate,
      projectId: taskData.projectId,
      assignedTo: taskData.assignedTo
    });
    console.log('Tarea creada:', response.data);
    return response.data;
  } catch (error) {
    console.error('Error al crear tarea:', error);
    throw error;
  }
};

/**
 * Ejemplo 5: Actualizar una tarea
 */
export const updateTask = async (taskId: number, updates: any) => {
  try {
    const response = await tasksApi.update(taskId, updates);
    console.log('Tarea actualizada:', response.data);
    return response.data;
  } catch (error) {
    console.error('Error al actualizar tarea:', error);
    throw error;
  }
};

/**
 * Ejemplo 6: Eliminar un proyecto
 */
export const deleteProject = async (projectId: number) => {
  try {
    await projectsApi.delete(projectId);
    console.log('Proyecto eliminado');
    return true;
  } catch (error) {
    console.error('Error al eliminar proyecto:', error);
    throw error;
  }
};

/**
 * Ejemplo 7: Uso en un componente React con useEffect
 */
/*
import { useEffect, useState } from 'react';
import { fetchProjects, fetchTasks } from '@/lib/apiExamples';

function MyComponent() {
  const [projects, setProjects] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        const projectsData = await fetchProjects();
        const tasksData = await fetchTasks();
        setProjects(projectsData);
        setTasks(tasksData);
      } catch (error) {
        console.error('Error cargando datos:', error);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  if (loading) return <div>Cargando...</div>;

  return (
    <div>
      <h1>Proyectos: {projects.length}</h1>
      <h1>Tareas: {tasks.length}</h1>
    </div>
  );
}
*/
