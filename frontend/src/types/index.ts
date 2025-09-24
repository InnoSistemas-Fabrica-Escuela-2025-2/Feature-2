export interface Project {
  id: string;
  name: string;
  description: string;
  objectives: string;
  deliveryDate: string;
  responsiblePeople: string[];
  createdAt: string;
  updatedAt: string;
}

export interface Task {
  id: string;
  projectId: string;
  title: string;
  description: string;
  deliveryDate: string;
  responsiblePeople: string[];
  status: 'pending' | 'in-progress' | 'completed';
  createdAt: string;
  updatedAt: string;
}

export interface ProjectFormData {
  name: string;
  description: string;
  objectives: string;
  deliveryDate: string;
  responsiblePeople: string[];
}

export interface TaskFormData {
  title: string;
  description: string;
  deliveryDate: string;
  responsiblePeople: string[];
}