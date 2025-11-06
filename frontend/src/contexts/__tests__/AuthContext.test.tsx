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
    mockAuthLogin.mockImplementation(({ email, password }: { email: string; password: string }) => {
      if (email === VALID_EMAIL && password === VALID_PASSWORD) {
        return Promise.resolve({
          data: {
            token: "token-valid",
            email: VALID_EMAIL,
            role: "estudiante",
            fullName: "María García",
          },
        });
      }

      if (email === BLOCKED_EMAIL && password === VALID_PASSWORD) {
        return Promise.resolve({
          data: {
            token: "token-blocked",
            email: BLOCKED_EMAIL,
            role: "estudiante",
            fullName: "Juan Pérez",
          },
        });
      }

      return Promise.reject(new Error("Invalid credentials"));
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
    });

    expect(loginResult?.success).toBe(true);

    const storedUser = localStorage.getItem("currentUser");
    expect(storedUser).not.toBeNull();
    if (storedUser) {
  expect(JSON.parse(storedUser).correo).toBe(VALID_EMAIL);
    }

    expect(toastSuccessMock).toHaveBeenCalledWith(expect.stringContaining("María"));
    expect(result.current.loginAttempts.permanentlyBlocked).toBe(false);
  });

  it("informa los intentos restantes ante credenciales incorrectas", async () => {
    const { result } = renderHook(() => useAuth(), { wrapper });

    await act(async () => {
      await Promise.resolve();
    });
    expect(result.current.isLoading).toBe(false);

    let lastResult: { success: boolean; error?: string } | undefined;
    for (let attempt = 0; attempt < 3; attempt += 1) {
      await act(async () => {
        const promise = result.current.login("usuario_incorrecto", "clave_incorrecta");
        await vi.advanceTimersByTimeAsync(800);
        lastResult = await promise;
      });

      expect(lastResult?.success).toBe(false);
      const remaining = 4 - attempt;
      expect(lastResult?.error).toContain(`${remaining} intento`);
  expect(result.current.isLoading).toBe(false);
    }

    expect(localStorage.getItem("currentUser")).toBeNull();
  });

  it("bloquea temporalmente tras cinco intentos fallidos y permite reingresar luego del bloqueo", async () => {
    const { result } = renderHook(() => useAuth(), { wrapper });

    await act(async () => {
      await Promise.resolve();
    });
    expect(result.current.isLoading).toBe(false);

    let lastResult: { success: boolean; error?: string } | undefined;
    for (let attempt = 0; attempt < 5; attempt += 1) {
      await act(async () => {
  const promise = result.current.login(BLOCKED_EMAIL, BLOCKED_PASSWORD);
        await vi.advanceTimersByTimeAsync(800);
        lastResult = await promise;
      });
  expect(result.current.isLoading).toBe(false);
    }

    expect(lastResult?.success).toBe(false);
    expect(lastResult?.error).toContain("Cuenta bloqueada");

    await act(async () => {
  const promise = result.current.login(BLOCKED_EMAIL, BLOCKED_PASSWORD);
      await vi.advanceTimersByTimeAsync(10);
      lastResult = await promise;
    });

    expect(lastResult?.error).toContain("Cuenta bloqueada");

    vi.setSystemTime(new Date("2025-01-01T12:16:00Z"));

    await act(async () => {
  const promise = result.current.login(BLOCKED_EMAIL, VALID_PASSWORD);
      await vi.advanceTimersByTimeAsync(800);
      lastResult = await promise;
    });

    expect(lastResult?.success).toBe(true);
    const storedLogin = localStorage.getItem("currentUser");
    expect(storedLogin).not.toBeNull();
    if (storedLogin) {
  expect(JSON.parse(storedLogin).correo).toBe(BLOCKED_EMAIL);
    }
    expect(toastSuccessMock).toHaveBeenCalledWith(expect.stringContaining("Juan"));
  });
});
