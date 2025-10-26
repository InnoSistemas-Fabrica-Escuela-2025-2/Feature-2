import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Calendar, Users, TrendingUp } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Progress } from '@/components/ui/progress';
import { useAuth } from '@/contexts/AuthContext';
import CreateProjectDialog from '@/components/projects/CreateProjectDialog';
import api from '@/lib/api';
import { useToast } from '@/hooks/use-toast';

const Proyectos = () => {
  const { user } = useAuth();
  const { toast } = useToast();
  const [projects, setProjects] = useState<any[]>([]);
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [loading, setLoading] = useState(true);

  // Cargar proyectos desde el backend
  useEffect(() => {
    const fetchProjects = async () => {
      try {
        setLoading(true);
        const response = await api.get('/project/listAll');
        setProjects(response.data);
      } catch (error) {
        console.error('Error al cargar proyectos:', error);
        toast({
          title: "Error",
          description: "No se pudieron cargar los proyectos",
          variant: "destructive",
        });
      } finally {
        setLoading(false);
      }
    };

    fetchProjects();
  }, [toast]);

  // Por ahora mostrar todos los proyectos (hasta que implementes autenticación y miembros)
  const userProjects = projects;

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
      {loading ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <p className="text-muted-foreground">Cargando proyectos...</p>
          </CardContent>
        </Card>
      ) : userProjects.length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <div className="h-24 w-24 rounded-full bg-muted flex items-center justify-center mb-4" aria-hidden="true">
              <TrendingUp className="h-12 w-12 text-muted-foreground" />
            </div>
            <h3 className="text-lg font-semibold mb-2">No hay proyectos</h3>
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
          {userProjects.map((project) => (
            <Card key={project.id} className="hover:shadow-lg transition-shadow">
              <CardHeader>
                <CardTitle className="line-clamp-1">
                  <Link
                    to={`/proyectos/${project.id}`}
                    className="hover:text-primary transition-colors"
                  >
                    {project.name}
                  </Link>
                </CardTitle>
                <CardDescription className="line-clamp-2">
                  {project.description}
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                {/* Progress */}
                <div className="space-y-2">
                  <div className="flex items-center justify-between text-sm">
                    <span className="text-muted-foreground">Tareas</span>
                    <span className="font-medium">{project.tasks?.length || 0}</span>
                  </div>
                </div>

                {/* Metadata */}
                <div className="flex items-center justify-between text-sm text-muted-foreground">
                  <div className="flex items-center gap-1">
                    <Users className="h-4 w-4" aria-hidden="true" />
                    <span>{project.objectives?.length || 0} objetivos</span>
                  </div>
                  <div className="flex items-center gap-1">
                    <Calendar className="h-4 w-4" aria-hidden="true" />
                    <time dateTime={project.deadline}>
                      {new Date(project.deadline).toLocaleDateString('es-ES', {
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
        onProjectCreated={async (newProject) => {
          // Recargar proyectos del backend
          try {
            const response = await api.get('/project/listAll');
            setProjects(response.data);
          } catch (error) {
            console.error('Error al recargar proyectos:', error);
          }
        }}
      />
    </div>
  );
};

export default Proyectos;
