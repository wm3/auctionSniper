#!/bin/bash
mvn install:install-file -Dfile=lib/windowlicker-swing-DEV.jar -DgroupId=com.objogate.wl -DartifactId=windowlickr-swing -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/windowlicker-core-DEV.jar -DgroupId=com.objogate.wl -DartifactId=windowlickr-core -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/windowlicker-web-DEV.jar -DgroupId=com.objogate.wl -DartifactId=windowlickr-web -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/tools.jar -DgroupId=com.objogate.wl -DartifactId=windowlickr-tools -Dversion=1.0.0 -Dpackaging=jar
