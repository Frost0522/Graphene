@echo off
cd src 
javac *java   
cd ../
java src/Main.java %1 "graphenef"
cd src
move *class ../../bin/src >nul