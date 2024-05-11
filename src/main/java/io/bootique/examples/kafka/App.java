package io.bootique.examples.kafka;

import io.bootique.BQCoreModule;
import io.bootique.BQModule;
import io.bootique.Bootique;
import io.bootique.di.Binder;
import io.bootique.meta.application.OptionMetadata;

public class App implements BQModule {

    public static final String TOPIC_OPT = "topic";
    public static final String KAFKA_CLUSTER = "c1";

    public static void main(String[] args) {
        Bootique.app(args)
                .autoLoadModules()
                .module(App.class)
                .exec()
                .exit();
    }

    @Override
    public void configure(Binder binder) {
        OptionMetadata topicOpt = OptionMetadata
                .builder(TOPIC_OPT)
                .description("Kafka topic name")
                .valueRequired("topic_name").build();

        BQCoreModule.extend(binder)
                .addCommand(ProducerCommand.class)
                .addCommand(ConsumerCommand.class)
                .addOption(topicOpt);
    }
}