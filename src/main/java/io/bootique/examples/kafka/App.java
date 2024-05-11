package io.bootique.examples.kafka;

import io.bootique.BQCoreModule;
import io.bootique.BQModule;
import io.bootique.Bootique;
import io.bootique.di.Binder;

public class App implements BQModule {

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
        BQCoreModule.extend(binder)
                .addCommand(ProducerCommand.class)
                .addCommand(ConsumerCommand.class);
    }
}