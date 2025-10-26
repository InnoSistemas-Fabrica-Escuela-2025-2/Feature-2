import { useParams } from 'react-router-dom';
import { useProject } from '@/context/ProjectContext';
import { TaskForm } from '@/components/TaskForm';

const EditTask = () => {
  const { taskId } = useParams<{ taskId: string }>();
  const { state } = useProject();
  
  const task = state.tasks.find(t => t.id === taskId);

  return (
    <div className="min-h-screen bg-background">
      <TaskForm editingTask={task} />
    </div>
  );
};

export default EditTask;