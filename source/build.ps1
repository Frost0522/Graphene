#Optional bind to update scripts
[CmdletBinding()] 
param(
    [switch]$reload,
    $isGenerated
)

# Script content
$grapheneContent = @'
[CmdletBinding()]
param (
    [switch]$s,
    [switch]$f,
    [switch]$p,
    [switch]$v,
    [switch]$allPrograms,
    $file
)

switch ($true) {
    {$s} {
        if (-not $file) {Write-Host "A positional argument for a Graphene file name must be provided."; exit 1}
        java -jar ../bin/src/graphene.jar $file "graphenes"; break;
    }
    {$f} {
        if (-not $file) {Write-Host "A positional argument for a Graphene file name must be provided."; exit 1}
        java -jar ../bin/src/graphene.jar $file "graphenef"; break;
    }
    {$p} {
        if (-not $file) {Write-Host "A positional argument for a Graphene file name must be provided."; exit 1}
        java -jar ../bin/src/graphene.jar $file "graphenep"; break;
    }
    {$v} {
        if (-not $file) {Write-Host "A positional argument for a Graphene file name must be provided."; exit 1}
        java -jar ../bin/src/graphene.jar $file "graphenev"; break;
    }
    {$allPrograms} {
        if ($file) {Write-Host "No positional argument needed."; exit 1}
        $files = Get-ChildItem -Path "../programs/*.gr";
        $files | ForEach-Object {
            java -jar ../bin/src/graphene.jar ("../programs/" + $_.BaseName) "graphenec";
            if ($_ -ne $files[-1]) {Write-Host ""}
        }
        break;
    }
    default {
        if (-not $file) {Write-Host "A positional argument for a Graphene file name must be provided."; exit 1}
        java -jar ../bin/src/graphene.jar $file "graphenec"; break;
    }
}
'@

$graphenesContent = @'
param([string]$file)
if (-not $file) {Write-Host "A positional argument for a Graphene file name must be provided."; exit 1}
java -jar ../bin/src/graphenes.jar $file "graphenes";
'@

$graphenefContent = @'
param([string]$file)
if (-not $file) {Write-Host "A positional argument for a Graphene file name must be provided."; exit 1}
java -jar ../bin/src/graphenef.jar $file "graphenef";
'@

$graphenepContent = @'
param([string]$file)
if (-not $file) {Write-Host "A positional argument for a Graphene file name must be provided."; exit 1}
java -jar ../bin/src/graphenep.jar $file "graphenep";
'@

$graphenevContent = @'
param([string]$file)
if (-not $file) {Write-Host "A positional argument for a Graphene file name must be provided."; exit 1}
java -jar ../bin/src/graphenev.jar $file "graphenev";
'@

# Function to make scripts
$isGenerated = $false
function Make-Script($fileName, $content) {
    $filePath = Join-Path ./ $fileName
    if (-not (Test-Path -Path $filePath)) {
        Set-Content -Path $filePath -Value $content
        Write-Host "Generated script: $filePath"
        $isGenerated = $true
    }
}

# Function to reload scripts
function Reload-Script($fileName, $content) {
    $filePath = Join-Path ./ $fileName
    if ((Test-Path -Path $filePath)) {
        Set-Content -Path $filePath -Value $content -Force
    }
}

# Create the scripts
Make-Script "graphene.ps1" $grapheneContent
Make-Script "graphenes.ps1" $graphenesContent
Make-Script "graphenef.ps1" $graphenefContent
Make-Script "graphenep.ps1" $graphenepContent
Make-Script "graphenev.ps1" $graphenevContent

# Output for fresh build
if ($isGenerated) {Write-Host "All scripts have been generated successfully."}

if ($reload) {
    Reload-Script "graphene.ps1" $grapheneContent
    Reload-Script "graphenes.ps1" $graphenesContent
    Reload-Script "graphenef.ps1" $graphenefContent
    Reload-Script "graphenep.ps1" $graphenepContent
    Reload-Script "graphenev.ps1" $graphenevContent
    Write-Host "Reloading scripts."
} 
else {
    # Compile source code
    javac -d ./out ./src/*.java
    jar cfe "graphene.jar" src.Main -C ./out .
    Move-Item -Path "./graphene.jar" -Destination "../bin/src" -Force
    Remove-Item -Recurse -Force ./out

    Write-Host "Compilation completed successfully."
}