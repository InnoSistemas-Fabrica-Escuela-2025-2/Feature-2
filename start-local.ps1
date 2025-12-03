# Script para iniciar todos los servicios en local
# Cada servicio se abre en una nueva ventana de PowerShell

$ErrorActionPreference = "Stop"
$root = $PSScriptRoot

Write-Host "=== Iniciando todos los servicios en modo LOCAL ===" -ForegroundColor Green
Write-Host ""

# Gateway
Write-Host "[1/4] Iniciando Gateway (puerto 8080)..." -ForegroundColor Cyan
$gatewayScript = @"
Set-Location '$root\backend\gateway'
.\mvnw spring-boot:run '-Dspring-boot.run.arguments=--spring.profiles.active=local'
"@
Start-Process powershell -ArgumentList "-NoExit", "-Command", $gatewayScript
Start-Sleep -Seconds 3

# Authenticator
Write-Host "[2/4] Iniciando Authenticator (puerto 8081)..." -ForegroundColor Cyan
$authScript = @"
Set-Location '$root\backend\authenticator'
.\mvnw spring-boot:run '-Dspring-boot.run.arguments=--spring.profiles.active=local'
"@
Start-Process powershell -ArgumentList "-NoExit", "-Command", $authScript
Start-Sleep -Seconds 3

# InnoSistemas
Write-Host "[3/4] Iniciando InnoSistemas (puerto 8082)..." -ForegroundColor Cyan
$innoScript = @"
Set-Location '$root\backend\innosistemas'
.\mvnw spring-boot:run '-Dspring-boot.run.arguments=--spring.profiles.active=local'
"@
Start-Process powershell -ArgumentList "-NoExit", "-Command", $innoScript
Start-Sleep -Seconds 3

# Frontend
Write-Host "[4/4] Iniciando Frontend (puerto 5173)..." -ForegroundColor Cyan
$frontendScript = @"
Set-Location '$root\frontend'
npm run dev
"@
Start-Process powershell -ArgumentList "-NoExit", "-Command", $frontendScript

Write-Host ""
Write-Host "=== Todos los servicios están iniciando ===" -ForegroundColor Green
Write-Host ""
Write-Host "Se abrieron 4 ventanas de PowerShell:" -ForegroundColor Yellow
Write-Host "  1. Gateway       -> http://localhost:8080" -ForegroundColor White
Write-Host "  2. Authenticator -> http://localhost:8081" -ForegroundColor White
Write-Host "  3. InnoSistemas  -> http://localhost:8082" -ForegroundColor White
Write-Host "  4. Frontend      -> http://localhost:5173" -ForegroundColor White
Write-Host ""
Write-Host "Configuracion:" -ForegroundColor Cyan
Write-Host "  - Base de datos: Neon PostgreSQL (cloud)" -ForegroundColor Gray
Write-Host "  - Kafka: Deshabilitado (solo para InnoSistemas)" -ForegroundColor Gray
Write-Host "  - Perfil activo: local" -ForegroundColor Gray
Write-Host ""
Write-Host "Espera 30-60 segundos para que todos los servicios inicien." -ForegroundColor Yellow
Write-Host "Presiona cualquier tecla para cerrar este mensaje..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
