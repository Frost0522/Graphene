@echo off
cd src
javac *java
cd ../
for /r %%v in (../tests/*) do java src/Main.java ../tests/%%~nv "graphenep"
cd src
move *class ../../bin/src >nul