#!/bin/bash

source ../../../skripte-bash/einstellungen.sh


################################################################################
# Folgende Parameter m�ssen �berpr�ft und evtl. angepasst werden               #
################################################################################

# Parameter f�r den Java-Interpreter, als Standard werden die Einstellungen aus
# einstellungen.sh verwendet.
#jvmArgs="-Dfile.encoding=ISO-8859-1"

# Parameter f�r den Datenverteiler, als Standard werden die Einstellungen aus
# einstellungen.sh verwendet.
#dav1="-datenverteiler=localhost:8083 -benutzer=Tester -authentifizierung=passwd -debugFilePath=.."

# Optionaler Parameter. Wenn -reset gesetzt ist, dann werden alle Ereignisse und
# Ereignistypen gel�scht.
#optional=-reset

################################################################################
# Ab hier muss nichts mehr angepasst werden                                    #
################################################################################

# Applikation starten
java $jvmArgs -cp ../de.bsvrz.iav.gllib-runtime.jar \
	de.bsvrz.iav.gllib.gllib.KalenderInitialisierer \
	$dav1 \
	-debugLevelFileText=all \
	-debugLevelStdErrText=off \
	-debugSetLoggerAndLevel=:warning \
	-debugSetLoggerAndLevel=de.bsvrz.iav:config \
	$optional \
	&
