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
