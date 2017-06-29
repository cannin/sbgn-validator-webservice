# SBGN Validation Webservice
A SparkJava-based simple deployable REST API to validate SBGNML files.

# Test Request
```
curl -X POST --data "xml=$(cat src/main/resources/example.sbgn)" http://localhost:4567/validateString
curl -X POST http://localhost:4567/test
```

# Install 
## Install libsbgn jar to local Maven repository
```
mvn install:install-file -Dfile=PATH/lib/org.sbgn-with-dependencies.jar -DgroupId=org.sbgn -DartifactId=org.sbgn-with-dependencies -Dversion=0.3 -Dpackaging=jar
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

# Run webservice
```
java -jar /code/target/sbgn-validator-webservice-0.1-jar-with-dependencies.jar 
```

# Docker 
## Build
```
docker build -t cannin/sbgn-validator-webservice .
```

## Run 
```
docker run --name sbgn-valid -p 9000:4567 -t cannin/sbgn-validator-webservice 
docker run --name sbgn-valid -i -p 9000:4567 -t cannin/sbgn-validator-webservice bash
```