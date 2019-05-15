To run this project:

Run the following:

1. ```git clone https://github.com/jhleung/tweety```
    
2. ```cd tweety```
    
3. Create a twitter4j.properties under src/main/resources with the following and replace with your own consumer key/secret and access token
```
        debug=true
        oauth.consumerKey=***********************
        oauth.consumerSecret=******************************************
        oauth.accessToken=**************************************************
        oauth.accessTokenSecret=******************************************
```

4. Install maven or check that you have it installed
	mvn -v

5. ```mvn package``` 

6. ```java -jar target/tweety-1.0-SNAPSHOT.jar server```

Publish Tweet:

	curl -d "tweet=<your_tweet>" http://localhost:8080/api/1.0/twitter/tweet"

Pull Tweet:
	
	curl http://localhost:8080/api/1.0/twitter/timeline

Coverage:
	run mvn prepare-package from tweety/. Open up target/site/jacoco/index.html to see test coverage

