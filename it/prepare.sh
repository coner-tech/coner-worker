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
conerCoreServiceJar="coner-core-service-$conerCoreVersion.jar"
conerCoreServiceTestConfig="test.yml"
check_extracted() {
    source=$1
    output=$2
    if [ ! -f ${source} ]; then
        echo "Failed to extract ${output} from ${source}"
        exit 1
    fi
}
if [ ! -f ${conerCoreServiceJar} ]; then
    wget ${conerCoreUrl}
    if [ ! -f ${conerCoreServiceOutputFile} ]; then
        echo "Failed to download (not found on disk: $conerCoreServiceOutputFile)"
        exit 1
    fi
    unzip ${conerCoreServiceOutputFile}
    check_extracted ${conerCoreServiceOutputFile} ${conerCoreServiceJar}
    check_extracted ${conerCoreServiceOutputFile} ${conerCoreServiceTestConfig}
    rm ${conerCoreServiceOutputFile}
fi