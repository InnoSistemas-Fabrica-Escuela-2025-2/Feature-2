import { render, screen, waitFor, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { vi } from "vitest";

vi.mock("sonner", () => ({
  toast: {
    success: vi.fn(),
    error: vi.fn(),
  },
}));

beforeAll(() => {
  Object.defineProperty(Element.prototype, "hasPointerCapture", {
    configurable: true,
    writable: true,
    value: () => false,
  });

  Object.defineProperty(Element.prototype, "releasePointerCapture", {
    configurable: true,
    writable: true,
    value: () => {},
  });

  if (!Element.prototype.scrollIntoView) {
    Object.defineProperty(Element.prototype, "scrollIntoView", {
      configurable: true,
      writable: true,
      value: () => {},
    });
  }
});

import { toast } from "sonner";
import EditTaskDialog from "../EditTaskDialog";
import type { Task, TaskStatus } from "@/types";

const toastSuccessMock = toast.success as unknown as ReturnType<typeof vi.fn>;
const toastErrorMock = toast.error as unknown as ReturnType<typeof vi.fn>;

const statusLabels: Record<TaskStatus, string> = {
  "pendiente": "Pendiente",
  "en-progreso": "En Progreso",
  "finalizado": "Finalizado",
};

const baseTask: Task = {
  id: "task-01",
  proyectoId: "project-01",
  titulo: "Diseñar flujo de autenticación",
  descripcion: "Validar credenciales y definir tokens de acceso.",
  fechaEntrega: new Date("2025-02-01"),
  fechaCreacion: new Date("2025-01-01"),
  responsableId: "user-123",
  estado: "pendiente",
  prioridad: "media",
};

const renderDialog = (overrides: Partial<Task> = {}) => {
  const task = { ...baseTask, ...overrides };
  const onOpenChange = vi.fn();
  const onTaskUpdated = vi.fn();

  render(
    <EditTaskDialog task={task} open onOpenChange={onOpenChange} onTaskUpdated={onTaskUpdated} />
  );

  return { onOpenChange, onTaskUpdated };
};

describe("EditTaskDialog", () => {
  beforeEach(() => {
    toastSuccessMock.mockReset();
    toastErrorMock.mockReset();
  });

  it("solicita confirmación antes de actualizar el estado y persiste los cambios", async () => {
    const user = userEvent.setup();
    const { onTaskUpdated } = renderDialog();

    const statusSelect = screen.getByRole("combobox", { name: /estado/i });
    await user.click(statusSelect);
    await user.click(await screen.findByRole("option", { name: /en progreso/i }));

    const alertDialog = await screen.findByRole("alertdialog");
    expect(alertDialog).toBeInTheDocument();

    const confirmButton = within(alertDialog).getByRole("button", { name: /confirmar cambio/i });
    await user.click(confirmButton);

    expect(toastSuccessMock).toHaveBeenNthCalledWith(1, expect.stringContaining("En Progreso"));

    const titleInput = screen.getByLabelText(/título/i);
    await user.clear(titleInput);
    await user.type(titleInput, " Ajustes finales ");

    const deadlineInput = screen.getByLabelText(/fecha de entrega/i);
    await user.clear(deadlineInput);
    await user.type(deadlineInput, "2025-03-10");

    await user.click(screen.getByRole("button", { name: /guardar cambios/i }));

    expect(onTaskUpdated).toHaveBeenCalledTimes(1);
    const updatedTask = onTaskUpdated.mock.calls[0][0] as Task;

    expect(updatedTask.estado).toBe("en-progreso");
    expect(updatedTask.titulo).toBe("Ajustes finales");
    expect(updatedTask.fechaEntrega.toISOString()).toBe(new Date("2025-03-10").toISOString());

    expect(toastSuccessMock).toHaveBeenNthCalledWith(2, "Tarea actualizada correctamente");
  });

  it("permite cancelar el cambio de estado y conservar el valor original", async () => {
    const user = userEvent.setup();
    const { onTaskUpdated } = renderDialog();

    await user.click(screen.getByRole("combobox", { name: /estado/i }));
    await user.click(await screen.findByRole("option", { name: /en progreso/i }));

    const alertDialog = await screen.findByRole("alertdialog");
    const cancelButton = within(alertDialog).getByRole("button", { name: /cancelar/i });
    await user.click(cancelButton);

  await waitFor(() => expect(screen.queryByRole("alertdialog")).not.toBeInTheDocument());

    expect(toastSuccessMock).not.toHaveBeenCalled();

    await user.click(screen.getByRole("button", { name: /guardar cambios/i }));

    expect(onTaskUpdated).toHaveBeenCalledTimes(1);
    const updatedTask = onTaskUpdated.mock.calls[0][0] as Task;

    expect(updatedTask.estado).toBe("pendiente");
    expect(toastSuccessMock).toHaveBeenCalledWith("Tarea actualizada correctamente");
  });

  it.each([
    {
      current: "pendiente" as TaskStatus,
      next: "finalizado" as TaskStatus,
      message:
        "No se puede pasar directamente de Pendiente a Completada. Primero debe pasar por En Progreso.",
    },
    {
      current: "finalizado" as TaskStatus,
      next: "pendiente" as TaskStatus,
      message: "No se puede pasar de Completada a Pendiente. Solo puede volver a En Progreso.",
    },
  ])("rechaza cambios de estado inválidos (%s → %s)", async ({ current, next, message }) => {
    const user = userEvent.setup();
    renderDialog({ estado: current });

    await user.click(screen.getByRole("combobox", { name: /estado/i }));
    const option = await screen.findByRole("option", { name: statusLabels[next] });
    await user.click(option);

    expect(toastErrorMock).toHaveBeenCalledWith(message);
    expect(screen.queryByRole("alertdialog")).not.toBeInTheDocument();
    expect(toastSuccessMock).not.toHaveBeenCalled();
  });
});
