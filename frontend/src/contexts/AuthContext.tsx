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
  const [loginAttempts, setLoginAttempts] = useState<LoginAttempt>({
    count: 0,
    lastAttempt: 0,
    blockedUntil: null,
    permanentlyBlocked: false,
    firstBlockPassed: false,
  });

  useEffect(() => {
    // Check for existing session
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }

    // Check for stored login attempts
    const storedAttempts = localStorage.getItem('loginAttempts');
    if (storedAttempts) {
      setLoginAttempts(JSON.parse(storedAttempts));
    }

    setIsLoading(false);
  }, []);

  // Session inactivity timeout (15 minutes)
  useEffect(() => {
    if (!user) return;

    const INACTIVITY_TIMEOUT = 15 * 60 * 1000; // 15 minutes in milliseconds
    let inactivityTimer: NodeJS.Timeout;

    const resetInactivityTimer = () => {
      clearTimeout(inactivityTimer);
      inactivityTimer = setTimeout(() => {
        setUser(null);
        localStorage.removeItem('currentUser');
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

    const now = Date.now();

    const preCheckResult = handlePreLoginChecks(now);
    if (preCheckResult) {
      return preCheckResult;
    }

    syncUnblockedState(now);

    const apiResult = await attemptAuthentication(correo, contrasena);
    if (apiResult.success) {
      return apiResult;
    }

    return handleFailedLogin(now, apiResult.errorMessage);
  };

  const handlePreLoginChecks = (now: number) => {
    if (loginAttempts.permanentlyBlocked) {
      setIsLoading(false);
      return buildFailure('Has alcanzado el límite de intentos. Contacta a soporte para desbloquear tu cuenta.');
    }

    if (loginAttempts.blockedUntil && now < loginAttempts.blockedUntil) {
      const remainingTime = Math.ceil((loginAttempts.blockedUntil - now) / 60000);
      setIsLoading(false);
      return buildFailure(`Cuenta bloqueada. Intenta de nuevo en ${remainingTime} minuto${remainingTime > 1 ? 's' : ''}.`);
    }

    return null;
  };

  const syncUnblockedState = (now: number) => {
    if (loginAttempts.blockedUntil && now >= loginAttempts.blockedUntil) {
      const updatedAttempts = {
        ...loginAttempts,
        blockedUntil: null,
        count: 0,
        firstBlockPassed: true,
      };
      setLoginAttempts(updatedAttempts);
      localStorage.setItem('loginAttempts', JSON.stringify(updatedAttempts));
    }
  };

  const attemptAuthentication = async (correo: string, contrasena: string) => {
    try {
      const response = await authApi.login({ email: correo, password: contrasena });
      const token = response.data.token;
      localStorage.setItem('token', token);

      const displayName = deriveDisplayName(response.data);
      const loggedInUser: User = {
        id: response.data.id ? String(response.data.id) : response.data.email,
        nombre: displayName,
        correo: response.data.email,
        rol: response.data.role as 'estudiante' | 'profesor',
        fechaRegistro: new Date()
      };

      persistSuccessfulLogin(loggedInUser);
  setIsLoading(false);
  return { success: true as const };
    } catch (error: any) {
      console.error('Login error:', error);
      return {
        success: false as const,
        error,
        errorMessage: error.response?.data?.message || error.message || 'Credenciales incorrectas'
      };
    }
  };

  const persistSuccessfulLogin = (loggedInUser: User) => {
    setUser(loggedInUser);
    localStorage.setItem('currentUser', JSON.stringify(loggedInUser));

    const resetAttempts = {
      count: 0,
      lastAttempt: 0,
      blockedUntil: null,
      permanentlyBlocked: false,
      firstBlockPassed: false,
    };

    setLoginAttempts(resetAttempts);
    localStorage.setItem('loginAttempts', JSON.stringify(resetAttempts));
    toast.success(`Bienvenido, ${loggedInUser.nombre}`);
  };

  const handleFailedLogin = (now: number, errorMessage: string) => {
    const newAttempts: LoginAttempt = {
      ...loginAttempts,
      count: loginAttempts.count + 1,
      lastAttempt: now,
      blockedUntil: null,
    };

    const maxAttempts = loginAttempts.firstBlockPassed ? 2 : 5;

    const blockOutcome = maybeBlockAccount(newAttempts, maxAttempts, now);
    if (blockOutcome) {
      return blockOutcome;
    }

    setLoginAttempts(newAttempts);
    localStorage.setItem('loginAttempts', JSON.stringify(newAttempts));
    setIsLoading(false);

    const remainingAttempts = maxAttempts - newAttempts.count;
    const description = `${errorMessage}. Te quedan ${remainingAttempts} intento${remainingAttempts > 1 ? 's' : ''}.`;

  return buildFailure(description);
  };

  const maybeBlockAccount = (attempts: LoginAttempt, maxAttempts: number, now: number) => {
    if (attempts.count < maxAttempts) {
      return null;
    }

    attempts.count = 0;

    if (loginAttempts.firstBlockPassed) {
      attempts.permanentlyBlocked = true;
      setLoginAttempts(attempts);
      localStorage.setItem('loginAttempts', JSON.stringify(attempts));
      setIsLoading(false);
      return buildFailure('Has alcanzado el límite de intentos. Contacta a soporte para desbloquear tu cuenta.');
    }

    attempts.blockedUntil = now + 15 * 60 * 1000;
    setLoginAttempts(attempts);
    localStorage.setItem('loginAttempts', JSON.stringify(attempts));
    setIsLoading(false);
    return buildFailure('Has superado el límite de intentos. Cuenta bloqueada por 15 minutos.');
  };

  const buildFailure = (error: string) => {
    setIsLoading(false);
    return { success: false as const, error };
  };

  const resetLoginAttempts = () => {
    const resetAttempts = { 
      count: 0, 
      lastAttempt: 0, 
      blockedUntil: null, 
      permanentlyBlocked: false,
      firstBlockPassed: false,
    };
    setLoginAttempts(resetAttempts);
    localStorage.setItem('loginAttempts', JSON.stringify(resetAttempts));
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('currentUser');
    localStorage.removeItem('token'); // Remove JWT token
    toast.info('Sesión cerrada correctamente');
  };

  const updateUser = async (userData: Partial<User>) => {
    if (!user) return;
    
    setIsLoading(true);
    
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 500));
    
    const updatedUser = { ...user, ...userData };
    setUser(updatedUser);
    localStorage.setItem('currentUser', JSON.stringify(updatedUser));
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
