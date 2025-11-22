import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { GraduationCap, Mail, CheckCircle2 } from 'lucide-react';
import { toast } from 'sonner';

const RecuperarCuenta = () => {
  const [correo, setCorreo] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [emailSent, setEmailSent] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!correo.trim() || !correo.includes('@')) {
      toast.error('Por favor, ingresa un correo electrónico válido');
      return;
    }

    setIsLoading(true);
    
    // Simulate API call for password reset
    setTimeout(() => {
      setEmailSent(true);
      setIsLoading(false);
      toast.success('Solicitud enviada correctamente');
    }, 1500);
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary/5 via-background to-accent/5 p-4">
      <Card className="w-full max-w-md">
        <CardHeader className="space-y-1 text-center">
          <div className="flex justify-center mb-4">
            <div className="h-16 w-16 rounded-full bg-primary flex items-center justify-center">
              <GraduationCap className="h-8 w-8 text-primary-foreground" />
            </div>
          </div>
          <CardTitle className="text-2xl font-bold">
            Recuperar Cuenta
          </CardTitle>
          <CardDescription>
            {emailSent 
              ? 'Revisa tu correo electrónico' 
              : 'Ingresa tu correo institucional para recuperar el acceso'
            }
          </CardDescription>
        </CardHeader>

        <CardContent>
          {emailSent ? (
            <div className="space-y-4">
              <Alert className="border-success bg-success/10">
                <CheckCircle2 className="h-4 w-4 text-success" />
                <AlertDescription className="text-success">
                  Hemos enviado las instrucciones de recuperación a <strong>{correo}</strong>. 
                  Por favor revisa tu bandeja de entrada y sigue los pasos indicados.
                </AlertDescription>
              </Alert>
              
              <div className="space-y-2">
                <p className="text-sm text-muted-foreground">
                  Si no recibes el correo en los próximos minutos, verifica tu carpeta de spam 
                  o contacta al soporte técnico.
                </p>
                <Button asChild variant="outline" className="w-full">
                  <Link to="/login">Volver al inicio de sesión</Link>
                </Button>
              </div>
            </div>
          ) : (
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="email">
                  Correo Institucional
                  <span className="text-destructive ml-1">*</span>
                </Label>
                <div className="relative">
                  <Mail className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
                  <Input
                    id="email"
                    type="email"
                    placeholder="tu.nombre@universidad.edu"
                    value={correo}
                    onChange={(e) => setCorreo(e.target.value)}
                    className="pl-10"
                    required
                    disabled={isLoading}
                  />
                </div>
              </div>

              <div className="space-y-3">
                <Button type="submit" className="w-full" disabled={isLoading}>
                  {isLoading ? 'Enviando...' : 'Enviar Instrucciones'}
                </Button>
                
                <Button asChild variant="ghost" className="w-full">
                  <Link to="/login">Cancelar y volver</Link>
                </Button>
              </div>

              <div className="text-center pt-4 border-t">
                <p className="text-sm text-muted-foreground">
                  ¿Necesitas más ayuda?{' '}
                  <a 
                    href="mailto:soporte@universidad.edu" 
                    className="text-primary hover:underline font-medium"
                  >
                    Contactar Soporte
                  </a>
                </p>
              </div>
            </form>
          )}
        </CardContent>
      </Card>
    </div>
  );
};

export default RecuperarCuenta;
