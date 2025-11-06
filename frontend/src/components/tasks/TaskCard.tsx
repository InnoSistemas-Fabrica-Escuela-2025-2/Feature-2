import { Task, Project, TaskStatus } from '@/types';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Calendar, Edit, Trash2 } from 'lucide-react';
import { tasksApi } from '@/lib/api';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '@/components/ui/alert-dialog';
import { toast } from 'sonner';
import { useState } from 'react';

interface TaskCardProps {
  task: Task;
  project?: Project;
  onEdit: () => void;
  onDelete: (taskId: string) => void;
}

const STATUS_LABELS: Record<TaskStatus, string> = {
  pendiente: 'Pendiente',
  'en-progreso': 'En progreso',
  finalizado: 'Finalizada'
};

const TaskCard = ({ task, project, onEdit, onDelete }: TaskCardProps) => {
  const [isDeleting, setIsDeleting] = useState(false);

  const displayStatus = task.estadoLabel ?? STATUS_LABELS[task.estado] ?? task.estado;
  const projectLabel = project?.nombre || project?.name || task.projectName;
  const projectDateValue = task.fechaEntrega || (task as any).deadline;
  const deadlineDate = projectDateValue ? new Date(projectDateValue) : null;
  const hasValidDeadline = deadlineDate instanceof Date && !Number.isNaN(deadlineDate.getTime());
  
  const handleDelete = async () => {
    try {
      setIsDeleting(true);
      console.log('Deleting task:', task.id);
      
      await tasksApi.delete(parseInt(task.id));
      
      toast.success('Tarea eliminada correctamente');
      onDelete(task.id);
    } catch (error: any) {
      console.error('Error deleting task:', error);
      toast.error('Error al eliminar la tarea');
    } finally {
      setIsDeleting(false);
    }
  };

  return (
    <Card className="hover:shadow-lg transition-shadow" role="article" aria-label={`Tarea: ${task.titulo}`}>
      <CardHeader>
        <div className="flex items-start justify-between gap-2">
          <CardTitle className="text-base line-clamp-2">{task.titulo}</CardTitle>
          <span 
            className={`status-badge status-${task.estado} shrink-0`}
            role="status"
            aria-label={`Estado de la tarea: ${displayStatus}`}
          >
            {displayStatus}
          </span>
        </div>
      </CardHeader>
      <CardContent className="space-y-3">
        <p className="text-sm text-muted-foreground line-clamp-2">{task.descripcion}</p>
        
        {projectLabel && (
          <p className="text-xs text-muted-foreground">
            Proyecto: <span className="font-medium">{projectLabel}</span>
          </p>
        )}

        {hasValidDeadline && (
          <div className="flex items-center gap-1 text-xs text-muted-foreground">
            <Calendar className="h-3 w-3" aria-hidden="true" />
            <time dateTime={deadlineDate!.toISOString()}>
              Vence: {deadlineDate!.toLocaleDateString('es-ES')}
            </time>
          </div>
        )}

        <div className="flex gap-2 pt-2">
          <Button 
            variant="outline" 
            size="sm" 
            onClick={onEdit} 
            className="flex-1"
            aria-label={`Editar tarea: ${task.titulo}`}
          >
            <Edit className="h-3 w-3 mr-1" aria-hidden="true" />
            <span>Editar</span>
          </Button>
          
          <AlertDialog>
            <AlertDialogTrigger asChild>
              <Button 
                variant="destructive" 
                size="sm"
                aria-label={`Eliminar tarea: ${task.titulo}`}
              >
                <Trash2 className="h-3 w-3" aria-hidden="true" />
                <span className="sr-only">Eliminar</span>
              </Button>
            </AlertDialogTrigger>
            <AlertDialogContent>
              <AlertDialogHeader>
                <AlertDialogTitle>¿Eliminar tarea?</AlertDialogTitle>
                <AlertDialogDescription>
                  Esta acción no se puede deshacer. La tarea será eliminada permanentemente.
                </AlertDialogDescription>
              </AlertDialogHeader>
              <AlertDialogFooter>
                <AlertDialogCancel disabled={isDeleting}>Cancelar</AlertDialogCancel>
                <AlertDialogAction onClick={handleDelete} disabled={isDeleting}>
                  {isDeleting ? 'Eliminando...' : 'Eliminar'}
                </AlertDialogAction>
              </AlertDialogFooter>
            </AlertDialogContent>
          </AlertDialog>
        </div>
      </CardContent>
    </Card>
  );
};

export default TaskCard;
