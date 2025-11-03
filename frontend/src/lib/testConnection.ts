/**
 * 🧪 Test de Conexión con Arquitectura de Microservicios
 * 
 * Este archivo prueba la conexión con todos los microservicios:
 * - Gateway (8080)
 * - Authenticator (8081)
 * - InnoSistemas (8082)
 */

import { checkAllServices, projectsApi, tasksApi } from './api';

console.log('✅ testConnection.ts cargado correctamente');

/**
 * Función para probar la conexión con todos los microservicios
 * Se puede llamar desde la consola del navegador: window.testConnection()
 */
export const testConnection = async () => {
  console.log('� Verificando conexión con microservicios...\n');
  console.log('📋 Arquitectura de Microservicios:');
  console.log('   • Gateway (8080) - API Gateway principal');
  console.log('   • Authenticator (8081) - Servicio de autenticación');
  console.log('   • InnoSistemas (8082) - Servicio de proyectos y tareas\n');
  
  const results = await checkAllServices();
  
  console.log('\n📊 Resultados:');
  console.log(`   Gateway:       ${results.gateway ? '✅' : '❌'}`);
  console.log(`   Authenticator: ${results.authenticator ? '✅' : '❌'}`);
  console.log(`   InnoSistemas:  ${results.innosistemas ? '✅' : '❌'}`);
  
  const allConnected = results.gateway && results.authenticator && results.innosistemas;
  
  if (allConnected) {
    console.log('\n✨ Todos los servicios están operativos');
    
    // Probar endpoints adicionales
    try {
      console.log('\n🧪 Probando endpoints...');
      
      const projectsResponse = await projectsApi.getAll();
      console.log(`✅ Proyectos: ${projectsResponse.data.length} registros`);
      
      const tasksResponse = await tasksApi.getAll();
      console.log(`✅ Tareas: ${tasksResponse.data.length} registros`);
      
    } catch (error: unknown) {
      console.warn('⚠️ Error al probar endpoints:', error);
    }
  } else {
    console.log('\n⚠️ Algunos servicios no están disponibles');
    console.log('\n🔧 Pasos para solucionar:');
    
    if (!results.gateway) {
      console.log('   1. Iniciar Gateway:');
      console.log('      cd backend/gateway');
      console.log('      ./mvnw spring-boot:run\n');
    }
    
    if (!results.authenticator) {
      console.log('   2. Iniciar Authenticator:');
      console.log('      cd backend/authenticator');
      console.log('      ./mvnw spring-boot:run\n');
    }
    
    if (!results.innosistemas) {
      console.log('   3. Iniciar InnoSistemas:');
      console.log('      cd backend/innosistemas');
      console.log('      ./mvnw spring-boot:run\n');
    }
  }
  
  return results;
};

// Exportar para uso en consola del navegador
if (globalThis.window !== undefined) {
  (globalThis.window as any).testConnection = testConnection;
}

export default testConnection;
