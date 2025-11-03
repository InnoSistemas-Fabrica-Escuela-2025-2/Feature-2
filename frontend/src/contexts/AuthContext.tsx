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

    // Check if account is permanently blocked
    if (loginAttempts.permanentlyBlocked) {
      setIsLoading(false);
      return {
        success: false,
        error: 'Has alcanzado el límite de intentos. Contacta a soporte para desbloquear tu cuenta.'
      };
    }

    // Check if account is temporarily blocked
    if (loginAttempts.blockedUntil && now < loginAttempts.blockedUntil) {
      const remainingTime = Math.ceil((loginAttempts.blockedUntil - now) / 60000);
      setIsLoading(false);
      return {
        success: false,
        error: `Cuenta bloqueada. Intenta de nuevo en ${remainingTime} minuto${remainingTime > 1 ? 's' : ''}.`
      };
    }

    // If block time has passed, clear it
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

    // Real API call to backend
    try {
      const response = await authApi.login({ email: correo, password: contrasena });
      
      // Transform backend response to match our User type
      const loggedInUser: User = {
        id: response.data.email, // Using email as ID for now
        nombre: response.data.email.split('@')[0], // Extract name from email
        correo: response.data.email,
        rol: response.data.role as 'estudiante' | 'profesor',
        fechaRegistro: new Date()
      };
      
      // Successful login - reset attempts
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
      setIsLoading(false);
      return { success: true };
    } catch (error: any) {
      // Login failed - handle error from backend
      console.error('Login error:', error);
      
      // Failed login - increment attempts
      const newAttempts: LoginAttempt = {
        ...loginAttempts,
        count: loginAttempts.count + 1,
        lastAttempt: now,
        blockedUntil: null,
      };

      // Determine max attempts based on whether first block has passed
      const maxAttempts = loginAttempts.firstBlockPassed ? 2 : 5;

      // Check if should block or permanently block
      if (newAttempts.count >= maxAttempts) {
        if (loginAttempts.firstBlockPassed) {
          // After second chance, block permanently
          newAttempts.permanentlyBlocked = true;
          newAttempts.count = 0;
          setLoginAttempts(newAttempts);
          localStorage.setItem('loginAttempts', JSON.stringify(newAttempts));
          setIsLoading(false);
          return { 
            success: false, 
            error: 'Has alcanzado el límite de intentos. Contacta a soporte para desbloquear tu cuenta.' 
          };
        } else {
          // First block: 15 minutes
          newAttempts.blockedUntil = now + (15 * 60 * 1000);
          newAttempts.count = 0;
          setLoginAttempts(newAttempts);
          localStorage.setItem('loginAttempts', JSON.stringify(newAttempts));
          setIsLoading(false);
          return { 
            success: false, 
            error: 'Has superado el límite de intentos. Cuenta bloqueada por 15 minutos.' 
          };
        }
      }

      setLoginAttempts(newAttempts);
      localStorage.setItem('loginAttempts', JSON.stringify(newAttempts));
      
      setIsLoading(false);
      
      const remainingAttempts = maxAttempts - newAttempts.count;
      const backendError = error.response?.data?.message || error.message || 'Credenciales incorrectas';
      const errorMessage = `${backendError}. Te quedan ${remainingAttempts} intento${remainingAttempts > 1 ? 's' : ''}.`;
      
      return { success: false, error: errorMessage };
    }
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
