## Testing

Tests are handeled with the [maven-surefire-plugin](http://maven.apache.org/surefire/maven-surefire-plugin/maven-surefire-plugin) .

### Run Integration Test
* start the mongoDB container   
  `docker start mongodb`
* run all tests     
  `mvn integration-test`

### Run a Single Test
* run StringUtil tests   
  `mvn integration-test -Dtest=StringUtilTest` 

### Run a Set of Tests
* run the test in folder unit    
  `mvn integration-test -Dtest=unit/*` 
