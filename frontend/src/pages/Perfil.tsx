import { useState } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { User, Mail, Calendar, Shield, Save } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Switch } from '@/components/ui/switch';
import { toast } from 'sonner';
import { format } from 'date-fns';
import { es } from 'date-fns/locale';

export default function Perfil() {
  const { user, updateUser, isLoading } = useAuth();
  const [nombre, setNombre] = useState(user?.nombre || '');
  const [emailNotifications, setEmailNotifications] = useState(true);
  const [platformNotifications, setPlatformNotifications] = useState(true);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!nombre.trim()) {
      toast.error('El nombre no puede estar vacío');
      return;
    }

    await updateUser({ nombre });
  };

  if (!user) return null;

  return (
    <div className="container mx-auto px-4 py-6 max-w-4xl">
      <h1 className="text-3xl font-bold text-foreground mb-6 flex items-center gap-2">
        <User className="h-8 w-8" aria-hidden="true" />
        Mi Perfil
      </h1>

      <div className="grid gap-6">
        <Card>
          <CardHeader>
            <CardTitle>Información Personal</CardTitle>
            <CardDescription>
              Gestiona tu información institucional
            </CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-6">
              <div className="space-y-2">
                <Label htmlFor="nombre" className="required">
                  Nombre completo
                </Label>
                <Input
                  id="nombre"
                  value={nombre}
                  onChange={(e) => setNombre(e.target.value)}
                  required
                  aria-required="true"
                  aria-describedby="nombre-description"
                />
                <p id="nombre-description" className="text-sm text-muted-foreground">
                  Tu nombre completo como aparece en registros institucionales
                </p>
              </div>

              <div className="space-y-2">
                <Label htmlFor="correo">Correo electrónico institucional</Label>
                <div className="flex items-center gap-2">
                  <Mail className="h-4 w-4 text-muted-foreground" aria-hidden="true" />
                  <Input
                    id="correo"
                    value={user.correo}
                    disabled
                    aria-describedby="correo-description"
                  />
                </div>
                <p id="correo-description" className="text-sm text-muted-foreground">
                  El correo institucional no se puede modificar
                </p>
              </div>

              <div className="space-y-2">
                <Label htmlFor="rol">Rol en la plataforma</Label>
                <div className="flex items-center gap-2">
                  <Shield className="h-4 w-4 text-muted-foreground" aria-hidden="true" />
                  <Badge variant={user.rol === 'profesor' ? 'default' : 'secondary'}>
                    {user.rol === 'profesor' ? 'Profesor' : 'Estudiante'}
                  </Badge>
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="fecha-registro">Fecha de registro</Label>
                <div className="flex items-center gap-2">
                  <Calendar className="h-4 w-4 text-muted-foreground" aria-hidden="true" />
                  <Input
                    id="fecha-registro"
                    value={format(user.fechaRegistro, "d 'de' MMMM 'de' yyyy", { locale: es })}
                    disabled
                  />
                </div>
              </div>

              <Button
                type="submit"
                disabled={isLoading}
                className="w-full sm:w-auto gap-2"
                aria-label="Guardar cambios en el perfil"
              >
                <Save className="h-4 w-4" aria-hidden="true" />
                {isLoading ? 'Guardando...' : 'Guardar cambios'}
              </Button>
            </form>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Preferencias de Notificaciones</CardTitle>
            <CardDescription>
              Configura cómo deseas recibir notificaciones
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="flex items-center justify-between">
              <div className="space-y-1">
                <Label htmlFor="email-notifications" className="cursor-pointer">
                  Notificaciones por correo
                </Label>
                <p className="text-sm text-muted-foreground">
                  Recibe alertas sobre fechas de entrega en tu correo institucional
                </p>
              </div>
              <Switch
                id="email-notifications"
                checked={emailNotifications}
                onCheckedChange={(checked) => {
                  setEmailNotifications(checked);
                  toast.success(
                    checked
                      ? 'Notificaciones por correo activadas'
                      : 'Notificaciones por correo desactivadas'
                  );
                }}
                aria-describedby="email-notifications-description"
              />
            </div>

            <div className="flex items-center justify-between">
              <div className="space-y-1">
                <Label htmlFor="platform-notifications" className="cursor-pointer">
                  Notificaciones en la plataforma
                </Label>
                <p className="text-sm text-muted-foreground">
                  Recibe alertas dentro de la aplicación sobre tareas y proyectos
                </p>
              </div>
              <Switch
                id="platform-notifications"
                checked={platformNotifications}
                onCheckedChange={(checked) => {
                  setPlatformNotifications(checked);
                  toast.success(
                    checked
                      ? 'Notificaciones en plataforma activadas'
                      : 'Notificaciones en plataforma desactivadas'
                  );
                }}
                aria-describedby="platform-notifications-description"
              />
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
