#!/bin/sh
# Compile and start the Smart Student Survival System (needs JDK 8 or newer).
cd "$(dirname "$0")"
mkdir -p out
javac -d out $(find src -name "*.java") && java -cp out gui.Main
