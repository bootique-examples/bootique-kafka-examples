[![verify](https://github.com/bootique-examples/bootique-kafka-producer/actions/workflows/verify.yml/badge.svg)](https://github.com/bootique-examples/bootique-kafka-producer/actions/workflows/verify.yml)

# bootique-kafka-producer

An example of the producer to send streams of data to topics in the [Kafka](https://kafka.apache.org) cluster integrated for [Bootique](http://bootique.io).

Also consider an example of [Kafka-consumer](https://github.com/bootique-examples/bootique-kafka-consumer). 

*For additional help/questions about this example send a message to
[Bootique forum](https://groups.google.com/forum/#!forum/bootique-user).*

## Prerequisites

* Java 1.8 or newer.
* Apache Maven.

## Build the Demo

Here is how to build it:

	git clone git@github.com:bootique-examples/bootique-kafka-producer.git
	cd bootique-kafka-producer
	mvn package

## Run the Demo

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

