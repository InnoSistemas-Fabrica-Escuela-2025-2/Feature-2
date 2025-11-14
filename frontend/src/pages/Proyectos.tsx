import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Calendar, TrendingUp, CheckCircle2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Progress } from '@/components/ui/progress';
import { useData } from '@/contexts/DataContext';
import CreateProjectDialog from '@/components/projects/CreateProjectDialog';

const Proyectos = () => {
  const { projects, isLoading, refreshData } = useData();
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);

  const getCompletedTasks = (project) => {
    if (project.completedTasks != null) return project.completedTasks;

    if (!Array.isArray(project.tasks)) return 0;

    return project.tasks.filter((task) => {
      const rawStatus = String(task.estado || task.state?.name || task.status || '').toLowerCase();
      return rawStatus.includes('final') || rawStatus.includes('complet');
    }).length;
  };

  const formatDeadline = (rawDeadline) => {
    if (!rawDeadline) return { label: 'Sin fecha', dateTime: '' };

    const date = new Date(rawDeadline);

    if (Number.isNaN(date.getTime())) {
      return { label: 'Sin fecha', dateTime: '' };
    }

    return {
      label: date.toLocaleDateString('es-ES', {
        day: 'numeric',
        month: 'short',
        year: 'numeric',
      }),
      dateTime: date.toISOString(),
    };
  };

  const getProgress = (completed, total) => {
    if (completed == null || total === 0) return 0;

    const percent = Math.round((completed / total) * 100);
    return Math.min(100, Math.max(0, percent));
  };

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
      {isLoading && (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <p className="text-muted-foreground">Cargando proyectos...</p>
          </CardContent>
        </Card>
      )}

      {!isLoading && projects.length === 0 && (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <div
              className="h-24 w-24 rounded-full bg-muted flex items-center justify-center mb-4"
              aria-hidden="true"
            >
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
      )}

      {!isLoading && projects.length > 0 && (
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          {projects.map((project) => {
            const totalTasks =
              project.totalTasks ??
              (Array.isArray(project.tasks) ? project.tasks.length : 0);

            const completedTasks = getCompletedTasks(project);

            const progressValue = getProgress(completedTasks, totalTasks);

            const { label: deadlineLabel, dateTime: deadlineDateTime } =
              formatDeadline(project.deadline || project.fechaEntrega);

            return (
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
                    {project.description ||
                      project.descripcion ||
                      'Sin descripción disponible'}
                  </CardDescription>
                </CardHeader>

                <CardContent className="space-y-4">
                  {/* Progress */}
                  <div className="space-y-2">
                    <div className="flex items-center justify-between text-sm">
                      <span className="text-muted-foreground">Progreso</span>
                      <span className="font-medium">{progressValue}%</span>
                    </div>

                    <Progress
                      value={progressValue}
                      aria-label={`Progreso del proyecto: ${progressValue}%`}
                    />
                  </div>

                  {/* Metadata */}
                  <div className="flex items-center justify-between text-sm text-muted-foreground">
                    <div className="flex items-center gap-1">
                      <CheckCircle2 className="h-4 w-4" aria-hidden="true" />
                      <span>
                        {completedTasks}/{totalTasks} tareas completadas
                      </span>
                    </div>

                    <div className="flex items-center gap-1">
                      <Calendar className="h-4 w-4" aria-hidden="true" />
                      <time dateTime={deadlineDateTime}>{deadlineLabel}</time>
                    </div>
                  </div>

                  <Button asChild variant="outline" className="w-full">
                    <Link to={`/proyectos/${project.id}`}>Ver Detalles</Link>
                  </Button>
                </CardContent>
              </Card>
            );
          })}
        </div>
      )}

      {/* Create Project Dialog */}
      <CreateProjectDialog
        open={isCreateDialogOpen}
        onOpenChange={setIsCreateDialogOpen}
        onProjectCreated={refreshData}
      />
    </div>
  );
};

export default Proyectos;
