# 🛑 Script para detener todos los microservicios
# Ejecutar con: ./stop-all-services.ps1

Write-Host "🛑 Deteniendo todos los microservicios..." -ForegroundColor Red
Write-Host ""

# Función para detener procesos en un puerto específico
function Stop-ProcessOnPort {
    param([int]$Port)
    
    $connections = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue
    
    if ($connections) {
        foreach ($connection in $connections) {
            $processId = $connection.OwningProcess
            $process = Get-Process -Id $processId -ErrorAction SilentlyContinue
            
            if ($process) {
                Write-Host "   Deteniendo proceso $($process.Name) (PID: $processId) en puerto $Port..." -ForegroundColor Yellow
                Stop-Process -Id $processId -Force
                Write-Host "   ✅ Proceso detenido" -ForegroundColor Green
            }
        }
    } else {
        Write-Host "   ℹ️  No hay procesos corriendo en el puerto $Port" -ForegroundColor Gray
    }
}

# Detener servicios
Write-Host "📦 Deteniendo Gateway (8080)..." -ForegroundColor Yellow
Stop-ProcessOnPort -Port 8080
Write-Host ""

Write-Host "📦 Deteniendo Authenticator (8081)..." -ForegroundColor Yellow
Stop-ProcessOnPort -Port 8081
Write-Host ""

Write-Host "📦 Deteniendo InnoSistemas (8082)..." -ForegroundColor Yellow
Stop-ProcessOnPort -Port 8082
Write-Host ""

# Detener procesos de Maven adicionales
Write-Host "🧹 Limpiando procesos de Maven..." -ForegroundColor Yellow
Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like "*spring-boot*" } | Stop-Process -Force
Write-Host ""

Write-Host "✅ Todos los servicios han sido detenidos" -ForegroundColor Green
