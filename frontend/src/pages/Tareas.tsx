import { useState } from 'react';
import { Plus, LayoutList, Columns } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { mockTasks, mockProjects } from '@/data/mockData';
import { useAuth } from '@/contexts/AuthContext';
import { Task, TaskStatus } from '@/types';
import TaskCard from '@/components/tasks/TaskCard';
import CreateTaskDialog from '@/components/tasks/CreateTaskDialog';
import EditTaskDialog from '@/components/tasks/EditTaskDialog';
import { TaskKanban } from '@/components/tasks/TaskKanban';

const Tareas = () => {
  const { user } = useAuth();
  const [tasks, setTasks] = useState(mockTasks);
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [editingTask, setEditingTask] = useState<Task | null>(null);
  const [viewMode, setViewMode] = useState<'list' | 'kanban'>('list');

  // Filter tasks assigned to current user
  const myTasks = tasks.filter(t => t.responsableId === user?.id);

  const filterTasksByStatus = (status: TaskStatus | 'todas') => {
    if (status === 'todas') return myTasks;
    return myTasks.filter(t => t.estado === status);
  };

  const handleTaskCreated = (newTask: Task) => {
    setTasks([...tasks, newTask]);
  };

  const handleTaskUpdated = (updatedTask: Task) => {
    setTasks(tasks.map(t => t.id === updatedTask.id ? updatedTask : t));
  };

  const handleTaskDeleted = (taskId: string) => {
    setTasks(tasks.filter(t => t.id !== taskId));
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
          <div className="flex items-center gap-1 border rounded-lg p-1">
            <Button
              variant={viewMode === 'list' ? 'default' : 'ghost'}
              size="sm"
              onClick={() => setViewMode('list')}
              aria-label="Vista de lista"
              aria-pressed={viewMode === 'list'}
            >
              <LayoutList className="h-4 w-4" aria-hidden="true" />
            </Button>
            <Button
              variant={viewMode === 'kanban' ? 'default' : 'ghost'}
              size="sm"
              onClick={() => setViewMode('kanban')}
              aria-label="Vista Kanban"
              aria-pressed={viewMode === 'kanban'}
            >
              <Columns className="h-4 w-4" aria-hidden="true" />
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
                    project={mockProjects.find(p => p.id === task.proyectoId)}
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
                    project={mockProjects.find(p => p.id === task.proyectoId)}
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
                    project={mockProjects.find(p => p.id === task.proyectoId)}
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
                    project={mockProjects.find(p => p.id === task.proyectoId)}
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
