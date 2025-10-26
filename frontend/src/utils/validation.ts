export const validateRequired = (value: string): string | null => {
  if (!value || value.trim() === '') {
    return 'Este campo es obligatorio';
  }
  return null;
};

export const validateDate = (dateString: string): string | null => {
  if (!dateString) {
    return 'La fecha es obligatoria';
  }

  // Verificar formato básico de fecha
  const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
  if (!dateRegex.test(dateString)) {
    return 'Formato de fecha inválido. Use YYYY-MM-DD';
  }

  const date = new Date(dateString);
  
  // Verificar si la fecha es válida
  if (isNaN(date.getTime())) {
    return 'Fecha inválida';
  }

  // Verificar que la fecha no sea anterior a hoy
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  date.setHours(0, 0, 0, 0);

  if (date < today) {
    return 'La fecha no puede ser anterior a la fecha actual';
  }

  return null;
};

export const validateArray = (array: string[]): string | null => {
  if (!array || array.length === 0) {
    return 'Debe asignar al menos una persona responsable';
  }
  return null;
};

export const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleDateString('es-ES', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
};

export const formatDateForInput = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toISOString().split('T')[0];
};