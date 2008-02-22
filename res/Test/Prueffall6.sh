#!/bin/bash

cp=../de.bsvrz.iav.gllib-runtime.jar:../de.bsvrz.iav.gllib-test.jar

java -cp $cp org.junit.runner.JUnitCore de.bsvrz.iav.gllib.gllib.Prueffall6
