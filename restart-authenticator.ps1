# Script para Reiniciar el Authenticator

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   REINICIANDO AUTHENTICATOR" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Ir al directorio del Authenticator
$authenticatorPath = "backend\authenticator"
Set-Location $authenticatorPath

Write-Host "Ubicacion: $authenticatorPath`n" -ForegroundColor Yellow

# Matar proceso en puerto 8081 si existe
Write-Host "Verificando si hay un proceso en puerto 8081..." -ForegroundColor Cyan
$process = Get-NetTCPConnection -LocalPort 8081 -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty OwningProcess

if ($process) {
    Write-Host "Proceso encontrado (PID: $process). Deteniendo..." -ForegroundColor Yellow
    Stop-Process -Id $process -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 2
    Write-Host "Proceso detenido`n" -ForegroundColor Green
} else {
    Write-Host "No hay procesos en puerto 8081`n" -ForegroundColor Green
}

# Compilar y ejecutar
Write-Host "Compilando Authenticator..." -ForegroundColor Cyan
Write-Host "----------------------------------------`n" -ForegroundColor DarkGray

# Ejecutar Maven
.\mvnw.cmd spring-boot:run

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   AUTHENTICATOR INICIADO" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "`nAuthenticator corriendo en: http://localhost:8081" -ForegroundColor Green
Write-Host "`nAhora puedes probar el login desde el frontend`n" -ForegroundColor Yellow
