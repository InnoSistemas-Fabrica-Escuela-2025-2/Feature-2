# Script para probar Docker Compose localmente
# Sin caracteres especiales para evitar problemas de encoding

param(
    [switch]$Build,
    [switch]$Down,
    [switch]$Status
)

$ErrorActionPreference = "Stop"

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  Docker Compose - Integration Test" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan

# Verificar Docker
try {
    docker --version | Out-Null
    Write-Host "[OK] Docker disponible" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Docker no disponible" -ForegroundColor Red
    exit 1
}

# Opción: Detener todo
if ($Down) {
    Write-Host "`n[DOWN] Deteniendo todos los servicios..." -ForegroundColor Yellow
    docker-compose down -v
    Write-Host "[OK] Servicios detenidos" -ForegroundColor Green
    exit 0
}

# Opción: Ver estado
if ($Status) {
    Write-Host "`n[STATUS] Estado de servicios:" -ForegroundColor Cyan
    docker-compose ps
    exit 0
}

# Opción: Build
if ($Build) {
    Write-Host "`n[BUILD] Construyendo imagenes..." -ForegroundColor Yellow
    docker-compose build --no-cache
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[OK] Imagenes construidas exitosamente" -ForegroundColor Green
    } else {
        Write-Host "[ERROR] Fallo en construccion" -ForegroundColor Red
        exit 1
    }
    exit 0
}

# Por defecto: Levantar servicios
Write-Host "`n[START] Iniciando servicios..." -ForegroundColor Yellow
Write-Host ""

# Fase 1: Infraestructura
Write-Host "Fase 1: Infraestructura (Postgres, Zookeeper, Kafka)" -ForegroundColor Cyan
docker-compose up -d postgres zookeeper kafka

Write-Host "  Esperando a que la infraestructura este lista (30s)..." -ForegroundColor Gray
Start-Sleep -Seconds 30

# Fase 2: Microservicios
Write-Host "`nFase 2: Microservicios (Authenticator, Innosistemas, Notifications)" -ForegroundColor Cyan
docker-compose up -d authenticator innosistemas notifications

Write-Host "  Esperando a que los microservicios esten listos (20s)..." -ForegroundColor Gray
Start-Sleep -Seconds 20

# Fase 3: Gateway
Write-Host "`nFase 3: Gateway" -ForegroundColor Cyan
docker-compose up -d gateway

Write-Host "  Esperando a que el gateway este listo (10s)..." -ForegroundColor Gray
Start-Sleep -Seconds 10

Write-Host "`n[OK] Todos los servicios iniciados" -ForegroundColor Green
Write-Host ""

# Mostrar estado final
Write-Host "Estado de servicios:" -ForegroundColor Cyan
docker-compose ps

Write-Host "`n=====================================" -ForegroundColor Cyan
Write-Host "Endpoints disponibles:" -ForegroundColor Yellow
Write-Host "  Gateway:        http://localhost:8080" -ForegroundColor White
Write-Host "  Authenticator:  http://localhost:8081/actuator/health" -ForegroundColor White
Write-Host "  Innosistemas:   http://localhost:8082/actuator/health" -ForegroundColor White
Write-Host "  Notifications:  http://localhost:8083/actuator/health" -ForegroundColor White
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Para ver logs: docker-compose logs -f [servicio]" -ForegroundColor Gray
Write-Host "Para detener:  .\test-docker.ps1 -Down" -ForegroundColor Gray
