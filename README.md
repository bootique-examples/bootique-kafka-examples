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

The following command prints a help message with supported options:
```bash  
java -jar target/bootique-kafka-examples-3.0.jar
```

```
NAME
      bootique-kafka-examples-3.0.jar

OPTIONS
      --config=yaml_location
           Specifies YAML config location, which can be a file path or a URL.

      --consumer
           Starts a Kafka consumer for the specified topic

      -h, --help
           Prints this message.

      -H, --help-config
           Prints information about application modules and their configuration options.

      -p, --producer
           Starts an interactive Kafka producer for the specified topic

      -t topic_name, --topic=topic_name
           Kafka topic name
```

So first, let's run a producer that will write to `bq-kafka-example` topic:
```
java -jar target/bootique-kafka-examples-3.0.jar --producer \
    --config=config.yml \
    --topic=bq-kafka-example 
```

This starts an interactive console app that allows you to type messages, one line at a time, that are sent to Kafka:

```
INFO  main i.b.k.c.p.DefaultKafkaProducerBuilder: Creating producer. Cluster: 127.0.0.1:9092.
INFO  main o.a.k.c.t.i.KafkaMetricsCollector: initializing Kafka metrics collector
INFO  main o.a.k.c.u.AppInfoParser: Kafka version: 3.7.0
INFO  main o.a.k.c.u.AppInfoParser: Kafka commitId: 2ae524ed625438c5
INFO  main o.a.k.c.u.AppInfoParser: Kafka startTimeMs: 1715466159773
(producer) bq-kafka-example > Hi!
(producer) bq-kafka-example > Hi again!
```

You can read these messages from the topic using Kafka provided console consumer, but let's start our own consumer. 
Open a new terminal window, change to `bootique-kafka-examples/` directory, and run the same Java app, but with
`--consumer` command:

```
java -jar target/bootique-kafka-examples-3.0.jar --consumer \
    --config=config.yml \
    --topic=bq-kafka-example
```
Now, return to the running producer window, and type more messages. All of them should be mirrored in the consumer 
command output. You can stop either producer or consumer with `Ctrl-c`.
