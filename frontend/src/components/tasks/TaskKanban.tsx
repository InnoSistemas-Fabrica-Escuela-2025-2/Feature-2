import { Task } from '@/types';
import TaskCard from './TaskCard';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Clock, PlayCircle, CheckCircle } from 'lucide-react';

interface TaskKanbanProps {
  readonly tasks: readonly Task[];
  readonly onEdit: (task: Task) => void;
  readonly onDelete: (taskId: string) => void;
}

export function TaskKanban({ tasks, onEdit, onDelete }: TaskKanbanProps) {
  const pendingTasks = tasks.filter(t => t.estado === 'pendiente');
  const inProgressTasks = tasks.filter(t => t.estado === 'en-progreso');
  const completedTasks = tasks.filter(t => t.estado === 'finalizado');

  const columns = [
    {
      status: 'pendiente' as const,
      title: 'Pendiente',
      icon: Clock,
      tasks: pendingTasks,
      color: 'bg-pending/10 border-pending',
    },
    {
      status: 'en-progreso' as const,
      title: 'En Progreso',
      icon: PlayCircle,
      tasks: inProgressTasks,
      color: 'bg-in-progress/10 border-in-progress',
    },
    {
      status: 'finalizado' as const,
      title: 'Completado',
      icon: CheckCircle,
      tasks: completedTasks,
      color: 'bg-success/10 border-success',
    },
  ];

  return (
    <div 
      className="grid grid-cols-1 md:grid-cols-3 gap-6"
      aria-label="Tablero Kanban de tareas"
    >
      {columns.map(column => (
        <Card key={column.status} className={`${column.color} border-2`}>
          <CardHeader className="pb-3">
            <CardTitle className="flex items-center justify-between">
              <span className="flex items-center gap-2">
                <column.icon className="h-5 w-5" aria-hidden="true" />
                {column.title}
              </span>
              <Badge variant="secondary" aria-label={`${column.tasks.length} tareas`}>
                {column.tasks.length}
              </Badge>
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            {column.tasks.length === 0 ? (
              <p className="text-sm text-muted-foreground text-center py-8">
                No hay tareas {column.title.toLowerCase()}
              </p>
            ) : (
              <div className="space-y-3" aria-label={`Tareas ${column.title.toLowerCase()}`}>
                {column.tasks.map(task => (
                  <div key={task.id}>
                    <TaskCard
                      task={task}
                      project={undefined}
                      onEdit={() => onEdit(task)}
                      onDelete={onDelete}
                    />
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>
      ))}
    </div>
  );
}
