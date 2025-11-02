import { ReactElement, ReactNode } from "react";
import { render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import userEvent from "@testing-library/user-event";
import { vi } from "vitest";

import Login from "../Login";

const mockNavigate = vi.fn();
const mockUseAuth = vi.fn();

vi.mock("@/contexts/AuthContext", () => ({
  useAuth: () => mockUseAuth(),
  AuthProvider: ({ children }: { children: ReactNode }) => <>{children}</>,
}));

vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual<typeof import("react-router-dom")>("react-router-dom");
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

const renderWithRouter = (ui: ReactElement) =>
  render(<MemoryRouter initialEntries={["/login"]}>{ui}</MemoryRouter>);

describe("Login", () => {
  beforeEach(() => {
    mockNavigate.mockReset();
    mockUseAuth.mockReset();
  });

  it("renders the login form when the user is not authenticated", () => {
    mockUseAuth.mockReturnValue({
      isAuthenticated: false,
      isLoading: false,
      login: vi.fn(),
    });

    renderWithRouter(<Login />);

    expect(screen.getByRole("button", { name: /iniciar sesión/i })).toBeInTheDocument();
    expect(screen.getByLabelText(/correo institucional/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/contraseña/i)).toBeInTheDocument();
  });

  it("shows a validation error when fields are submitted empty", async () => {
    mockUseAuth.mockReturnValue({
      isAuthenticated: false,
      isLoading: false,
      login: vi.fn(),
    });

    renderWithRouter(<Login />);

    await userEvent.click(screen.getByRole("button", { name: /iniciar sesión/i }));

    expect(
      screen.getByText(/por favor, completa todos los campos obligatorios/i)
    ).toBeInTheDocument();
  });

  it("submits credentials and navigates to dashboard on success", async () => {
    const loginMock = vi.fn().mockResolvedValue({ success: true });
    mockUseAuth.mockReturnValue({
      isAuthenticated: false,
      isLoading: false,
      login: loginMock,
    });

    renderWithRouter(<Login />);

    const correoInput = screen.getByLabelText(/correo institucional/i);
    const passwordInput = screen.getByLabelText(/contraseña/i);

    await userEvent.type(correoInput, "test@example.com");
    await userEvent.type(passwordInput, "123456");

    await userEvent.click(screen.getByRole("button", { name: /iniciar sesión/i }));

    await waitFor(() => {
      expect(loginMock).toHaveBeenCalledWith("test@example.com", "123456");
      expect(mockNavigate).toHaveBeenCalledWith("/dashboard");
    });
  });
});
