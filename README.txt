To run this project:

Publish Tweet:

Run the following:
cd ~/Downloads/
git clone https://github.com/Twitter4J/Twitter4J
git clone https://github.com/jhleung/tweety
cd tweety
Modify config.properties to add your consumer key/secret and access token/secret and run below command
	vim tweety/src/main/resources/config.properties
Modify <user_name> to your username and run below command
	javac -cp ./src/main/:/Users/<user_name>/Downloads/twitter4j-4.0.7/lib/twitter4j-core-4.0.7.jar ./src/main/java/PublishTweet.java
Modify Class-Path variable in Manifest.txt
	change <user_name> to your username in Users/<user_name>/Downloads/twitter4j-4.0.7/lib/twitter4j-core-4.0.7.jar
jar cfm publishTweet.jar Manifest.txt src/main/java/PublishTweet.class
java -jar publishTweet.jar "<your_tweet>"


