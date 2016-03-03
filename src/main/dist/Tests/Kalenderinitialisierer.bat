@ECHO OFF


REM ############################################################################
REM Folgende Parameter müssen überprüft und evtl. angepasst werden.
REM ############################################################################

REM (1) Allgemeine Einstellungen laden.
CALL ..\..\..\skripte-dosshell\einstellungen.bat

REM (2) Parameter für den Java-Interpreter, als Standard werden die
REM Einstellungen aus einstellungen.bat verwendet. Kann verwendet werden, wenn
REM (1) nicht funktioniert.
REM SET jvmArgs=-Dfile.encoding=ISO-8859-1
SET jvmArgs=%jvmArgs% -Xmx256m

REM (3) Parameter für den Datenverteiler, als Standard werden die Einstellungen
REM aus einstellungen.bat verwendet. Kann verwendet werden, wenn (1) nicht
REM funktioniert.
REM SET dav1=-datenverteiler=localhost:8083 -benutzer=Tester -authentifizierung=passwd -debugFilePath=..

REM (4) Der Klassenpfad für die Applikation.
SET classpath=..\de.bsvrz.iav.gllib-runtime.jar;..\de.bsvrz.iav.gllib-test.jar

REM (5) Optionaler Parameter. Wenn -reset gesetzt ist, dann werden alle
REM Ereignisse und Ereignistypen gelöscht.
REM SET optional=-reset


REM ############################################################################
REM Ab hier muss nichts mehr angepasst werden.
REM ############################################################################

CHCP 1252
TITLE SWE 5.5 Funktionen Ganglinien (inoffizieller Kalenderinitialisierer)

java %jvmArgs% -cp %classpath% de.bsvrz.iav.gllib.gllib.junit.KalenderInitialisierer ^
	%dav1% ^
	-debugLevelFileText=all ^
	-debugLevelStdErrText=all ^
	-debugSetLoggerAndLevel=:warning ^
	-debugSetLoggerAndLevel=de.bsvrz.iav:config ^
	%optional%
	

REM Nach dem Beenden warten, damit Meldungen gelesen werden können
PAUSE
