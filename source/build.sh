#!/bin/bash

# Function to display error messages and exit
function error_exit {
    echo "$1" 1>&2
    exit 1
}

# Function to create or update scripts
function create_or_update_script {
    local fileName="$1"
    local content="$2"
    local filePath="./$fileName"
    if [ "$reload" = true ]; then
        echo "$content" > "$filePath"
        echo "Reloaded script: $filePath"
    else
        if [ ! -f "$filePath" ]; then
            echo "$content" > "$filePath"
            echo "Generated script: $filePath"
        fi
    fi
}

# Argument for file path
file=$1
# Default value
reload=false

# Parse command-line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --reload) reload=true; shift; ;;
        *) error_exit "Invalid option: $1" ;;
    esac
done

# Script contents
grapheneContent='#!/bin/bash
# Function to display error messages and exit
function error_exit {
    echo "$1" 1>&2
    exit 1
}

# Default mode
mode="graphenev"

# Check for arguments
while getopts ":sfp:" opt; do
    case ${opt} in
        s ) mode="graphenes"; shift $((OPTIND -1)) ;;
        f ) mode="graphenef"; shift $((OPTIND -1)) ;;
        p )mode="graphenep"; shift $((OPTIND -2)) ;;
        : )error_exit "Invalid option: -$OPTARG requires an argument" ;;
        \? ) error_exit "Invalid option: -$OPTARG" ;;
    esac
done

file=$1
if [ -z "$file" ]; then
    error_exit "A positional argument for a Graphene file name must be provided."
fi

# Run the Java program with the correct mode
java -jar ../bin/src/graphene.jar "$file" "$mode"
'

graphenesContent='#!/bin/bash
file="$1"
if [ -z "$file" ]; then
    echo "A positional argument for a Graphene file name must be provided." 1>&2
    exit 1
fi
java -jar ../bin/src/graphenes.jar "$file" "graphenes"
'

graphenefContent='#!/bin/bash
file="$1"
if [ -z "$file" ]; then
    echo "A positional argument for a Graphene file name must be provided." 1>&2
    exit 1
fi
java -jar ../bin/src/graphenef.jar "$file" "graphenef"
'

graphenepContent='#!/bin/bash
file="$1"
if [ -z "$file" ]; then
    echo "A positional argument for a Graphene file name must be provided." 1>&2
    exit 1
fi
java -jar ../bin/src/graphenep.jar "$file" "graphenep"
'

# Create or update the scripts
create_or_update_script "graphene.sh" "$grapheneContent"
create_or_update_script "graphenes.sh" "$graphenesContent"
create_or_update_script "graphenef.sh" "$graphenefContent"
create_or_update_script "graphenep.sh" "$graphenepContent"

# If reload was not requested, compile source code
if [ "$reload" = false ]; then
    javac -d ./out ./src/*.java
    jar cfe "graphene.jar" src.Main -C ./out .
    mv "./graphene.jar" "../bin/src"
    rm -rf ./out
    echo "Compilation completed successfully."
else
    echo "Scripts reloaded successfully."
fi