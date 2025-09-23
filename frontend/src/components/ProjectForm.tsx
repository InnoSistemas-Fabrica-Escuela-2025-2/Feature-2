import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
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
import { Project, ProjectFormData } from '@/types';
import { useToast } from '@/hooks/use-toast';

interface ProjectFormProps {
  editingProject?: Project;
}

export const ProjectForm: React.FC<ProjectFormProps> = ({ editingProject }) => {
  const { dispatch } = useProject();
  const navigate = useNavigate();
  const { toast } = useToast();
  
  const [formData, setFormData] = useState<ProjectFormData>({
    name: '',
    description: '',
    objectives: '',
    deliveryDate: '',
    responsiblePeople: [],
  });
  
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [newPerson, setNewPerson] = useState('');
  const [showCancelDialog, setShowCancelDialog] = useState(false);
  const [hasChanges, setHasChanges] = useState(false);

  useEffect(() => {
    if (editingProject) {
      setFormData({
        name: editingProject.name,
        description: editingProject.description,
        objectives: editingProject.objectives,
        deliveryDate: editingProject.deliveryDate,
        responsiblePeople: [...editingProject.responsiblePeople],
      });
    }
  }, [editingProject]);

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};
    
    const nameError = validateRequired(formData.name);
    if (nameError) newErrors.name = nameError;
    
    const descriptionError = validateRequired(formData.description);
    if (descriptionError) newErrors.description = descriptionError;
    
    const objectivesError = validateRequired(formData.objectives);
    if (objectivesError) newErrors.objectives = objectivesError;
    
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

    const now = new Date().toISOString();
    
    if (editingProject) {
      const updatedProject: Project = {
        ...editingProject,
        ...formData,
        updatedAt: now,
      };
      dispatch({ type: 'UPDATE_PROJECT', payload: updatedProject });
      toast({
        title: "Proyecto actualizado",
        description: "El proyecto se ha actualizado correctamente",
        variant: "default",
      });
    } else {
      const newProject: Project = {
        id: Date.now().toString(),
        ...formData,
        createdAt: now,
        updatedAt: now,
      };
      dispatch({ type: 'ADD_PROJECT', payload: newProject });
      toast({
        title: "Proyecto creado",
        description: "El proyecto se ha creado correctamente",
        variant: "default",
      });
    }
    
    navigate('/');
  };

  const handleCancel = () => {
    if (hasChanges) {
      setShowCancelDialog(true);
    } else {
      navigate('/');
    }
  };

  const confirmCancel = () => {
    setShowCancelDialog(false);
    navigate('/');
  };

  return (
    <div className="container mx-auto p-6 max-w-2xl">
      <div className="flex items-center gap-4 mb-6">
        <Button variant="ghost" onClick={handleCancel} className="gap-2">
          <ArrowLeft className="h-4 w-4" />
          Volver
        </Button>
        <h1 className="text-2xl font-bold">
          {editingProject ? 'Modificar Proyecto' : 'Crear Proyecto Nuevo'}
        </h1>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Información del Proyecto</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-2">
              <Label htmlFor="name">Nombre del Proyecto *</Label>
              <Input
                id="name"
                value={formData.name}
                onChange={(e) => handleInputChange('name', e.target.value)}
                placeholder="Ingrese el nombre del proyecto"
                className={errors.name ? 'border-destructive' : ''}
              />
              {errors.name && (
                <p className="text-sm text-destructive">{errors.name}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="description">Descripción *</Label>
              <Textarea
                id="description"
                value={formData.description}
                onChange={(e) => handleInputChange('description', e.target.value)}
                placeholder="Describa el proyecto"
                rows={4}
                className={errors.description ? 'border-destructive' : ''}
              />
              {errors.description && (
                <p className="text-sm text-destructive">{errors.description}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="objectives">Objetivos *</Label>
              <Textarea
                id="objectives"
                value={formData.objectives}
                onChange={(e) => handleInputChange('objectives', e.target.value)}
                placeholder="Defina los objetivos del proyecto"
                rows={4}
                className={errors.objectives ? 'border-destructive' : ''}
              />
              {errors.objectives && (
                <p className="text-sm text-destructive">{errors.objectives}</p>
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
                {editingProject ? 'Actualizar Proyecto' : 'Crear Proyecto'}
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
        title="¿Cancelar creación?"
        description="Se perderán todos los datos ingresados. ¿Está seguro que desea cancelar?"
        onConfirm={confirmCancel}
      />
    </div>
  );
};