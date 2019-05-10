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

5. ```mvn clean compile assembly:single``` 

Publish Tweet:

	java -jar target/tweety-1.0-SNAPSHOT-jar-with-dependencies.jar "<your_tweet>"

Pull Tweet:

	java -jar target/tweety-1.0-SNAPSHOT-jar-with-dependencies.jar
