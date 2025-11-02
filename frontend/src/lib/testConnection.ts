/**
 * 🧪 ARCHIVO DE PRUEBA - PUEDES BORRAR DESPUÉS
 * 
 * Este archivo prueba la conexión con el backend en http://localhost:8082
 */

import api from './api';

// Función para probar la conexión
export const testConnection = async () => {
  console.log('🔄 Probando conexión con el backend en http://localhost:8082...');
  
  try {
    // Probar endpoint de tareas
    console.log('📋 Obteniendo tareas...');
    const tasksResponse = await api.get('/project/task/listAll');
    console.log('✅ Tareas obtenidas:', tasksResponse.data);
    
    // Probar endpoint de proyectos
    console.log('📁 Obteniendo proyectos...');
    const projectsResponse = await api.get('/project/project/listAll');
    console.log('✅ Proyectos obtenidos:', projectsResponse.data);
    
    console.log('🎉 ¡Conexión exitosa con el backend!');
    return true;
  } catch (error: any) {
    console.error('❌ Error al conectar con el backend:', error);
    console.error('Detalles:', error.response?.data || error.message);
    return false;
  }
};

// Ejecutar la prueba automáticamente cuando se importa
testConnection();
