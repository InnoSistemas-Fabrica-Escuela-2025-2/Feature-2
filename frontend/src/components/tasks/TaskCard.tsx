import { Task, Project } from '@/types';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Calendar, Edit, Trash2 } from 'lucide-react';
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

interface TaskCardProps {
  task: Task;
  project?: Project;
  onEdit: () => void;
  onDelete: (taskId: string) => void;
}

const TaskCard = ({ task, project, onEdit, onDelete }: TaskCardProps) => {
  const handleDelete = () => {
    onDelete(task.id);
    toast.success('Tarea eliminada correctamente');
  };

  return (
    <Card className="hover:shadow-lg transition-shadow" role="article" aria-label={`Tarea: ${task.titulo}`}>
      <CardHeader>
        <div className="flex items-start justify-between gap-2">
          <CardTitle className="text-base line-clamp-2">{task.titulo}</CardTitle>
          <span 
            className={`status-badge status-${task.estado} shrink-0`}
            role="status"
            aria-label={`Estado de la tarea: ${task.estado}`}
          >
            {task.estado}
          </span>
        </div>
      </CardHeader>
      <CardContent className="space-y-3">
        <p className="text-sm text-muted-foreground line-clamp-2">{task.descripcion}</p>
        
        {project && (
          <p className="text-xs text-muted-foreground">
            Proyecto: <span className="font-medium">{project.nombre}</span>
          </p>
        )}

        <div className="flex items-center gap-1 text-xs text-muted-foreground">
          <Calendar className="h-3 w-3" aria-hidden="true" />
          <time dateTime={task.fechaEntrega.toISOString()}>
            Vence: {new Date(task.fechaEntrega).toLocaleDateString('es-ES')}
          </time>
        </div>

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
                <AlertDialogCancel>Cancelar</AlertDialogCancel>
                <AlertDialogAction onClick={handleDelete}>Eliminar</AlertDialogAction>
              </AlertDialogFooter>
            </AlertDialogContent>
          </AlertDialog>
        </div>
      </CardContent>
    </Card>
  );
};

export default TaskCard;
