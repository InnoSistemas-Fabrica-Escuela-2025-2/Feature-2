# Ejecuta cada servicio en una nueva ventana externa de PowerShell (no se cierran)

$root = $PSScriptRoot

Write-Host "=== Iniciando todos los servicios ===" -ForegroundColor Green
Write-Host ""

$targets = @(
    @{ Name = 'Gateway';       Dir = Join-Path $root 'backend\gateway';       Cmd = '.\mvnw'; Args = 'spring-boot:run', '-Dspring-boot.run.arguments=--spring.profiles.active=local' },
    @{ Name = 'Authenticator'; Dir = Join-Path $root 'backend\authenticator'; Cmd = '.\mvnw'; Args = 'spring-boot:run', '-Dspring-boot.run.arguments=--spring.profiles.active=local' },
    @{ Name = 'InnoSistemas';  Dir = Join-Path $root 'backend\innosistemas';  Cmd = '.\mvnw'; Args = 'spring-boot:run', '-Dspring-boot.run.arguments=--spring.profiles.active=local' },
    @{ Name = 'Frontend';      Dir = Join-Path $root 'frontend';              Cmd = 'npm'; Args = 'run', 'dev' }
)

foreach ($t in $targets) {
    Write-Host "Abriendo $($t.Name) en nueva ventana..." -ForegroundColor Cyan
    Write-Host "  Directorio: $($t.Dir)" -ForegroundColor Gray
    Write-Host "  Comando: $($t.Cmd) $($t.Args -join ' ')" -ForegroundColor Gray
    Write-Host ""
    
    if ($t.Args) {
        $cmdString = "cd '$($t.Dir)'; & '$($t.Cmd)' $($t.Args -join ' ')"
    } else {
        $cmdString = "cd '$($t.Dir)'; & '$($t.Cmd)'"
    }
    
    Start-Process -FilePath 'powershell.exe' -ArgumentList '-NoExit', '-Command', $cmdString
    Start-Sleep -Seconds 2
}

Write-Host "=== Todos los servicios se están iniciando ===" -ForegroundColor Green
Write-Host ""
Write-Host "Se han abierto 4 ventanas de PowerShell:" -ForegroundColor Yellow
Write-Host "  1. Gateway (puerto 8080)" -ForegroundColor White
Write-Host "  2. Authenticator" -ForegroundColor White
Write-Host "  3. InnoSistemas" -ForegroundColor White
Write-Host "  4. Frontend (puerto 5173)" -ForegroundColor White
Write-Host ""
Write-Host "Espera unos momentos a que todos los servicios inicien completamente." -ForegroundColor Yellow
