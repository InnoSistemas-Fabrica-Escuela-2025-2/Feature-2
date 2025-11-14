import React, { createContext, useContext, useReducer, ReactNode } from 'react';
import { Project, Task } from '@/types';

interface ProjectState {
  projects: Project[];
  tasks: Task[];
  selectedProject: Project | null;
  selectedTask: Task | null;
}

type ProjectAction =
  | { type: 'ADD_PROJECT'; payload: Project }
  | { type: 'UPDATE_PROJECT'; payload: Project }
  | { type: 'DELETE_PROJECT'; payload: string }
  | { type: 'SELECT_PROJECT'; payload: Project | null }
  | { type: 'ADD_TASK'; payload: Task }
  | { type: 'UPDATE_TASK'; payload: Task }
  | { type: 'DELETE_TASK'; payload: string }
  | { type: 'SELECT_TASK'; payload: Task | null };

const initialState: ProjectState = {
  projects: [],
  tasks: [],
  selectedProject: null,
  selectedTask: null,
};

const projectReducer = (state: ProjectState, action: ProjectAction): ProjectState => {
  switch (action.type) {
    case 'ADD_PROJECT':
      return {
        ...state,
        projects: [...state.projects, action.payload],
      };
    case 'UPDATE_PROJECT':
      return {
        ...state,
        projects: state.projects.map(project =>
          project.id === action.payload.id ? action.payload : project
        ),
      };
    case 'DELETE_PROJECT':
      return {
        ...state,
          projects: state.projects.filter(project => project.id !== action.payload),
          tasks: state.tasks.filter(task => String(task.proyectoId) !== String(action.payload)),
      };
    case 'SELECT_PROJECT':
      return {
        ...state,
        selectedProject: action.payload,
        selectedTask: null,
      };
    case 'ADD_TASK':
      return {
        ...state,
        tasks: [...state.tasks, action.payload],
      };
    case 'UPDATE_TASK':
      return {
        ...state,
        tasks: state.tasks.map(task =>
          task.id === action.payload.id ? action.payload : task
        ),
      };
    case 'DELETE_TASK':
      return {
        ...state,
        tasks: state.tasks.filter(task => task.id !== action.payload),
      };
    case 'SELECT_TASK':
      return {
        ...state,
        selectedTask: action.payload,
      };
    default:
      return state;
  }
};

interface ProjectContextType {
  state: ProjectState;
  dispatch: React.Dispatch<ProjectAction>;
  getTasksByProject: (projectId: string) => Task[];
}

const ProjectContext = createContext<ProjectContextType | undefined>(undefined);

export const ProjectProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [state, dispatch] = useReducer(projectReducer, initialState);

  const getTasksByProject = (projectId: string): Task[] => {
    return state.tasks.filter(task => String(task.proyectoId) === String(projectId));
  };

  return (
    <ProjectContext.Provider value={{ state, dispatch, getTasksByProject }}>
      {children}
    </ProjectContext.Provider>
  );
};

export const useProject = () => {
  const context = useContext(ProjectContext);
  if (context === undefined) {
    throw new Error('useProject must be used within a ProjectProvider');
  }
  return context;
};