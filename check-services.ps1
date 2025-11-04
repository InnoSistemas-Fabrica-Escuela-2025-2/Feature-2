# Verifica que todos los servicios esten respondiendo

Write-Host "=== Verificando servicios ===" -ForegroundColor Cyan

$services = @(
    @{ Name = 'Gateway';       Url = 'http://localhost:8080/actuator/health' },
    @{ Name = 'Authenticator'; Url = 'http://localhost:8081/actuator/health' },
    @{ Name = 'Innosistemas';  Url = 'http://localhost:8082/actuator/health' },
    @{ Name = 'Frontend';      Url = 'http://localhost:5173' }
)

foreach ($svc in $services) {
    try {
        $response = Invoke-WebRequest -Uri $svc.Url -TimeoutSec 5 -ErrorAction Stop
        Write-Host "OK $($svc.Name) esta respondiendo" -ForegroundColor Green
    }
    catch {
        Write-Host "ERROR $($svc.Name) NO esta respondiendo" -ForegroundColor Red
        Write-Host "  URL: $($svc.Url)" -ForegroundColor Yellow
    }
}