# Ejecuta cada servicio en una nueva ventana externa de PowerShell (no se cierran)

$root = $PSScriptRoot

Write-Host "=== Iniciando todos los servicios ===" -ForegroundColor Green
Write-Host ""

$targets = @(
    @{ Name = 'Gateway';       Dir = Join-Path $root 'backend\gateway';       Cmd = '.\mvnw spring-boot:run' },
    @{ Name = 'Authenticator'; Dir = Join-Path $root 'backend\authenticator'; Cmd = '.\mvnw spring-boot:run' },
    @{ Name = 'InnoSistemas';  Dir = Join-Path $root 'backend\innosistemas';  Cmd = '.\mvnw spring-boot:run' },
    @{ Name = 'Frontend';      Dir = Join-Path $root 'frontend';              Cmd = 'npm run dev' }
)

foreach ($t in $targets) {
    Write-Host "Abriendo $($t.Name) en nueva ventana..." -ForegroundColor Cyan
    Write-Host "  Directorio: $($t.Dir)" -ForegroundColor Gray
    Write-Host "  Comando: $($t.Cmd)" -ForegroundColor Gray
    Write-Host ""
    
    Start-Process -FilePath 'powershell.exe' -ArgumentList '-NoExit','-Command',$t.Cmd -WorkingDirectory $t.Dir
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
