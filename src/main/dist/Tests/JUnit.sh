#!/bin/bash


################################################################################
# Folgende Parameter m�ssen �berpr�ft und evtl. angepasst werden.              #
################################################################################

# Der Klassenpfad f�r die Ganglinien-Bibliothek und deren JUnit-Testklassen.
cp=../de.bsvrz.iav.gllib-runtime.jar:../de.bsvrz.iav.gllib-test.jar


################################################################################
# Ab hier muss nichts mehr angepasst werden.                                   #
################################################################################

java -cp $cp com.bitctrl.junit.TestRunner de.bsvrz.iav.gllib
