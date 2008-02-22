#!/bin/bash

cp=../de.bsvrz.iav.gllib-runtime.jar:../de.bsvrz.iav.gllib-test.jar

java -cp $cp com.bitctrl.junit.TestRunner de.bsvrz.iav.gllib
