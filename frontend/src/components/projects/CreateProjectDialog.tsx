import { useState, FormEvent } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { Project } from '@/types';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
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
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { AlertCircle } from 'lucide-react';
import { toast } from 'sonner';

interface CreateProjectDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onProjectCreated: (project: Project) => void;
}

const CreateProjectDialog = ({
  open,
  onOpenChange,
  onProjectCreated,
}: CreateProjectDialogProps) => {
  const { user } = useAuth();
  const [nombre, setNombre] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [objetivos, setObjetivos] = useState('');
  const [fechaEntrega, setFechaEntrega] = useState('');
  const [error, setError] = useState('');
  const [showCancelDialog, setShowCancelDialog] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const resetForm = () => {
    setNombre('');
    setDescripcion('');
    setObjetivos('');
    setFechaEntrega('');
    setError('');
  };

  const handleCancel = () => {
    if (nombre || descripcion || objetivos || fechaEntrega) {
      setShowCancelDialog(true);
    } else {
      onOpenChange(false);
      resetForm();
    }
  };

  const confirmCancel = () => {
    setShowCancelDialog(false);
    onOpenChange(false);
    resetForm();
  };

  const validateForm = (): boolean => {
    if (!nombre.trim()) {
      setError('El nombre del proyecto es obligatorio');
      return false;
    }

    if (!descripcion.trim()) {
      setError('La descripción es obligatoria');
      return false;
    }

    if (!objetivos.trim()) {
      setError('Los objetivos son obligatorios');
      return false;
    }

    if (!fechaEntrega) {
      setError('La fecha de entrega es obligatoria');
      return false;
    }

    // Validate date format
    const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
    if (!dateRegex.test(fechaEntrega)) {
      setError('Formato de fecha incorrecto. Use AAAA-MM-DD');
      return false;
    }

    // Validate date is not in the past
    const selectedDate = new Date(fechaEntrega);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    if (selectedDate < today) {
      setError('La fecha de entrega no puede ser anterior a hoy');
      return false;
    }

    return true;
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');

    if (!validateForm()) {
      return;
    }

    setIsSubmitting(true);

    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 500));

    const newProject: Project = {
      id: `p${Date.now()}`,
      nombre: nombre.trim(),
      descripcion: descripcion.trim(),
      objetivos: objetivos.trim(),
      fechaEntrega: new Date(fechaEntrega),
      fechaCreacion: new Date(),
      creadorId: user?.id || '',
      miembros: [user?.id || ''],
      progreso: 0,
    };

    onProjectCreated(newProject);
    toast.success('Proyecto creado exitosamente');
    onOpenChange(false);
    resetForm();
    setIsSubmitting(false);
  };

  return (
    <>
      <Dialog open={open} onOpenChange={onOpenChange}>
        <DialogContent className="sm:max-w-[600px]" aria-describedby="dialog-description">
          <DialogHeader>
            <DialogTitle>Crear Nuevo Proyecto</DialogTitle>
            <DialogDescription id="dialog-description">
              Completa la información para crear un nuevo proyecto académico
            </DialogDescription>
          </DialogHeader>

          <form onSubmit={handleSubmit} className="space-y-4" noValidate>
            {error && (
              <Alert variant="destructive" role="alert" aria-live="polite">
                <AlertCircle className="h-4 w-4" aria-hidden="true" />
                <AlertDescription>{error}</AlertDescription>
              </Alert>
            )}

            <div className="space-y-2">
              <Label htmlFor="nombre">
                Nombre del Proyecto
                <span className="text-destructive ml-1" aria-label="campo obligatorio">*</span>
              </Label>
              <Input
                id="nombre"
                value={nombre}
                onChange={(e) => setNombre(e.target.value)}
                placeholder="Ej: Sistema de Gestión de Biblioteca"
                required
                aria-required="true"
                aria-invalid={error && !nombre ? 'true' : 'false'}
                disabled={isSubmitting}
                maxLength={100}
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
                placeholder="Describe brevemente el proyecto..."
                required
                aria-required="true"
                aria-invalid={error && !descripcion ? 'true' : 'false'}
                disabled={isSubmitting}
                rows={3}
                maxLength={500}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="objetivos">
                Objetivos
                <span className="text-destructive ml-1" aria-label="campo obligatorio">*</span>
              </Label>
              <Textarea
                id="objetivos"
                value={objetivos}
                onChange={(e) => setObjetivos(e.target.value)}
                placeholder="Define los objetivos principales del proyecto..."
                required
                aria-required="true"
                aria-invalid={error && !objetivos ? 'true' : 'false'}
                disabled={isSubmitting}
                rows={3}
                maxLength={500}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="fechaEntrega">
                Fecha de Entrega
                <span className="text-destructive ml-1" aria-label="campo obligatorio">*</span>
              </Label>
              <Input
                id="fechaEntrega"
                type="date"
                value={fechaEntrega}
                onChange={(e) => setFechaEntrega(e.target.value)}
                required
                aria-required="true"
                aria-invalid={error && !fechaEntrega ? 'true' : 'false'}
                disabled={isSubmitting}
                min={new Date().toISOString().split('T')[0]}
              />
            </div>

            <DialogFooter className="gap-2 sm:gap-0">
              <Button
                type="button"
                variant="outline"
                onClick={handleCancel}
                disabled={isSubmitting}
              >
                Cancelar
              </Button>
              <Button type="submit" disabled={isSubmitting}>
                {isSubmitting ? 'Creando...' : 'Crear Proyecto'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Cancel Confirmation Dialog */}
      <AlertDialog open={showCancelDialog} onOpenChange={setShowCancelDialog}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>¿Cancelar creación?</AlertDialogTitle>
            <AlertDialogDescription>
              Los datos ingresados se perderán. ¿Estás seguro de que deseas cancelar?
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>No, continuar</AlertDialogCancel>
            <AlertDialogAction onClick={confirmCancel}>
              Sí, cancelar
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  );
};

export default CreateProjectDialog;
