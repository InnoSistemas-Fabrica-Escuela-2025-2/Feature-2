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
            <Alert variant="destructive">
              <AlertCircle className="h-4 w-4" />
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}
          <div className="space-y-2">
            <Label htmlFor="titulo">Título *</Label>
            <Input id="titulo" value={titulo} onChange={(e) => setTitulo(e.target.value)} required />
          </div>
          <div className="space-y-2">
            <Label htmlFor="descripcion">Descripción *</Label>
            <Textarea id="descripcion" value={descripcion} onChange={(e) => setDescripcion(e.target.value)} required />
          </div>
          <div className="space-y-2">
            <Label htmlFor="proyecto">Proyecto *</Label>
            <Select value={proyectoId} onValueChange={setProyectoId} required>
              <SelectTrigger>
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
            <Label htmlFor="fecha">Fecha de Entrega *</Label>
            <Input id="fecha" type="date" value={fechaEntrega} onChange={(e) => setFechaEntrega(e.target.value)} min={new Date().toISOString().split('T')[0]} required />
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
