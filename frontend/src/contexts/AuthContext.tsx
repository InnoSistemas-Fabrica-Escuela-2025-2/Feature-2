import React, { createContext, useContext, useState, useEffect } from 'react';
import { User, AuthState } from '@/types';
import { authApi } from '@/lib/api';
import { toast } from 'sonner';

interface LoginAttempt {
  count: number;
  lastAttempt: number;
  blockedUntil: number | null;
  permanentlyBlocked: boolean;
  firstBlockPassed: boolean;
}

type LoginAttemptsStore = Record<string, LoginAttempt>;

interface LoginAttemptBlockOutcome {
  record: LoginAttempt;
  message: string;
}

const createInitialLoginAttempt = (): LoginAttempt => ({
  count: 0,
  lastAttempt: 0,
  blockedUntil: null,
  permanentlyBlocked: false,
  firstBlockPassed: false,
});

const normalizeEmail = (correo: string) => correo.trim().toLowerCase();

const shouldPersistAttempt = (attempt: LoginAttempt) =>
  attempt.count > 0 ||
  attempt.blockedUntil !== null ||
  attempt.permanentlyBlocked ||
  attempt.firstBlockPassed ||
  attempt.lastAttempt !== 0;

interface AuthContextType extends AuthState {
  login: (correo: string, contrasena: string) => Promise<{ success: boolean; error?: string }>;
  logout: () => void;
  updateUser: (userData: Partial<User>) => Promise<void>;
  loginAttempts: LoginAttempt;
  resetLoginAttempts: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const deriveDisplayName = (data: any): string => {
  if (!data) {
    return 'Usuario';
  }

  const explicitName = data.fullName || data.nombre || data.name;
  if (explicitName && typeof explicitName === 'string') {
    return explicitName;
  }

  const email: string | undefined = data.email;
  if (typeof email === 'string' && email.includes('@')) {
    const localPart = email.split('@')[0] ?? '';
    const formatted = localPart
      .replace(/[._-]+/g, ' ')
      .split(' ')
      .map((segment) => segment.trim())
      .filter(Boolean)
      .map((segment) => segment.charAt(0).toUpperCase() + segment.slice(1))
      .join(' ')
      .replace(/\bDe\b/g, 'de');

    return formatted || 'Usuario';
  }

  return 'Usuario';
};

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [attemptsStore, setAttemptsStore] = useState<LoginAttemptsStore>({});
  const [loginAttempts, setLoginAttempts] = useState<LoginAttempt>(createInitialLoginAttempt());

  useEffect(() => {
    // Try to load current user from backend using cookie-based session (if backend exposes /person/me)
    const fetchCurrentUser = async () => {
      try {
        const res = await authApi.me();
        const data = res.data;
        const displayName = deriveDisplayName(data);
        const loggedInUser: User = {
          id: data.id ? String(data.id) : data.email,
          nombre: displayName,
          correo: data.email,
          rol: data.role as 'estudiante' | 'profesor',
          fechaRegistro: new Date(),
        };
        setUser(loggedInUser);
      } catch (err) {
        // If backend doesn't expose /me or user not authenticated, leave user null
        setUser(null);
      }
    };

    void fetchCurrentUser();

    // Check for stored login attempts (per email)
    const storedAttempts = localStorage.getItem('loginAttempts');
    if (storedAttempts) {
      try {
        const parsed = JSON.parse(storedAttempts);
        if (parsed && typeof parsed === 'object' && !Array.isArray(parsed)) {
          if ('count' in parsed) {
            // Legacy structure (single record) – drop so new schema starts clean per user
            localStorage.removeItem('loginAttempts');
          } else {
            setAttemptsStore(parsed as LoginAttemptsStore);
          }
        }
      } catch (error) {
        console.error('Error reading stored login attempts:', error);
        localStorage.removeItem('loginAttempts');
      }
    }

    setIsLoading(false);
  }, []);

  const persistAttemptsStore = (store: LoginAttemptsStore) => {
    if (Object.keys(store).length === 0) {
      localStorage.removeItem('loginAttempts');
    } else {
      localStorage.setItem('loginAttempts', JSON.stringify(store));
    }
  };

  const updateAttemptsForEmail = (emailKey: string, nextAttempt: LoginAttempt) => {
    setAttemptsStore((prev) => {
      const updated = { ...prev };
      const attemptClone = { ...nextAttempt };

      if (shouldPersistAttempt(attemptClone)) {
        updated[emailKey] = attemptClone;
      } else {
        delete updated[emailKey];
      }

      persistAttemptsStore(updated);
      return updated;
    });
  };

  const resetAttemptsForEmail = (emailKey: string) => {
    setAttemptsStore((prev) => {
      if (!(emailKey in prev)) {
        return prev;
      }

      const updated = { ...prev };
      delete updated[emailKey];
      persistAttemptsStore(updated);
      return updated;
    });
  };

  // Session inactivity timeout (15 minutes)
  useEffect(() => {
    if (!user) return;

    const INACTIVITY_TIMEOUT = 15 * 60 * 1000; // 15 minutes in milliseconds
    let inactivityTimer: NodeJS.Timeout;

    const resetInactivityTimer = () => {
      clearTimeout(inactivityTimer);
      inactivityTimer = setTimeout(() => {
        setUser(null);
        // Do not persist session in localStorage; simply clear in-memory state on timeout
        toast.error('Tu sesión ha sido cerrada por inactividad. Por favor, inicia sesión nuevamente.');
      }, INACTIVITY_TIMEOUT);
    };

    // Events that reset the inactivity timer
    const events = ['mousedown', 'keydown', 'scroll', 'touchstart', 'click'];
    
    events.forEach(event => {
      window.addEventListener(event, resetInactivityTimer);
    });

    // Start the initial timer
    resetInactivityTimer();

    // Cleanup
    return () => {
      clearTimeout(inactivityTimer);
      events.forEach(event => {
        window.removeEventListener(event, resetInactivityTimer);
      });
    };
  }, [user]);

  const login = async (correo: string, contrasena: string): Promise<{ success: boolean; error?: string }> => {
    setIsLoading(true);

    const emailKey = normalizeEmail(correo);
    const baseAttempts = attemptsStore[emailKey]
      ? { ...attemptsStore[emailKey] }
      : createInitialLoginAttempt();

    setLoginAttempts(baseAttempts);

    const now = Date.now();

    const preCheckResult = handlePreLoginChecks(baseAttempts, now);
    if (preCheckResult) {
      return preCheckResult;
    }

    const refreshedAttempts = syncUnblockedState(baseAttempts, emailKey, now);
    setLoginAttempts(refreshedAttempts);

    const apiResult = await attemptAuthentication(correo, contrasena, emailKey);
    if (apiResult.success) {
      return apiResult;
    }

    return handleFailedLogin(emailKey, refreshedAttempts, now, apiResult.errorMessage);
  };

  const handlePreLoginChecks = (attemptRecord: LoginAttempt, now: number) => {
    if (attemptRecord.permanentlyBlocked) {
      return buildFailure('Has alcanzado el límite de intentos. Contacta a soporte para desbloquear tu cuenta.');
    }

    if (attemptRecord.blockedUntil && now < attemptRecord.blockedUntil) {
      const remainingTime = Math.ceil((attemptRecord.blockedUntil - now) / 60000);
      return buildFailure(`Cuenta bloqueada. Intenta de nuevo en ${remainingTime} minuto${remainingTime > 1 ? 's' : ''}.`);
    }

    return null;
  };

  const syncUnblockedState = (attemptRecord: LoginAttempt, emailKey: string, now: number): LoginAttempt => {
    if (attemptRecord.blockedUntil && now >= attemptRecord.blockedUntil) {
      const updatedAttempts: LoginAttempt = {
        ...attemptRecord,
        blockedUntil: null,
        count: 0,
        firstBlockPassed: true,
      };
      updateAttemptsForEmail(emailKey, updatedAttempts);
      return updatedAttempts;
    }

    return attemptRecord;
  };

  const attemptAuthentication = async (correo: string, contrasena: string, emailKey: string) => {
    try {
      const response = await authApi.login({ email: correo, password: contrasena });
      const displayName = deriveDisplayName(response.data);
      const loggedInUser: User = {
        id: response.data.id ? String(response.data.id) : response.data.email,
        nombre: displayName,
        correo: response.data.email,
        rol: response.data.role as 'estudiante' | 'profesor',
        fechaRegistro: new Date()
      };

      persistSuccessfulLogin(loggedInUser, emailKey);
      setIsLoading(false);
      return { success: true as const };
    } catch (error: any) {
      console.error('Login error:', error);
      const status = error.response?.status;
      const backendMessage = error.response?.data?.message;
      let friendlyMessage = backendMessage;

      if (!friendlyMessage) {
        if (!error.response) {
          friendlyMessage = 'No se pudo contactar al servicio de autenticación. Verifica tu conexión.';
        } else if (status === 401) {
          friendlyMessage = 'Correo o contraseña incorrectos.';
        } else if (status === 500 || status === 404) {
          friendlyMessage = 'No encontramos una cuenta registrada con ese correo.';
        } else {
          friendlyMessage = error.message || 'Credenciales incorrectas';
        }
      }

      return {
        success: false as const,
        error,
        errorMessage: friendlyMessage || 'Credenciales incorrectas'
      };
    }
  };

  const persistSuccessfulLogin = (loggedInUser: User, emailKey: string) => {
    // Keep user in memory only; session token is managed via HttpOnly cookie from the backend
    setUser(loggedInUser);

    const resetAttempts = createInitialLoginAttempt();

    setLoginAttempts(resetAttempts);
    resetAttemptsForEmail(emailKey);
    toast.success(`Bienvenido, ${loggedInUser.nombre}`);
  };

  const handleFailedLogin = (
    emailKey: string,
    currentAttempts: LoginAttempt,
    now: number,
    errorMessage: string,
  ) => {
    const updatedAttempts: LoginAttempt = {
      ...currentAttempts,
      count: currentAttempts.count + 1,
      lastAttempt: now,
      blockedUntil: null,
    };

    const maxAttempts = currentAttempts.firstBlockPassed ? 2 : 5;

    const blockOutcome = maybeBlockAccount(emailKey, updatedAttempts, maxAttempts, now);
    if (blockOutcome) {
      setLoginAttempts(blockOutcome.record);
      return buildFailure(blockOutcome.message);
    }

    updateAttemptsForEmail(emailKey, updatedAttempts);
    setLoginAttempts(updatedAttempts);

    const remainingAttempts = maxAttempts - updatedAttempts.count;
    const description = `${errorMessage || 'Credenciales incorrectas'}. Te quedan ${remainingAttempts} intento${remainingAttempts > 1 ? 's' : ''}.`;

    return buildFailure(description);
  };

  const maybeBlockAccount = (
    emailKey: string,
    attempts: LoginAttempt,
    maxAttempts: number,
    now: number,
  ): LoginAttemptBlockOutcome | null => {
    if (attempts.count < maxAttempts) {
      return null;
    }

    const resetCount: LoginAttempt = {
      ...attempts,
      count: 0,
    };

    if (attempts.firstBlockPassed) {
      const permanentlyBlocked: LoginAttempt = {
        ...resetCount,
        permanentlyBlocked: true,
      };
      updateAttemptsForEmail(emailKey, permanentlyBlocked);
      return {
        record: permanentlyBlocked,
        message: 'Has alcanzado el límite de intentos. Contacta a soporte para desbloquear tu cuenta.',
      };
    }

    const blockedAttempts: LoginAttempt = {
      ...resetCount,
      blockedUntil: now + 15 * 60 * 1000,
    };
    updateAttemptsForEmail(emailKey, blockedAttempts);
    return {
      record: blockedAttempts,
      message: 'Has superado el límite de intentos. Cuenta bloqueada por 15 minutos.',
    };
  };

  const buildFailure = (error: string) => {
    setIsLoading(false);
    return { success: false as const, error };
  };

  const resetLoginAttempts = () => {
    setAttemptsStore({});
    persistAttemptsStore({});
    setLoginAttempts(createInitialLoginAttempt());
  };

  const logout = () => {
    void authApi.logout().catch((error: unknown) => {
      console.error('Error al cerrar sesión en el servidor:', error);
    });
    setUser(null);
    toast.info('Sesión cerrada correctamente');
  };

  const updateUser = async (userData: Partial<User>) => {
    if (!user) return;
    
    setIsLoading(true);
    
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 500));
    
    const updatedUser = { ...user, ...userData };
    setUser(updatedUser);
    toast.success('Perfil actualizado correctamente');
    
    setIsLoading(false);
  };

  return (
    <AuthContext.Provider 
      value={{ 
        user, 
        isAuthenticated: !!user, 
        isLoading, 
        login, 
        logout,
        updateUser,
        loginAttempts,
        resetLoginAttempts
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
