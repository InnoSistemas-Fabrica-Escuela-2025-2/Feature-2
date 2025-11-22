import { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '@/contexts/AuthContext';
import {
  LayoutDashboard,
  FolderKanban,
  CheckSquare,
  Bell,
  User,
  LogOut,
  Menu,
  X,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Sheet, SheetContent, SheetHeader, SheetTitle, SheetTrigger } from '@/components/ui/sheet';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Separator } from '@/components/ui/separator';

export function MobileNav() {
  const { user, logout } = useAuth();
  const location = useLocation();
  const [open, setOpen] = useState(false);

  const navItems = [
    { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
    { to: '/proyectos', icon: FolderKanban, label: 'Proyectos' },
    { to: '/notificaciones', icon: Bell, label: 'Notificaciones' },
    { to: '/perfil', icon: User, label: 'Perfil' },
  ];

  const isActive = (path: string) => location.pathname === path;

  const handleLogout = () => {
    logout();
    setOpen(false);
  };

  return (
    <Sheet open={open} onOpenChange={setOpen}>
      <SheetTrigger asChild>
        <Button
          variant="ghost"
          size="icon"
          className="md:hidden"
          aria-label="Abrir menú de navegación"
        >
          <Menu className="h-6 w-6" aria-hidden="true" />
        </Button>
      </SheetTrigger>
      <SheetContent side="left" className="w-72">
        <SheetHeader>
          <SheetTitle className="text-left">
            Sistema de Gestión Académica
          </SheetTitle>
        </SheetHeader>

        <div className="mt-6 space-y-6">
          <div className="flex items-center gap-3 p-3 bg-accent rounded-lg">
            <Avatar>
              <AvatarFallback className="bg-primary text-primary-foreground">
                {user?.nombre.charAt(0).toUpperCase()}
              </AvatarFallback>
            </Avatar>
            <div className="flex-1 min-w-0">
              <p className="font-medium truncate">{user?.nombre}</p>
              <p className="text-sm text-muted-foreground capitalize">
                {user?.rol}
              </p>
            </div>
          </div>

          <Separator />

          <nav aria-label="Navegación principal móvil">
            <ul className="space-y-2" role="list">
              {navItems.map((item) => {
                const Icon = item.icon;
                const active = isActive(item.to);
                return (
                  <li key={item.to}>
                    <Link
                      to={item.to}
                      onClick={() => setOpen(false)}
                      className={`flex items-center gap-3 px-3 py-2 rounded-lg transition-colors ${
                        active
                          ? 'bg-primary text-primary-foreground'
                          : 'hover:bg-accent'
                      }`}
                      aria-current={active ? 'page' : undefined}
                    >
                      <Icon className="h-5 w-5" aria-hidden="true" />
                      <span>{item.label}</span>
                    </Link>
                  </li>
                );
              })}
            </ul>
          </nav>

          <Separator />

          <Button
            variant="ghost"
            className="w-full justify-start gap-3 text-destructive hover:text-destructive hover:bg-destructive/10"
            onClick={handleLogout}
            aria-label="Cerrar sesión"
          >
            <LogOut className="h-5 w-5" aria-hidden="true" />
            Cerrar sesión
          </Button>
        </div>
      </SheetContent>
    </Sheet>
  );
}
