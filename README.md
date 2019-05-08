To run this project:

Run the following:

1. ```git clone https://github.com/jhleung/tweety```
    
2. ```cd tweety```
    
3. Create a twitter4j.properties under src/main/resources with the following and replace with your own consumer key/secret and access token
```
        debug=true
        auth.consumerKey=***********************
        oauth.consumerSecret=******************************************
        oauth.accessToken=**************************************************
        oauth.accessTokenSecret=******************************************
```

4. ```javac -cp ./src/main/:lib/twitter4j-core-4.0.7.jar ./src/main/java/*.java```

5. ```jar cfm tweety.jar Manifest.txt src/main/java/Tweety.class```

Publish Tweet:

    java -jar tweety.jar "<your_tweet>"

Pull Tweet:

    java -jar tweety.jar
