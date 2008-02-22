@ECHO OFF

SET CP=..\de.bsvrz.iav.gllib-runtime.jar;..\de.bsvrz.iav.gllib-test.jar

CHCP 1252
TITLE JUnit-Test für SWE 5.4 Funktionen Fuzzy

java -cp %CP% com.bitctrl.junit.TestRunner de.bsvrz.iav.gllib

pause
