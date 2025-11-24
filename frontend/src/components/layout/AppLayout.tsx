import { ReactNode } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { useAuth } from '@/contexts/AuthContext';
import { Button } from '@/components/ui/button';
import {
  LayoutDashboard,
  FolderKanban,
  CheckSquare,
  Bell,
  User,
  LogOut,
  GraduationCap,
} from 'lucide-react';
import { cn } from '@/lib/utils';
import { NotificationsDropdown } from './NotificationsDropdown';
import { MobileNav } from './MobileNav';

interface AppLayoutProps {
  children: ReactNode;
}

const AppLayout = ({ children }: AppLayoutProps) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const navigation = [
    {
      name: 'Dashboard',
      href: '/dashboard',
      icon: LayoutDashboard,
    },
    {
      name: 'Proyectos',
      href: '/proyectos',
      icon: FolderKanban,
    },
    {
      name: 'Notificaciones',
      href: '/notificaciones',
      icon: Bell,
    },
    {
      name: 'Perfil',
      href: '/perfil',
      icon: User,
    },
  ];

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-background">
      {/* Skip to main content link */}
      <a href="#main-content" className="skip-link">
        Saltar al contenido principal
      </a>

      {/* Header */}
      <header className="sticky top-0 z-40 border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <nav className="container flex h-16 items-center justify-between" role="navigation" aria-label="Navegación principal">
          <div className="flex items-center gap-2">
            <MobileNav />
            <GraduationCap className="h-6 w-6 text-primary" aria-hidden="true" />
            <h1 className="text-xl font-bold">
              <Link to="/dashboard" className="hover:text-primary transition-colors">
                Gestión Académica
              </Link>
            </h1>
          </div>

          <div className="flex items-center gap-2">
            <NotificationsDropdown />
            <span className="text-sm text-muted-foreground hidden sm:inline">
              {user?.nombre} ({user?.rol})
            </span>
            <Button
              variant="outline"
              size="sm"
              onClick={handleLogout}
              aria-label="Cerrar sesión"
              className="hidden sm:flex"
            >
              <LogOut className="h-4 w-4 mr-2" aria-hidden="true" />
              Salir
            </Button>
          </div>
        </nav>
      </header>

      <div className="container flex-1 items-start md:grid md:grid-cols-[220px_minmax(0,1fr)] md:gap-6 lg:grid-cols-[240px_minmax(0,1fr)] lg:gap-10 py-6">
        {/* Sidebar Navigation */}
        <aside className="fixed top-16 z-30 -ml-2 hidden h-[calc(100vh-4rem)] w-full shrink-0 md:sticky md:block">
          <nav className="h-full w-full" role="navigation" aria-label="Navegación secundaria">
            <ul className="flex flex-col gap-2 p-4">
              {navigation.map((item) => {
                const isActive = location.pathname === item.href;
                return (
                  <li key={item.name}>
                    <Link
                      to={item.href}
                      className={cn(
                        'flex items-center gap-3 rounded-lg px-3 py-2 text-sm transition-all hover:bg-accent',
                        isActive
                          ? 'bg-accent text-accent-foreground font-medium'
                          : 'text-muted-foreground hover:text-foreground'
                      )}
                      aria-current={isActive ? 'page' : undefined}
                    >
                      <item.icon className="h-4 w-4" aria-hidden="true" />
                      {item.name}
                    </Link>
                  </li>
                );
              })}
            </ul>
          </nav>
        </aside>

        {/* Main Content */}
        <main
          id="main-content"
          className="flex w-full flex-col overflow-hidden"
          role="main"
          tabIndex={-1}
        >
          {children}
        </main>
      </div>
    </div>
  );
};

export default AppLayout;
