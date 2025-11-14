import { useState, FormEvent } from 'react';
import { Task, TaskStatus } from '@/types';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog';
import { toast } from 'sonner';

interface EditTaskDialogProps {
  task: Task;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onTaskUpdated: (task: Task) => Promise<void>;
}

const EditTaskDialog = ({ task, open, onOpenChange, onTaskUpdated }: EditTaskDialogProps) => {
  const [titulo, setTitulo] = useState(task.titulo);
  const [descripcion, setDescripcion] = useState(task.descripcion);
  const [fechaEntrega, setFechaEntrega] = useState(
    new Date(task.fechaEntrega).toISOString().split('T')[0]
  );
  const [estado, setEstado] = useState<TaskStatus>(task.estado);
  const [showStatusConfirm, setShowStatusConfirm] = useState(false);
  const [pendingStatus, setPendingStatus] = useState<TaskStatus | null>(null);
  const [isSaving, setIsSaving] = useState(false);

  const handleStatusChange = (newStatus: TaskStatus) => {
    if (newStatus !== estado) {
      // Validate state transition rules
      if (estado === 'pendiente' && newStatus === 'finalizado') {
        toast.error('No se puede pasar directamente de Pendiente a Completada. Primero debe pasar por En Progreso.');
        return;
      }
      
      if (estado === 'finalizado' && newStatus === 'pendiente') {
        toast.error('No se puede pasar de Completada a Pendiente. Solo puede volver a En Progreso.');
        return;
      }
      
      setPendingStatus(newStatus);
      setShowStatusConfirm(true);
    }
  };

  const confirmStatusChange = () => {
    if (pendingStatus) {
      setEstado(pendingStatus);
      toast.success(`Estado cambiado a: ${getStatusLabel(pendingStatus)}`);
    }
    setShowStatusConfirm(false);
    setPendingStatus(null);
  };

  const getStatusLabel = (status: TaskStatus): string => {
    const labels = {
      'pendiente': 'Pendiente',
      'en-progreso': 'En Progreso',
      'finalizado': 'Finalizado'
    };
    return labels[status] || status;
  };

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    
    if (!titulo.trim()) {
      toast.error('El título es obligatorio');
      return;
    }

    if (!fechaEntrega) {
      toast.error('La fecha de entrega es obligatoria');
      return;
    }

    const updatedTask: Task = {
      ...task,
      titulo: titulo.trim(),
      descripcion: descripcion.trim(),
      fechaEntrega: new Date(fechaEntrega),
      estado
    };

    try {
      setIsSaving(true);
      await onTaskUpdated(updatedTask);
      toast.success('Tarea actualizada correctamente');
      onOpenChange(false);
    } catch (error: any) {
      console.error('Error al actualizar la tarea desde el formulario:', error);
      const message = error?.message || 'Error al actualizar la tarea';
      toast.error(message);
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <>
      <Dialog open={open} onOpenChange={onOpenChange}>
        <DialogContent className="sm:max-w-[500px]">
          <DialogHeader>
            <DialogTitle>Editar Tarea</DialogTitle>
          </DialogHeader>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="edit-titulo">
                Título{''}
                <span className="text-destructive ml-1" aria-label="campo obligatorio">*</span>
              </Label>
              <Input
                id="edit-titulo"
                value={titulo}
                onChange={(e) => setTitulo(e.target.value)}
                placeholder="Nombre de la tarea"
                required
                aria-required="true"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="edit-descripcion">Descripción</Label>
              <Textarea
                id="edit-descripcion"
                value={descripcion}
                onChange={(e) => setDescripcion(e.target.value)}
                placeholder="Describe los detalles de la tarea"
                rows={4}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="edit-fecha">
                Fecha{' '}de{' '}Entrega
                <span className="text-destructive ml-1" aria-label="campo obligatorio">*</span>
              </Label>
              <Input
                id="edit-fecha"
                type="date"
                value={fechaEntrega}
                onChange={(e) => setFechaEntrega(e.target.value)}
                required
                aria-required="true"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="edit-estado">
                Estado{''}
                <span className="text-destructive ml-1" aria-label="campo obligatorio">*</span>
              </Label>
              <Select value={estado} onValueChange={handleStatusChange}>
                <SelectTrigger id="edit-estado">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="pendiente">Pendiente</SelectItem>
                  <SelectItem value="en-progreso">En Progreso</SelectItem>
                  <SelectItem value="finalizado">Finalizado</SelectItem>
                </SelectContent>
              </Select>
              <p className="text-xs text-muted-foreground">
                Cambiar el estado requerirá confirmación
              </p>
            </div>

            <DialogFooter>
              <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
                Cancelar
              </Button>
              <Button type="submit" disabled={isSaving}>
                {isSaving ? 'Guardando...' : 'Guardar Cambios'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Status Change Confirmation Dialog */}
      <AlertDialog open={showStatusConfirm} onOpenChange={setShowStatusConfirm}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>¿Confirmar cambio de estado?</AlertDialogTitle>
            <AlertDialogDescription>
              Estás a punto de cambiar el estado de la tarea de{' '}
              <strong>{getStatusLabel(estado)}</strong> a{' '}
              <strong>{pendingStatus ? getStatusLabel(pendingStatus) : ''}</strong>.
              ¿Deseas continuar?
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel onClick={() => setPendingStatus(null)}>
              Cancelar
            </AlertDialogCancel>
            <AlertDialogAction onClick={confirmStatusChange}>
              Confirmar Cambio
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  );
};

export default EditTaskDialog;
