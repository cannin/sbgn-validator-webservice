# SBGN Validation Webservice

Simple deployable REST API to validate sbgnml files.

# Test Request
```
curl -X POST --data "xml=$(cat src/main/resources/example.sbgn)" http://localhost:4567/validateString
curl -X POST http://localhost:4567/test
```

# Install 
## Install libsbgn jar to local Maven repository
```
mvn install:install-file -Dfile=PATH/lib/org.sbgn-with-dependencies.jar -DgroupId=org.sbgn -DartifactId=org.sbgn-with-dependencies -Dversion=1.0 -Dpackaging=jar
```

## Compile/Build with Maven
```
mvn clean install
```

## Build Jar with Dependencies 
```
// mvn assembly:single // NOT WORKING 
mvn clean package assembly:single
```

