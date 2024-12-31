#!/bin/bash

# Optional bind to update scripts
reload=false
isGenerated=false

# Parse arguments
while [[ "$#" -gt 0 ]]; do
    case $1 in
        -reload) reload=true ;;
        *) echo "Unknown parameter passed: $1"; exit 1 ;;
    esac
    shift
done

# Script content
grapheneContent='
#!/bin/bash
s=false
f=false
p=false
v=false
allPrograms=false
file=""

while [[ "$#" -gt 0 ]]; do
    case $1 in
        -s) s=true ;;
        -f) f=true ;;
        -p) p=true ;;
        -v) v=true ;;
        -allPrograms) allPrograms=true ;;
        *) file="$1" ;;
    esac
    shift
done

if $s; then
    if [ -z "$file" ]; then echo "A positional argument for a Graphene file name must be provided."; exit 1; fi
    java -jar ../bin/src/graphene.jar "$file" "graphenes"
elif $f; then
    if [ -z "$file" ]; then echo "A positional argument for a Graphene file name must be provided."; exit 1; fi
    java -jar ../bin/src/graphene.jar "$file" "graphenef"
elif $p; then
    if [ -z "$file" ]; then echo "A positional argument for a Graphene file name must be provided."; exit 1; fi
    java -jar ../bin/src/graphene.jar "$file" "graphenep"
elif $v; then
    if [ -z "$file" ]; then echo "A positional argument for a Graphene file name must be provided."; exit 1; fi
    java -jar ../bin/src/graphene.jar "$file" "graphenev"
elif $allPrograms; then
    if [ -n "$file" ]; then echo "No positional argument needed."; exit 1; fi
    for file in ../programs/*.gr; do
        java -jar ../bin/src/graphene.jar "${file%.*}" "graphenev"
    done
else
    if [ -z "$file" ]; then echo "A positional argument for a Graphene file name must be provided."; exit 1; fi
    java -jar ../bin/src/graphene.jar "$file" "graphenec"
fi
'

graphenesContent='
#!/bin/bash
file="$1"
if [ -z "$file" ]; then echo "A positional argument for a Graphene file name must be provided."; exit 1; fi
java -jar ../bin/src/graphenes.jar "$file" "graphenes"
'

graphenefContent='
#!/bin/bash
file="$1"
if [ -z "$file" ]; then echo "A positional argument for a Graphene file name must be provided."; exit 1; fi
java -jar ../bin/src/graphenef.jar "$file" "graphenef"
'

graphenepContent='
#!/bin/bash
file="$1"
if [ -z "$file" ]; then echo "A positional argument for a Graphene file name must be provided."; exit 1; fi
java -jar ../bin/src/graphenep.jar "$file" "graphenep"
'

graphenevContent='
#!/bin/bash
file="$1"
if [ -z "$file" ]; then echo "A positional argument for a Graphene file name must be provided."; exit 1; fi
java -jar ../bin/src/graphenev.jar "$file" "graphenev"
'

# Function to make scripts
function MakeScript() {
    local fileName="$1"
    local content="$2"
    if [ ! -f "$fileName" ]; then
        echo "$content" > "$fileName"
        chmod +x "$fileName"
        echo "Generated script: $fileName"
        isGenerated=true
    fi
}

# Function to reload scripts
function ReloadScript() {
    local fileName="$1"
    local content="$2"
    if [ -f "$fileName" ]; then
        echo "$content" > "$fileName"
        chmod +x "$fileName"
    fi
}

# Create the scripts
MakeScript "graphene.sh" "$grapheneContent"
MakeScript "graphenes.sh" "$graphenesContent"
MakeScript "graphenef.sh" "$graphenefContent"
MakeScript "graphenep.sh" "$graphenepContent"
MakeScript "graphenev.sh" "$graphenevContent"

# Output for fresh build
if [ "$isGenerated" = true ]; then
    echo "All scripts have been generated successfully."
fi

if [ "$reload" = true ]; then
    ReloadScript "graphene.sh" "$grapheneContent"
    ReloadScript "graphenes.sh" "$graphenesContent"
    ReloadScript "graphenef.sh" "$graphenefContent"
    ReloadScript "graphenep.sh" "$graphenepContent"
    ReloadScript "graphenev.sh" "$graphenevContent"
    echo "Reloading scripts."
else
    # Compile source code
    mkdir -p out
    javac -d ./out ./src/*.java
    jar cfe "graphene.jar" src.Main -C ./out .
    mv -f "./graphene.jar" "../bin/src"
    rm -rf ./out

    echo "Compilation completed successfully."
fi
