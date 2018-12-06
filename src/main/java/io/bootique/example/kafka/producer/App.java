package io.bootique.example.kafka.producer;

import com.google.inject.Binder;
import com.google.inject.Module;
import io.bootique.BQCoreModule;
import io.bootique.Bootique;
import io.bootique.meta.application.OptionMetadata;

/**
 * Main app class.
 */
public class App implements Module {

    static final String DEFAULT_CLUSTER_NAME = "default_cluster";
    private static final String BOOTSTRAP_SERVER_OPT = "bootstrap";

    public static void main(String[] args) {
        Bootique.app(args)
                .autoLoadModules()
                .module(App.class)
                .exec()
                .exit();
    }

    private static OptionMetadata bootstrapOption() {
        return OptionMetadata
                .builder(BOOTSTRAP_SERVER_OPT)
                .description("Single Kafka bootstrap server.")
                .valueRequired("host:port")
                .build();
    }

    @Override
    public void configure(Binder binder) {
        BQCoreModule.extend(binder)
                .addOption(bootstrapOption())
                .mapConfigPath(BOOTSTRAP_SERVER_OPT, "kafkaclient.clusters." + DEFAULT_CLUSTER_NAME)
                .setDefaultCommand(KafkaProducerCommand.class);
    }


}