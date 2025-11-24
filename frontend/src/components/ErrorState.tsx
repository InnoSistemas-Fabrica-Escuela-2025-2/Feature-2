import { WifiOff, AlertCircle } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';

interface ErrorStateProps {
  title?: string;
  message: string;
  onRetry: () => void;
  isRetrying?: boolean;
}

export function ErrorState({ 
  title = "ERROR OBTENCION DATOS",
  message, 
  onRetry, 
  isRetrying = false 
}: ErrorStateProps) {
  return (
    <div className="flex items-center justify-center min-h-[400px] p-4">
      <Card className="w-full max-w-md border-destructive/50 bg-destructive/5">
        <CardContent className="pt-6 pb-8 text-center space-y-6">
          {/* Error Icon */}
          <div className="flex justify-center">
            <div className="relative">
              <WifiOff 
                className="h-24 w-24 text-destructive" 
                strokeWidth={1.5}
                aria-hidden="true"
              />
              <AlertCircle 
                className="h-10 w-10 text-destructive absolute -bottom-1 -right-1 bg-background rounded-full" 
                aria-hidden="true"
              />
            </div>
          </div>

          {/* Error Title */}
          <div className="space-y-2">
            <h3 className="text-lg font-bold text-destructive uppercase tracking-wide">
              {title}
            </h3>
            
            {/* Error Message */}
            <p className="text-sm text-foreground leading-relaxed px-4">
              {message}
            </p>
          </div>

          {/* Retry Button */}
          <Button
            onClick={onRetry}
            disabled={isRetrying}
            size="lg"
            className="w-full max-w-xs mx-auto font-bold uppercase tracking-wider"
            variant="destructive"
          >
            {isRetrying ? 'Reintentando...' : 'REINTENTAR'}
          </Button>
        </CardContent>
      </Card>
    </div>
  );
}
