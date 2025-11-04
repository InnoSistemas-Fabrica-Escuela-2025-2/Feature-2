import { useState } from 'react';
import { Plus, LayoutList, Columns } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Task, TaskStatus } from '@/types';
import { useData } from '@/contexts/DataContext';
import TaskCard from '@/components/tasks/TaskCard';
import CreateTaskDialog from '@/components/tasks/CreateTaskDialog';
import EditTaskDialog from '@/components/tasks/EditTaskDialog';
import { TaskKanban } from '@/components/tasks/TaskKanban';
import { tasksApi } from '@/lib/api';

const Tareas = () => {
  const { tasks, projects, isLoading, refreshData, states } = useData();
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [editingTask, setEditingTask] = useState<Task | null>(null);
  const [viewMode, setViewMode] = useState<'list' | 'kanban'>('list');

  // Filter tasks - show all tasks for now
  const myTasks = tasks;

  const filterTasksByStatus = (status: TaskStatus | 'todas') => {
    if (status === 'todas') return myTasks;
    return myTasks.filter(t => t.estado === status);
  };

  const handleTaskCreated = (_newTask: Task) => {
    void refreshData();
  };

  const handleTaskUpdated = async (updatedTask: Task) => {
    const parseNumeric = (value: string | number): number | null => {
      if (typeof value === 'number') {
        return Number.isFinite(value) ? value : null;
      }
      const parsed = Number(value);
      return Number.isFinite(parsed) ? parsed : null;
    };

    const taskId = parseNumeric(updatedTask.id);
    const projectId = parseNumeric(updatedTask.proyectoId);

    if (taskId === null || projectId === null) {
      throw new Error('Los identificadores de la tarea o del proyecto no son válidos.');
    }

    const normalizedStatus = updatedTask.estado.toLowerCase();
    const matchingState = states.find((state) =>
      (state.name || state.nombre || '').toLowerCase() === normalizedStatus
    );

    const stateId = matchingState?.id ?? updatedTask.estadoId;

    if (!stateId) {
      throw new Error('No se encontró un estado válido para actualizar la tarea.');
    }

    const deadlineDate = updatedTask.fechaEntrega instanceof Date
      ? updatedTask.fechaEntrega
      : new Date(updatedTask.fechaEntrega);

    if (Number.isNaN(deadlineDate.getTime())) {
      throw new Error('La fecha de entrega proporcionada no es válida.');
    }

    const payload = {
      id: taskId,
      title: updatedTask.titulo,
      description: updatedTask.descripcion,
      deadline: deadlineDate.toISOString(),
      responsible: updatedTask.responsable || updatedTask.responsableId || 'Sin asignar',
      project: { id: projectId },
      state: { id: stateId },
    };

    try {
      await tasksApi.update(payload);
      await refreshData();
    } catch (error: any) {
      console.error('Error actualizando la tarea:', error);
      const message = error?.response?.data?.message || error?.message || 'Error al actualizar la tarea.';
      throw new Error(message);
    }
  };

  const handleTaskDeleted = (_taskId: string) => {
    void refreshData();
  };

  const pendingTasks = filterTasksByStatus('pendiente');
  const inProgressTasks = filterTasksByStatus('en-progreso');
  const completedTasks = filterTasksByStatus('finalizado');

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Mis Tareas</h1>
          <p className="text-muted-foreground mt-2">
            Organiza y da seguimiento a tus tareas asignadas
          </p>
        </div>
        <div className="flex items-center gap-2">
          <div 
            className="flex items-center gap-1 border rounded-lg p-1"
            role="group"
            aria-label="Selector de vista"
          >
            <Button
              variant={viewMode === 'list' ? 'default' : 'ghost'}
              size="sm"
              onClick={() => setViewMode('list')}
              aria-label="Cambiar a vista de lista"
              aria-pressed={viewMode === 'list'}
            >
              <LayoutList className="h-4 w-4" aria-hidden="true" />
              <span className="sr-only">Vista de lista</span>
            </Button>
            <Button
              variant={viewMode === 'kanban' ? 'default' : 'ghost'}
              size="sm"
              onClick={() => setViewMode('kanban')}
              aria-label="Cambiar a vista Kanban"
              aria-pressed={viewMode === 'kanban'}
            >
              <Columns className="h-4 w-4" aria-hidden="true" />
              <span className="sr-only">Vista Kanban</span>
            </Button>
          </div>
          <Button
            onClick={() => setIsCreateDialogOpen(true)}
            aria-label="Crear nueva tarea"
          >
            <Plus className="h-4 w-4 mr-2" aria-hidden="true" />
            Nueva Tarea
          </Button>
        </div>
      </div>

      {/* Task Views */}
      {viewMode === 'kanban' ? (
        <TaskKanban
          tasks={myTasks}
          onEdit={setEditingTask}
          onDelete={handleTaskDeleted}
        />
      ) : (
        <Tabs defaultValue="todas" className="w-full">
          <TabsList className="grid w-full grid-cols-4 lg:w-[500px]">
            <TabsTrigger value="todas">
              Todas ({myTasks.length})
            </TabsTrigger>
            <TabsTrigger value="pendiente">
              Pendientes ({pendingTasks.length})
            </TabsTrigger>
            <TabsTrigger value="en-progreso">
              En Progreso ({inProgressTasks.length})
            </TabsTrigger>
            <TabsTrigger value="finalizado">
              Finalizadas ({completedTasks.length})
            </TabsTrigger>
          </TabsList>

          <TabsContent value="todas" className="space-y-4 mt-6">
            {myTasks.length === 0 ? (
              <Card>
                <CardContent className="flex flex-col items-center justify-center py-12">
                  <p className="text-muted-foreground text-center mb-4">
                    No tienes tareas asignadas
                  </p>
                  <Button onClick={() => setIsCreateDialogOpen(true)}>
                    <Plus className="h-4 w-4 mr-2" aria-hidden="true" />
                    Crear Tarea
                  </Button>
                </CardContent>
              </Card>
            ) : (
              <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                {myTasks.map(task => (
                  <TaskCard
                    key={task.id}
                    task={task}
                    project={projects.find(p => p.id === task.proyectoId)}
                    onEdit={() => setEditingTask(task)}
                    onDelete={handleTaskDeleted}
                  />
                ))}
              </div>
            )}
          </TabsContent>

          <TabsContent value="pendiente" className="space-y-4 mt-6">
            {pendingTasks.length === 0 ? (
              <Card>
                <CardContent className="flex flex-col items-center justify-center py-12">
                  <p className="text-muted-foreground">No tienes tareas pendientes</p>
                </CardContent>
              </Card>
            ) : (
              <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                {pendingTasks.map(task => (
                  <TaskCard
                    key={task.id}
                    task={task}
                    project={projects.find(p => p.id === task.proyectoId)}
                    onEdit={() => setEditingTask(task)}
                    onDelete={handleTaskDeleted}
                  />
                ))}
              </div>
            )}
          </TabsContent>

          <TabsContent value="en-progreso" className="space-y-4 mt-6">
            {inProgressTasks.length === 0 ? (
              <Card>
                <CardContent className="flex flex-col items-center justify-center py-12">
                  <p className="text-muted-foreground">No tienes tareas en progreso</p>
                </CardContent>
              </Card>
            ) : (
              <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                {inProgressTasks.map(task => (
                  <TaskCard
                    key={task.id}
                    task={task}
                    project={projects.find(p => p.id === task.proyectoId)}
                    onEdit={() => setEditingTask(task)}
                    onDelete={handleTaskDeleted}
                  />
                ))}
              </div>
            )}
          </TabsContent>

          <TabsContent value="finalizado" className="space-y-4 mt-6">
            {completedTasks.length === 0 ? (
              <Card>
                <CardContent className="flex flex-col items-center justify-center py-12">
                  <p className="text-muted-foreground">No tienes tareas finalizadas</p>
                </CardContent>
              </Card>
            ) : (
              <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                {completedTasks.map(task => (
                  <TaskCard
                    key={task.id}
                    task={task}
                    project={projects.find(p => p.id === task.proyectoId)}
                    onEdit={() => setEditingTask(task)}
                    onDelete={handleTaskDeleted}
                  />
                ))}
              </div>
            )}
          </TabsContent>
        </Tabs>
      )}

      {/* Dialogs */}
      <CreateTaskDialog
        open={isCreateDialogOpen}
        onOpenChange={setIsCreateDialogOpen}
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
};

export default Tareas;
