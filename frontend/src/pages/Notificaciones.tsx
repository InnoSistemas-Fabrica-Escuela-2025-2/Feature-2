import { useState } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { mockNotifications } from '@/data/mockData';
import { Bell, Check, Trash2, Filter } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { toast } from 'sonner';
import { formatDistanceToNow } from 'date-fns';
import { es } from 'date-fns/locale';

export default function Notificaciones() {
  const { user } = useAuth();
  const [notifications, setNotifications] = useState(
    mockNotifications.filter(n => n.userId === user?.id)
  );

  const unreadCount = notifications.filter(n => !n.leida).length;

  const markAsRead = (id: string) => {
    setNotifications(prev =>
      prev.map(n => (n.id === id ? { ...n, leida: true } : n))
    );
    toast.success('Notificación marcada como leída');
  };

  const markAllAsRead = () => {
    setNotifications(prev => prev.map(n => ({ ...n, leida: true })));
    toast.success('Todas las notificaciones marcadas como leídas');
  };

  const deleteNotification = (id: string) => {
    setNotifications(prev => prev.filter(n => n.id !== id));
    toast.success('Notificación eliminada');
  };

  const getNotificationIcon = (tipo: string) => {
    const icons: Record<string, string> = {
      'tarea-proxima': '⏰',
      'tarea-vencida': '❌',
      'proyecto-actualizado': '📋',
      'asignacion-nueva': '📌',
    };
    return icons[tipo] || '🔔';
  };

  const filteredNotifications = (filter: 'all' | 'unread' | 'read') => {
    if (filter === 'unread') return notifications.filter(n => !n.leida);
    if (filter === 'read') return notifications.filter(n => n.leida);
    return notifications;
  };

  return (
    <div className="container mx-auto px-4 py-6 max-w-4xl">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-3xl font-bold text-foreground flex items-center gap-2">
            <Bell className="h-8 w-8" aria-hidden="true" />
            Notificaciones
          </h1>
          <p className="text-muted-foreground mt-1" role="status" aria-live="polite" aria-atomic="true">
            {unreadCount > 0
              ? `Tienes ${unreadCount} notificación${unreadCount !== 1 ? 'es' : ''} sin leer`
              : 'No tienes notificaciones sin leer'}
          </p>
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
        <TabsList className="grid w-full grid-cols-3 mb-6" role="tablist" aria-label="Filtros de notificaciones">
          <TabsTrigger value="all" className="gap-2" role="tab" aria-label="Ver todas las notificaciones">
            <Filter className="h-4 w-4" aria-hidden="true" />
            <span>Todas ({notifications.length})</span>
          </TabsTrigger>
          <TabsTrigger value="unread" className="gap-2" role="tab" aria-label="Ver notificaciones sin leer">
            <span>Sin leer ({unreadCount})</span>
          </TabsTrigger>
          <TabsTrigger value="read" className="gap-2" role="tab" aria-label="Ver notificaciones leídas">
            <span>Leídas ({notifications.length - unreadCount})</span>
          </TabsTrigger>
        </TabsList>

        {(['all', 'unread', 'read'] as const).map(filter => (
          <TabsContent key={filter} value={filter} className="space-y-4">
            {filteredNotifications(filter).length === 0 ? (
              <Card>
                <CardContent className="pt-6 text-center text-muted-foreground">
                  <Bell className="h-12 w-12 mx-auto mb-3 opacity-50" aria-hidden="true" />
                  <p>No hay notificaciones {filter === 'unread' ? 'sin leer' : filter === 'read' ? 'leídas' : ''}</p>
                </CardContent>
              </Card>
            ) : (
              filteredNotifications(filter).map(notification => (
                <Card
                  key={notification.id}
                  className={`transition-all ${
                    !notification.leida ? 'border-l-4 border-l-primary bg-accent/5' : ''
                  }`}
                  role="article"
                  aria-label={`Notificación: ${notification.mensaje}`}
                  aria-live={!notification.leida ? 'polite' : 'off'}
                >
                  <CardHeader>
                    <div className="flex items-start justify-between gap-4">
                      <div className="flex items-start gap-3 flex-1">
                        <span className="text-2xl" role="img" aria-label={notification.tipo}>
                          {getNotificationIcon(notification.tipo)}
                        </span>
                        <div className="flex-1">
                          <CardTitle className="text-lg">
                            {notification.mensaje}
                            {!notification.leida && (
                              <span className="sr-only"> (sin leer)</span>
                            )}
                          </CardTitle>
                          <CardDescription className="mt-1">
                            <time dateTime={notification.fecha.toISOString()}>
                              {formatDistanceToNow(notification.fecha, {
                                addSuffix: true,
                                locale: es,
                              })}
                            </time>
                          </CardDescription>
                        </div>
                      </div>
                      <div className="flex items-center gap-2">
                        {!notification.leida && (
                          <Button
                            size="sm"
                            variant="ghost"
                            onClick={() => markAsRead(notification.id)}
                            aria-label={`Marcar como leída: ${notification.mensaje}`}
                          >
                            <Check className="h-4 w-4" aria-hidden="true" />
                            <span className="sr-only">Marcar como leída</span>
                          </Button>
                        )}
                        <Button
                          size="sm"
                          variant="ghost"
                          onClick={() => deleteNotification(notification.id)}
                          aria-label={`Eliminar notificación: ${notification.mensaje}`}
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
        ))}
      </Tabs>
    </div>
  );
}
