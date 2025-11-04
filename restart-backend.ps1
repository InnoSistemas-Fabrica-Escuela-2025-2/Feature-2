# Script para reiniciar todos los servicios backend

Write-Host "`n=============================================" -ForegroundColor Cyan
Write-Host "   REINICIANDO SERVICIOS BACKEND" -ForegroundColor Cyan
Write-Host "=============================================`n" -ForegroundColor Cyan

$root = $PSScriptRoot

# Matar procesos en los puertos
$ports = @(
    @{ Port = 8080; Name = 'Gateway' },
    @{ Port = 8081; Name = 'Authenticator' },
    @{ Port = 8082; Name = 'InnoSistemas' }
)

foreach ($p in $ports) {
    Write-Host "Verificando puerto $($p.Port) ($($p.Name))..." -NoNewline
    $process = Get-NetTCPConnection -LocalPort $p.Port -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty OwningProcess
    
    if ($process) {
        Write-Host " Deteniendo (PID: $process)" -ForegroundColor Yellow
        Stop-Process -Id $process -Force -ErrorAction SilentlyContinue
    } else {
        Write-Host " Libre" -ForegroundColor Green
    }
}

Write-Host "`nEsperando 3 segundos...`n" -ForegroundColor Gray
Start-Sleep -Seconds 3

# Iniciar servicios en ventanas separadas
$services = @(
    @{ Name = 'Gateway';       Dir = Join-Path $root 'backend\gateway';       Cmd = '.\mvnw.cmd spring-boot:run' },
    @{ Name = 'Authenticator'; Dir = Join-Path $root 'backend\authenticator'; Cmd = '.\mvnw.cmd spring-boot:run' },
    @{ Name = 'InnoSistemas';  Dir = Join-Path $root 'backend\innosistemas';  Cmd = '.\mvnw.cmd spring-boot:run' }
)

foreach ($svc in $services) {
    Write-Host "Iniciando $($svc.Name)..." -ForegroundColor Cyan
    
    Start-Process powershell.exe -ArgumentList @(
        '-NoExit',
        '-Command',
        "Write-Host 'Iniciando $($svc.Name)...' -ForegroundColor Cyan; cd '$($svc.Dir)'; $($svc.Cmd)"
    )
    
    Start-Sleep -Seconds 2
}

Write-Host "`n=============================================" -ForegroundColor Cyan
Write-Host "   SERVICIOS BACKEND REINICIADOS" -ForegroundColor Cyan
Write-Host "=============================================`n" -ForegroundColor Cyan

Write-Host "Gateway:       http://localhost:8080" -ForegroundColor Green
Write-Host "Authenticator: http://localhost:8081" -ForegroundColor Green
Write-Host "InnoSistemas:  http://localhost:8082" -ForegroundColor Green

Write-Host "`nEspera 30-60 segundos para que Spring Boot inicie.`n" -ForegroundColor Yellow
