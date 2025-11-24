# Script para probar la integración completa localmente
# Autor: GitHub Copilot
# Fecha: 2025-11-23

param(
    [switch]$Build,
    [switch]$Down,
    [switch]$Logs,
    [switch]$Status
)

$ErrorActionPreference = "Stop"

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   Test Local - Integration Branch   " -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Función para verificar Docker
function Test-Docker {
    try {
        docker --version | Out-Null
        Write-Host "✓ Docker está disponible" -ForegroundColor Green
        return $true
    } catch {
        Write-Host "✗ Docker no está disponible o no está ejecutándose" -ForegroundColor Red
        Write-Host "  Por favor inicia Docker Desktop" -ForegroundColor Yellow
        return $false
    }
}

# Función para mostrar estado de servicios
function Show-ServicesStatus {
    Write-Host "`n📊 Estado de los Servicios:" -ForegroundColor Cyan
    Write-Host "======================================" -ForegroundColor Cyan
    
    $containers = docker-compose ps --format json | ConvertFrom-Json
    
    if ($containers) {
        foreach ($container in $containers) {
            $name = $container.Name
            $state = $container.State
            $ports = $container.Publishers
            
            $statusIcon = if ($state -eq "running") { "✓" } else { "✗" }
            $statusColor = if ($state -eq "running") { "Green" } else { "Red" }
            
            Write-Host "$statusIcon $name" -ForegroundColor $statusColor -NoNewline
            if ($ports) {
                Write-Host " → $ports" -ForegroundColor Gray
            } else {
                Write-Host ""
            }
        }
    } else {
        Write-Host "No hay contenedores ejecutándose" -ForegroundColor Yellow
    }
}

# Función para verificar salud de endpoints
function Test-Endpoints {
    Write-Host "`n🔍 Verificando Endpoints:" -ForegroundColor Cyan
    Write-Host "======================================" -ForegroundColor Cyan
    
    $endpoints = @(
        @{ Name = "Gateway"; Url = "http://localhost:8080/actuator/health" },
        @{ Name = "Authenticator"; Url = "http://localhost:8081/actuator/health" },
        @{ Name = "Innosistemas"; Url = "http://localhost:8082/actuator/health" },
        @{ Name = "Notifications"; Url = "http://localhost:8083/actuator/health" }
    )
    
    foreach ($endpoint in $endpoints) {
        try {
            $response = Invoke-WebRequest -Uri $endpoint.Url -TimeoutSec 2 -UseBasicParsing
            if ($response.StatusCode -eq 200) {
                Write-Host "✓ $($endpoint.Name) - OK" -ForegroundColor Green
            }
        } catch {
            Write-Host "✗ $($endpoint.Name) - No responde" -ForegroundColor Red
        }
    }
}

# Verificar Docker
if (-not (Test-Docker)) {
    exit 1
}

# Manejar opciones
if ($Down) {
    Write-Host "`n🛑 Deteniendo servicios..." -ForegroundColor Yellow
    docker-compose down
    Write-Host "✓ Servicios detenidos" -ForegroundColor Green
    exit 0
}

if ($Logs) {
    Write-Host "`n📋 Mostrando logs (Ctrl+C para salir)..." -ForegroundColor Yellow
    docker-compose logs -f
    exit 0
}

if ($Status) {
    Show-ServicesStatus
    Test-Endpoints
    exit 0
}

# Build de imágenes
if ($Build) {
    Write-Host "`n🔨 Construyendo imágenes Docker..." -ForegroundColor Yellow
    Write-Host "Esto puede tomar varios minutos..." -ForegroundColor Gray
    
    docker-compose build --no-cache
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "✗ Error al construir imágenes" -ForegroundColor Red
        exit 1
    }
    
    Write-Host "✓ Imágenes construidas exitosamente" -ForegroundColor Green
}

# Iniciar servicios
Write-Host "`n🚀 Iniciando servicios..." -ForegroundColor Yellow

# Primero infraestructura (Postgres, Kafka, Zookeeper)
Write-Host "`n1️⃣ Levantando infraestructura (Postgres, Zookeeper, Kafka)..." -ForegroundColor Cyan
docker-compose up -d postgres zookeeper kafka

Write-Host "`n   Esperando a que Kafka esté listo (30 segundos)..." -ForegroundColor Gray
Start-Sleep -Seconds 30

# Luego microservicios
Write-Host "`n2️⃣ Levantando microservicios..." -ForegroundColor Cyan
docker-compose up -d authenticator innosistemas notifications

Write-Host "`n   Esperando a que los servicios estén listos (20 segundos)..." -ForegroundColor Gray
Start-Sleep -Seconds 20

# Finalmente gateway
Write-Host "`n3️⃣ Levantando Gateway..." -ForegroundColor Cyan
docker-compose up -d gateway

Write-Host "`n   Esperando a que Gateway esté listo (10 segundos)..." -ForegroundColor Gray
Start-Sleep -Seconds 10

# Mostrar estado
Show-ServicesStatus
Test-Endpoints

# Instrucciones finales
Write-Host "`n" -NoNewline
Write-Host "=====================================" -ForegroundColor Green
Write-Host "   ✓ Sistema Iniciado Correctamente   " -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green
Write-Host ""
Write-Host "📌 Servicios disponibles:" -ForegroundColor Cyan
Write-Host "   • Gateway:        http://localhost:8080" -ForegroundColor White
Write-Host "   • Authenticator:  http://localhost:8081" -ForegroundColor White
Write-Host "   • Innosistemas:   http://localhost:8082" -ForegroundColor White
Write-Host "   • Notifications:  http://localhost:8083" -ForegroundColor White
Write-Host "   • PostgreSQL:     localhost:5432" -ForegroundColor White
Write-Host "   • Kafka:          localhost:9092" -ForegroundColor White
Write-Host ""
Write-Host "📋 Comandos útiles:" -ForegroundColor Cyan
Write-Host "   .\test-local.ps1 -Status    # Ver estado de servicios" -ForegroundColor Gray
Write-Host "   .\test-local.ps1 -Logs      # Ver logs en tiempo real" -ForegroundColor Gray
Write-Host "   .\test-local.ps1 -Down      # Detener todos los servicios" -ForegroundColor Gray
Write-Host ""
Write-Host "🎯 Siguiente paso:" -ForegroundColor Yellow
Write-Host "   cd ..\frontend" -ForegroundColor White
Write-Host "   npm run dev" -ForegroundColor White
Write-Host ""
