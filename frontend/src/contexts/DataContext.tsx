import { createContext, useContext, useState, useEffect, ReactNode, useCallback } from 'react';
import { projectsApi, statesApi } from '@/lib/api';
import { useAuth } from './AuthContext';
import type { TaskState, TaskStatus } from '@/types';

interface DataContextType {
  projects: any[];
  tasks: any[];
  states: TaskState[];
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
  const [states, setStates] = useState<TaskState[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [lastUpdated, setLastUpdated] = useState<Date | null>(null);

  const loadData = useCallback(async () => {
    if (!user) return;

    const activeUserId = user.id;
    
    try {
      setIsLoading(true);
      setError(null);
      
      const studentId = Number.parseInt(user.id, 10);
      const shouldFilterByStudent = user.rol === 'estudiante' && Number.isFinite(studentId);

      const [projectsResponse, statesResponse] = await Promise.all([
        shouldFilterByStudent ? projectsApi.getByStudentId(studentId) : projectsApi.getAll(),
        statesApi.getAll().catch((error: unknown) => {
          console.warn('⚠️ No fue posible obtener los estados.', error);
          return { data: [] } as { data: TaskState[] };
        })
      ]);

      if (!user || user.id !== activeUserId) {
        return;
      }

      const projectsData = projectsResponse.data || [];
      const statesData: TaskState[] = statesResponse.data || [];

      const normalizeProjectKey = (value: string | number) => String(value);

      setStates(statesData);
      
      // Extract all tasks from all projects and normalize field names
      const statusMapping: Record<string, TaskStatus> = {
        'pendiente': 'pendiente',
        'pendientes': 'pendiente',
        'en progreso': 'en-progreso',
        'en-progreso': 'en-progreso',
        'progreso': 'en-progreso',
        'completada': 'finalizado',
        'completadas': 'finalizado',
        'completado': 'finalizado',
        'completados': 'finalizado',
        'finalizada': 'finalizado',
        'finalizadas': 'finalizado',
        'finalizado': 'finalizado',
        'finalizados': 'finalizado'
      };

      const statusLabels: Record<TaskStatus, string> = {
        'pendiente': 'Pendiente',
        'en-progreso': 'En progreso',
        'finalizado': 'Finalizada'
      };

      const normalizeStatusKey = (value: string) =>
        value
          .normalize('NFD')
          .replace(/[\u0300-\u036f]/g, '')
          .toLowerCase()
          .trim();

      const allTasks = projectsData.flatMap((project: any) => 
        (project.tasks || []).map((task: any) => {
          const rawStatus = String(task.state?.name || task.estado || '').trim();
          const normalizedKey = normalizeStatusKey(rawStatus);
          const canonicalStatus: TaskStatus = rawStatus
            ? statusMapping[normalizedKey] ?? 'pendiente'
            : 'pendiente';
          const statusLabel = rawStatus || statusLabels[canonicalStatus];

          return {
            ...task,
            // Normalize field names - backend uses different names
            id: task.id,
            titulo: task.title || task.titulo,
            descripcion: task.description || task.descripcion,
            fechaEntrega: task.deadline || task.fechaEntrega,
            fechaCreacion: task.creationDate || task.fechaCreacion,
            estado: canonicalStatus,
            estadoLabel: statusLabel,
            responsableId: task.responsibleId || task.responsableId || task.responsible,
            responsable: task.responsible || task.responsable,
            prioridad: task.priority || task.prioridad,
            estadoId: task.state?.id ?? task.estadoId,
            // Additional fields
            proyectoId: project.id,
            projectName: project.name || project.nombre
          };
        })
      );
      
      const tasksByProject = new Map<string, { total: number; completed: number }>();

      for (const task of allTasks) {
        const counterKey = normalizeProjectKey(task.proyectoId);
        const currentCounters = tasksByProject.get(counterKey) ?? { total: 0, completed: 0 };
        currentCounters.total += 1;
        if (task.estado === 'finalizado') {
          currentCounters.completed += 1;
        }
        tasksByProject.set(counterKey, currentCounters);
      }

      const enrichedProjects = projectsData.map((project: any) => {
        const counters = tasksByProject.get(normalizeProjectKey(project.id)) ?? { total: 0, completed: 0 };
        const progress = counters.total > 0
          ? Math.round((counters.completed / counters.total) * 100)
          : 0;

        return {
          ...project,
          progreso: progress,
          totalTasks: counters.total,
          completedTasks: counters.completed,
        };
      });

      if (!user || user.id !== activeUserId) {
        return;
      }

      setProjects(enrichedProjects);
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
      if (!user || user.id !== activeUserId) {
        return;
      }
      setIsLoading(false);
    }
  }, [user]);

  // Reload data whenever the authenticated user changes and clear caches on logout
  useEffect(() => {
    if (!user) {
      setProjects([]);
      setTasks([]);
      setStates([]);
      setLastUpdated(null);
      setIsLoading(false);
      return;
    }

    console.log('📊 Loading data for user:', user.id);
    setProjects([]);
    setTasks([]);
    setLastUpdated(null);
    loadData();
  }, [loadData, user?.id, user?.rol]);

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
        states,
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
