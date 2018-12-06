package io.bootique.example.kafka.producer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.bootique.kafka.client.producer.KafkaProducerFactory;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.shutdown.ShutdownManager;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Runs Kafka producer.
 */
public class KafkaProducerCommand extends CommandWithMetadata {

    private static final String TOPIC_OPT = "topic";
    private static final String QUIT_COMMAND = "\\q";

    private Provider<KafkaProducerFactory> kafkaProvider;
    private ShutdownManager shutdownManager;

    @Inject
    public KafkaProducerCommand(Provider<KafkaProducerFactory> kafkaProvider, ShutdownManager shutdownManager) {
        super(CommandMetadata.builder("producer").addOption(topicOption()));
        this.kafkaProvider = kafkaProvider;
        this.shutdownManager = shutdownManager;
    }

    private static OptionMetadata topicOption() {
        return OptionMetadata.builder(TOPIC_OPT).description("Kafka topic to write data to.")
                .valueRequired("topic_name").build();
    }

    @Override
    public CommandOutcome run(Cli cli) {

        String topic = cli.optionString(TOPIC_OPT);
        if (topic == null) {
            return CommandOutcome.failed(-1, "No --topic specified");
        }

        Producer<byte[], String> producer = kafkaProvider.get()
                .charValueProducer()
                .cluster(App.DEFAULT_CLUSTER_NAME)
                .create();

        shutdownManager.addShutdownHook(() -> {
            producer.close();
            // give a bit of time to stop..
            Thread.sleep(200);
        });

        return runConsole(topic, producer);
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

            producer.send(new ProducerRecord<>(topic, message));
            System.out.print(topic + " > ");
        }
    }
}
