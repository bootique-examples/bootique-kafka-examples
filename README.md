[![verify](https://github.com/bootique-examples/bootique-kafka-examples/actions/workflows/verify.yml/badge.svg)](https://github.com/bootique-examples/bootique-kafka-examples/actions/workflows/verify.yml)

# Bootique 3.x Kafka Examples

This is an example Bootique Kafka app with producer and consumer. Different Git branches contain example code for different versions of Bootique:

* [3.x](https://github.com/bootique-examples/bootique-kafka-examples/tree/3.x)
* [2.x](https://github.com/bootique-examples/bootique-kafka-examples/tree/2.x)
* [1.x](https://github.com/bootique-examples/bootique-kafka-examples/tree/1.x)

## Prerequisites

To build and run the project, make sure you have the following installed on your machine, and then follow the steps below:

* Docker
* Java 11 or newer
* Maven

## Checkout
```
git clone git@github.com:bootique-examples/bootique-kafka-examples.git
cd bootique-kafka-examples
```

## Start Kafka Locally

```bash
docker-compose -f docker-compose.yml up -d
```

## Build and package

Run the following command to build the code and package the app:
```
mvn clean package
```

## Run

Now you can check the options available in your app:

    NAME
          bootique-kafka-producer-0.0.1-SNAPSHOT.jar
    
    OPTIONS
          -b host:port, --bootstrap=host:port
               Single Kafka bootstrap server.
    
          -c yaml_location, --config=yaml_location
               Specifies YAML config location, which can be a file path or a URL.
    
          -h, --help
               Prints this message.
    
          -H, --help-config
               Prints information about application modules and their configuration options.
    
          -t topic_name, --topic=topic_name
               Kafka topic to write data to.
        
To test this example, you will need a Kafka broker release 0.10.0.0 and a topic to write some string data into it. 
Run Zookeeper and Kafka broker both on localhost from Kafka root directory:

    bin/zookeeper-server-start.sh config/zookeeper.properties
    
    bin/kafka-server-start.sh config/server.properties
    
Run kafka-console-consumer.sh script to read string data from a topic:
        
    bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic topic 

Run the producer:

    java -jar target/bootique-kafka-producer-0.0.1-SNAPSHOT.jar --bootstrap=localhost:9092 --topic=topic 

