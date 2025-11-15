import { ReactElement } from "react";
import { render, screen, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { vi } from "vitest";

import Tareas from "../Tareas";

type TaskTestShape = {
  id: string;
  proyectoId: string;
  titulo: string;
  descripcion: string;
  fechaEntrega: Date;
  fechaCreacion: Date;
  responsableId: string;
  responsable?: string;
  estado: "pendiente" | "en-progreso" | "finalizado";
  prioridad: string;
  estadoId?: number;
};

const {
  mockUseData,
  mockRefreshData,
  mockTasksUpdate,
  mockCreateTask,
  mockEditTask,
} = vi.hoisted(() => {
  return {
    mockUseData: vi.fn(),
    mockRefreshData: vi.fn().mockResolvedValue(undefined),
    mockTasksUpdate: vi.fn().mockResolvedValue(undefined),
    mockCreateTask: vi.fn(),
    mockEditTask: vi.fn(),
  };
});

const renderWithUser = (ui: ReactElement) => {
  const user = userEvent.setup();
  return { user, ...render(ui) };
};

vi.mock("@/contexts/DataContext", () => ({
  useData: () => mockUseData(),
}));

vi.mock("@/lib/api", () => ({
  tasksApi: {
    update: (payload: unknown) => mockTasksUpdate(payload),
  },
}));

vi.mock("@/components/tasks/CreateTaskDialog", () => ({
  default: ({ open, onOpenChange, onTaskCreated }: any) => {
    if (!open) {
      return null;
    }

    return (
      <div data-testid="mock-create-task-dialog">
        <p>Crear tarea</p>
        <button
          type="button"
          onClick={() => {
            mockCreateTask();
            onTaskCreated({ id: "temp-1" });
          }}
        >
          confirmar creación
        </button>
        <button type="button" onClick={() => onOpenChange(false)}>
          cerrar creación
        </button>
      </div>
    );
  },
}));

vi.mock("@/components/tasks/EditTaskDialog", () => ({
  default: ({ task, open, onOpenChange, onTaskUpdated }: any) => {
    if (!open) {
      return null;
    }

    return (
      <div data-testid="mock-edit-task-dialog">
        <p>Editar tarea: {task.titulo}</p>
        <button
          type="button"
          onClick={() => {
            mockEditTask(task);
            onTaskUpdated(task);
          }}
        >
          guardar cambios
        </button>
        <button type="button" onClick={() => onOpenChange(false)}>
          cerrar edición
        </button>
      </div>
    );
  },
}));

vi.mock("@/components/tasks/TaskCard", () => ({
  default: ({ task, onEdit }: any) => (
    <article data-testid={`task-card-${task.id}`}>
      <h3>{task.titulo}</h3>
      <button type="button" onClick={() => onEdit()}>
        editar {task.titulo}
      </button>
    </article>
  ),
}));

vi.mock("@/components/tasks/TaskKanban", () => ({
  TaskKanban: () => <div data-testid="task-kanban" />,
}));

describe("Tareas", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    mockRefreshData.mockResolvedValue(undefined);
    mockTasksUpdate.mockResolvedValue(undefined);
  });

  it("abre el diálogo de creación y refresca los datos después de crear una tarea", async () => {
    mockUseData.mockReturnValue({
      tasks: [],
      projects: [],
      states: [],
      isLoading: false,
      refreshData: mockRefreshData,
    });

    const { user } = renderWithUser(<Tareas />);

    await user.click(
      screen.getByRole("button", { name: /nueva tarea/i })
    );

    const dialog = screen.getByTestId("mock-create-task-dialog");
    expect(dialog).toBeInTheDocument();

    await user.click(
      within(dialog).getByRole("button", { name: /confirmar creación/i })
    );

    expect(mockCreateTask).toHaveBeenCalledTimes(1);
    expect(mockRefreshData).toHaveBeenCalledTimes(1);

    await user.click(
      within(dialog).getByRole("button", { name: /cerrar creación/i })
    );

    expect(screen.queryByTestId("mock-create-task-dialog")).not.toBeInTheDocument();
  });

  it("muestra un mensaje cuando no existen tareas finalizadas", async () => {
    const sampleTasks: TaskTestShape[] = [
      {
        id: "1",
        proyectoId: "alpha",
        titulo: "Diseñar wireframes",
        descripcion: "Preparar prototipo",
        fechaEntrega: new Date("2025-05-10"),
        fechaCreacion: new Date("2025-04-01"),
        responsableId: "user-1",
        estado: "pendiente",
        prioridad: "alta",
      },
    ];

    mockUseData.mockReturnValue({
      tasks: sampleTasks,
      projects: [],
      states: [],
      isLoading: false,
      refreshData: mockRefreshData,
    });

    const { user } = renderWithUser(<Tareas />);

    await user.click(
      screen.getByRole("tab", { name: /finalizadas/i })
    );

    expect(
      screen.getByText(/no tienes tareas finalizadas/i)
    ).toBeInTheDocument();
  });
  });
