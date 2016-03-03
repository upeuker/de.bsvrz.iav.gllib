@echo off

rem Das Verzeichnis, in dem das Backup landen soll
set backupVerzeichnis="$1"

rem Das aktuelle Verzeichnis der Ganglinien-Datenbank
set name=

rem Datenbank-Nutzer
set nutzer=derby

rem Datenbank-Passwort
set pass=derby

java -cp ../../de.bsvrz.iav.gllib-runtime.jar de.bsvrz.iav.gllib.gllib.Backup %backupVerzeichnis% localhost:1527 -name=%name% -nutzer=%nutzer% -pass=%$pass%


