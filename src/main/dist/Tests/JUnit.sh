#!/bin/bash


################################################################################
# Folgende Parameter müssen überprüft und evtl. angepasst werden.              #
################################################################################

# Der Klassenpfad für die Ganglinien-Bibliothek und deren JUnit-Testklassen.
cp=../de.bsvrz.iav.gllib-runtime.jar:../de.bsvrz.iav.gllib-test.jar


################################################################################
# Ab hier muss nichts mehr angepasst werden.                                   #
################################################################################

java -cp $cp com.bitctrl.junit.TestRunner de.bsvrz.iav.gllib
