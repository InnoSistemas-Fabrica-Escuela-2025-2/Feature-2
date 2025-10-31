import { useAuth } from '@/contexts/AuthContext';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { mockProjects, mockTasks, mockNotifications } from '@/data/mockData';
import { CheckCircle2, Clock, AlertCircle, TrendingUp } from 'lucide-react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Progress } from '@/components/ui/progress';

const Dashboard = () => {
  const { user } = useAuth();

  // Filter data for current user
  const userProjects = mockProjects.filter(p => p.miembros.includes(user?.id || ''));
  const userTasks = mockTasks.filter(t => {
    const project = mockProjects.find(p => p.id === t.proyectoId);
    return project?.miembros.includes(user?.id || '');
  });
  
  const myAssignedTasks = userTasks.filter(t => t.responsableId === user?.id);
  const pendingTasks = myAssignedTasks.filter(t => t.estado === 'pendiente');
  const inProgressTasks = myAssignedTasks.filter(t => t.estado === 'en-progreso');
  const completedTasks = myAssignedTasks.filter(t => t.estado === 'finalizado');
  
  const unreadNotifications = mockNotifications.filter(
    n => n.userId === user?.id && !n.leida
  ).length;

  // Calculate upcoming deadlines (next 7 days)
  const today = new Date();
  const nextWeek = new Date(today.getTime() + 7 * 24 * 60 * 60 * 1000);
  const upcomingDeadlines = myAssignedTasks.filter(t => {
    const deadline = new Date(t.fechaEntrega);
    return deadline >= today && deadline <= nextWeek && t.estado !== 'finalizado';
  });

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Page Header - WCAG 2.4.2, 2.4.6 */}
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Dashboard</h1>
        <p className="text-muted-foreground mt-2" role="status" aria-live="polite">
          Bienvenido, {user?.nombre}. Aquí está el resumen de tus proyectos y tareas.
        </p>
      </div>

      {/* Stats Cards - WCAG 1.3.1, 4.1.2 */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4" role="group" aria-label="Resumen de estadísticas">
        <Card role="article" aria-labelledby="stat-projects">
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
              userProjects.map((project) => (
                <div key={project.id} className="space-y-2">
                  <div className="flex items-center justify-between">
                    <Link
                      to={`/proyectos/${project.id}`}
                      className="font-medium hover:text-primary transition-colors"
                    >
                      {project.nombre}
                    </Link>
                    <span className="text-sm text-muted-foreground">
                      {project.progreso}%
                    </span>
                  </div>
                  <Progress value={project.progreso} aria-label={`Progreso del proyecto ${project.nombre}: ${project.progreso}%`} />
                </div>
              ))
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
                    <p className="text-sm font-medium leading-none">{task.titulo}</p>
                    <p className="text-sm text-muted-foreground">
                      Vence: {new Date(task.fechaEntrega).toLocaleDateString('es-ES', {
                        day: 'numeric',
                        month: 'short',
                      })}
                    </p>
                  </div>
                  <span className={`status-badge status-${task.estado}`}>
                    {task.estado}
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
                const projectTasks = mockTasks.filter(t => t.proyectoId === project.id);
                const completedProjectTasks = projectTasks.filter(t => t.estado === 'finalizado');
                const progress = projectTasks.length > 0
                  ? Math.round((completedProjectTasks.length / projectTasks.length) * 100)
                  : 0;

                return (
                  <div key={project.id} className="space-y-2">
                    <div className="flex items-center justify-between">
                      <Link
                        to={`/proyectos/${project.id}`}
                        className="font-medium hover:text-primary transition-colors"
                      >
                        {project.nombre}
                      </Link>
                      <span className="text-sm text-muted-foreground">
                        {completedProjectTasks.length}/{projectTasks.length} tareas completadas
                      </span>
                    </div>
                    <Progress value={progress} aria-label={`Progreso del equipo en ${project.nombre}: ${progress}%`} />
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
