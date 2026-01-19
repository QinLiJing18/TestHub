# PowerShell script to start infrastructure and services (Windows)
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location "$ScriptDir\.."

Write-Host "Starting infrastructure with docker-compose..."
docker-compose up -d
Write-Host "Starting infrastructure with docker compose..."
# prefer classic docker-compose, fall back to `docker compose` plugin if available
if (Get-Command docker-compose -ErrorAction SilentlyContinue) {
    $compose = 'docker-compose'
} else {
    # test docker compose plugin
    $res = & docker compose version 2>$null
    if ($LASTEXITCODE -eq 0) { $compose = 'docker compose' } else { Write-Error "ERROR: neither 'docker-compose' nor 'docker compose' is available. Install Docker Compose or Docker Desktop."; exit 1 }
}

Write-Host "Using: $compose"
& $compose up -d

# Wait for containers to become healthy (timeout 180s)
$timeout = 180
$sleep = 5
$elapsed = 0

Write-Host "Waiting for containers to report healthy status..."
while ($elapsed -lt $timeout) {
    # check named containers directly
    $containers = @('testhub-mysql','testhub-redis','testhub-nacos','testhub-emqx')
    $unhealthy = $false
    foreach ($c in $containers) {
        $id = docker ps -q -f "name=$c"
        if (-not $id) { $unhealthy = $true; break }
        $status = docker inspect --format='{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}' $id 2>$null
        if (-not $status -or ($status -ne 'healthy' -and $status -ne 'running')) { $unhealthy = $true; break }
    }
    if (-not $unhealthy) { Write-Host "All named containers are running/healthy."; break }
    Start-Sleep -Seconds $sleep
    $elapsed += $sleep
}

if ($elapsed -ge $timeout) {
    Write-Error "Timeout waiting for healthy containers. Run 'docker-compose ps' and 'docker-compose logs -f' to inspect."
    exit 1
}

Write-Host "Building project (mvn clean install -DskipTests)..."
mvn -T 1C clean install -DskipTests

Write-Host "Starting testhub-gateway and testhub-auth in new terminals..."
Start-Process powershell -ArgumentList "-NoExit -Command cd testhub-gateway; mvn spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit -Command cd testhub-auth; mvn spring-boot:run"

Write-Host "Gateway and Auth should start in the spawned terminals."
