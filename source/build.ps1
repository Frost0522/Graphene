[CmdletBinding()]
param (
    [switch]$c,
    [switch]$p,
    [switch]$t,
    [switch]$gs,
    [switch]$gf,
    [switch]$gp,
    $file
)

if (-not ($c -or $p -or $t -or $gs -or $gf -or $gp)) {
    New-Item -ItemType File -Path "./MANIFEST.MF" > $null
    Set-Content -Path "./MANIFEST.MF" -Value "Manifest-Version: 1.0`nMain-Class: src.Main"
    javac ./src/*.java
    jar cfm "current.jar" "MANIFEST.MF" ./src/*.class
    Move-Item -Path "./current.jar" -Destination "../bin/src" -Force
    Remove-Item ./MANIFEST.MF
    Remove-Item ./src/*class
    exit 1
}

switch ($true) {
    {$c} {
        if (-not $file) {Write-Host "A positional argument for a Graphene file name must be provided."; exit 1}
        java -jar ../bin/src/current.jar $file "graphenev"; break;
        }
    {$p} {Get-ChildItem ../programs *.gr | ForEach-Object {
        if ($file) {Write-Host "No positional argument for a Graphene file is required."; exit 1}
            java -jar "../bin/src/current.jar" "../programs/$([System.IO.Path]::GetFileNameWithoutExtension($_))" "graphenev"
            }; break;
        }
    {$t} {Get-ChildItem ../tests *.gr | ForEach-Object {
        if ($file) {Write-Host "No positional argument for a Graphene file is required."; exit 1}
            java -jar "../bin/src/current.jar" "../tests/$([System.IO.Path]::GetFileNameWithoutExtension($_))" "graphenev"
            }; break;
        }
    {$gs} {
        if (-not $file) {Write-Host "A positional argument for a Graphene file name must be provided."; exit 1}
        java -jar ../bin/src/graphenes.jar $file "graphenes"; break;
        }
    {$gf} {
        if (-not $file) {Write-Host "A positional argument for a Graphene file name must be provided."; exit 1}
        java -jar ../bin/src/graphenef.jar $file "graphenef"; break;
        }
    {$gp} {
        if (-not $file) {Write-Host "A positional argument for a Graphene file name must be provided."; exit 1}
        java -jar ../bin/src/graphenep.jar $file "graphenep"; break;
        }
    default {Write-Host "Not a vaild build command."; break}
}