import { ReactNode } from "react";
import { renderHook, act } from "@testing-library/react";
import { describe, expect, it, beforeEach, afterEach, vi } from "vitest";

import { AuthProvider, useAuth } from "../AuthContext";

const { toastSuccessMock, toastErrorMock, toastInfoMock } = vi.hoisted(() => ({
  toastSuccessMock: vi.fn(),
  toastErrorMock: vi.fn(),
  toastInfoMock: vi.fn(),
}));

vi.mock("sonner", () => ({
  toast: {
    success: toastSuccessMock,
    error: toastErrorMock,
    info: toastInfoMock,
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
      const promise = result.current.login("maria.garcia@universidad.edu", "123456");
      await vi.advanceTimersByTimeAsync(800);
      loginResult = await promise;
    });

    expect(loginResult?.success).toBe(true);

    const storedUser = localStorage.getItem("currentUser");
    expect(storedUser).not.toBeNull();
    if (storedUser) {
      expect(JSON.parse(storedUser).correo).toBe("maria.garcia@universidad.edu");
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
        const promise = result.current.login("juan.perez@universidad.edu", "0000");
        await vi.advanceTimersByTimeAsync(800);
        lastResult = await promise;
      });
  expect(result.current.isLoading).toBe(false);
    }

    expect(lastResult?.success).toBe(false);
    expect(lastResult?.error).toContain("Cuenta bloqueada");

    await act(async () => {
      const promise = result.current.login("juan.perez@universidad.edu", "0000");
      await vi.advanceTimersByTimeAsync(10);
      lastResult = await promise;
    });

    expect(lastResult?.error).toContain("Cuenta bloqueada");

    vi.setSystemTime(new Date("2025-01-01T12:16:00Z"));

    await act(async () => {
      const promise = result.current.login("juan.perez@universidad.edu", "123456");
      await vi.advanceTimersByTimeAsync(800);
      lastResult = await promise;
    });

    expect(lastResult?.success).toBe(true);
    const storedLogin = localStorage.getItem("currentUser");
    expect(storedLogin).not.toBeNull();
    if (storedLogin) {
      expect(JSON.parse(storedLogin).correo).toBe("juan.perez@universidad.edu");
    }
    expect(toastSuccessMock).toHaveBeenCalledWith(expect.stringContaining("Juan"));
  });
});
