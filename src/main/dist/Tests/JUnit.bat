@ECHO OFF


REM ############################################################################
REM Folgende Parameter m�ssen �berpr�ft und evtl. angepasst werden.
REM ############################################################################

REM Der Klassenpfad f�r die Ganglinien-Bibliothek und deren JUnit-Testklassen.
SET CP=..\de.bsvrz.iav.gllib-runtime.jar;..\de.bsvrz.iav.gllib-test.jar


REM ############################################################################
REM Ab hier muss nichts mehr angepasst werden.
REM ############################################################################

CHCP 1252
TITLE SWE 5.5 Funktionen Ganglinien (JUnit-Test)

java -cp %CP% com.bitctrl.junit.TestRunner de.bsvrz.iav.gllib


REM Nach dem Beenden warten, damit Meldungen gelesen werden k�nnen
PAUSE
