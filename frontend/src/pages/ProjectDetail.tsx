import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { projectsApi, tasksApi } from '@/lib/api';
import { useAuth } from '@/contexts/AuthContext';
import { useData } from '@/contexts/DataContext';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Input } from '@/components/ui/input';
import { useToast } from '@/hooks/use-toast';
import { 
  ArrowLeft, 
  Calendar, 
  Users, 
  Target, 
  CheckCircle2, 
  Clock, 
  AlertCircle,
  Plus,
  Filter,
  LayoutList,
  LayoutGrid,
  Edit,
  Trash2,
  User as UserIcon,
  AlertTriangle
} from 'lucide-react';
import { ErrorState } from '@/components/ErrorState';
import { TaskKanban } from '@/components/tasks/TaskKanban';
import TaskCard from '@/components/tasks/TaskCard';
import CreateTaskDialog from '@/components/tasks/CreateTaskDialog';
import EditTaskDialog from '@/components/tasks/EditTaskDialog';
import { Task, TaskStatus } from '@/types';
import { format, isPast, isWithinInterval, addDays } from 'date-fns';
import { es } from 'date-fns/locale';

type ViewMode = 'list' | 'kanban';
type FilterStatus = 'all' | 'pendiente' | 'en-progreso' | 'finalizado';
type FilterPriority = 'all' | 'alta' | 'media' | 'baja';

export default function ProjectDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { toast } = useToast();
  const { user } = useAuth();
  const { tasks: allTasks, projects, refreshData, states } = useData();
  
  const [project, setProject] = useState<any>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [viewMode, setViewMode] = useState<ViewMode>('list');
  const [filterStatus, setFilterStatus] = useState<FilterStatus>('all');
  const [filterPriority, setFilterPriority] = useState<FilterPriority>('all');
  const [filterResponsable, setFilterResponsable] = useState<string>('all');
  const [searchQuery, setSearchQuery] = useState('');
  const [showCreateDialog, setShowCreateDialog] = useState(false);
  const [editingTask, setEditingTask] = useState<Task | null>(null);
  const [selectedTaskDetail, setSelectedTaskDetail] = useState<Task | null>(null);

  const loadProject = async () => {
    try {
      setIsLoading(true);
      setError(null);
      const response = await projectsApi.getAll();
      const projects = response.data || [];
      const foundProject = projects.find((p: any) => p.id === Number.parseInt(id || '0'));
      
      if (foundProject) {
        setProject(foundProject);
      } else {
        setError('Proyecto no encontrado');
      }
    } catch (error) {
      console.error('Error loading project:', error);
      setError('No se pudo cargar el proyecto');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (id) {
      loadProject();
    }
  }, [id]);

  // Filter tasks for this project
  const projectTasks = allTasks.filter((task: any) => 
    String(task.proyectoId) === String(project?.id)
  );

  // Filter "My Tasks" - tasks assigned to current user
  const myTasks = projectTasks.filter((task: any) => 
    String(task.responsableId) === String(user?.id)
  );

  // Apply filters
  const getFilteredTasks = (tasksList: any[]) => {
    return tasksList.filter((task: any) => {
      // Status filter
      if (filterStatus !== 'all' && task.estado !== filterStatus) {
        return false;
      }
      
      // Priority filter
      if (filterPriority !== 'all' && task.prioridad?.toLowerCase() !== filterPriority) {
        return false;
      }
      
      // Responsable filter
      if (filterResponsable !== 'all' && String(task.responsableId) !== filterResponsable) {
        return false;
      }
      
      // Search query
      if (searchQuery && !task.titulo?.toLowerCase().includes(searchQuery.toLowerCase())) {
        return false;
      }
      
      return true;
    });
  };

  const filteredProjectTasks = getFilteredTasks(projectTasks);
  const filteredMyTasks = getFilteredTasks(myTasks);

  // Get unique responsables for filter
  const uniqueResponsables = Array.from(new Set(projectTasks.map((t: any) => t.responsableId)))
    .filter(Boolean)
    .map(id => ({
      id,
      name: projectTasks.find((t: any) => String(t.responsableId) === String(id))?.responsable || `Usuario ${id}`
    }));

  // Task status helpers
  const getStatusColor = (status: string) => {
    switch (status) {
      case 'finalizado':
        return 'bg-green-500/10 text-green-700 border-green-200';
      case 'en-progreso':
        return 'bg-blue-500/10 text-blue-700 border-blue-200';
      case 'pendiente':
        return 'bg-yellow-500/10 text-yellow-700 border-yellow-200';
      default:
        return 'bg-gray-500/10 text-gray-700 border-gray-200';
    }
  };

  const getStatusLabel = (status: string) => {
    switch (status) {
      case 'finalizado':
        return 'Finalizado';
      case 'en-progreso':
        return 'En Progreso';
      case 'pendiente':
        return 'Pendiente';
      default:
        return status;
    }
  };

  const getPriorityColor = (priority: string) => {
    switch (priority?.toLowerCase()) {
      case 'alta':
        return 'bg-red-500/10 text-red-700 border-red-200';
      case 'media':
        return 'bg-orange-500/10 text-orange-700 border-orange-200';
      case 'baja':
        return 'bg-green-500/10 text-green-700 border-green-200';
      default:
        return 'bg-gray-500/10 text-gray-700 border-gray-200';
    }
  };

  // Check if task is overdue or close to deadline
  const getTaskUrgency = (task: any) => {
    if (!task.fechaEntrega) return null;
    const deadline = new Date(task.fechaEntrega);
    const now = new Date();
    const threeDaysFromNow = addDays(now, 3);

    if (isPast(deadline) && task.estado !== 'finalizado') {
      return 'overdue';
    }
    if (isWithinInterval(deadline, { start: now, end: threeDaysFromNow }) && task.estado !== 'finalizado') {
      return 'soon';
    }
    return null;
  };

  // Handle task actions
  const handleTaskCreated = async (task: Task) => {
    await refreshData();
    setShowCreateDialog(false);
    toast({ title: 'Tarea creada exitosamente' });
  };

  const handleTaskUpdated = async (updatedTask: Task) => {
    try {
      const stateIdMap: Record<TaskStatus, number> = {
        'pendiente': 1,
        'en-progreso': 2,
        'finalizado': 3
      };

      const payload = {
        id: updatedTask.id,
        titulo: updatedTask.titulo,
        descripcion: updatedTask.descripcion,
        fechaEntrega: updatedTask.fechaEntrega,
        responsableId: updatedTask.responsableId,
        estadoId: stateIdMap[updatedTask.estado] || 1,
        prioridad: updatedTask.prioridad,
      };

      await tasksApi.update(payload);
      await refreshData();
      setEditingTask(null);
      toast({ title: 'Tarea actualizada exitosamente' });
    } catch (error) {
      console.error('Error updating task:', error);
      toast({ 
        title: 'Error al actualizar tarea',
        description: 'Por favor intenta nuevamente',
        variant: 'destructive'
      });
    }
  };

  const handleTaskDeleted = async (taskId: string | number) => {
    try {
      await tasksApi.delete(Number(taskId));
      await refreshData();
      toast({ title: 'Tarea eliminada exitosamente' });
    } catch (error) {
      console.error('Error deleting task:', error);
      toast({ 
        title: 'Error al eliminar tarea',
        description: 'Por favor intenta nuevamente',
        variant: 'destructive'
      });
    }
  };

  // Render task item
  const renderTaskItem = (task: any) => {
    const urgency = getTaskUrgency(task);
    
    return (
      <Card 
        key={task.id}
        className={`hover:shadow-md transition-shadow ${
          urgency === 'overdue' ? 'border-l-4 border-l-red-500' : 
          urgency === 'soon' ? 'border-l-4 border-l-orange-500' : ''
        }`}
      >
        <CardContent className="p-4">
          <div className="flex items-start justify-between gap-4">
            <div className="flex-1 min-w-0">
              <div className="flex items-center gap-2 mb-2">
                <button
                  onClick={() => setSelectedTaskDetail(task)}
                  className="font-semibold text-lg hover:text-primary transition-colors text-left"
                >
                  {task.titulo}
                </button>
                {urgency === 'overdue' && (
                  <Badge variant="destructive" className="gap-1">
                    <AlertTriangle className="h-3 w-3" />
                    Vencida
                  </Badge>
                )}
                {urgency === 'soon' && (
                  <Badge variant="outline" className="gap-1 bg-orange-50 border-orange-300 text-orange-700">
                    <Clock className="h-3 w-3" />
                    Próxima a vencer
                  </Badge>
                )}
              </div>
              
              <p className="text-sm text-muted-foreground line-clamp-2 mb-3">
                {task.descripcion}
              </p>
              
              <div className="flex flex-wrap gap-2">
                <Badge variant="outline" className={getStatusColor(task.estado)}>
                  {getStatusLabel(task.estado)}
                </Badge>
                
                {task.prioridad && (
                  <Badge variant="outline" className={getPriorityColor(task.prioridad)}>
                    {task.prioridad}
                  </Badge>
                )}
                
                {task.responsable && (
                  <Badge variant="outline" className="gap-1">
                    <UserIcon className="h-3 w-3" />
                    {task.responsable}
                  </Badge>
                )}
                
                {task.fechaEntrega && (
                  <Badge variant="outline" className="gap-1">
                    <Calendar className="h-3 w-3" />
                    {format(new Date(task.fechaEntrega), "d 'de' MMM", { locale: es })}
                  </Badge>
                )}
              </div>
            </div>
            
            <div className="flex gap-1">
              <Button
                variant="ghost"
                size="icon"
                onClick={() => setEditingTask(task)}
                aria-label="Editar tarea"
              >
                <Edit className="h-4 w-4" />
              </Button>
              <Button
                variant="ghost"
                size="icon"
                onClick={() => {
                  if (confirm('¿Estás seguro de eliminar esta tarea?')) {
                    handleTaskDeleted(task.id);
                  }
                }}
                aria-label="Eliminar tarea"
              >
                <Trash2 className="h-4 w-4" />
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>
    );
  };

  // Show error state
  if (error && !isLoading) {
    return (
      <div className="container mx-auto py-6">
        <Button
          variant="ghost"
          size="icon"
          onClick={() => navigate('/proyectos')}
          className="mb-4"
        >
          <ArrowLeft className="h-5 w-5" />
        </Button>
        <ErrorState
          message="No fue posible cargar la información del proyecto. Intente nuevamente."
          onRetry={loadProject}
          isRetrying={isLoading}
        />
      </div>
    );
  }

  // Show loading state
  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-muted-foreground">Cargando proyecto...</p>
        </div>
      </div>
    );
  }

  if (!project) {
    return null;
  }

  const completedTasks = projectTasks.filter((t: any) => t.estado === 'finalizado').length;
  const inProgressTasks = projectTasks.filter((t: any) => t.estado === 'en-progreso').length;
  const pendingTasks = projectTasks.filter((t: any) => t.estado === 'pendiente').length;

  return (
    <div className="container mx-auto py-6 space-y-6">
      {/* Header */}
      <div className="flex items-center gap-4">
        <Button
          variant="ghost"
          size="icon"
          onClick={() => navigate('/proyectos')}
          aria-label="Volver a proyectos"
        >
          <ArrowLeft className="h-5 w-5" />
        </Button>
        <div className="flex-1">
          <h1 className="text-3xl font-bold">{project.name}</h1>
          <p className="text-muted-foreground mt-1">{project.description}</p>
        </div>
      </div>

      {/* Project Info Cards */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Tareas</CardTitle>
            <Target className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{projectTasks.length}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Completadas</CardTitle>
            <CheckCircle2 className="h-4 w-4 text-green-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-600">{completedTasks}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">En Progreso</CardTitle>
            <Clock className="h-4 w-4 text-blue-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-blue-600">{inProgressTasks}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Pendientes</CardTitle>
            <AlertCircle className="h-4 w-4 text-yellow-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-yellow-600">{pendingTasks}</div>
          </CardContent>
        </Card>
      </div>

      {/* Tareas Section */}
      <Card>
        <CardHeader>
          <div className="flex items-center justify-between flex-wrap gap-4">
            <CardTitle className="text-2xl">Tareas del Proyecto</CardTitle>
            <div className="flex gap-2">
              <Button
                variant={viewMode === 'list' ? 'default' : 'outline'}
                size="sm"
                onClick={() => setViewMode('list')}
                aria-label="Vista de lista"
              >
                <LayoutList className="h-4 w-4" />
              </Button>
              <Button
                variant={viewMode === 'kanban' ? 'default' : 'outline'}
                size="sm"
                onClick={() => setViewMode('kanban')}
                aria-label="Vista kanban"
              >
                <LayoutGrid className="h-4 w-4" />
              </Button>
              <Button onClick={() => setShowCreateDialog(true)} className="gap-2">
                <Plus className="h-4 w-4" />
                Nueva Tarea
              </Button>
            </div>
          </div>

          {/* Filters */}
          <div className="grid grid-cols-1 md:grid-cols-4 gap-3 mt-4">
            <Input
              placeholder="Buscar tareas..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full"
            />
            
            <Select value={filterStatus} onValueChange={(v) => setFilterStatus(v as FilterStatus)}>
              <SelectTrigger>
                <SelectValue placeholder="Estado" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Todos los estados</SelectItem>
                <SelectItem value="pendiente">Pendiente</SelectItem>
                <SelectItem value="en-progreso">En Progreso</SelectItem>
                <SelectItem value="finalizado">Finalizado</SelectItem>
              </SelectContent>
            </Select>

            <Select value={filterPriority} onValueChange={(v) => setFilterPriority(v as FilterPriority)}>
              <SelectTrigger>
                <SelectValue placeholder="Prioridad" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Todas las prioridades</SelectItem>
                <SelectItem value="alta">Alta</SelectItem>
                <SelectItem value="media">Media</SelectItem>
                <SelectItem value="baja">Baja</SelectItem>
              </SelectContent>
            </Select>

            <Select value={filterResponsable} onValueChange={setFilterResponsable}>
              <SelectTrigger>
                <SelectValue placeholder="Responsable" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Todos los responsables</SelectItem>
                {uniqueResponsables.map((r: any) => (
                  <SelectItem key={r.id} value={String(r.id)}>
                    {r.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </CardHeader>

        <CardContent>
          <Tabs defaultValue="all" className="w-full">
            <TabsList className="grid w-full grid-cols-2">
              <TabsTrigger value="all">
                Todas las Tareas ({filteredProjectTasks.length})
              </TabsTrigger>
              <TabsTrigger value="mine" className="gap-2">
                <UserIcon className="h-4 w-4" />
                Mis Tareas ({filteredMyTasks.length})
              </TabsTrigger>
            </TabsList>

            <TabsContent value="all" className="mt-6">
              {viewMode === 'kanban' ? (
                <TaskKanban
                  tasks={filteredProjectTasks}
                  onEdit={(task) => setEditingTask(task)}
                  onDelete={handleTaskDeleted}
                />
              ) : (
                <div className="space-y-3">
                  {filteredProjectTasks.length === 0 ? (
                    <p className="text-center text-muted-foreground py-8">
                      No hay tareas que coincidan con los filtros
                    </p>
                  ) : (
                    filteredProjectTasks.map(renderTaskItem)
                  )}
                </div>
              )}
            </TabsContent>

            <TabsContent value="mine" className="mt-6">
              {viewMode === 'kanban' ? (
                <TaskKanban
                  tasks={filteredMyTasks}
                  onEdit={(task) => setEditingTask(task)}
                  onDelete={handleTaskDeleted}
                />
              ) : (
                <div className="space-y-3">
                  {filteredMyTasks.length === 0 ? (
                    <div className="text-center py-12">
                      <UserIcon className="h-12 w-12 mx-auto text-muted-foreground/50 mb-3" />
                      <p className="text-muted-foreground">
                        No tienes tareas asignadas en este proyecto
                      </p>
                    </div>
                  ) : (
                    filteredMyTasks.map(renderTaskItem)
                  )}
                </div>
              )}
            </TabsContent>
          </Tabs>
        </CardContent>
      </Card>

      {/* Dialogs */}
      <CreateTaskDialog
        open={showCreateDialog}
        onOpenChange={setShowCreateDialog}
        onTaskCreated={handleTaskCreated}
      />

      {editingTask && (
        <EditTaskDialog
          task={editingTask}
          open={!!editingTask}
          onOpenChange={(open) => !open && setEditingTask(null)}
          onTaskUpdated={handleTaskUpdated}
        />
      )}
    </div>
  );
}