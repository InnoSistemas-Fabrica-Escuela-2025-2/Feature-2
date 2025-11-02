import { useState, FormEvent } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { Task } from '@/types';
import { mockProjects, mockUsers } from '@/data/mockData';
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
  const [titulo, setTitulo] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [fechaEntrega, setFechaEntrega] = useState('');
  const [proyectoId, setProyectoId] = useState('');
  const [responsableId, setResponsableId] = useState(user?.id || '');
  const [error, setError] = useState('');

  const userProjects = mockProjects.filter(p => p.miembros.includes(user?.id || ''));

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');

    if (!titulo.trim() || !descripcion.trim() || !fechaEntrega || !proyectoId) {
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

    const newTask: Task = {
      id: `t${Date.now()}`,
      proyectoId,
      titulo: titulo.trim(),
      descripcion: descripcion.trim(),
      fechaEntrega: selectedDate,
      fechaCreacion: new Date(),
      responsableId,
      estado: 'pendiente',
    };

    onTaskCreated(newTask);
    toast.success('Tarea creada exitosamente');
    onOpenChange(false);
    setTitulo('');
    setDescripcion('');
    setFechaEntrega('');
    setProyectoId('');
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
              Título
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
              Descripción
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
              Proyecto
              <span className="text-destructive ml-1" aria-label="campo obligatorio">*</span>
            </Label>
            <Select value={proyectoId} onValueChange={setProyectoId} required>
              <SelectTrigger id="proyecto" aria-required="true">
                <SelectValue placeholder="Selecciona un proyecto" />
              </SelectTrigger>
              <SelectContent>
                {userProjects.map(p => (
                  <SelectItem key={p.id} value={p.id}>{p.nombre}</SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
          <div className="space-y-2">
            <Label htmlFor="fecha">
              Fecha de Entrega
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
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>Cancelar</Button>
            <Button type="submit">Crear Tarea</Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default CreateTaskDialog;
