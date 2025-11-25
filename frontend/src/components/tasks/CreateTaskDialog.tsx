import { useState, FormEvent, useEffect } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { useData } from '@/contexts/DataContext';
import { Task } from '@/types';
import { tasksApi, teamsApi, notificationsApi } from '@/lib/api';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { AlertCircle } from 'lucide-react';
import { toast } from 'sonner';

interface CreateTaskDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onTaskCreated: (task: Task) => void;
}

const CreateTaskDialog = ({ open, onOpenChange, onTaskCreated }: CreateTaskDialogProps) => {
  const { user } = useAuth();
  const { projects, refreshData } = useData();
  const [titulo, setTitulo] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [fechaEntrega, setFechaEntrega] = useState('');
  const [proyectoId, setProyectoId] = useState('');
  const [responsableId, setResponsableId] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [teamMembers, setTeamMembers] = useState<string[]>([]);
  const [loadingMembers, setLoadingMembers] = useState(false);

  // Show all projects for now (no filtering)
  const userProjects = projects;

  // Get project members based on selected project
  const selectedProject = projects.find(p => p.id.toString() === proyectoId);

  // Load team members when project is selected
  useEffect(() => {
    const loadTeamMembers = async () => {
      if (!selectedProject?.team?.id) {
        setTeamMembers([]);
        return;
      }
      
      try {
        setLoadingMembers(true);
        const response = await teamsApi.getStudentsEmails(selectedProject.team.id);
        setTeamMembers(response.data || []);
      } catch (error) {
        console.error('Error loading team members:', error);
        setTeamMembers([]);
      } finally {
        setLoadingMembers(false);
      }
    };
    
    loadTeamMembers();
  }, [selectedProject?.team?.id]);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');

    if (!titulo.trim() || !descripcion.trim() || !fechaEntrega || !proyectoId || !responsableId.trim()) {
      setError('Todos los campos son obligatorios');
      return;
    }

    const selectedDate = new Date(fechaEntrega);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    if (selectedDate < today) {
      setError('La fecha no puede ser anterior a hoy');
      return;
    }

    try {
      setIsLoading(true);
      
      // Prepare task data for backend
      const taskData = {
        title: titulo.trim(),
        description: descripcion.trim(),
        deadline: selectedDate.toISOString(),
        responsible_email: responsableId.trim(),
        project: { id: Number.parseInt(proyectoId) }
      };

      console.log('Creating task:', taskData);
      
      const response = await tasksApi.create(taskData);
      console.log('Task created successfully:', response.data);
      
      // Refresh data from backend
      await refreshData();
      
      toast.success('Tarea creada exitosamente');
      onTaskCreated(response.data);
      onOpenChange(false);
      
      // Enviar notificación por email al responsable (no bloquear flujo si falla)
      try {
        const emailEvent = {
          to: taskData.responsible_email,
          subject: `Nueva tarea asignada: ${taskData.title}`,
          body: `Se te asignó la tarea \"${taskData.title}\" para el proyecto. Fecha límite: ${new Date(taskData.deadline).toLocaleDateString()}`
        };
        notificationsApi.send(emailEvent).catch(err => console.error('Error enviando notificación:', err));
      } catch (notifyErr) {
        console.error('No se pudo iniciar el envío de notificación:', notifyErr);
      }

      // Reset form
      setTitulo('');
      setDescripcion('');
      setFechaEntrega('');
      setProyectoId('');
      setResponsableId('');
    } catch (error: any) {
      console.error('Error creating task:', error);
      setError(error.response?.data?.message || 'Error al crear la tarea');
      toast.error('Error al crear la tarea');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Crear Nueva Tarea</DialogTitle>
          <DialogDescription>Agrega una tarea al proyecto</DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          {error && (
            <Alert variant="destructive" role="alert" aria-live="assertive" aria-atomic="true">
              <AlertCircle className="h-4 w-4" aria-hidden="true" />
              <AlertDescription id="create-task-error">{error}</AlertDescription>
            </Alert>
          )}
          <div className="space-y-2">
            <Label htmlFor="titulo">
              Título{''}
              <span className="text-destructive ml-1" aria-label="campo obligatorio">*</span>
            </Label>
            <Input
              id="titulo"
              value={titulo}
              onChange={(e) => setTitulo(e.target.value)}
              required
              aria-required="true"
              aria-invalid={error && !titulo.trim() ? 'true' : 'false'}
              aria-describedby={error ? "create-task-error" : undefined}
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="descripcion">
              Descripción{''}
              <span className="text-destructive ml-1" aria-label="campo obligatorio">*</span>
            </Label>
            <Textarea
              id="descripcion"
              value={descripcion}
              onChange={(e) => setDescripcion(e.target.value)}
              required
              aria-required="true"
              aria-invalid={error && !descripcion.trim() ? 'true' : 'false'}
              aria-describedby={error ? "create-task-error" : undefined}
              rows={4}
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="proyecto">
              Proyecto{''}
              <span className="text-destructive ml-1" aria-label="campo obligatorio">*</span>
            </Label>
            <Select value={proyectoId} onValueChange={setProyectoId} required>
              <SelectTrigger id="proyecto" aria-required="true">
                <SelectValue placeholder="Selecciona un proyecto" />
              </SelectTrigger>
              <SelectContent>
                {userProjects.length === 0 ? (
                  <SelectItem value="no-projects" disabled>No hay proyectos disponibles</SelectItem>
                ) : (
                  userProjects.map(p => (
                    <SelectItem key={p.id} value={p.id.toString()}>
                      {p.name || p.nombre}
                    </SelectItem>
                  ))
                )}
              </SelectContent>
            </Select>
          </div>
          <div className="space-y-2">
            <Label htmlFor="responsable">
              Responsable
              <span className="text-destructive ml-1" aria-label="campo obligatorio">*</span>
            </Label>
            <Select value={responsableId} onValueChange={setResponsableId} disabled={!proyectoId || loadingMembers}>
              <SelectTrigger id="responsable">
                <SelectValue placeholder={
                  !proyectoId ? "Primero selecciona un proyecto" :
                  loadingMembers ? "Cargando miembros..." :
                  teamMembers.length === 0 ? "No hay miembros disponibles" :
                  "Selecciona un responsable"
                } />
              </SelectTrigger>
              <SelectContent>
                {teamMembers.length === 0 ? (
                  <SelectItem value="sin-miembros" disabled>
                    No hay miembros en el equipo
                  </SelectItem>
                ) : (
                  teamMembers.map((email: string) => (
                    <SelectItem key={email} value={email}>
                      {email}
                    </SelectItem>
                  ))
                )}
              </SelectContent>
            </Select>
          </div>
          <div className="space-y-2">
            <Label htmlFor="fecha">
              Fecha{' '}de{' '}Entrega
              <span className="text-destructive ml-1" aria-label="campo obligatorio">*</span>
            </Label>
            <Input
              id="fecha"
              type="date"
              value={fechaEntrega}
              onChange={(e) => setFechaEntrega(e.target.value)}
              min={new Date().toISOString().split('T')[0]}
              required
              aria-required="true"
              aria-invalid={error && !fechaEntrega ? 'true' : 'false'}
              aria-describedby={error ? "create-task-error" : undefined}
            />
          </div>
          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)} disabled={isLoading}>
              Cancelar
            </Button>
            <Button type="submit" disabled={isLoading}>
              {isLoading ? 'Creando...' : 'Crear Tarea'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default CreateTaskDialog;
