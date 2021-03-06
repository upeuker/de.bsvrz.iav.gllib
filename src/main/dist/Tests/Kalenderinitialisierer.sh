#!/bin/bash

source ../../../skripte-bash/einstellungen.sh


################################################################################
# Folgende Parameter m�ssen �berpr�ft und evtl. angepasst werden.              #
################################################################################

# (1) Allgemeine Einstellungen laden.
source ../../skripte-bash/einstellungen.sh

# (2) Parameter f�r den Java-Interpreter, als Standard werden die Einstellungen
# aus einstellungen.sh verwendet. Kann verwendet werden, wenn (1) nicht
# funktioniert.
#jvmArgs="-Dfile.encoding=ISO-8859-1"

# (3) Parameter f�r den Datenverteiler, als Standard werden die Einstellungen
# aus einstellungen.sh verwendet. Kann verwendet werden, wenn (1) nicht
# funtkioniert.
#dav1="-datenverteiler=localhost:8083 -benutzer=Tester -authentifizierung=passwd -debugFilePath=.."

# Optionaler Parameter. Wenn -reset gesetzt ist, dann werden alle Ereignisse und
# Ereignistypen gel�scht.
#optional=-reset


################################################################################
# Ab hier muss nichts mehr angepasst werden.                                   #
################################################################################

# Applikation starten
java $jvmArgs -cp ../de.bsvrz.iav.gllib-runtime.jar:../de.bsvrz.iav.gllib-test.jar \
	de.bsvrz.iav.gllib.gllib.junit.KalenderInitialisierer \
	$dav1 \
	-debugLevelFileText=all \
	-debugLevelStdErrText=all \
	-debugSetLoggerAndLevel=:warning \
	-debugSetLoggerAndLevel=de.bsvrz.iav:config \
	$optional &
