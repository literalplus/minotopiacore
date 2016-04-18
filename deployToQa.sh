#!/bin/bash

# Temporarily deploys this workspace's MTC version to mtc-qa
# Re-run Jenkins build to reset environment
# For this to work, you need to have mt-mikey defined in your /etc/hosts

echo "Building..."
mvn clean install
echo "Built..."

echo "Deploying... (0/2)"
cd bootstrap/target
scp -C minotopiacore-*.jar mt-mikey:/home/minecraft/plugins/qa/mtc.jar
echo "Deploying... (1/2)"
scp -C minotopiacore-*.jar mt-mikey:/home/minecraft/mtc-qa/plugins/mtc.jar
echo "Deploying... (2/2)"

echo "Deployed..."
echo "Done!"
