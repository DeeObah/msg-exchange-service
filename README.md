# msg-exchange-service
An application to receive messages from some source(s), persist the message and forward it to some destination

Getting Started

Get a comapy of the project by cloning or downloading the project zip.

Prerequisites

1.A mysql database hosted localhost with 3306 and schema name msg_exchange.

2.Databse user with name exchange and password 3xc#@n8e with create, inster and update rights.

3.Test bench application running on localhost port 8181

Installing

Run the following command from the main application directory to install jars and download dependencies.

mvn clean install

Starting the application

Option 1: Run the main application in msg-exchange-service module.

Option 2: From the main application folder, run the following below

java -jar msg-exchange-service/target/msg-exchange-service-1.0-RELEASE.jar 

Application Log

Application is configured to log activity to /data/logs/msg-exchange/msg_exchange.log
