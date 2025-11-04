import { ReactElement } from "react";
import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { vi } from "vitest";

import Proyectos from "../Proyectos";

const mockUseAuth = vi.fn();
const { mockUseData } = vi.hoisted(() => ({
  mockUseData: vi.fn(),
}));

vi.mock("@/contexts/AuthContext", () => ({
  useAuth: () => mockUseAuth(),
}));

vi.mock("@/contexts/DataContext", () => ({
  useData: () => mockUseData(),
}));

const renderWithRouter = (ui: ReactElement) =>
  render(<MemoryRouter initialEntries={["/proyectos"]}>{ui}</MemoryRouter>);

describe("Proyectos", () => {
  beforeEach(() => {
    mockUseAuth.mockReset();
    mockUseData.mockReset();
  });

  it("muestra los proyectos donde el usuario participa y controles accesibles", () => {
    mockUseAuth.mockReturnValue({
      user: {
        id: "1",
        nombre: "María García",
        correo: "maria.garcia@universidad.edu",
        rol: "estudiante",
        fechaRegistro: new Date("2024-01-15"),
      },
    });

    mockUseData.mockReturnValue({
      projects: [
        {
          id: "project-1",
          nombre: "Plataforma de Seguimiento",
          descripcion: "Gestión académica integral",
          progreso: 45,
          deadline: "2030-12-01T00:00:00.000Z",
          tasks: [{ id: "task-1" }],
        },
        {
          id: "project-2",
          nombre: "Sistema de Evaluaciones",
          descripcion: "Automatiza calificaciones",
          progreso: 60,
          deadline: "2030-11-01T00:00:00.000Z",
          tasks: [{ id: "task-2" }],
        },
      ],
      tasks: [],
      states: [],
      isLoading: false,
      error: null,
      refreshData: vi.fn(),
      lastUpdated: null,
    });

    renderWithRouter(<Proyectos />);

    expect(screen.getByRole("heading", { level: 1, name: /proyectos/i })).toBeInTheDocument();

    expect(
      screen.getByRole("button", { name: /crear nuevo proyecto/i })
    ).toHaveAccessibleName(/crear nuevo proyecto/i);

    const detailLinks = screen.getAllByRole("link", { name: /ver detalles/i });
    expect(detailLinks).toHaveLength(2);
    detailLinks.forEach((link) => expect(link).toHaveAttribute("href"));

    expect(
      screen.getByLabelText(/progreso del proyecto: 45%/i)
    ).toBeInTheDocument();
  });

  it("muestra un estado vacío accesible cuando no existen proyectos asignados", () => {
    mockUseAuth.mockReturnValue({
      user: {
        id: "999",
        nombre: "Usuario sin proyectos",
        correo: "sin.proyecto@universidad.edu",
        rol: "estudiante",
        fechaRegistro: new Date("2024-01-15"),
      },
    });

    mockUseData.mockReturnValue({
      projects: [],
      tasks: [],
      states: [],
      isLoading: false,
      error: null,
      refreshData: vi.fn(),
      lastUpdated: null,
    });

    renderWithRouter(<Proyectos />);

    expect(screen.getByText(/no tienes proyectos/i)).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /crear proyecto/i })).toBeInTheDocument();
    expect(
      screen.getByText(/comienza creando tu primer proyecto académico/i)
    ).toBeInTheDocument();
  });
});
