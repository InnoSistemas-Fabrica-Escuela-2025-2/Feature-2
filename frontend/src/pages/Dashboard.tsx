import { useAuth } from '@/contexts/AuthContext';
import { useData } from '@/contexts/DataContext';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { CheckCircle2, Clock, AlertCircle, TrendingUp } from 'lucide-react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Progress } from '@/components/ui/progress';
import { ErrorState } from '@/components/ErrorState';

const Dashboard = () => {
  const { user } = useAuth();
  const { projects, tasks, isLoading, error, refreshData } = useData();

  // Filter data - show all for now
  const userProjects = projects;
  const userTasks = tasks;
  
  const normalizeTaskStatus = (task: any): 'pendiente' | 'en-progreso' | 'finalizado' => {
    const estado = typeof task.estado === 'string' ? task.estado : '';
    if (estado === 'pendiente' || estado === 'en-progreso' || estado === 'finalizado') {
      return estado;
    }

    const fallback = String(task.state?.name || '').toLowerCase();
    if (fallback.includes('prog')) return 'en-progreso';
    if (fallback.includes('final') || fallback.includes('complet')) return 'finalizado';
    return 'pendiente';
  };

  const myAssignedTasks = userTasks;
  const pendingTasks = myAssignedTasks.filter((t: any) => normalizeTaskStatus(t) === 'pendiente');
  const inProgressTasks = myAssignedTasks.filter((t: any) => normalizeTaskStatus(t) === 'en-progreso');
  const completedTasks = myAssignedTasks.filter((t: any) => normalizeTaskStatus(t) === 'finalizado');
  
  const unreadNotifications = 0; // Por ahora no hay notificaciones en el backend

  // Calculate upcoming deadlines (next 7 days)
  const today = new Date();
  const nextWeek = new Date(today.getTime() + 7 * 24 * 60 * 60 * 1000);
  const upcomingDeadlines = myAssignedTasks.filter(t => {
    const deadlineDate = t.deadline || t.fechaEntrega;
    if (!deadlineDate) return false;
    const deadline = new Date(deadlineDate);
    const status = normalizeTaskStatus(t);
    const isNotCompleted = status !== 'finalizado';
    return deadline >= today && deadline <= nextWeek && isNotCompleted;
  });

  // Show error state if data loading failed
  if (error && !isLoading) {
    return (
      <ErrorState
        message="No fue posible cargar la información del proyecto. Intente nuevamente."
        onRetry={refreshData}
        isRetrying={isLoading}
      />
    );
  }

  // Show loading state
  if (isLoading && projects.length === 0) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-muted-foreground">Cargando dashboard...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Page Header - WCAG 2.4.2, 2.4.6 */}
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Dashboard</h1>
        <p className="text-muted-foreground mt-2">
          Bienvenido, {user?.nombre}. Aquí está el resumen de tus proyectos y tareas.
        </p>
      </div>

      {/* Stats Cards - WCAG 1.3.1, 4.1.2 */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle id="stat-projects" className="text-sm font-medium">Proyectos Activos</CardTitle>
            <TrendingUp className="h-4 w-4 text-muted-foreground" aria-hidden="true" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold" aria-label={`${userProjects.length} proyectos activos`}>
              {userProjects.length}
            </div>
            <p className="text-xs text-muted-foreground mt-1">
              {userTasks.length} tareas totales
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Tareas Pendientes</CardTitle>
            <AlertCircle className="h-4 w-4 text-warning" aria-hidden="true" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{pendingTasks.length}</div>
            <p className="text-xs text-muted-foreground mt-1">
              {upcomingDeadlines.length} próximas a vencer
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">En Progreso</CardTitle>
            <Clock className="h-4 w-4 text-in-progress" aria-hidden="true" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{inProgressTasks.length}</div>
            <p className="text-xs text-muted-foreground mt-1">
              Tareas activas
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Completadas</CardTitle>
            <CheckCircle2 className="h-4 w-4 text-success" aria-hidden="true" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{completedTasks.length}</div>
            <p className="text-xs text-muted-foreground mt-1">
              {myAssignedTasks.length > 0 
                ? `${Math.round((completedTasks.length / myAssignedTasks.length) * 100)}% completado`
                : '0% completado'}
            </p>
          </CardContent>
        </Card>
      </div>

      <div className="grid gap-6 md:grid-cols-2">
        {/* Recent Projects */}
        <Card>
          <CardHeader>
            <CardTitle>Proyectos Recientes</CardTitle>
            <CardDescription>
              Tus proyectos activos con el progreso actual
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            {userProjects.length === 0 ? (
              <p className="text-sm text-muted-foreground text-center py-4">
                No tienes proyectos activos
              </p>
            ) : (
              <>
                {userProjects.slice(0, 5).map((project) => {
                  const progressValue = project.progreso ?? 0;
                  const clampedProgress = Math.min(100, Math.max(0, Math.round(progressValue)));

                  return (
                    <div key={project.id} className="space-y-2">
                      <div className="flex items-center justify-between">
                        <Link
                          to={`/proyectos/${project.id}`}
                          className="font-medium hover:text-primary transition-colors"
                        >
                          {project.name || project.nombre}
                        </Link>
                        <span className="text-sm text-muted-foreground">
                          {clampedProgress}%
                        </span>
                      </div>
                      <Progress
                        value={clampedProgress}
                        aria-label={`Progreso del proyecto ${project.name || project.nombre}: ${clampedProgress}%`}
                      />
                    </div>
                  );
                })}
                {userProjects.length > 5 && (
                  <p className="text-sm text-muted-foreground text-center pt-2">
                    +{userProjects.length - 5} proyectos más
                  </p>
                )}
              </>
            )}
            <Button asChild className="w-full mt-4">
              <Link to="/proyectos">Ver Todos los Proyectos</Link>
            </Button>
          </CardContent>
        </Card>

        {/* Upcoming Deadlines */}
        <Card>
          <CardHeader>
            <CardTitle>Próximas Entregas</CardTitle>
            <CardDescription>
              Tareas que vencen en los próximos 7 días
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            {upcomingDeadlines.length === 0 ? (
              <p className="text-sm text-muted-foreground text-center py-4">
                No tienes entregas próximas
              </p>
            ) : (
              upcomingDeadlines.slice(0, 5).map((task) => (
                <div
                  key={task.id}
                  className="flex items-start justify-between gap-4 p-3 rounded-lg border hover:bg-accent transition-colors"
                >
                  <div className="flex-1 space-y-1">
                    <p className="text-sm font-medium leading-none">{task.title || task.titulo}</p>
                    <p className="text-sm text-muted-foreground">
                      Vence: {new Date(task.deadline || task.fechaEntrega).toLocaleDateString('es-ES', {
                        day: 'numeric',
                        month: 'short',
                      })}
                    </p>
                  </div>
                  <span className={`status-badge status-${normalizeTaskStatus(task)}`}>
                    {task.estadoLabel || task.estado}
                  </span>
                </div>
              ))
            )}
            <Button asChild variant="outline" className="w-full mt-4">
              <Link to="/tareas">Ver Todas las Tareas</Link>
            </Button>
          </CardContent>
        </Card>
      </div>

      {/* Quick Stats for Professors */}
      {user?.rol === 'profesor' && (
        <Card>
          <CardHeader>
            <CardTitle>Vista de Profesor</CardTitle>
            <CardDescription>
              Resumen del progreso de los equipos
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {userProjects.map((project) => {
                const fallbackProjectTasks = tasks.filter(t => t.proyectoId === project.id);
                const totalTasks = project.totalTasks ?? fallbackProjectTasks.length;
                const completedProjectTasks = project.completedTasks ?? fallbackProjectTasks.filter(t => normalizeTaskStatus(t) === 'finalizado').length;
                const progress = totalTasks > 0
                  ? Math.round((completedProjectTasks / totalTasks) * 100)
                  : 0;
                const clampedProgress = Math.min(100, Math.max(0, progress));

                return (
                  <div key={project.id} className="space-y-2">
                    <div className="flex items-center justify-between">
                      <Link
                        to={`/proyectos/${project.id}`}
                        className="font-medium hover:text-primary transition-colors"
                      >
                        {project.name || project.nombre}
                      </Link>
                      <span className="text-sm text-muted-foreground">
                        {completedProjectTasks}/{totalTasks} tareas completadas
                      </span>
                    </div>
                    <Progress
                      value={clampedProgress}
                      aria-label={`Progreso del equipo en ${project.name || project.nombre}: ${clampedProgress}%`}
                    />
                  </div>
                );
              })}
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
};

export default Dashboard;
