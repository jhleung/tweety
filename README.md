To run this project:

Run the following:
git clone https://github.com/jhleung/tweety
cd tweety
Modify config.properties to add your consumer key/secret and access token/secret
	vim tweety/src/main/resources/config.properties
javac -cp ./src/main/:lib/twitter4j-core-4.0.7.jar ./src/main/java/*.java
jar cfm tweety.jar Manifest.txt src/main/java/Tweety.class

Publish Tweet:
java -jar tweety.jar "<your_tweet>"

Pull Tweet:
java -jar tweety.jar 
