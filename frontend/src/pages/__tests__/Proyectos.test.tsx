import { ReactElement } from "react";
import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { vi } from "vitest";

import Proyectos from "../Proyectos";

const mockUseAuth = vi.fn();

vi.mock("@/contexts/AuthContext", () => ({
  useAuth: () => mockUseAuth(),
}));

const renderWithRouter = (ui: ReactElement) =>
  render(<MemoryRouter initialEntries={["/proyectos"]}>{ui}</MemoryRouter>);

describe("Proyectos", () => {
  beforeEach(() => {
    mockUseAuth.mockReset();
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

    renderWithRouter(<Proyectos />);

    expect(screen.getByText(/no tienes proyectos/i)).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /crear proyecto/i })).toBeInTheDocument();
    expect(
      screen.getByText(/comienza creando tu primer proyecto académico/i)
    ).toBeInTheDocument();
  });
});
