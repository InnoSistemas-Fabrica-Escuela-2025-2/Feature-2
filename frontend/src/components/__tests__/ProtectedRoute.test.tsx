import { ReactNode } from "react";
import { render, screen } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { vi } from "vitest";

import ProtectedRoute from "../ProtectedRoute";

const mockUseAuth = vi.fn();

vi.mock("@/contexts/AuthContext", () => ({
  useAuth: () => mockUseAuth(),
  AuthProvider: ({ children }: { children: ReactNode }) => <>{children}</>,
}));

describe("ProtectedRoute", () => {
  beforeEach(() => {
    mockUseAuth.mockReset();
  });

  it("shows a loading indicator while authentication is pending", () => {
    mockUseAuth.mockReturnValue({ isLoading: true, isAuthenticated: false });

    render(
      <MemoryRouter>
        <ProtectedRoute>
          <div>Private content</div>
        </ProtectedRoute>
      </MemoryRouter>
    );

    expect(screen.getByText(/cargando/i)).toBeInTheDocument();
  });

  it("redirects to login when the user is not authenticated", () => {
    mockUseAuth.mockReturnValue({ isLoading: false, isAuthenticated: false });

    render(
      <MemoryRouter initialEntries={["/dashboard"]}>
        <Routes>
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <div>Dashboard content</div>
              </ProtectedRoute>
            }
          />
          <Route path="/login" element={<div>Login Page</div>} />
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByText("Login Page")).toBeInTheDocument();
  });

  it("renders the protected content when the user is authenticated", () => {
    mockUseAuth.mockReturnValue({ isLoading: false, isAuthenticated: true });

    render(
      <MemoryRouter initialEntries={["/dashboard"]}>
        <Routes>
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <div>Dashboard content</div>
              </ProtectedRoute>
            }
          />
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByText("Dashboard content")).toBeInTheDocument();
  });
});
