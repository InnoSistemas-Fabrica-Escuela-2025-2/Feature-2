import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useProject } from '@/context/ProjectContext';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { ConfirmationDialog } from '@/components/ConfirmationDialog';
import { ArrowLeft, Plus, X, Save } from 'lucide-react';
import { validateRequired, validateDate, validateArray } from '@/utils/validation';
import { Task, TaskFormData } from '@/types';
import { useToast } from '@/hooks/use-toast';

interface TaskFormProps {
  editingTask?: Task;
}

export const TaskForm: React.FC<TaskFormProps> = ({ editingTask }) => {
  const { projectId } = useParams<{ projectId: string }>();
  const { state, dispatch } = useProject();
  const navigate = useNavigate();
  const { toast } = useToast();
  
  const [formData, setFormData] = useState<TaskFormData>({
    title: '',
    description: '',
    deliveryDate: '',
    responsiblePeople: [],
  });
  
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [newPerson, setNewPerson] = useState('');
  const [showCancelDialog, setShowCancelDialog] = useState(false);
  const [hasChanges, setHasChanges] = useState(false);

  const selectedProject = state.selectedProject;

  useEffect(() => {
    if (editingTask) {
      const deliveryDateStr = typeof editingTask.deliveryDate === 'string' 
        ? editingTask.deliveryDate 
        : (editingTask.deliveryDate instanceof Date ? editingTask.deliveryDate.toISOString().split('T')[0] : '');
      
      setFormData({
        title: editingTask.title || '',
        description: editingTask.description || '',
        deliveryDate: deliveryDateStr,
        responsiblePeople: editingTask.responsiblePeople ? [...editingTask.responsiblePeople] : [],
      });
    }
  }, [editingTask]);

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};
    
    const titleError = validateRequired(formData.title);
    if (titleError) newErrors.title = titleError;
    
    const descriptionError = validateRequired(formData.description);
    if (descriptionError) newErrors.description = descriptionError;
    
    const dateError = validateDate(formData.deliveryDate);
    if (dateError) newErrors.deliveryDate = dateError;
    
    const peopleError = validateArray(formData.responsiblePeople);
    if (peopleError) newErrors.responsiblePeople = peopleError;
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleInputChange = (field: string, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    setHasChanges(true);
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: '' }));
    }
  };

  const handleAddPerson = () => {
    if (newPerson.trim() && !formData.responsiblePeople.includes(newPerson.trim())) {
      setFormData(prev => ({
        ...prev,
        responsiblePeople: [...prev.responsiblePeople, newPerson.trim()]
      }));
      setNewPerson('');
      setHasChanges(true);
      if (errors.responsiblePeople) {
        setErrors(prev => ({ ...prev, responsiblePeople: '' }));
      }
    }
  };

  const handleRemovePerson = (personToRemove: string) => {
    setFormData(prev => ({
      ...prev,
      responsiblePeople: prev.responsiblePeople.filter(person => person !== personToRemove)
    }));
    setHasChanges(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      toast({
        title: "Error en el formulario",
        description: "Por favor corrige los errores antes de continuar",
        variant: "destructive",
      });
      return;
    }

    if (!projectId) {
      toast({
        title: "Error",
        description: "No se encontró el proyecto",
        variant: "destructive",
      });
      return;
    }

    const now = new Date().toISOString();
    
    if (editingTask) {
      const updatedTask: Task = {
        ...editingTask,
        ...formData,
        updatedAt: now,
      };
      dispatch({ type: 'UPDATE_TASK', payload: updatedTask });
      toast({
        title: "Tarea actualizada",
        description: "La tarea se ha actualizado correctamente",
        variant: "default",
      });
    } else {
      const newTask: Task = {
        id: Date.now().toString(),
        proyectoId: projectId,
        titulo: formData.title,
        descripcion: formData.description,
        fechaEntrega: formData.deliveryDate,
        fechaCreacion: now,
        responsableId: 'current-user',
        estado: 'pendiente',
        title: formData.title,
        description: formData.description,
        deliveryDate: formData.deliveryDate,
        createdAt: now,
        updatedAt: now,
        status: 'pending',
        responsiblePeople: formData.responsiblePeople,
      };
      dispatch({ type: 'ADD_TASK', payload: newTask });
      toast({
        title: "Tarea creada",
        description: "La tarea se ha creado correctamente",
        variant: "default",
      });
    }
    
    navigate(`/projects/${projectId}/tasks`);
  };

  const handleCancel = () => {
    if (hasChanges) {
      setShowCancelDialog(true);
    } else {
      navigate(`/projects/${projectId}/tasks`);
    }
  };

  const confirmCancel = () => {
    setShowCancelDialog(false);
    navigate(`/projects/${projectId}/tasks`);
  };

  if (!selectedProject) {
    return <div>Cargando...</div>;
  }

  return (
    <div className="container mx-auto p-6 max-w-2xl">
      <div className="flex items-center gap-4 mb-6">
        <Button variant="ghost" onClick={handleCancel} className="gap-2">
          <ArrowLeft className="h-4 w-4" />
          Volver
        </Button>
        <div>
          <h1 className="text-2xl font-bold">
            {editingTask ? 'Modificar Tarea' : 'Crear Tarea Nueva'}
          </h1>
          <p className="text-muted-foreground">Proyecto: {selectedProject.name}</p>
        </div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Información de la Tarea</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-2">
              <Label htmlFor="title">Título de la Tarea *</Label>
              <Input
                id="title"
                value={formData.title}
                onChange={(e) => handleInputChange('title', e.target.value)}
                placeholder="Ingrese el título de la tarea"
                className={errors.title ? 'border-destructive' : ''}
              />
              {errors.title && (
                <p className="text-sm text-destructive">{errors.title}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="description">Descripción *</Label>
              <Textarea
                id="description"
                value={formData.description}
                onChange={(e) => handleInputChange('description', e.target.value)}
                placeholder="Describa la tarea a realizar"
                rows={4}
                className={errors.description ? 'border-destructive' : ''}
              />
              {errors.description && (
                <p className="text-sm text-destructive">{errors.description}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="deliveryDate">Fecha de Entrega *</Label>
              <Input
                id="deliveryDate"
                type="date"
                value={formData.deliveryDate}
                onChange={(e) => handleInputChange('deliveryDate', e.target.value)}
                className={errors.deliveryDate ? 'border-destructive' : ''}
              />
              {errors.deliveryDate && (
                <p className="text-sm text-destructive">{errors.deliveryDate}</p>
              )}
            </div>

            <div className="space-y-4">
              <Label>Personas Responsables *</Label>
              <div className="flex gap-2">
                <Input
                  value={newPerson}
                  onChange={(e) => setNewPerson(e.target.value)}
                  placeholder="Nombre de la persona responsable"
                  onKeyPress={(e) => e.key === 'Enter' && (e.preventDefault(), handleAddPerson())}
                />
                <Button type="button" onClick={handleAddPerson} variant="outline" size="icon">
                  <Plus className="h-4 w-4" />
                </Button>
              </div>
              
              {formData.responsiblePeople.length > 0 && (
                <div className="flex flex-wrap gap-2">
                  {formData.responsiblePeople.map((person, index) => (
                    <Badge key={index} variant="secondary" className="gap-1">
                      {person}
                      <button
                        type="button"
                        onClick={() => handleRemovePerson(person)}
                        className="ml-1 hover:text-destructive"
                      >
                        <X className="h-3 w-3" />
                      </button>
                    </Badge>
                  ))}
                </div>
              )}
              
              {errors.responsiblePeople && (
                <p className="text-sm text-destructive">{errors.responsiblePeople}</p>
              )}
            </div>

            <div className="flex gap-4 pt-4">
              <Button type="submit" className="gap-2 flex-1">
                <Save className="h-4 w-4" />
                {editingTask ? 'Actualizar Tarea' : 'Crear Tarea'}
              </Button>
              <Button type="button" variant="outline" onClick={handleCancel}>
                Cancelar
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>

      <ConfirmationDialog
        open={showCancelDialog}
        onOpenChange={setShowCancelDialog}
        title="¿Cancelar operación?"
        description="Se perderán todos los datos ingresados. ¿Está seguro que desea cancelar?"
        onConfirm={confirmCancel}
      />
    </div>
  );
};