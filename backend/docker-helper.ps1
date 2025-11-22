# Docker Build and Test Script for Windows PowerShell
# Este script facilita la construcción y prueba de los servicios Docker

Write-Host "================================" -ForegroundColor Cyan
Write-Host "Docker Build & Deploy Helper" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

# Function to display menu
function Show-Menu {
    Write-Host "Selecciona una opción:" -ForegroundColor Yellow
    Write-Host "1. Construir todos los servicios"
    Write-Host "2. Iniciar todos los servicios (desarrollo)"
    Write-Host "3. Detener todos los servicios"
    Write-Host "4. Ver logs de servicios"
    Write-Host "5. Limpiar contenedores y volúmenes"
    Write-Host "6. Construir servicio individual"
    Write-Host "7. Probar health checks"
    Write-Host "8. Ver estado de servicios"
    Write-Host "9. Reconstruir un servicio específico"
    Write-Host "0. Salir"
    Write-Host ""
}

# Function to build all services
function Build-AllServices {
    Write-Host "Construyendo todos los servicios..." -ForegroundColor Green
    docker-compose build
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Servicios construidos exitosamente" -ForegroundColor Green
    } else {
        Write-Host "✗ Error al construir servicios" -ForegroundColor Red
    }
}

# Function to start all services
function Start-AllServices {
    Write-Host "Iniciando todos los servicios..." -ForegroundColor Green
    docker-compose up -d
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Servicios iniciados exitosamente" -ForegroundColor Green
        Write-Host ""
        Write-Host "URLs de los servicios:" -ForegroundColor Cyan
        Write-Host "  Gateway:        http://localhost:8080" -ForegroundColor White
        Write-Host "  Authenticator:  http://localhost:8081" -ForegroundColor White
        Write-Host "  Innosistemas:   http://localhost:8082" -ForegroundColor White
        Write-Host "  PostgreSQL:     localhost:5432" -ForegroundColor White
    } else {
        Write-Host "✗ Error al iniciar servicios" -ForegroundColor Red
    }
}

# Function to stop all services
function Stop-AllServices {
    Write-Host "Deteniendo todos los servicios..." -ForegroundColor Yellow
    docker-compose down
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Servicios detenidos exitosamente" -ForegroundColor Green
    } else {
        Write-Host "✗ Error al detener servicios" -ForegroundColor Red
    }
}

# Function to view logs
function Show-Logs {
    Write-Host "Selecciona el servicio:" -ForegroundColor Yellow
    Write-Host "1. Todos"
    Write-Host "2. Gateway"
    Write-Host "3. Authenticator"
    Write-Host "4. Innosistemas"
    Write-Host "5. PostgreSQL"
    
    $service = Read-Host "Opción"
    
    switch ($service) {
        "1" { docker-compose logs -f }
        "2" { docker-compose logs -f gateway }
        "3" { docker-compose logs -f authenticator }
        "4" { docker-compose logs -f innosistemas }
        "5" { docker-compose logs -f postgres }
        default { Write-Host "Opción inválida" -ForegroundColor Red }
    }
}

# Function to clean up
function Clean-All {
    Write-Host "⚠️  ADVERTENCIA: Esto eliminará todos los contenedores, redes y volúmenes" -ForegroundColor Red
    $confirm = Read-Host "¿Estás seguro? (s/n)"
    
    if ($confirm -eq "s" -or $confirm -eq "S") {
        Write-Host "Limpiando..." -ForegroundColor Yellow
        docker-compose down -v
        docker system prune -f
        Write-Host "✓ Limpieza completada" -ForegroundColor Green
    } else {
        Write-Host "Cancelado" -ForegroundColor Yellow
    }
}

# Function to build individual service
function Build-IndividualService {
    Write-Host "Selecciona el servicio a construir:" -ForegroundColor Yellow
    Write-Host "1. Gateway"
    Write-Host "2. Authenticator"
    Write-Host "3. Innosistemas"
    
    $service = Read-Host "Opción"
    
    switch ($service) {
        "1" { 
            Write-Host "Construyendo Gateway..." -ForegroundColor Green
            docker build -t gateway:latest ./gateway
        }
        "2" { 
            Write-Host "Construyendo Authenticator..." -ForegroundColor Green
            docker build -t authenticator:latest ./authenticator
        }
        "3" { 
            Write-Host "Construyendo Innosistemas..." -ForegroundColor Green
            docker build -t innosistemas:latest ./innosistemas
        }
        default { Write-Host "Opción inválida" -ForegroundColor Red }
    }
}

# Function to test health checks
function Test-HealthChecks {
    Write-Host "Probando health checks..." -ForegroundColor Green
    Write-Host ""
    
    Write-Host "Gateway (8080):" -ForegroundColor Cyan
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing -TimeoutSec 5
        Write-Host "  Status: $($response.StatusCode) - OK" -ForegroundColor Green
    } catch {
        Write-Host "  ✗ No disponible" -ForegroundColor Red
    }
    
    Write-Host "Authenticator (8081):" -ForegroundColor Cyan
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8081/actuator/health" -UseBasicParsing -TimeoutSec 5
        Write-Host "  Status: $($response.StatusCode) - OK" -ForegroundColor Green
    } catch {
        Write-Host "  ✗ No disponible" -ForegroundColor Red
    }
    
    Write-Host "Innosistemas (8082):" -ForegroundColor Cyan
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8082/actuator/health" -UseBasicParsing -TimeoutSec 5
        Write-Host "  Status: $($response.StatusCode) - OK" -ForegroundColor Green
    } catch {
        Write-Host "  ✗ No disponible" -ForegroundColor Red
    }
}

# Function to show service status
function Show-Status {
    Write-Host "Estado de los servicios:" -ForegroundColor Cyan
    docker-compose ps
}

# Function to rebuild specific service
function Rebuild-Service {
    Write-Host "Selecciona el servicio a reconstruir:" -ForegroundColor Yellow
    Write-Host "1. Gateway"
    Write-Host "2. Authenticator"
    Write-Host "3. Innosistemas"
    
    $service = Read-Host "Opción"
    
    $serviceName = switch ($service) {
        "1" { "gateway" }
        "2" { "authenticator" }
        "3" { "innosistemas" }
        default { $null }
    }
    
    if ($serviceName) {
        Write-Host "Reconstruyendo $serviceName..." -ForegroundColor Green
        docker-compose up -d --build $serviceName
        Write-Host "✓ $serviceName reconstruido" -ForegroundColor Green
    } else {
        Write-Host "Opción inválida" -ForegroundColor Red
    }
}

# Main loop
do {
    Show-Menu
    $option = Read-Host "Selecciona una opción"
    Write-Host ""
    
    switch ($option) {
        "1" { Build-AllServices }
        "2" { Start-AllServices }
        "3" { Stop-AllServices }
        "4" { Show-Logs }
        "5" { Clean-All }
        "6" { Build-IndividualService }
        "7" { Test-HealthChecks }
        "8" { Show-Status }
        "9" { Rebuild-Service }
        "0" { 
            Write-Host "¡Hasta luego!" -ForegroundColor Cyan
            break 
        }
        default { Write-Host "Opción inválida" -ForegroundColor Red }
    }
    
    Write-Host ""
    Write-Host "Presiona Enter para continuar..." -ForegroundColor Gray
    Read-Host
    Clear-Host
} while ($option -ne "0")
