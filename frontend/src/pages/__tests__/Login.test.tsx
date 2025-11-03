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

  it("permite navegar con tabulador y enviar con enter", async () => {
    const loginMock = vi.fn().mockResolvedValue({ success: true });
    mockUseAuth.mockReturnValue({
      isAuthenticated: false,
      isLoading: false,
      login: loginMock,
    });

    renderWithRouter(<Login />);

    const skipLink = screen.getByRole("link", { name: /saltar al contenido principal/i });
    const correoInput = screen.getByLabelText(/correo institucional/i);
    const passwordInput = screen.getByLabelText(/contraseña/i);
    const submitButton = screen.getByRole("button", { name: /iniciar sesión/i });

    await userEvent.tab();
    expect(skipLink).toHaveFocus();

    await userEvent.tab();
    expect(correoInput).toHaveFocus();
    await userEvent.type(correoInput, "maria.garcia@universidad.edu");

    await userEvent.tab();
    expect(passwordInput).toHaveFocus();
    await userEvent.type(passwordInput, "123456");

    await userEvent.tab();
    expect(submitButton).toHaveFocus();

    await userEvent.keyboard("{Enter}");

    await waitFor(() => {
      expect(loginMock).toHaveBeenCalledWith("maria.garcia@universidad.edu", "123456");
      expect(mockNavigate).toHaveBeenCalledWith("/dashboard");
    });
  });

  it("marca el campo de correo como obligatorio y accesible", () => {
    mockUseAuth.mockReturnValue({
      isAuthenticated: false,
      isLoading: false,
      login: vi.fn(),
    });

    renderWithRouter(<Login />);

    const correoInput = screen.getByLabelText(/correo institucional/i);

    expect(correoInput).toBeRequired();
    expect(correoInput).toHaveAttribute("aria-required", "true");

    const correoLabel = screen.getByText(/correo institucional/i);
    expect(correoLabel.tagName).toBe("LABEL");
    expect(correoLabel).toHaveTextContent("*");
  });

  it("muestra alertas accesibles cuando falta información", async () => {
    mockUseAuth.mockReturnValue({
      isAuthenticated: false,
      isLoading: false,
      login: vi.fn(),
    });

    renderWithRouter(<Login />);

    await userEvent.click(screen.getByRole("button", { name: /iniciar sesión/i }));

    const alert = await screen.findByRole("alert");
    expect(alert).toHaveAttribute("aria-live", "assertive");
    expect(alert).toHaveTextContent(/por favor, completa todos los campos obligatorios/i);
  });
});
