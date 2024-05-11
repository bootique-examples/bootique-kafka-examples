package io.bootique.examples.kafka;

import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.kafka.client.consumer.KafkaConsumerFactory;
import io.bootique.kafka.client.consumer.KafkaPollingTracker;
import io.bootique.meta.application.CommandMetadata;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import javax.inject.Inject;
import javax.inject.Provider;
import java.time.Duration;

public class ConsumerCommand extends CommandWithMetadata {

    private final Provider<KafkaConsumerFactory> consumerProvider;

    private static CommandMetadata metadata() {
        return CommandMetadata
                .builder(ConsumerCommand.class)
                .description("Starts a Kafka consumer for the specified topic")
                .build();
    }

    @Inject
    public ConsumerCommand(Provider<KafkaConsumerFactory> consumerProvider) {
        super(metadata());
        this.consumerProvider = consumerProvider;
    }

    @Override
    public CommandOutcome run(Cli cli) {

        String topic = cli.optionString(App.TOPIC_OPT);
        if (topic == null) {
            return CommandOutcome.failed(-1, "No '--topic' specified");
        }

        KafkaPollingTracker poll = consumerProvider.get()
                .charValueConsumer()
                .group("my-group")
                .autoCommit(true)
                .cluster(App.KAFKA_CLUSTER)
                .topics(topic)

                // start the consumer in the background
                .consume(this::consumeBatch, Duration.ofSeconds(1));

        return CommandOutcome.succeededAndForkedToBackground();
    }

    private void consumeBatch(Consumer<byte[], String> consumer, ConsumerRecords<byte[], String> data) {
        data.forEach(r -> System.out.println("(consumer) " + r.topic() + " > " + r.value()));
    }
}
