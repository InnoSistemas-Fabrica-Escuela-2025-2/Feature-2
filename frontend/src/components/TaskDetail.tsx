import { useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useProject } from '@/context/ProjectContext';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { ArrowLeft, Edit, Calendar, Users, Clock } from 'lucide-react';
import { formatDate } from '@/utils/validation';
import { useToast } from '@/hooks/use-toast';

export const TaskDetail: React.FC = () => {
  const { projectId, taskId } = useParams<{ projectId: string; taskId: string }>();
  const { state, dispatch, getTasksByProject } = useProject();
  const navigate = useNavigate();
  const { toast } = useToast();
  
  const { selectedProject, selectedTask } = state;
  const tasks = projectId ? getTasksByProject(projectId) : [];

  useEffect(() => {
    if (projectId && taskId && (!selectedTask || selectedTask.id !== taskId)) {
      const task = tasks.find(t => t.id === taskId);
      if (task) {
        dispatch({ type: 'SELECT_TASK', payload: task });
      } else {
        navigate(`/projects/${projectId}/tasks`);
      }
    }
  }, [projectId, taskId, selectedTask, tasks, dispatch, navigate]);

  const handleEditTask = () => {
    navigate(`/projects/${projectId}/tasks/${taskId}/edit`);
  };

  const handleStatusChange = (newStatus: string) => {
    if (selectedTask) {
      const updatedTask = {
        ...selectedTask,
        status: newStatus as 'pending' | 'in-progress' | 'completed',
        updatedAt: new Date().toISOString(),
      };
      dispatch({ type: 'UPDATE_TASK', payload: updatedTask });
      toast({
        title: "Estado actualizado",
        description: "El estado de la tarea se ha actualizado correctamente",
        variant: "default",
      });
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

  if (!selectedProject || !selectedTask) {
    return <div>Cargando...</div>;
  }

  return (
    <div className="container mx-auto p-6 max-w-4xl">
      <div className="flex items-center gap-4 mb-6">
        <Button 
          variant="ghost" 
          onClick={() => navigate(`/projects/${projectId}/tasks`)} 
          className="gap-2"
        >
          <ArrowLeft className="h-4 w-4" />
          Volver a Tareas
        </Button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Información principal de la tarea */}
        <div className="lg:col-span-2">
          <Card>
            <CardHeader>
              <div className="flex justify-between items-start gap-4">
                <div className="flex-1">
                  <CardTitle className="text-2xl mb-2">{selectedTask.title}</CardTitle>
                  <p className="text-muted-foreground">
                    Proyecto: {selectedProject.name}
                  </p>
                </div>
                <div className="flex gap-2">
                  {getStatusBadge(selectedTask.status)}
                  <Button onClick={handleEditTask} variant="outline" className="gap-2">
                    <Edit className="h-4 w-4" />
                    Modificar tarea
                  </Button>
                </div>
              </div>
            </CardHeader>
            <CardContent className="space-y-6">
              <div>
                <h3 className="font-semibold mb-2">Descripción</h3>
                <p className="text-muted-foreground whitespace-pre-wrap">
                  {selectedTask.description}
                </p>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="flex items-center gap-3">
                  <Calendar className="h-5 w-5 text-primary" />
                  <div>
                    <p className="text-sm font-medium">Fecha de Entrega</p>
                    <p className="text-sm text-muted-foreground">
                      {formatDate(selectedTask.deliveryDate)}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-3">
                  <Clock className="h-5 w-5 text-primary" />
                  <div>
                    <p className="text-sm font-medium">Creada</p>
                    <p className="text-sm text-muted-foreground">
                      {formatDate(selectedTask.createdAt)}
                    </p>
                  </div>
                </div>
              </div>

              <div>
                <div className="flex items-center gap-3 mb-3">
                  <Users className="h-5 w-5 text-primary" />
                  <h3 className="font-semibold">Personas Responsables</h3>
                </div>
                <div className="flex flex-wrap gap-2">
                  {selectedTask.responsiblePeople.map((person, index) => (
                    <Badge key={index} variant="secondary" className="text-sm">
                      {person}
                    </Badge>
                  ))}
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Panel lateral de estado y acciones */}
        <div className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Estado de la Tarea</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <label className="text-sm font-medium mb-2 block">Estado Actual</label>
                <Select 
                  value={selectedTask.status} 
                  onValueChange={handleStatusChange}
                >
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="pending">Pendiente</SelectItem>
                    <SelectItem value="in-progress">En Progreso</SelectItem>
                    <SelectItem value="completed">Completada</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Información del Proyecto</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              <div>
                <p className="text-sm font-medium">Proyecto</p>
                <p className="text-sm text-muted-foreground">{selectedProject.name}</p>
              </div>
              <div>
                <p className="text-sm font-medium">Entrega del Proyecto</p>
                <p className="text-sm text-muted-foreground">
                  {formatDate(selectedProject.deliveryDate)}
                </p>
              </div>
              <div>
                <p className="text-sm font-medium">Responsables del Proyecto</p>
                <div className="flex flex-wrap gap-1 mt-1">
                  {selectedProject.responsiblePeople.slice(0, 2).map((person, index) => (
                    <Badge key={index} variant="outline" className="text-xs">
                      {person}
                    </Badge>
                  ))}
                  {selectedProject.responsiblePeople.length > 2 && (
                    <Badge variant="outline" className="text-xs">
                      +{selectedProject.responsiblePeople.length - 2}
                    </Badge>
                  )}
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
};