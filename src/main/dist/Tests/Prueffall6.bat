@ECHO OFF


REM ############################################################################
REM Folgende Parameter müssen überprüft und evtl. angepasst werden.
REM ############################################################################

REM Der Klassenpfad für die Ganglinien-Bibliothek und deren JUnit-Testklassen.
SET CP=..\de.bsvrz.iav.gllib-runtime.jar;..\de.bsvrz.iav.gllib-test.jar


REM ############################################################################
REM Ab hier muss nichts mehr angepasst werden.
REM ############################################################################

CHCP 1252
TITLE SWE 5.5 Funktionen Ganglinien - Prüffall 6: Cut-Operation

java -cp %CP% org.junit.runner.JUnitCore de.bsvrz.iav.gllib.gllib.Prueffall6


REM Nach dem Beenden warten, damit Meldungen gelesen werden können
PAUSE
