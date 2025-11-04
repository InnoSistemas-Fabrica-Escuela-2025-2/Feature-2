import { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { projectsApi } from '@/lib/api';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { useToast } from '@/hooks/use-toast';
import { ArrowLeft, Calendar, Users, Target, CheckCircle2, Clock, AlertCircle } from 'lucide-react';

export default function ProjectDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { toast } = useToast();
  const [project, setProject] = useState<any>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const loadProject = async () => {
      try {
        setIsLoading(true);
        const response = await projectsApi.getAll();
        const projects = response.data || [];
        const foundProject = projects.find((p: any) => p.id === parseInt(id || '0'));
        
        if (foundProject) {
          setProject(foundProject);
        } else {
          toast({
            title: 'Error',
            description: 'Proyecto no encontrado',
            variant: 'destructive'
          });
          navigate('/proyectos');
        }
      } catch (error) {
        console.error('Error loading project:', error);
        toast({
          title: 'Error',
          description: 'No se pudo cargar el proyecto',
          variant: 'destructive'
        });
        navigate('/proyectos');
      } finally {
        setIsLoading(false);
      }
    };

    if (id) {
      loadProject();
    }
  }, [id, navigate, toast]);

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

  const tasks = project.tasks || [];
  const completedTasks = tasks.filter((t: any) => t.status === 'finalizado').length;
  const inProgressTasks = tasks.filter((t: any) => t.status === 'en-progreso').length;
  const pendingTasks = tasks.filter((t: any) => t.status === 'pendiente').length;

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

  return (
    <div className="container mx-auto py-6 space-y-6">
      {/* Header */}
      <div className="flex items-center gap-4">
        <Button
          variant="ghost"
          size="icon"
          onClick={() => navigate('/proyectos')}
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
            <CardTitle className="text-sm font-medium">
              Total Tareas
            </CardTitle>
            <Target className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{tasks.length}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">
              Completadas
            </CardTitle>
            <CheckCircle2 className="h-4 w-4 text-green-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-600">{completedTasks}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">
              En Progreso
            </CardTitle>
            <Clock className="h-4 w-4 text-blue-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-blue-600">{inProgressTasks}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">
              Pendientes
            </CardTitle>
            <AlertCircle className="h-4 w-4 text-yellow-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-yellow-600">{pendingTasks}</div>
          </CardContent>
        </Card>
      </div>

      {/* Project Details */}
      <div className="grid gap-6 md:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>Información del Proyecto</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex items-start gap-3">
              <Target className="h-5 w-5 text-muted-foreground mt-0.5" />
              <div className="flex-1">
                <p className="font-medium">Objetivos</p>
                {project.objectives && project.objectives.length > 0 ? (
                  <ul className="text-sm text-muted-foreground space-y-1 mt-1">
                    {project.objectives.map((obj: any, index: number) => (
                      <li key={obj.id || index} className="flex items-start gap-2">
                        <span className="text-primary mt-1">•</span>
                        <span>{obj.description}</span>
                      </li>
                    ))}
                  </ul>
                ) : (
                  <p className="text-sm text-muted-foreground">No especificados</p>
                )}
              </div>
            </div>

            <div className="flex items-start gap-3">
              <Calendar className="h-5 w-5 text-muted-foreground mt-0.5" />
              <div className="flex-1">
                <p className="font-medium">Fecha de Entrega</p>
                <p className="text-sm text-muted-foreground">
                  {project.deadline ? new Date(project.deadline).toLocaleDateString('es-ES', {
                    day: 'numeric',
                    month: 'long',
                    year: 'numeric'
                  }) : 'No especificada'}
                </p>
              </div>
            </div>

            <div className="flex items-start gap-3">
              <Users className="h-5 w-5 text-muted-foreground mt-0.5" />
              <div className="flex-1">
                <p className="font-medium">Equipo</p>
                <p className="text-sm text-muted-foreground">
                  {project.team?.name || 'Sin equipo asignado'}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Progress Card */}
        <Card>
          <CardHeader>
            <CardTitle>Progreso del Proyecto</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div>
              <div className="flex justify-between mb-2">
                <span className="text-sm font-medium">Progreso General</span>
                <span className="text-sm font-medium">{project.progress || 0}%</span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2.5">
                <div 
                  className="bg-primary h-2.5 rounded-full transition-all" 
                  style={{ width: `${project.progress || 0}%` }}
                ></div>
              </div>
            </div>

            <div className="space-y-2">
              <div className="flex justify-between items-center">
                <span className="text-sm text-muted-foreground">Finalizadas</span>
                <span className="text-sm font-medium">{completedTasks} / {tasks.length}</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-sm text-muted-foreground">En progreso</span>
                <span className="text-sm font-medium">{inProgressTasks}</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-sm text-muted-foreground">Pendientes</span>
                <span className="text-sm font-medium">{pendingTasks}</span>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Tasks List */}
      <Card>
        <CardHeader>
          <CardTitle>Tareas del Proyecto</CardTitle>
        </CardHeader>
        <CardContent>
          {tasks.length === 0 ? (
            <p className="text-center text-muted-foreground py-8">
              No hay tareas en este proyecto
            </p>
          ) : (
            <div className="space-y-3">
              {tasks.map((task: any) => (
                <div 
                  key={task.id}
                  className="flex items-center justify-between p-4 border rounded-lg hover:bg-accent/50 transition-colors"
                >
                  <div className="flex-1">
                    <h4 className="font-medium">{task.name}</h4>
                    <p className="text-sm text-muted-foreground line-clamp-1">
                      {task.description}
                    </p>
                    <div className="flex gap-2 mt-2">
                      <Badge variant="outline" className={getStatusColor(task.status)}>
                        {getStatusLabel(task.status)}
                      </Badge>
                      {task.assignedTo && (
                        <Badge variant="outline">
                          <Users className="h-3 w-3 mr-1" />
                          Asignada
                        </Badge>
                      )}
                    </div>
                  </div>
                  <Button variant="ghost" size="sm" asChild>
                    <Link to={`/tareas/${task.id}`}>
                      Ver Detalle
                    </Link>
                  </Button>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
