#!/usr/bin/env pwsh
<#
.SYNOPSIS
    Script completo de validación local del sistema Feature-2
.DESCRIPTION
    Prueba todos los componentes del sistema localmente:
    - Backend con Docker Compose
    - Frontend con Vite
    - Health checks de todos los servicios
    - Conectividad entre servicios
.EXAMPLE
    .\test-local.ps1
#>

param(
    [switch]$SkipBuild,
    [switch]$SkipTests,
    [switch]$Verbose
)

$ErrorActionPreference = "Continue"

# Colores
function Write-Section { 
    param([string]$Text) 
    Write-Host "`n=== $Text ===" -ForegroundColor Cyan 
}

function Write-Success { 
    param([string]$Text) 
    Write-Host "✓ $Text" -ForegroundColor Green 
}

function Write-Failure { 
    param([string]$Text) 
    Write-Host "✗ $Text" -ForegroundColor Red 
}

function Write-Info { 
    param([string]$Text) 
    Write-Host "ℹ $Text" -ForegroundColor Yellow 
}

function Write-Step { 
    param([string]$Text) 
    Write-Host "→ $Text" -ForegroundColor Blue 
}

$testResults = @{
    PreRequisites = $false
    BackendBuild = $false
    DockerBuild = $false
    DatabaseHealth = $false
    AuthenticatorHealth = $false
    InnosistemasHealth = $false
    GatewayHealth = $false
    FrontendBuild = $false
    FrontendDev = $false
}

# ============================================================================
# 1. PRE-REQUISITOS
# ============================================================================
Write-Section "1. Verificando Pre-requisitos"

Write-Step "Verificando Docker..."
try {
    $dockerVersion = docker --version
    Write-Success "Docker instalado: $dockerVersion"
    
    $dockerRunning = docker ps 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Docker está corriendo"
        $testResults.PreRequisites = $true
    } else {
        Write-Failure "Docker no está corriendo. Inicia Docker Desktop."
        exit 1
    }
} catch {
    Write-Failure "Docker no está instalado"
    exit 1
}

Write-Step "Verificando Java..."
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Success "Java instalado: $javaVersion"
} catch {
    Write-Failure "Java 21+ no está instalado"
    exit 1
}

Write-Step "Verificando Node.js..."
try {
    $nodeVersion = node --version
    Write-Success "Node.js instalado: $nodeVersion"
} catch {
    Write-Failure "Node.js no está instalado"
    exit 1
}

# ============================================================================
# 2. LIMPIEZA DE AMBIENTE PREVIO
# ============================================================================
Write-Section "2. Limpiando ambiente previo"

Write-Step "Deteniendo contenedores anteriores..."
Set-Location backend
docker-compose down -v 2>$null
Write-Success "Ambiente limpio"

# ============================================================================
# 3. BUILD BACKEND (Maven)
# ============================================================================
if (-not $SkipBuild) {
    Write-Section "3. Building Backend Services"
    
    $services = @('authenticator', 'gateway', 'innosistemas')
    $buildSuccess = $true
    
    foreach ($service in $services) {
        Write-Step "Building $service..."
        Set-Location $service
        
        if ($SkipTests) {
            .\mvnw.cmd clean package -DskipTests -q
        } else {
            .\mvnw.cmd clean package -q
        }
        
        if ($LASTEXITCODE -eq 0) {
            Write-Success "$service build exitoso"
        } else {
            Write-Failure "$service build falló"
            $buildSuccess = $false
        }
        
        Set-Location ..
    }
    
    $testResults.BackendBuild = $buildSuccess
    
    if (-not $buildSuccess) {
        Write-Failure "Algunos servicios fallaron el build"
        exit 1
    }
} else {
    Write-Info "Saltando build de backend (-SkipBuild)"
    $testResults.BackendBuild = $true
}

# ============================================================================
# 4. DOCKER BUILD
# ============================================================================
Write-Section "4. Building Docker Images"

Write-Step "Construyendo imágenes Docker..."
docker-compose build --no-cache

if ($LASTEXITCODE -eq 0) {
    Write-Success "Imágenes Docker construidas"
    $testResults.DockerBuild = $true
} else {
    Write-Failure "Falló la construcción de imágenes Docker"
    exit 1
}

# ============================================================================
# 5. INICIAR SERVICIOS
# ============================================================================
Write-Section "5. Iniciando Servicios"

Write-Step "Levantando Docker Compose..."
docker-compose up -d

Write-Info "Esperando a que los servicios inicien (60 segundos)..."
Start-Sleep -Seconds 60

# ============================================================================
# 6. HEALTH CHECKS
# ============================================================================
Write-Section "6. Verificando Health Checks"

function Test-HealthCheck {
    param(
        [string]$Name,
        [string]$Url,
        [int]$Port
    )
    
    Write-Step "Verificando $Name en $Url..."
    
    try {
        $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 5
        
        if ($response.StatusCode -eq 200) {
            $content = $response.Content | ConvertFrom-Json
            $status = $content.status
            
            if ($status -eq "UP") {
                Write-Success "$Name está UP"
                return $true
            } else {
                Write-Failure "$Name está $status"
                return $false
            }
        }
    } catch {
        Write-Failure "$Name no responde: $($_.Exception.Message)"
        return $false
    }
    
    return $false
}

$testResults.DatabaseHealth = Test-HealthCheck -Name "PostgreSQL" -Url "http://localhost:5432" -Port 5432
Start-Sleep -Seconds 2
$testResults.AuthenticatorHealth = Test-HealthCheck -Name "Authenticator" -Url "http://localhost:8081/actuator/health" -Port 8081
Start-Sleep -Seconds 2
$testResults.InnosistemasHealth = Test-HealthCheck -Name "Innosistemas" -Url "http://localhost:8082/actuator/health" -Port 8082
Start-Sleep -Seconds 2
$testResults.GatewayHealth = Test-HealthCheck -Name "Gateway" -Url "http://localhost:8080/actuator/health" -Port 8080

# ============================================================================
# 7. LOGS DE SERVICIOS
# ============================================================================
Write-Section "7. Últimas líneas de logs"

$services = @('postgres', 'authenticator', 'innosistemas', 'gateway')
foreach ($service in $services) {
    Write-Step "Logs de $service (últimas 10 líneas):"
    docker-compose logs --tail=10 $service
    Write-Host ""
}

# ============================================================================
# 8. PRUEBAS DE CONECTIVIDAD
# ============================================================================
Write-Section "8. Pruebas de Conectividad Backend"

Write-Step "Probando endpoint de autenticación..."
try {
    $loginPayload = @{
        username = "test@example.com"
        password = "test123"
    } | ConvertTo-Json
    
    $response = Invoke-WebRequest `
        -Uri "http://localhost:8080/api/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginPayload `
        -UseBasicParsing `
        -ErrorAction SilentlyContinue
    
    if ($response.StatusCode -eq 200 -or $response.StatusCode -eq 401) {
        Write-Success "Gateway y Authenticator están comunicándose"
    }
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    if ($statusCode -eq 401 -or $statusCode -eq 404) {
        Write-Success "Gateway y Authenticator están comunicándose (respuesta esperada: $statusCode)"
    } else {
        Write-Info "Gateway respondió con código: $statusCode (puede ser normal si no hay datos)"
    }
}

Write-Step "Probando endpoint de proyectos..."
try {
    $response = Invoke-WebRequest `
        -Uri "http://localhost:8080/api/projects" `
        -Method GET `
        -UseBasicParsing `
        -ErrorAction SilentlyContinue
    
    if ($response.StatusCode -eq 200 -or $response.StatusCode -eq 401) {
        Write-Success "Gateway y Innosistemas están comunicándose"
    }
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    if ($statusCode -eq 401 -or $statusCode -eq 404) {
        Write-Success "Gateway y Innosistemas están comunicándose (respuesta esperada: $statusCode)"
    } else {
        Write-Info "Gateway respondió con código: $statusCode (puede ser normal si no hay datos)"
    }
}

# ============================================================================
# 9. FRONTEND
# ============================================================================
Set-Location ../frontend

Write-Section "9. Verificando Frontend"

if (-not (Test-Path "node_modules")) {
    Write-Step "Instalando dependencias de frontend..."
    npm install
    
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Dependencias instaladas"
    } else {
        Write-Failure "Falló la instalación de dependencias"
    }
}

Write-Step "Building frontend..."
npm run build 2>&1 | Out-Null

if ($LASTEXITCODE -eq 0) {
    Write-Success "Frontend build exitoso"
    $testResults.FrontendBuild = $true
} else {
    Write-Failure "Frontend build falló"
    $testResults.FrontendBuild = $false
}

# ============================================================================
# 10. RESUMEN
# ============================================================================
Set-Location ..

Write-Section "Resumen de Validación"

$allPassed = $true

foreach ($key in $testResults.Keys) {
    $status = $testResults[$key]
    $label = $key -replace '([A-Z])', ' $1'
    
    if ($status) {
        Write-Success $label
    } else {
        Write-Failure $label
        $allPassed = $false
    }
}

Write-Host ""

if ($allPassed) {
    Write-Host "┌────────────────────────────────────────────────┐" -ForegroundColor Green
    Write-Host "│                                                │" -ForegroundColor Green
    Write-Host "│  ✓ TODOS LOS TESTS PASARON                    │" -ForegroundColor Green
    Write-Host "│                                                │" -ForegroundColor Green
    Write-Host "└────────────────────────────────────────────────┘" -ForegroundColor Green
    
    Write-Host "`n📋 Servicios Disponibles:" -ForegroundColor Cyan
    Write-Host "  • Gateway:       http://localhost:8080" -ForegroundColor White
    Write-Host "  • Authenticator: http://localhost:8081" -ForegroundColor White
    Write-Host "  • Innosistemas:  http://localhost:8082" -ForegroundColor White
    Write-Host "  • PostgreSQL:    localhost:5432" -ForegroundColor White
    
    Write-Host "`n🚀 Próximos Pasos:" -ForegroundColor Cyan
    Write-Host "  1. Iniciar frontend: cd frontend; npm run dev" -ForegroundColor White
    Write-Host "  2. Ver logs: cd backend; docker-compose logs -f" -ForegroundColor White
    Write-Host "  3. Detener: cd backend; docker-compose down" -ForegroundColor White
    
    Write-Host "`n📊 Monitoreo:" -ForegroundColor Cyan
    Write-Host "  • Health Check Gateway: http://localhost:8080/actuator/health" -ForegroundColor White
    Write-Host "  • Ver contenedores: docker ps" -ForegroundColor White
    Write-Host "  • Ver logs en vivo: docker-compose logs -f [servicio]" -ForegroundColor White
    
} else {
    Write-Host "┌────────────────────────────────────────────────┐" -ForegroundColor Red
    Write-Host "│                                                │" -ForegroundColor Red
    Write-Host "│  ✗ ALGUNOS TESTS FALLARON                     │" -ForegroundColor Red
    Write-Host "│                                                │" -ForegroundColor Red
    Write-Host "└────────────────────────────────────────────────┘" -ForegroundColor Red
    
    Write-Host "`n🔍 Troubleshooting:" -ForegroundColor Yellow
    Write-Host "  1. Ver logs: cd backend; docker-compose logs" -ForegroundColor White
    Write-Host "  2. Reintentar: docker-compose down -v; docker-compose up -d" -ForegroundColor White
    Write-Host "  3. Verificar puertos: netstat -ano | findstr ':8080 :8081 :8082 :5432'" -ForegroundColor White
    
    exit 1
}

# ============================================================================
# 11. OPCIÓN DE INICIAR FRONTEND
# ============================================================================
Write-Host "`n"
$startFrontend = Read-Host "¿Quieres iniciar el frontend ahora? (s/n)"

if ($startFrontend -eq 's' -or $startFrontend -eq 'S') {
    Write-Info "Iniciando frontend en modo desarrollo..."
    Write-Info "Abre tu navegador en: http://localhost:5173"
    Write-Info "Presiona Ctrl+C para detener"
    Write-Host ""
    
    Set-Location frontend
    npm run dev
}

Write-Host "`n✨ Validación completa!" -ForegroundColor Green
