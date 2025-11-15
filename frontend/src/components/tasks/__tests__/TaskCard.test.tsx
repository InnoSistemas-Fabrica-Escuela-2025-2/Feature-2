import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { vi } from "vitest";

const { mockToastSuccess, mockToastError, mockTasksDelete } = vi.hoisted(() => ({
  mockToastSuccess: vi.fn(),
  mockToastError: vi.fn(),
  mockTasksDelete: vi.fn(),
}));

vi.mock("sonner", () => ({
  toast: {
    success: mockToastSuccess,
    error: mockToastError,
  },
}));

vi.mock("@/lib/api", () => ({
  tasksApi: {
    delete: (taskId: number) => mockTasksDelete(taskId),
  },
}));
import TaskCard from "../TaskCard";
import type { Project, Task } from "@/types";

const toastSuccessMock = mockToastSuccess;
const toastErrorMock = mockToastError;

const baseTask: Task = {
  id: "task-1",
  proyectoId: "project-1",
  titulo: "Configurar entorno de despliegue",
  descripcion: "Documentar y preparar pipelines para despliegue continuo.",
  fechaCreacion: new Date("2025-01-01"),
  fechaEntrega: new Date("2025-11-01"),
  responsableId: "user-1",
  estado: "en-progreso",
  prioridad: "alta",
};

const mockProject: Project = {
  id: "project-1",
  nombre: "Portal Académico",
  descripcion: "Gestión de cursos y entregables.",
  objetivos: "Digitalizar la gestión académica.",
  fechaCreacion: new Date("2025-01-01"),
  fechaEntrega: new Date("2025-12-01"),
  creadorId: "user-1",
  equipoId: "team-1",
  miembros: ["user-1", "user-2"],
  progreso: 40,
};

describe("TaskCard", () => {
  beforeEach(() => {
    toastSuccessMock.mockReset();
    toastErrorMock.mockReset();
    mockTasksDelete.mockReset();
    mockTasksDelete.mockResolvedValue(undefined);
  });

  it("solicita confirmación antes de eliminar una tarea", async () => {
    const onEdit = vi.fn();
    const onDelete = vi.fn();

    render(<TaskCard task={baseTask} project={mockProject} onEdit={onEdit} onDelete={onDelete} />);

    await userEvent.click(
      screen.getByRole("button", {
        name: /eliminar tarea: configurar entorno de despliegue/i,
      })
    );

    expect(
      await screen.findByRole("heading", { name: /¿eliminar tarea\?/i })
    ).toBeInTheDocument();

    const confirmButton = await screen.findByRole("button", { name: /^eliminar$/i });
    await userEvent.click(confirmButton);

    expect(onDelete).toHaveBeenCalledWith("task-1");
    expect(mockTasksDelete).toHaveBeenCalledWith(NaN);
    expect(toastSuccessMock).toHaveBeenCalledWith("Tarea eliminada correctamente");
    expect(toastErrorMock).not.toHaveBeenCalled();
    expect(onEdit).not.toHaveBeenCalled();
  });
});
