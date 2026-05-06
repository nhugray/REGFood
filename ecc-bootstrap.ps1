[CmdletBinding()]
param(
    [ValidateSet('codex', 'claude', 'cursor', 'antigravity', 'gemini', 'opencode', 'codebuddy')]
    [string]$Target = 'codex',

    [ValidateSet('core', 'developer', 'security', 'research', 'full')]
    [string]$Profile = 'developer',

    [switch]$SkipInstall,
    [switch]$RepairOnly
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Invoke-Ecc {
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$CommandArgs,

        [switch]$AllowNonZeroExit
    )

    Write-Host "[ECC] ecc $($CommandArgs -join ' ')" -ForegroundColor Cyan
    & npm exec --yes --package=ecc-universal -- ecc @CommandArgs | Out-Host

    $exitCode = $LASTEXITCODE

    if ($exitCode -ne 0 -and -not $AllowNonZeroExit) {
        throw "ECC command failed with exit code ${LASTEXITCODE}: ecc $($CommandArgs -join ' ')"
    }

    return [int]$exitCode
}

Write-Host "[ECC] Bootstrap start" -ForegroundColor Green
Write-Host "[ECC] Target=$Target Profile=$Profile SkipInstall=$SkipInstall RepairOnly=$RepairOnly" -ForegroundColor DarkGray

if (-not $RepairOnly -and -not $SkipInstall) {
    [void](Invoke-Ecc -CommandArgs @('install', '--target', $Target, '--profile', $Profile))
}

# First diagnostic pass
$doctorFailed = $false
try {
    $initialDoctorExit = Invoke-Ecc -CommandArgs @('doctor', '--target', $Target) -AllowNonZeroExit
    if ($initialDoctorExit -ne 0) {
        $doctorFailed = $true
        Write-Warning "Doctor reported issues (exit code $initialDoctorExit). Attempting repair..."
    }
}
catch {
    $doctorFailed = $true
    Write-Warning "Doctor reported issues. Attempting repair..."
}

if ($RepairOnly -or $doctorFailed) {
    [void](Invoke-Ecc -CommandArgs @('repair', '--target', $Target))
    $finalDoctorExit = Invoke-Ecc -CommandArgs @('doctor', '--target', $Target) -AllowNonZeroExit
    if ($finalDoctorExit -ne 0) {
        Write-Warning "Doctor still reports warnings after repair (exit code $finalDoctorExit). Keeping setup as-is."
    }
}

Write-Host "[ECC] Bootstrap done" -ForegroundColor Green
Write-Host "[ECC] Next: restart Codex/agent session so AGENTS/MCP/prompt state is reloaded." -ForegroundColor Yellow
