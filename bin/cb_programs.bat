@echo off
cd src
javac *java
cd ../
for /r %%v in (../programs/*) do java src/Main.java ../programs/%%~nv "graphenef"
cd src
move *class ../../bin/src >nul