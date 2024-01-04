@echo off

echo Compiling...
call kotlinc translate.kt -include-runtime -d translate.jar

echo Exporting files...
call java -jar translate.jar "retweet.txt"

echo Complete
pause