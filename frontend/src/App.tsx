import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { ProjectProvider } from "@/context/ProjectContext";
import Index from "./pages/Index";
import CreateProject from "./pages/CreateProject";
import TasksPage from "./pages/TasksPage";
import CreateTask from "./pages/CreateTask";
import EditTask from "./pages/EditTask";
import TaskDetail from "./pages/TaskDetail";
import NotFound from "./pages/NotFound";

const queryClient = new QueryClient();

const App = () => (
  <QueryClientProvider client={queryClient}>
    <ProjectProvider>
      <TooltipProvider>
        <Toaster />
        <Sonner />
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<Index />} />
            <Route path="/create-project" element={<CreateProject />} />
            <Route path="/projects/:projectId/tasks" element={<TasksPage />} />
            <Route path="/projects/:projectId/create-task" element={<CreateTask />} />
            <Route path="/projects/:projectId/tasks/:taskId" element={<TaskDetail />} />
            <Route path="/projects/:projectId/tasks/:taskId/edit" element={<EditTask />} />
            {/* ADD ALL CUSTOM ROUTES ABOVE THE CATCH-ALL "*" ROUTE */}
            <Route path="*" element={<NotFound />} />
          </Routes>
        </BrowserRouter>
      </TooltipProvider>
    </ProjectProvider>
  </QueryClientProvider>
);

export default App;
