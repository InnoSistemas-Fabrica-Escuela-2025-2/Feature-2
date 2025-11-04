import { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { ArrowLeft, Calendar, Users, Target, FileText } from 'lucide-react';
import { useToast } from '@/hooks/use-toast';
import { useData } from '@/contexts/DataContext';

export default function TaskDetail() {
  const { taskId } = useParams<{ taskId: string }>();
  const navigate = useNavigate();
  const { toast } = useToast();
  const { projects, isLoading } = useData();
  const [task, setTask] = useState<any>(null);
  const [project, setProject] = useState<any>(null);

  useEffect(() => {
    if (!taskId || isLoading) return;

    // Buscar la tarea en todos los proyectos
    let foundTask = null;
    let foundProject = null;

    for (const proj of projects) {
      const taskInProject = (proj.tasks || []).find((t: any) => t.id === parseInt(taskId));
      if (taskInProject) {
        foundTask = taskInProject;
        foundProject = proj;
        break;
      }
    }

    if (foundTask && foundProject) {
      setTask(foundTask);
      setProject(foundProject);
    } else {
      toast({
        title: 'Error',
        description: 'Tarea no encontrada',
        variant: 'destructive'
      });
      navigate('/tareas');
    }
  }, [taskId, projects, isLoading, navigate, toast]);

  if (isLoading || !task) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-muted-foreground">Cargando tarea...</p>
        </div>
      </div>
    );
  }

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

  const formatDate = (dateString: string) => {
    if (!dateString) return 'No especificada';
    return new Date(dateString).toLocaleDateString('es-ES', {
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    });
  };

  return (
    <div className="container mx-auto py-6 space-y-6">
      {/* Header */}
      <div className="flex items-center gap-4">
        <Button 
          variant="ghost" 
          onClick={() => navigate(-1)} 
          className="gap-2"
        >
          <ArrowLeft className="h-4 w-4" />
          Volver
        </Button>
      </div>

      {/* Task Main Info */}
      <Card>
        <CardHeader>
          <div className="flex items-start justify-between">
            <div className="flex-1">
              <CardTitle className="text-2xl mb-2">{task.title || task.name}</CardTitle>
              <Badge className={getStatusColor(task.status)}>
                {getStatusLabel(task.status)}
              </Badge>
            </div>
          </div>
        </CardHeader>
        <CardContent className="space-y-6">
          {/* Description */}
          <div className="flex items-start gap-3">
            <FileText className="h-5 w-5 text-muted-foreground mt-0.5" />
            <div className="flex-1">
              <p className="font-medium mb-1">Descripción</p>
              <p className="text-sm text-muted-foreground">
                {task.description || 'Sin descripción'}
              </p>
            </div>
          </div>

          {/* Project Info */}
          {project && (
            <div className="flex items-start gap-3">
              <Target className="h-5 w-5 text-muted-foreground mt-0.5" />
              <div className="flex-1">
                <p className="font-medium mb-1">Proyecto</p>
                <Link 
                  to={`/proyectos/${project.id}`}
                  className="text-sm text-primary hover:underline"
                >
                  {project.name}
                </Link>
              </div>
            </div>
          )}

          {/* Due Date */}
          <div className="flex items-start gap-3">
            <Calendar className="h-5 w-5 text-muted-foreground mt-0.5" />
            <div className="flex-1">
              <p className="font-medium mb-1">Fecha de Entrega</p>
              <p className="text-sm text-muted-foreground">
                {formatDate(task.deadline)}
              </p>
            </div>
          </div>

          {/* Responsible Person */}
          {task.responsible && (
            <div className="flex items-start gap-3">
              <Users className="h-5 w-5 text-muted-foreground mt-0.5" />
              <div className="flex-1">
                <p className="font-medium mb-1">Responsable</p>
                <p className="text-sm text-muted-foreground">
                  {task.responsible.name || task.responsible.email || 'No asignado'}
                </p>
              </div>
            </div>
          )}
        </CardContent>
      </Card>

      {/* Additional Information */}
      <Card>
        <CardHeader>
          <CardTitle>Información Adicional</CardTitle>
        </CardHeader>
        <CardContent className="space-y-3">
          <div className="flex justify-between py-2 border-b">
            <span className="text-sm font-medium">ID de la Tarea</span>
            <span className="text-sm text-muted-foreground">{task.id}</span>
          </div>
          <div className="flex justify-between py-2 border-b">
            <span className="text-sm font-medium">Fecha de Creación</span>
            <span className="text-sm text-muted-foreground">
              {formatDate(task.createdAt || task.creationDate)}
            </span>
          </div>
          {task.state && (
            <div className="flex justify-between py-2">
              <span className="text-sm font-medium">Estado del Sistema</span>
              <span className="text-sm text-muted-foreground">{task.state.name}</span>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
