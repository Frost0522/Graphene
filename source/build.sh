#!/bin/bash

build() {
   printf "Manifest-Version: 1.0""\n""Main-Class: src.Main""\n" > ./MANIFEST.MF;
   javac ./src/*java
   jar cfm ./current.jar ./MANIFEST.MF ./src/*.class
   mv ./current.jar ../bin/src; rm ./MANIFEST.MF; rm ./src/*class;
}

current() {
   posArgCheck
   java -jar ../bin/src/current.jar $file "graphenev"
}

programs() {
for file in ../programs/*; do
   java ./src/Main.java "${file%.*}" "graphenev"
done
}

tests() {
for file in ../tests/*; do
   java ./src/Main.java "${file%.*}" "graphenev"
done
}

graphenes() {
   posArgCheck
   java -jar ../bin/src/graphenes.jar $file "graphenes"
}

graphenef() {
   posArgCheck
   java -jar ../bin/src/graphenef.jar $file "graphenef"
}

graphenep() {
   posArgCheck
   java -jar ../bin/src/graphenep.jar $file "graphenep"
}

posArgCheck() {
   if [ ! $file ]; then
      case $option in
         -c) echo "Current build requires positional argument to file path."; exit 1; ;;
         -gs) echo "Graphenes requires second positional argument to file path."; exit 1; ;;
         -gp) echo "Graphenep requires second positional argument to file path."; exit 1; ;; 
         -gf) echo "Graphenef requires second positional argument to file path."; exit 1; ;;
         "") ;;
      esac
      exit 1
   fi
}

option=$1

case $option in
   -c) file=$2; current ;;
   -p) programs ;;
   -t) tests ;;
   -gs) file=$2; graphenes ;;
   -gp) file=$2; graphenep ;;
   -gf) file=$2; graphenef ;;
   "") build ;;
   *) echo "Not a valid build argument." ;;
esac