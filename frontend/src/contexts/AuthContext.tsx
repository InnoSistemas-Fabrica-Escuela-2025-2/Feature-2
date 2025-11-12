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

  useEffect(() => {
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
        setUser(null);
      }
    };
    void fetchCurrentUser();
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
        toast.error('Tu sesión ha sido cerrada por inactividad. Por favor, inicia sesión nuevamente.');
      }, INACTIVITY_TIMEOUT);
    };
    const events = ['mousedown', 'keydown', 'scroll', 'touchstart', 'click'];
    events.forEach(event => {
      window.addEventListener(event, resetInactivityTimer);
    });
    resetInactivityTimer();
    return () => {
      clearTimeout(inactivityTimer);
      events.forEach(event => {
        window.removeEventListener(event, resetInactivityTimer);
      });
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
      setIsLoading(false);
      toast.success(`Bienvenido, ${loggedInUser.nombre}`);
      return { success: true as const };
    } catch (error: any) {
      setIsLoading(false);
      const status = error.response?.status;
      const backendMessage = error.response?.data?.message;
      let friendlyMessage = backendMessage;
      if (status === 423 || (friendlyMessage && friendlyMessage.toLowerCase().includes('bloqueado'))) {
        friendlyMessage = 'Tu cuenta está bloqueada por intentos fallidos. Contacta a soporte para desbloquearla.';
      } else if (!friendlyMessage) {
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
      return { success: false as const, error: friendlyMessage };
    }
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
        updateUser
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
