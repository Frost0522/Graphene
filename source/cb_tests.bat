@echo off
cd src
javac *java
cd ../
for /r %%v in (../tests/*) do java src/Main.java ../tests/%%~nv "graphenef"
cd src
move *class ../../bin/src >nul