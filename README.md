
To run this project:  
  
Run the following:  
  
1. ```git clone https://github.com/jhleung/tweety```
  
2. ```cd tweety```
  
3. Copy config-example.yml into a new file called config.yml under tweety/ .
  
    Authorization:
  
      Replace the asterisks with your own consumer key & secret and access token & access token secret. For the debug field, valid values are true or false.
  
    Logging:
  
      Replace the asterisks in logging.loggers.com.level property with desired logging level.
      It is recommended to keep the global logging.level set to OFF to turn off logs coming from other libraries (apache, jackson, etc.).
      logging.loggers.com.level will give you the necessary logs from this application itself.
      Valid values are TRACE, DEBUG, INFO, ERROR. This application will not produce WARN or FATAL level errors.
  
  
        logging:
            level: OFF
            loggers:
                com:
                    level: ERROR
  
  
4. Install maven or check that you have it installed ```mvn -v```
  
5. ```mvn clean package```
  
6. ```java -jar target/tweety-1.0-SNAPSHOT.jar server config.yml```
  
Publish Tweet:
  
   ```curl -d "message=<your_tweet>" http://localhost:8080/api/1.0/twitter/tweet```

Reply to Tweet:
	
	tweet_id can be found by navigating to desired tweet and copy/pasting the number at the end of the URL. The tweet id from ```https://twitter.com/afraidofbandai1/status/1148620943583043585)``` would be 1148620943583043585.	

   ```curl -d "message=<your_tweet>&inReplyToId=<parent_tweet_id>" http://localhost:8080/api/1.0/twitter/reply```


Pull Home Timeline:
  
   ```curl http://localhost:8080/api/1.0/twitter/homeTimeline```

Pull User Timeline:

  ```curl http://localhost:8080/api/1.0/twitter/userTimeline```

Filter Tweet:

	```curl http://localhost:8080/api/1.0/homeTimeline/filter?keyword=<keyword>```
  
Coverage:
   ```run mvn prepare-package from tweety/```. Open up target/site/jacoco/index.html to see test coverage
