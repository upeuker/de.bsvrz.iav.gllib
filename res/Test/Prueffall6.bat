@ECHO OFF

SET CP=..\de.bsvrz.iav.gllib-runtime.jar;..\de.bsvrz.iav.gllib-test.jar

java -cp %CP% org.junit.runner.JUnitCore de.bsvrz.iav.gllib.gllib.Prueffall6

pause
