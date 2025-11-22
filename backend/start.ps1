# Quick Start Script - Inicia todos los servicios rápidamente
Write-Host "🚀 Iniciando servicios backend..." -ForegroundColor Cyan

# Verificar si Docker está corriendo
$dockerRunning = docker info 2>&1 | Select-String "Server Version"
if (-not $dockerRunning) {
    Write-Host "❌ Docker no está corriendo. Por favor inicia Docker Desktop." -ForegroundColor Red
    exit 1
}

Write-Host "✓ Docker está corriendo" -ForegroundColor Green

# Navegar al directorio backend
$backendDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $backendDir

Write-Host "📦 Construyendo servicios..." -ForegroundColor Yellow
docker-compose build

if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Build exitoso" -ForegroundColor Green
    
    Write-Host "🚢 Iniciando contenedores..." -ForegroundColor Yellow
    docker-compose up -d
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "✅ Servicios iniciados exitosamente!" -ForegroundColor Green
        Write-Host ""
        Write-Host "📍 URLs de los servicios:" -ForegroundColor Cyan
        Write-Host "   Gateway:        http://localhost:8080" -ForegroundColor White
        Write-Host "   Authenticator:  http://localhost:8081" -ForegroundColor White
        Write-Host "   Innosistemas:   http://localhost:8082" -ForegroundColor White
        Write-Host ""
        Write-Host "🔍 Health Checks:" -ForegroundColor Cyan
        Write-Host "   Gateway:        http://localhost:8080/actuator/health" -ForegroundColor White
        Write-Host "   Authenticator:  http://localhost:8081/actuator/health" -ForegroundColor White
        Write-Host "   Innosistemas:   http://localhost:8082/actuator/health" -ForegroundColor White
        Write-Host ""
        Write-Host "📊 Ver logs: docker-compose logs -f" -ForegroundColor Gray
        Write-Host "⏹️  Detener: docker-compose down" -ForegroundColor Gray
    } else {
        Write-Host "❌ Error al iniciar servicios" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "❌ Error al construir servicios" -ForegroundColor Red
    exit 1
}
