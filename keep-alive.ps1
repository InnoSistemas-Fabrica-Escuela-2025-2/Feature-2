# Script para mantener los servicios de Render activos
# Ejecuta cada 10 minutos para evitar que los servicios se duerman

$services = @(
    "https://innosistemas-core.onrender.com/actuator/health",
    "https://innosistemas-notifications.onrender.com/actuator/health",
    "https://innosistemas-gateway.onrender.com/actuator/health"
)

Write-Host "Iniciando keep-alive para servicios de Render..." -ForegroundColor Green
Write-Host "Presiona Ctrl+C para detener" -ForegroundColor Yellow
Write-Host ""

while ($true) {
    $timestamp = Get-Date -Format "HH:mm:ss"
    Write-Host "[$timestamp] Ping a servicios..." -ForegroundColor Cyan
    
    foreach ($service in $services) {
        try {
            $response = Invoke-RestMethod -Uri $service -Method GET -TimeoutSec 10 -ErrorAction Stop
            Write-Host "  ✓ $service - OK" -ForegroundColor Green
        } catch {
            Write-Host "  ✗ $service - Error: $($_.Exception.Message)" -ForegroundColor Red
        }
    }
    
    Write-Host "Esperando 10 minutos hasta el siguiente ping..." -ForegroundColor Gray
    Write-Host ""
    Start-Sleep -Seconds 600  # 10 minutos
}
