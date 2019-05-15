To run this project:

Run the following:

1. ```git clone https://github.com/jhleung/tweety```
    
2. ```cd tweety```
    
3. Copy config-example.yml into a new file called config.yml under tweety/ . Replace the asterisks with your own consumer key/secret and access token

4. Install maven or check that you have it installed
	mvn -v

5. ```mvn package``` 

6. ```java -jar target/tweety-1.0-SNAPSHOT.jar server config.yml```

Publish Tweet:

	curl -d "tweet=<your_tweet>" http://localhost:8080/api/1.0/twitter/tweet"

Pull Tweet:
	
	curl http://localhost:8080/api/1.0/twitter/timeline

Coverage:
	run mvn prepare-package from tweety/. Open up target/site/jacoco/index.html to see test coverage

