# Script para Reiniciar el Gateway
# Ejecuta este script cuando hagas cambios en el código del Gateway

Write-Host "`n═══════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "   REINICIANDO GATEWAY CON CORS ACTUALIZADO" -ForegroundColor Cyan
Write-Host "═══════════════════════════════════════════════════════════`n" -ForegroundColor Cyan

# Ir al directorio del Gateway
$gatewayPath = "backend\gateway"
Set-Location $gatewayPath

Write-Host "📍 Ubicación: $gatewayPath`n" -ForegroundColor Yellow

# Matar proceso en puerto 8080 si existe
Write-Host "🔍 Verificando si hay un proceso en puerto 8080..." -ForegroundColor Cyan
$process = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty OwningProcess

if ($process) {
    Write-Host "⚠️  Proceso encontrado (PID: $process). Deteniendo..." -ForegroundColor Yellow
    Stop-Process -Id $process -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 2
    Write-Host "✅ Proceso detenido`n" -ForegroundColor Green
} else {
    Write-Host "✅ No hay procesos en puerto 8080`n" -ForegroundColor Green
}

# Compilar y ejecutar
Write-Host "🔨 Compilando Gateway..." -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━`n" -ForegroundColor DarkGray

# Ejecutar Maven
.\mvnw.cmd spring-boot:run

Write-Host "`n═══════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "   GATEWAY INICIADO" -ForegroundColor Cyan
Write-Host "═══════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "`n✅ Gateway corriendo en: http://localhost:8080" -ForegroundColor Green
Write-Host "✅ CORS configurado para: http://localhost:5173" -ForegroundColor Green
Write-Host "`n💡 Ahora puedes probar el login desde el frontend`n" -ForegroundColor Yellow
