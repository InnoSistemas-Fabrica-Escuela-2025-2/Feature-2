import { useState } from 'react';
import { useProject } from '@/context/ProjectContext';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Plus, Calendar, Users, Target } from 'lucide-react';
import { formatDate } from '@/utils/validation';
import { useNavigate } from 'react-router-dom';

export const ProjectList: React.FC = () => {
  const { state, dispatch } = useProject();
  const navigate = useNavigate();
  const { projects } = state;

  const handleCreateProject = () => {
    navigate('/create-project');
  };

  const handleSelectProject = (projectId: string) => {
    const project = projects.find(p => p.id === projectId);
    if (project) {
      dispatch({ type: 'SELECT_PROJECT', payload: project });
      navigate(`/projects/${projectId}/tasks`);
    }
  };

  return (
    <div className="container mx-auto p-6 max-w-6xl">
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-3xl font-bold text-foreground">Gestión de Proyectos</h1>
          <p className="text-muted-foreground mt-2">
            Organiza y gestiona todos tus proyectos de manera eficiente
          </p>
        </div>
        <Button onClick={handleCreateProject} className="gap-2" size="lg">
          <Plus className="h-4 w-4" />
          Crear proyecto nuevo
        </Button>
      </div>

      {projects.length === 0 ? (
        <div className="text-center py-12">
          <div className="bg-muted rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
            <Target className="h-8 w-8 text-muted-foreground" />
          </div>
          <h3 className="text-lg font-semibold mb-2">No hay proyectos creados</h3>
          <p className="text-muted-foreground mb-6">
            Comienza creando tu primer proyecto para gestionar tareas y equipos
          </p>
          <Button onClick={handleCreateProject} variant="outline" className="gap-2">
            <Plus className="h-4 w-4" />
            Crear mi primer proyecto
          </Button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {projects.map((project) => (
            <Card
              key={project.id}
              className="cursor-pointer hover:shadow-lg transition-all duration-200 border hover:border-primary/20"
              onClick={() => handleSelectProject(project.id)}
            >
              <CardHeader className="pb-3">
                <CardTitle className="text-lg line-clamp-2">{project.name}</CardTitle>
              </CardHeader>
              <CardContent className="space-y-3">
                <p className="text-sm text-muted-foreground line-clamp-2">
                  {project.description}
                </p>
                
                <div className="space-y-2">
                  <div className="flex items-center gap-2 text-sm">
                    <Calendar className="h-4 w-4 text-primary" />
                    <span>{formatDate(project.deliveryDate)}</span>
                  </div>
                  
                  <div className="flex items-center gap-2 text-sm">
                    <Users className="h-4 w-4 text-primary" />
                    <span>{project.responsiblePeople.length} responsables</span>
                  </div>
                </div>

                <div className="flex flex-wrap gap-1 mt-3">
                  {project.responsiblePeople.slice(0, 3).map((person, index) => (
                    <Badge key={index} variant="secondary" className="text-xs">
                      {person}
                    </Badge>
                  ))}
                  {project.responsiblePeople.length > 3 && (
                    <Badge variant="secondary" className="text-xs">
                      +{project.responsiblePeople.length - 3}
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