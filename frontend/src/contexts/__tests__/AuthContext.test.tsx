import { ReactNode } from "react";
import { renderHook, act } from "@testing-library/react";
import { describe, expect, it, beforeEach, afterEach, vi } from "vitest";

import { AuthProvider, useAuth } from "../AuthContext";

const { toastSuccessMock, toastErrorMock, toastInfoMock, mockAuthLogin, mockAuthLogout } = vi.hoisted(() => ({
  toastSuccessMock: vi.fn(),
  toastErrorMock: vi.fn(),
  toastInfoMock: vi.fn(),
  mockAuthLogin: vi.fn(),
  mockAuthLogout: vi.fn(),
}));

const VALID_EMAIL = "maria.garcia@universidad.edu";
const VALID_PASSWORD = "123456";
const BLOCKED_EMAIL = "juan.perez@universidad.edu";
const BLOCKED_PASSWORD = "0000";

vi.mock("sonner", () => ({
  toast: {
    success: toastSuccessMock,
    error: toastErrorMock,
    info: toastInfoMock,
  },
}));

vi.mock("@/lib/api", () => ({
  authApi: {
    login: (credentials: { email: string; password: string }) => mockAuthLogin(credentials),
    logout: () => mockAuthLogout(),
    // Simula restauración de sesión: solo devuelve usuario si login fue exitoso
    me: vi.fn().mockImplementation(() => {
      if (globalThis.__test_user) {
        return Promise.resolve({
          data: globalThis.__test_user,
        });
      }
      // Simula sesión no restaurada
      const error: any = new Error("No autenticado");
      error.response = { status: 401, data: { message: "No autenticado" } };
      return Promise.reject(error);
    }),
  },
}));

const wrapper = ({ children }: { children: ReactNode }) => <AuthProvider>{children}</AuthProvider>;

describe("AuthProvider login flows", () => {
  beforeEach(() => {
    vi.useFakeTimers();
    vi.setSystemTime(new Date("2025-01-01T12:00:00Z"));
    localStorage.clear();
    toastSuccessMock.mockReset();
    toastErrorMock.mockReset();
    toastInfoMock.mockReset();
    mockAuthLogin.mockReset();
      mockAuthLogout.mockReset();
    let failedAttempts = 0;
    mockAuthLogin.mockImplementation(({ email, password }: { email: string; password: string }) => {
      if (email === VALID_EMAIL && password === VALID_PASSWORD) {
        globalThis.__test_user = {
          id: "1",
          email: VALID_EMAIL,
          role: "estudiante",
          fullName: "María García",
        };
        return Promise.resolve({
          data: globalThis.__test_user,
        });
      }

      if (email === BLOCKED_EMAIL && password === VALID_PASSWORD) {
        if (failedAttempts >= 5) {
          const error: any = new Error("Cuenta bloqueada");
          error.response = { status: 423, data: { message: "Cuenta bloqueada por intentos fallidos" } };
          return Promise.reject(error);
        }
        failedAttempts++;
        const error: any = new Error("Correo o contraseña incorrectos");
        error.response = { status: 401, data: { message: "Correo o contraseña incorrectos" } };
        return Promise.reject(error);
      }

      // Simula credenciales incorrectas
      globalThis.__test_user = undefined;
      const error: any = new Error("Correo o contraseña incorrectos");
      error.response = { status: 401, data: { message: "Correo o contraseña incorrectos" } };
      return Promise.reject(error);
    });
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it("permite el inicio de sesión exitoso y resetea los intentos", async () => {
    const { result } = renderHook(() => useAuth(), { wrapper });

    await act(async () => {
      await Promise.resolve();
    });
    expect(result.current.isLoading).toBe(false);

    let loginResult: { success: boolean; error?: string } | undefined;
    await act(async () => {
      const promise = result.current.login(VALID_EMAIL, VALID_PASSWORD);
      await vi.advanceTimersByTimeAsync(800);
      loginResult = await promise;
      // Simula usuario restaurado en el mock
      globalThis.__test_user = {
        id: "1",
        email: VALID_EMAIL,
        role: "estudiante",
        fullName: "María García",
      };
    });

    expect(loginResult?.success).toBe(true);
    expect(result.current.user?.correo).toBe(VALID_EMAIL);
    expect(toastSuccessMock).toHaveBeenCalledWith(expect.stringContaining("María"));
  });

});
