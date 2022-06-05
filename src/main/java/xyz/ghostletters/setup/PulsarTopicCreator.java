package xyz.ghostletters.setup;

import io.quarkus.runtime.StartupEvent;
import org.apache.pulsar.client.api.PulsarClientException;
import xyz.ghostletters.pulsar.KeySharedConsumer;
import xyz.ghostletters.pulsar.PulsarFunctionConfig;
import xyz.ghostletters.pulsar.RabbitSourceConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class PulsarTopicCreator {

    @Inject
    PulsarFunctionConfig pulsarFunctionConfig;

    @Inject
    RabbitSourceConfig rabbitSourceConfig;

    @Inject
    KeySharedConsumer keySharedConsumer;

    public void onStart(@Observes StartupEvent startupEvent) throws PulsarClientException {
        // must be created in right order. If topic-raw is created without
        // schema, then the function fails

        pulsarFunctionConfig.onStart();
        rabbitSourceConfig.onStart();
    }
}
