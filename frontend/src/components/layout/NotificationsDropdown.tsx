import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/contexts/AuthContext';
import { mockNotifications } from '@/data/mockData';
import { Bell, Check } from 'lucide-react';
import { Button } from '@/components/ui/button';
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Separator } from '@/components/ui/separator';
import { Badge } from '@/components/ui/badge';
import { formatDistanceToNow } from 'date-fns';
import { es } from 'date-fns/locale';
import { toast } from 'sonner';

export function NotificationsDropdown() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [notifications, setNotifications] = useState(
    mockNotifications.filter(n => n.userId === user?.id && !n.leida)
  );

  const unreadCount = notifications.length;

  const getNotificationIcon = (tipo: string) => {
    const icons: Record<string, string> = {
      'tarea-proxima': '⏰',
      'tarea-vencida': '❌',
      'proyecto-actualizado': '📋',
      'asignacion-nueva': '📌',
    };
    return icons[tipo] || '🔔';
  };

  const handleNotificationClick = (relacionadoId?: string) => {
    if (relacionadoId) {
      setOpen(false);
      navigate(`/tareas/${relacionadoId}`);
    }
  };

  const markAsRead = (id: string, e: React.MouseEvent) => {
    e.stopPropagation();
    setNotifications(prev => prev.filter(n => n.id !== id));
    toast.success('Notificación marcada como leída');
  };

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Button
          variant="ghost"
          size="icon"
          className="relative"
          aria-label={`Notificaciones${unreadCount > 0 ? ` (${unreadCount} sin leer)` : ''}`}
        >
          <Bell className="h-5 w-5" aria-hidden="true" />
          {unreadCount > 0 && (
            <Badge
              className="absolute -top-1 -right-1 h-5 min-w-5 flex items-center justify-center p-0 text-xs"
              variant="destructive"
            >
              {unreadCount}
            </Badge>
          )}
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-80 p-0" align="end">
        <div className="flex items-center justify-between p-4 border-b">
          <h3 className="font-semibold">Centro de Notificaciones</h3>
          {unreadCount > 0 && (
            <Badge variant="secondary">{unreadCount} sin leer</Badge>
          )}
        </div>

        <ScrollArea className="h-[400px]">
          {notifications.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-8 px-4 text-center">
              <Bell className="h-12 w-12 text-muted-foreground/50 mb-3" aria-hidden="true" />
              <p className="text-sm text-muted-foreground">
                No tienes notificaciones sin leer
              </p>
            </div>
          ) : (
            <div className="divide-y">
              {notifications.map((notification) => (
                <div
                  key={notification.id}
                  className="p-4 hover:bg-accent/50 cursor-pointer transition-colors"
                  onClick={() => handleNotificationClick(notification.relacionadoId)}
                >
                  <div className="flex items-start gap-3">
                    <span className="text-xl flex-shrink-0">
                      {getNotificationIcon(notification.tipo)}
                    </span>
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium leading-tight mb-1">
                        {notification.mensaje}
                      </p>
                      <p className="text-xs text-muted-foreground">
                        {formatDistanceToNow(notification.fecha, {
                          addSuffix: true,
                          locale: es,
                        })}
                      </p>
                    </div>
                    <Button
                      size="icon"
                      variant="ghost"
                      className="h-8 w-8 flex-shrink-0"
                      onClick={(e) => markAsRead(notification.id, e)}
                      aria-label="Marcar como leída"
                    >
                      <Check className="h-4 w-4" aria-hidden="true" />
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </ScrollArea>

        {unreadCount > 0 && (
          <>
            <Separator />
            <div className="p-2">
              <Button
                variant="ghost"
                className="w-full justify-center text-sm"
                onClick={() => {
                  setOpen(false);
                  navigate('/notificaciones');
                }}
              >
                Ver todas las notificaciones
              </Button>
            </div>
          </>
        )}
      </PopoverContent>
    </Popover>
  );
}
