# 🚀 Script para iniciar todos los microservicios
# Ejecutar con: ./start-all-services.ps1

Write-Host "🏗️  Iniciando Arquitectura de Microservicios InnoSistemas" -ForegroundColor Cyan
Write-Host ""

# Colores para la salida
$ErrorColor = "Red"
$SuccessColor = "Green"
$InfoColor = "Yellow"

# Función para verificar si un puerto está en uso
function Test-Port {
    param([int]$Port)
    $connection = Test-NetConnection -ComputerName localhost -Port $Port -InformationLevel Quiet -WarningAction SilentlyContinue
    return $connection
}

# Función para iniciar un servicio en segundo plano
function Start-Service {
    param(
        [string]$Name,
        [string]$Path,
        [int]$Port,
        [string]$LogFile
    )
    
    Write-Host "📦 Iniciando $Name en puerto $Port..." -ForegroundColor $InfoColor
    
    # Verificar si el puerto ya está en uso
    if (Test-Port -Port $Port) {
        Write-Host "   ⚠️  El puerto $Port ya está en uso. Servicio posiblemente ya está corriendo." -ForegroundColor $ErrorColor
        return $false
    }
    
    # Cambiar al directorio del servicio
    Push-Location $Path
    
    # Iniciar el servicio en segundo plano
    Start-Process -FilePath "cmd.exe" -ArgumentList "/c", "mvnw.cmd spring-boot:run > $LogFile 2>&1" -WindowStyle Minimized
    
    Pop-Location
    
    Write-Host "   ✅ $Name iniciado (puerto $Port)" -ForegroundColor $SuccessColor
    Write-Host "   📄 Log: $LogFile" -ForegroundColor $InfoColor
    Write-Host ""
    
    return $true
}

# Directorio base
$BaseDir = $PSScriptRoot

# Logs directory
$LogsDir = Join-Path $BaseDir "logs"
if (-not (Test-Path $LogsDir)) {
    New-Item -ItemType Directory -Path $LogsDir | Out-Null
}

Write-Host "📋 Orden de inicio:" -ForegroundColor Cyan
Write-Host "   1. Gateway (8080)" -ForegroundColor White
Write-Host "   2. Authenticator (8081)" -ForegroundColor White
Write-Host "   3. InnoSistemas (8082)" -ForegroundColor White
Write-Host ""

# Iniciar Gateway
$GatewayPath = Join-Path $BaseDir "backend\gateway"
$GatewayLog = Join-Path $LogsDir "gateway.log"
Start-Service -Name "Gateway" -Path $GatewayPath -Port 8080 -LogFile $GatewayLog
Start-Sleep -Seconds 15

# Iniciar Authenticator
$AuthPath = Join-Path $BaseDir "backend\authenticator"
$AuthLog = Join-Path $LogsDir "authenticator.log"
Start-Service -Name "Authenticator" -Path $AuthPath -Port 8081 -LogFile $AuthLog
Start-Sleep -Seconds 15

# Iniciar InnoSistemas
$InnoPath = Join-Path $BaseDir "backend\innosistemas"
$InnoLog = Join-Path $LogsDir "innosistemas.log"
Start-Service -Name "InnoSistemas" -Path $InnoPath -Port 8082 -LogFile $InnoLog
Start-Sleep -Seconds 15

Write-Host "🎉 Todos los servicios han sido iniciados" -ForegroundColor $SuccessColor
Write-Host ""
Write-Host "📊 Verificar estado:" -ForegroundColor Cyan
Write-Host "   Gateway:       http://localhost:8080/actuator/health" -ForegroundColor White
Write-Host "   Authenticator: http://localhost:8080/authenticator/person/message" -ForegroundColor White
Write-Host "   InnoSistemas:  http://localhost:8080/project/project/message" -ForegroundColor White
Write-Host ""
Write-Host "🌐 Frontend:" -ForegroundColor Cyan
Write-Host "   cd frontend" -ForegroundColor White
Write-Host "   npm run dev" -ForegroundColor White
Write-Host ""
Write-Host "📄 Logs en: $LogsDir" -ForegroundColor $InfoColor
Write-Host ""
Write-Host "⏹️  Para detener todos los servicios, ejecuta: ./stop-all-services.ps1" -ForegroundColor $InfoColor
