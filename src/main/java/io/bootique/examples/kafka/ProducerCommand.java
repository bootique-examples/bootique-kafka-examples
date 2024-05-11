package io.bootique.examples.kafka;

import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.kafka.client.producer.KafkaProducerFactory;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Runs an interactive Kafka producer.
 */
public class ProducerCommand extends CommandWithMetadata {

    private static final String TOPIC_OPT = "topic";
    private static final String QUIT_COMMAND = "\\q";

    private final Provider<KafkaProducerFactory> producerProvider;

    private static CommandMetadata metadata() {
        OptionMetadata omd = OptionMetadata.builder(TOPIC_OPT).description("Kafka topic name").valueRequired("topic_name").build();
        return CommandMetadata
                .builder(ProducerCommand.class)
                .description("Starts an interactive Kafka producer for the specified topic")
                .addOption(omd)
                .build();
    }

    @Inject
    public ProducerCommand(Provider<KafkaProducerFactory> producerProvider) {
        super(metadata());
        this.producerProvider = producerProvider;
    }

    @Override
    public CommandOutcome run(Cli cli) {

        String topic = cli.optionString(TOPIC_OPT);
        if (topic == null) {
            return CommandOutcome.failed(-1, "No '--topic' specified");
        }

        Producer<byte[], String> producer = producerProvider.get()
                .charValueProducer()
                .cluster(App.KAFKA_CLUSTER)
                .create();

        try {
            return runConsole(topic, producer);
        } finally {
            producer.close();
        }
    }

    private CommandOutcome runConsole(String topic, Producer<byte[], String> producer) {

        System.out.println();
        System.out.println("    Start typing messages below. Type '\\q' to exit.");
        System.out.println();

        try (BufferedReader stdinReader = new BufferedReader(new InputStreamReader(System.in))) {
            readAndPost(stdinReader, topic, producer);
            return CommandOutcome.succeeded();
        } catch (IOException ex) {
            return CommandOutcome.failed(-1, ex);
        }
    }

    private void readAndPost(BufferedReader reader, String topic, Producer<byte[], String> producer) throws IOException {

        System.out.print(topic + " > ");
        String message;
        while ((message = reader.readLine()) != null) {

            if (QUIT_COMMAND.equals(message)) {
                break;
            }

            if (!"".equals(message)) {
                producer.send(new ProducerRecord<>(topic, message));
            }

            System.out.print(topic + " > ");
        }
    }
}
