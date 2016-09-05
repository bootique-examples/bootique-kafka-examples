package io.bootique.example.kafka.producer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.bootique.cli.Cli;
import io.bootique.cli.CliOption;
import io.bootique.command.CommandMetadata;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.kafka.client.KafkaClientFactory;
import io.bootique.kafka.client.producer.ProducerConfig;
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
    private static final String BOOTSTRAP_SERVER_OPT = "bootstrap-server";

    private static final String DEFAULT_CLUSTER_NAME = "default";
    private static final String QUIT_COMMAND = "\\q";

    private Provider<KafkaClientFactory> kafkaProvider;
    private ShutdownManager shutdownManager;

    @Inject
    public KafkaProducerCommand(Provider<KafkaClientFactory> kafkaProvider, ShutdownManager shutdownManager) {
        super(CommandMetadata.builder("producer").addOption(topicOption()).addOption(clusterOption()));
        this.kafkaProvider = kafkaProvider;
        this.shutdownManager = shutdownManager;
    }

    private static CliOption topicOption() {
        return CliOption.builder(TOPIC_OPT).description("Kafka topic to write data to.")
                .valueRequired("topic_name").build();
    }

    private static CliOption clusterOption() {
        return CliOption.builder(BOOTSTRAP_SERVER_OPT).description("Single Kafka bootstrap server. " +
                "Can be specified multiple times. Optional. " +
                "If omitted, will be read from YAML or environment variable BQ_KAFKACLIENT_BOOTSTRAPSERVERS_DEFAULT.")
                .valueRequired("host:port").build();
    }

    @Override
    public CommandOutcome run(Cli cli) {

        String topic = cli.optionString(TOPIC_OPT);
        if (topic == null) {
            return CommandOutcome.failed(-1, "No --topic specified");
        }

        ProducerConfig<byte[], String> config = ProducerConfig
                .charValueConfig()
                .bootstrapServers(cli.optionStrings(BOOTSTRAP_SERVER_OPT))
                .build();

        Producer<byte[], String> producer = kafkaProvider.get().createProducer(DEFAULT_CLUSTER_NAME, config);

        shutdownManager.addShutdownHook(() -> {
            producer.close();
            // give a bit of time to stop..
            Thread.sleep(200);
        });

        return runConsole(topic, producer);
    }

    private CommandOutcome runConsole(String topic, Producer<byte[], String> producer) {

        System.out.println("");
        System.out.println("    Start typing messages below. Type '\\q' to exit.");
        System.out.println("");

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
