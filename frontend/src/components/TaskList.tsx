import { useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useProject } from '@/context/ProjectContext';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { ArrowLeft, Plus, Calendar, Users, ClipboardList } from 'lucide-react';
import { formatDate } from '@/utils/validation';

export const TaskList: React.FC = () => {
  const { projectId } = useParams<{ projectId: string }>();
  const { state, dispatch, getTasksByProject } = useProject();
  const navigate = useNavigate();
  
  const { selectedProject } = state;
  const tasks = projectId ? getTasksByProject(projectId) : [];

  useEffect(() => {
    if (projectId && !selectedProject) {
      const project = state.projects.find(p => p.id === projectId);
      if (project) {
        dispatch({ type: 'SELECT_PROJECT', payload: project });
      } else {
        navigate('/');
      }
    }
  }, [projectId, selectedProject, state.projects, dispatch, navigate]);

  const handleCreateTask = () => {
    navigate(`/projects/${projectId}/create-task`);
  };

  const handleSelectTask = (taskId: string) => {
    const task = tasks.find(t => t.id === taskId);
    if (task) {
      dispatch({ type: 'SELECT_TASK', payload: task });
      navigate(`/projects/${projectId}/tasks/${taskId}`);
    }
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'completed':
        return <Badge className="bg-accent text-accent-foreground">Completada</Badge>;
      case 'in-progress':
        return <Badge className="bg-warning text-warning-foreground">En Progreso</Badge>;
      default:
        return <Badge variant="secondary">Pendiente</Badge>;
    }
  };

  if (!selectedProject) {
    return <div>Cargando...</div>;
  }

  return (
    <div className="container mx-auto p-6 max-w-6xl">
      <div className="flex items-center gap-4 mb-6">
        <Button variant="ghost" onClick={() => navigate('/')} className="gap-2">
          <ArrowLeft className="h-4 w-4" />
          Volver a Proyectos
        </Button>
      </div>

      <div className="mb-8">
        <h1 className="text-3xl font-bold text-foreground mb-2">{selectedProject.name}</h1>
        <p className="text-muted-foreground mb-4">{selectedProject.description}</p>
        <div className="flex items-center gap-6 text-sm text-muted-foreground">
          <div className="flex items-center gap-2">
            <Calendar className="h-4 w-4" />
            <span>Entrega: {formatDate(selectedProject.deliveryDate)}</span>
          </div>
          <div className="flex items-center gap-2">
            <Users className="h-4 w-4" />
            <span>{selectedProject.responsiblePeople.length} responsables</span>
          </div>
        </div>
      </div>

      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-semibold">Tareas del Proyecto</h2>
        <Button onClick={handleCreateTask} className="gap-2">
          <Plus className="h-4 w-4" />
          Crear tarea nueva
        </Button>
      </div>

      {tasks.length === 0 ? (
        <div className="text-center py-12">
          <div className="bg-muted rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
            <ClipboardList className="h-8 w-8 text-muted-foreground" />
          </div>
          <h3 className="text-lg font-semibold mb-2">No hay tareas creadas</h3>
          <p className="text-muted-foreground mb-6">
            Comienza agregando tareas para organizar el trabajo de este proyecto
          </p>
          <Button onClick={handleCreateTask} variant="outline" className="gap-2">
            <Plus className="h-4 w-4" />
            Crear primera tarea
          </Button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {tasks.map((task) => (
            <Card
              key={task.id}
              className="cursor-pointer hover:shadow-lg transition-all duration-200 border hover:border-primary/20"
              onClick={() => handleSelectTask(task.id)}
            >
              <CardHeader className="pb-3">
                <div className="flex justify-between items-start gap-2">
                  <CardTitle className="text-lg line-clamp-2 flex-1">{task.title}</CardTitle>
                  {getStatusBadge(task.status)}
                </div>
              </CardHeader>
              <CardContent className="space-y-3">
                <p className="text-sm text-muted-foreground line-clamp-3">
                  {task.description}
                </p>
                
                <div className="space-y-2">
                  <div className="flex items-center gap-2 text-sm">
                    <Calendar className="h-4 w-4 text-primary" />
                    <span>{formatDate(task.deliveryDate)}</span>
                  </div>
                  
                  <div className="flex items-center gap-2 text-sm">
                    <Users className="h-4 w-4 text-primary" />
                    <span>{task.responsiblePeople.length} responsables</span>
                  </div>
                </div>

                <div className="flex flex-wrap gap-1 mt-3">
                  {task.responsiblePeople.slice(0, 3).map((person, index) => (
                    <Badge key={index} variant="secondary" className="text-xs">
                      {person}
                    </Badge>
                  ))}
                  {task.responsiblePeople.length > 3 && (
                    <Badge variant="secondary" className="text-xs">
                      +{task.responsiblePeople.length - 3}
                    </Badge>
                  )}
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};