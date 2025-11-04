import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { projectsApi } from '@/lib/api';
import { useAuth } from './AuthContext';

interface DataContextType {
  projects: any[];
  tasks: any[];
  isLoading: boolean;
  error: string | null;
  refreshData: () => Promise<void>;
  lastUpdated: Date | null;
}

const DataContext = createContext<DataContextType | undefined>(undefined);

export function DataProvider({ children }: { children: ReactNode }) {
  const { user } = useAuth();
  const [projects, setProjects] = useState<any[]>([]);
  const [tasks, setTasks] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [lastUpdated, setLastUpdated] = useState<Date | null>(null);

  const loadData = async () => {
    if (!user) return;
    
    try {
      setIsLoading(true);
      setError(null);
      
      const projectsResponse = await projectsApi.getAll();
      const projectsData = projectsResponse.data || [];
      
      setProjects(projectsData);
      
      // Extract all tasks from all projects and normalize field names
      const allTasks = projectsData.flatMap((project: any) => 
        (project.tasks || []).map((task: any) => ({
          ...task,
          // Normalize field names - backend uses different names
          id: task.id,
          titulo: task.title || task.titulo,
          descripcion: task.description || task.descripcion,
          fechaEntrega: task.deadline || task.fechaEntrega,
          fechaCreacion: task.creationDate || task.fechaCreacion,
          estado: task.state?.name || task.estado,
          responsableId: task.responsibleId || task.responsableId,
          prioridad: task.priority || task.prioridad,
          // Additional fields
          proyectoId: project.id,
          projectName: project.name || project.nombre
        }))
      );
      
      setTasks(allTasks);
      setLastUpdated(new Date());
      
      console.log('✅ Data loaded and cached:', {
        projects: projectsData.length,
        tasks: allTasks.length,
        timestamp: new Date().toISOString()
      });
    } catch (err: any) {
      console.error('❌ Error loading data:', err);
      setError(err.message || 'Error al cargar los datos');
    } finally {
      setIsLoading(false);
    }
  };

  // Load data only once when user logs in
  useEffect(() => {
    if (user && projects.length === 0) {
      console.log('📊 Initial data load...');
      loadData();
    }
  }, [user]);

  // Refresh function for manual updates
  const refreshData = async () => {
    console.log('🔄 Manual data refresh requested');
    await loadData();
  };

  return (
    <DataContext.Provider
      value={{
        projects,
        tasks,
        isLoading,
        error,
        refreshData,
        lastUpdated,
      }}
    >
      {children}
    </DataContext.Provider>
  );
}

export function useData() {
  const context = useContext(DataContext);
  if (context === undefined) {
    throw new Error('useData must be used within a DataProvider');
  }
  return context;
}
