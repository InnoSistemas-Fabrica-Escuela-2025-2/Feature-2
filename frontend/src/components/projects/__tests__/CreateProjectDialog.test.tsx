import { ReactNode } from "react";
import { render, screen, waitFor, fireEvent } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { vi } from "vitest";

import CreateProjectDialog from "../CreateProjectDialog";

const mockUseAuth = vi.fn();
const mockToastSuccess = vi.fn();

vi.mock("@/contexts/AuthContext", () => ({
  useAuth: () => mockUseAuth(),
  AuthProvider: ({ children }: { children: ReactNode }) => <>{children}</>,
}));

vi.mock("sonner", () => ({
  toast: {
    success: (message: string) => mockToastSuccess(message),
  },
}));

beforeAll(() => {
  if (!HTMLElement.prototype.hasPointerCapture) {
    Object.defineProperty(HTMLElement.prototype, "hasPointerCapture", {
      value: () => false,
      configurable: true,
    });
  }
  if (!HTMLElement.prototype.setPointerCapture) {
    Object.defineProperty(HTMLElement.prototype, "setPointerCapture", {
      value: () => {},
      configurable: true,
    });
  }
  if (!HTMLElement.prototype.releasePointerCapture) {
    Object.defineProperty(HTMLElement.prototype, "releasePointerCapture", {
      value: () => {},
      configurable: true,
    });
  }
  if (!Element.prototype.scrollIntoView) {
    Object.defineProperty(Element.prototype, "scrollIntoView", {
      value: () => {},
      configurable: true,
    });
  }
});

describe("CreateProjectDialog", () => {
  beforeEach(() => {
    mockUseAuth.mockReturnValue({
      user: {
        id: "1",
        nombre: "María García",
        correo: "maria.garcia@universidad.edu",
        rol: "estudiante",
        fechaRegistro: new Date("2024-01-15"),
      },
    });
    mockToastSuccess.mockReset();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("muestra errores cuando se intenta crear el proyecto sin datos obligatorios", async () => {
    const onProjectCreated = vi.fn();

    render(
      <CreateProjectDialog open onOpenChange={vi.fn()} onProjectCreated={onProjectCreated} />
    );

    await userEvent.click(screen.getByRole("button", { name: /crear proyecto/i }));

    expect(await screen.findByRole("alert")).toBeInTheDocument();
    expect(screen.getByText(/el nombre del proyecto es obligatorio/i)).toBeInTheDocument();
    expect(onProjectCreated).not.toHaveBeenCalled();
  });

  it("crea un proyecto y anuncia el éxito cuando el formulario es válido", async () => {
    const onProjectCreated = vi.fn();
    const onOpenChange = vi.fn();
    const fixedNow = 1_750_000_000_000;

    vi.spyOn(Date, "now").mockReturnValue(fixedNow);

    render(
      <CreateProjectDialog open onOpenChange={onOpenChange} onProjectCreated={onProjectCreated} />
    );

    await userEvent.type(screen.getByLabelText(/nombre del proyecto/i), "Nuevo Proyecto Inclusivo");
  await userEvent.type(screen.getByLabelText(/descripción/i), "Descripción del proyecto");
    await userEvent.type(screen.getByLabelText(/objetivos/i), "Mejorar accesibilidad");

  const dateInput = screen.getByLabelText(/fecha de entrega/i);
  fireEvent.input(dateInput, { target: { value: "2030-12-01" } });

  const teamTrigger = screen.getByLabelText(/equipo/i);
  await userEvent.click(teamTrigger);
  await userEvent.click(await screen.findByRole("option", { name: /equipo alpha/i }));

    await userEvent.click(screen.getByRole("button", { name: /^crear proyecto$/i }));

    await waitFor(() => {
      expect(onProjectCreated).toHaveBeenCalledTimes(1);
    });

    const createdProject = onProjectCreated.mock.calls[0][0];
    expect(createdProject).toMatchObject({
      id: `p${fixedNow}`,
      nombre: "Nuevo Proyecto Inclusivo",
      equipoId: "team1",
      progreso: 0,
    });
    expect(createdProject.miembros.length).toBeGreaterThan(0);

    expect(mockToastSuccess).toHaveBeenCalledWith("Proyecto creado exitosamente");
    expect(onOpenChange).toHaveBeenCalledWith(false);
  });
});
