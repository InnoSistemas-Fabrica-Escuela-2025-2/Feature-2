import React, { createContext, useContext, useState, useEffect } from 'react';
import { User, AuthState } from '@/types';
import { authApi } from '@/lib/api';
import { toast } from 'sonner';

interface AuthContextType extends AuthState {
  login: (correo: string, contrasena: string) => Promise<{ success: boolean; error?: string }>;
  logout: () => void;
  updateUser: (userData: Partial<User>) => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const deriveDisplayName = (data: any): string => {
  if (!data) return 'Usuario';
  const explicitName = data.fullName || data.nombre || data.name;
  if (explicitName && typeof explicitName === 'string') return explicitName;
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

  // Restaurar sesión desde localStorage al cargar
  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      try {
        const parsedUser = JSON.parse(storedUser);
        // Convertir fechaRegistro de string a Date
        parsedUser.fechaRegistro = new Date(parsedUser.fechaRegistro);
        setUser(parsedUser);
      } catch (err) {
        console.error('Error restoring session from localStorage:', err);
        localStorage.removeItem('user');
        setUser(null);
      }
    }
    setIsLoading(false);
  }, []);

  useEffect(() => {
    if (!user) return;
    const INACTIVITY_TIMEOUT = 15 * 60 * 1000;
    let inactivityTimer: NodeJS.Timeout;
    const resetInactivityTimer = () => {
      clearTimeout(inactivityTimer);
      inactivityTimer = setTimeout(() => {
        setUser(null);
        localStorage.removeItem('user');
        toast.error('Tu sesión ha sido cerrada por inactividad. Por favor, inicia sesión nuevamente.');
      }, INACTIVITY_TIMEOUT);
    };
    const events = ['mousedown', 'keydown', 'scroll', 'touchstart', 'click'];
    for (const event of events) {
      globalThis.addEventListener(event, resetInactivityTimer);
    }
    resetInactivityTimer();
    return () => {
      clearTimeout(inactivityTimer);
      for (const event of events) {
        globalThis.removeEventListener(event, resetInactivityTimer);
      }
    };
  }, [user]);

  const login = async (correo: string, contrasena: string): Promise<{ success: boolean; error?: string }> => {
    setIsLoading(true);
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
      setUser(loggedInUser);
      // Guardar en localStorage para persistencia
      localStorage.setItem('user', JSON.stringify(loggedInUser));
      // Guardar el token JWT en localStorage
      if (response.data.token) {
        localStorage.setItem('token', response.data.token);
      }
      setIsLoading(false);
      toast.success(`Bienvenido, ${loggedInUser.nombre}`);
      return { success: true as const };
    } catch (error: any) {
      setIsLoading(false);
      const status = error.response?.status;
      const data = error.response?.data || {};
      const backendMessage: string | undefined = data?.message;
      const code: string | undefined = data?.code;
      const remainingAttempts: number | undefined = data?.remainingAttempts;
      const remainingMillis: number | undefined = data?.remainingMillis;
      const permanent: boolean | undefined = data?.permanent;

      const formatRemaining = (ms: number) => {
        const totalSeconds = Math.max(0, Math.floor(ms / 1000));
        const minutes = Math.floor(totalSeconds / 60);
        const seconds = totalSeconds % 60;
        const mm = String(minutes);
        const ss = String(seconds).padStart(2, '0');
        return `${mm} min ${ss} s`;
      };

      let friendlyMessage: string | undefined = undefined;

      // Priorizar mensajes del contrato del backend
      if (status === 401) {
        if (typeof remainingAttempts === 'number') {
          friendlyMessage = `Correo o contraseña incorrectos. Te restan ${remainingAttempts} intento${remainingAttempts === 1 ? '' : 's'} antes del bloqueo de 15 minutos.`;
        } else {
          friendlyMessage = backendMessage || 'Correo o contraseña incorrectos.';
        }
      } else if (status === 423) {
        const isPermanent = Boolean(permanent) || code === 'AUTH_BLOCKED_PERMANENT';
        if (isPermanent) {
          friendlyMessage = backendMessage || 'Tu cuenta ha sido bloqueada permanentemente.';
        } else if (typeof remainingMillis === 'number') {
          friendlyMessage = `Tu cuenta está bloqueada temporalmente. Intenta de nuevo en ${formatRemaining(remainingMillis)}.`;
        } else {
          friendlyMessage = backendMessage || 'Tu cuenta está bloqueada temporalmente.';
        }
      } else if (!error.response) {
        friendlyMessage = 'No se pudo contactar al servicio de autenticación. Verifica tu conexión.';
      } else if (status === 404) {
        friendlyMessage = 'Servicio de autenticación no disponible (404).';
      } else if (status === 500) {
        friendlyMessage = backendMessage || 'Error interno del servidor (500). Intenta más tarde.';
      } else if (backendMessage) {
        friendlyMessage = backendMessage;
      } else {
        friendlyMessage = error.message || 'Error de autenticación';
      }
      return { success: false as const, error: friendlyMessage };
    }
  };

  const logout = () => {
    // Limpiar sesión localmente (backend usa JWT stateless)
    setUser(null);
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    toast.info('Sesión cerrada correctamente');
  };

  const updateUser = async (userData: Partial<User>) => {
    if (!user) return;
    setIsLoading(true);
    await new Promise(resolve => setTimeout(resolve, 500));
    const updatedUser = { ...user, ...userData };
    setUser(updatedUser);
    toast.success('Perfil actualizado correctamente');
    setIsLoading(false);
  };

  const contextValue = React.useMemo(
    () => ({
      user,
      isAuthenticated: !!user,
      isLoading,
      login,
      logout,
      updateUser
    }),
    [user, isLoading, login, logout, updateUser]
  );

  return (
    <AuthContext.Provider value={contextValue}>
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
