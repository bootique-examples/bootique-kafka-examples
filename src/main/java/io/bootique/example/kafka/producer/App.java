package io.bootique.example.kafka.producer;

import com.google.inject.Binder;
import com.google.inject.Module;
import io.bootique.BQCoreModule;
import io.bootique.Bootique;

/**
 * Main app class.
 */
public class App implements Module {

    public static void main(String[] args) {
        Bootique.app(args).autoLoadModules().module(App.class).run();
    }

    @Override
    public void configure(Binder binder) {
        BQCoreModule.extend(binder).setDefaultCommand(KafkaProducerCommand.class);
    }


}