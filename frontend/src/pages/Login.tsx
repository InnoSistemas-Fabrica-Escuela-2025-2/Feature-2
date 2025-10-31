import { useState, FormEvent } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from '@/contexts/AuthContext';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { GraduationCap, Lock, Mail, AlertCircle } from 'lucide-react';

const Login = () => {
  const { isAuthenticated, login, isLoading } = useAuth();
  const navigate = useNavigate();
  const [correo, setCorreo] = useState('');
  const [contrasena, setContrasena] = useState('');
  const [error, setError] = useState('');

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');

    // Client-side validation
    if (!correo || !contrasena) {
      setError('Por favor, completa todos los campos obligatorios');
      return;
    }

    if (!correo.trim()) {
      setError('El correo electrónico no puede estar vacío');
      return;
    }

    if (!correo.includes('@') || !correo.includes('.')) {
      setError('Por favor, ingresa un correo electrónico válido');
      return;
    }

    if (!contrasena.trim()) {
      setError('La contraseña no puede estar vacía');
      return;
    }

    if (contrasena.length < 6) {
      setError('La contraseña debe tener al menos 6 caracteres');
      return;
    }

    const result = await login(correo, contrasena);

    if (result.success) {
      navigate('/dashboard');
    } else {
      setError(result.error || 'Error al iniciar sesión. Intenta de nuevo.');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary/5 via-background to-accent/5 p-4">
      {/* Skip to main content link for accessibility */}
      <a href="#main-content" className="skip-link">
        Saltar al contenido principal
      </a>

      <Card className="w-full max-w-md" id="main-content">
        <CardHeader className="space-y-1 text-center">
          <div className="flex justify-center mb-4">
            <div className="h-16 w-16 rounded-full bg-primary flex items-center justify-center" aria-hidden="true">
              <GraduationCap className="h-8 w-8 text-primary-foreground" />
            </div>
          </div>
          <CardTitle className="text-2xl font-bold">
            Sistema de Gestión Académica
          </CardTitle>
          <CardDescription>
            Ingresa con tus credenciales institucionales para acceder a tus proyectos
          </CardDescription>
        </CardHeader>

        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4" noValidate>
            {error && (
              <Alert 
                variant="destructive" 
                role="alert" 
                aria-live="assertive"
                aria-atomic="true"
              >
                <AlertCircle className="h-4 w-4" aria-hidden="true" />
                <AlertDescription id="login-error">{error}</AlertDescription>
              </Alert>
            )}

            <div className="space-y-2">
              <Label htmlFor="email">
                Correo Institucional
                <span className="text-destructive ml-1" aria-label="campo obligatorio">*</span>
              </Label>
              <div className="relative">
                <Mail 
                  className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" 
                  aria-hidden="true"
                />
                <Input
                  id="email"
                  type="email"
                  placeholder="tu.nombre@universidad.edu"
                  value={correo}
                  onChange={(e) => setCorreo(e.target.value)}
                  className="pl-10"
                  required
                  aria-required="true"
                  aria-invalid={error && !correo ? 'true' : 'false'}
                  aria-describedby={error ? "login-error" : undefined}
                  autoComplete="email"
                  disabled={isLoading}
                />
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="password">
                Contraseña
                <span className="text-destructive ml-1" aria-label="campo obligatorio">*</span>
              </Label>
              <div className="relative">
                <Lock 
                  className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" 
                  aria-hidden="true"
                />
                <Input
                  id="password"
                  type="password"
                  placeholder="••••••••"
                  value={contrasena}
                  onChange={(e) => setContrasena(e.target.value)}
                  className="pl-10"
                  required
                  aria-required="true"
                  aria-invalid={error && !contrasena ? 'true' : 'false'}
                  aria-describedby={error ? "login-error" : undefined}
                  autoComplete="current-password"
                  disabled={isLoading}
                  minLength={6}
                />
              </div>
            </div>

            <Button
              type="submit"
              className="w-full"
              disabled={isLoading}
              aria-busy={isLoading}
            >
              {isLoading ? 'Iniciando sesión...' : 'Iniciar Sesión'}
            </Button>

            <div className="mt-4 p-4 bg-muted rounded-lg">
              <p className="text-sm text-muted-foreground text-center mb-2">
                <strong>Usuarios de prueba:</strong>
              </p>
              <ul className="text-xs text-muted-foreground space-y-1">
                <li>• <strong>Estudiante:</strong> maria.garcia@universidad.edu</li>
                <li>• <strong>Profesor:</strong> carlos.rodriguez@universidad.edu</li>
                <li>• <strong>Contraseña:</strong> cualquier texto (mínimo 6 caracteres)</li>
              </ul>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};

export default Login;
