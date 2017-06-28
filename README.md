# SBGN Validation Webservice

Simple deployable REST API to validate sbgnml files.

# Test Request
```
curl -X POST --data "xml=$(cat src/main/resources/example.sbgn)" http://localhost:4567/validateString
```