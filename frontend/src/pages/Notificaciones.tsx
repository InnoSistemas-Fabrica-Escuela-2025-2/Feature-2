import { useState, useEffect } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { notificationsApi } from '@/lib/api';
import { Bell, Check, Trash2, Filter } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { toast } from 'sonner';

interface Notification {
  id: number;
  id_student: number;
  name: string;
  content: string;
}

export default function Notificaciones() {
  const { user } = useAuth();
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (user?.id) {
      loadNotifications();
    }
  }, [user?.id]);

  const loadNotifications = async () => {
    if (!user?.id) return;
    
    setLoading(true);
    try {
      const response = await notificationsApi.getByStudentId(user.id);
      setNotifications(response.data || []);
    } catch (error) {
      console.error('Error al cargar notificaciones:', error);
      toast.error('No se pudieron cargar las notificaciones');
      setNotifications([]);
    } finally {
      setLoading(false);
    }
  };

  const unreadCount = notifications.length;

  const hasUnread = unreadCount > 0;
  const isPlural = unreadCount === 1;

  let notificationMessage: string;

  if (hasUnread) {
    notificationMessage = `Tienes ${unreadCount} notificación${isPlural ? '' : 'es'} sin leer`;
  } else {
    notificationMessage = 'No tienes notificaciones sin leer';
  }

  const markAsRead = async (id: number) => {
    try {
      await notificationsApi.delete(id);
      setNotifications(prev => prev.filter(n => n.id !== id));
      toast.success('Notificación marcada como leída');
    } catch (error) {
      console.error('Error al marcar como leída:', error);
      toast.error('No se pudo marcar la notificación como leída');
    }
  };

  const markAllAsRead = async () => {
    try {
      // Eliminar todas las notificaciones
      await Promise.all(notifications.map(n => notificationsApi.delete(n.id)));
      setNotifications([]);
      toast.success('Todas las notificaciones marcadas como leídas');
    } catch (error) {
      console.error('Error al marcar todas como leídas:', error);
      toast.error('No se pudieron marcar todas las notificaciones');
    }
  };

  const deleteNotification = async (id: number) => {
    try {
      await notificationsApi.delete(id);
      setNotifications(prev => prev.filter(n => n.id !== id));
      toast.success('Notificación eliminada');
    } catch (error) {
      console.error('Error al eliminar:', error);
      toast.error('No se pudo eliminar la notificación');
    }
  };

  return (
    <div className="container mx-auto px-4 py-6 max-w-4xl">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-3xl font-bold text-foreground flex items-center gap-2">
            <Bell className="h-8 w-8" aria-hidden="true" />
            Notificaciones
          </h1>
          <p className="text-muted-foreground mt-1">{notificationMessage}</p>
        </div>
        {unreadCount > 0 && (
          <Button
            onClick={markAllAsRead}
            variant="outline"
            className="gap-2"
            aria-label="Marcar todas las notificaciones como leídas"
          >
            <Check className="h-4 w-4" aria-hidden="true" />
            Marcar todas como leídas
          </Button>
        )}
      </div>

      <Tabs defaultValue="all" className="w-full">
        <TabsList className="grid w-full grid-cols-1 mb-6" role="tablist" aria-label="Filtros de notificaciones">
          <TabsTrigger value="all" className="gap-2" role="tab" aria-label="Ver todas las notificaciones">
            <Filter className="h-4 w-4" aria-hidden="true" />
            <span>Todas ({notifications.length})</span>
          </TabsTrigger>
        </TabsList>

        <TabsContent value="all" className="space-y-4">
          {loading ? (
            <Card>
              <CardContent className="pt-6 text-center text-muted-foreground">
                <p>Cargando notificaciones...</p>
              </CardContent>
            </Card>
          ) : notifications.length === 0 ? (
            <Card>
              <CardContent className="pt-6 text-center text-muted-foreground">
                <Bell className="h-12 w-12 mx-auto mb-3 opacity-50" aria-hidden="true" />
                <p>No hay notificaciones</p>
              </CardContent>
            </Card>
          ) : (
            notifications.map(notification => (
              <Card
                key={notification.id}
                className="transition-all border-l-4 border-l-primary bg-accent/5"
              >
                <CardHeader>
                  <div className="flex items-start justify-between gap-4">
                    <div className="flex items-start gap-3 flex-1">
                      <span className="text-2xl">
                        🔔
                      </span>
                      <div className="flex-1">
                        <CardTitle className="text-lg">
                          {notification.name}
                        </CardTitle>
                        <CardDescription className="mt-1">
                          {notification.content}
                        </CardDescription>
                      </div>
                    </div>
                    <div className="flex items-center gap-2">
                      <Button
                        size="sm"
                        variant="ghost"
                        onClick={() => markAsRead(notification.id)}
                        aria-label="Marcar como leída"
                      >
                        <Check className="h-4 w-4" aria-hidden="true" />
                        <span className="sr-only">Marcar como leída</span>
                      </Button>
                      <Button
                        size="sm"
                        variant="ghost"
                        onClick={() => deleteNotification(notification.id)}
                        aria-label="Eliminar notificación"
                      >
                        <Trash2 className="h-4 w-4" aria-hidden="true" />
                        <span className="sr-only">Eliminar</span>
                      </Button>
                    </div>
                  </div>
                </CardHeader>
              </Card>
            ))
          )}
        </TabsContent>
      </Tabs>
    </div>
  );
}
