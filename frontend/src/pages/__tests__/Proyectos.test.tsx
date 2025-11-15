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
