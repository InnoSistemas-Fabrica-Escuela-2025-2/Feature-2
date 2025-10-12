import { useState, FormEvent } from 'react';
import { Task, TaskStatus } from '@/types';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { toast } from 'sonner';

interface EditTaskDialogProps {
  task: Task;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onTaskUpdated: (task: Task) => void;
}

const EditTaskDialog = ({ task, open, onOpenChange, onTaskUpdated }: EditTaskDialogProps) => {
  const [titulo, setTitulo] = useState(task.titulo);
  const [descripcion, setDescripcion] = useState(task.descripcion);
  const [fechaEntrega, setFechaEntrega] = useState(task.fechaEntrega.toISOString().split('T')[0]);
  const [estado, setEstado] = useState<TaskStatus>(task.estado);

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    const updatedTask: Task = {
      ...task,
      titulo,
      descripcion,
      fechaEntrega: new Date(fechaEntrega),
      estado,
    };
    onTaskUpdated(updatedTask);
    toast.success('Tarea actualizada');
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Editar Tarea</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="edit-titulo">Título</Label>
            <Input id="edit-titulo" value={titulo} onChange={(e) => setTitulo(e.target.value)} required />
          </div>
          <div className="space-y-2">
            <Label htmlFor="edit-descripcion">Descripción</Label>
            <Textarea id="edit-descripcion" value={descripcion} onChange={(e) => setDescripcion(e.target.value)} required />
          </div>
          <div className="space-y-2">
            <Label htmlFor="edit-estado">Estado</Label>
            <Select value={estado} onValueChange={(v) => setEstado(v as TaskStatus)}>
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="pendiente">Pendiente</SelectItem>
                <SelectItem value="en-progreso">En Progreso</SelectItem>
                <SelectItem value="finalizado">Finalizado</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div className="space-y-2">
            <Label htmlFor="edit-fecha">Fecha de Entrega</Label>
            <Input id="edit-fecha" type="date" value={fechaEntrega} onChange={(e) => setFechaEntrega(e.target.value)} required />
          </div>
          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>Cancelar</Button>
            <Button type="submit">Guardar Cambios</Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default EditTaskDialog;
