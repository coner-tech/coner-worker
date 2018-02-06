#!/bin/bash

check_dependency() {
    dependency=$1;
    command -v $dependency >/dev/null 2>&1 || { echo >&2 "Aborting due to missing dependency: $dependency."; exit 1; }
}
check_dependency "wget"

if [ ! -d "environment" ]; then
    mkdir environment
fi
cd environment

# capture arguments
conerCoreVersion=$1

# prepare coner core service jar
conerCoreUrl="https://github.com/caeos/coner-core/releases/download/v$conerCoreVersion/coner-core-service-v$conerCoreVersion.zip"
conerCoreServiceOutputFile="coner-core-service-v$conerCoreVersion.zip"
download_coner_core_service() {
    wget ${conerCoreUrl}
    if [ ! -f ${conerCoreServiceOutputFile} ]; then
        echo "Failed to download (not found on disk: $conerCoreServiceOutputFile)"
        exit 1
    fi
    unzip ${conerCoreServiceOutputFile}
}
if [ ! -f ${conerCoreServiceOutputFile} ]; then
    download_coner_core_service
fi