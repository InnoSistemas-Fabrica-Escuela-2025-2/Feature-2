import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Calendar, Users, TrendingUp } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Progress } from '@/components/ui/progress';
import { useData } from '@/contexts/DataContext';
import CreateProjectDialog from '@/components/projects/CreateProjectDialog';

const Proyectos = () => {
  const { projects, isLoading, refreshData } = useData();
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Page Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Proyectos</h1>
          <p className="text-muted-foreground mt-2">
            Administra y da seguimiento a tus proyectos académicos
          </p>
        </div>
        <Button
          onClick={() => setIsCreateDialogOpen(true)}
          aria-label="Crear nuevo proyecto"
        >
          <Plus className="h-4 w-4 mr-2" aria-hidden="true" />
          Nuevo Proyecto
        </Button>
      </div>

      {/* Projects Grid */}
      {isLoading ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <p className="text-muted-foreground">Cargando proyectos...</p>
          </CardContent>
        </Card>
      ) : projects.length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <div className="h-24 w-24 rounded-full bg-muted flex items-center justify-center mb-4" aria-hidden="true">
              <TrendingUp className="h-12 w-12 text-muted-foreground" />
            </div>
            <h3 className="text-lg font-semibold mb-2">No tienes proyectos</h3>
            <p className="text-muted-foreground text-center mb-4">
              Comienza creando tu primer proyecto académico
            </p>
            <Button onClick={() => setIsCreateDialogOpen(true)}>
              <Plus className="h-4 w-4 mr-2" aria-hidden="true" />
              Crear Proyecto
            </Button>
          </CardContent>
        </Card>
      ) : (
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          {projects.map((project) => (
            <Card key={project.id} className="hover:shadow-lg transition-shadow">
              <CardHeader>
                <CardTitle className="line-clamp-1">
                  <Link
                    to={`/proyectos/${project.id}`}
                    className="hover:text-primary transition-colors"
                  >
                    {project.name || project.nombre}
                  </Link>
                </CardTitle>
                <CardDescription className="line-clamp-2">
                  {project.description || project.descripcion}
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                {/* Progress */}
                <div className="space-y-2">
                  <div className="flex items-center justify-between text-sm">
                    <span className="text-muted-foreground">Progreso</span>
                    <span className="font-medium">{project.progreso || 0}%</span>
                  </div>
                  <Progress
                    value={project.progreso || 0}
                    aria-label={`Progreso del proyecto: ${project.progreso || 0}%`}
                  />
                </div>

                {/* Metadata */}
                <div className="flex items-center justify-between text-sm text-muted-foreground">
                  <div className="flex items-center gap-1">
                    <Users className="h-4 w-4" aria-hidden="true" />
                    <span>{project.tasks?.length || 0} tareas</span>
                  </div>
                  <div className="flex items-center gap-1">
                    <Calendar className="h-4 w-4" aria-hidden="true" />
                    <time dateTime={project.deadline || project.fechaEntrega}>
                      {new Date(project.deadline || project.fechaEntrega).toLocaleDateString('es-ES', {
                        day: 'numeric',
                        month: 'short',
                        year: 'numeric',
                      })}
                    </time>
                  </div>
                </div>

                <Button asChild variant="outline" className="w-full">
                  <Link to={`/proyectos/${project.id}`}>
                    Ver Detalles
                  </Link>
                </Button>
              </CardContent>
            </Card>
          ))}
        </div>
      )}

      {/* Create Project Dialog */}
      <CreateProjectDialog
        open={isCreateDialogOpen}
        onOpenChange={setIsCreateDialogOpen}
        onProjectCreated={() => {
          refreshData();
        }}
      />
    </div>
  );
};

export default Proyectos;
