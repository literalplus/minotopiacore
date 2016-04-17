#!/bin/bash

# Temporarily deploys this workspace's MTC version to mtc-qa
# Re-run Jenkins build to reset environment
# For this to work, you need to have mt-mikey defined in your /etc/hosts

echo "Building..."
mvn clean install
echo "Built..."

echo "Deploying..."
cd bootstrap/target
scp -C minotopiacore-*.jar mt-mikey:/home/minecraft/plugins/qa/mtc.jar
scp -C minotopiacore-*.jar mt-mikey:/home/minecraft/mtc-qa/plugins/mtc.jar

echo "Deployed..."
echo "Done!"
