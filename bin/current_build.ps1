param($filename)
javac src/*java
java src/Main.java $filename "graphenep"
cd src
mv -Path *.class -Destination ../../bin/src -Force
cd ../