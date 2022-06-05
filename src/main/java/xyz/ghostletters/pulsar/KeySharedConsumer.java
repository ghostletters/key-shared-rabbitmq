package xyz.ghostletters.pulsar;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.apache.pulsar.client.api.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import xyz.ghostletters.event.CustomerEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class KeySharedConsumer {

    public static final String TOPIC_WITH_KEY = "persistent://public/default/topic-with-key";

    @ConfigProperty(name = "quarkus.http.port")
    String httpPort;

    PulsarClient pulsarClient = PulsarClient.builder()
            .serviceUrl("pulsar://localhost:6650")
            .build();

    Consumer<CustomerEvent> consumer;

    public KeySharedConsumer() throws PulsarClientException {
    }

    MessageListener myMessageListener = (consumer, msg) -> {
        try {
            System.out.print("Message received: " + new String(msg.getData()));
            System.out.println(" - consumed by app port: " + httpPort);
            consumer.acknowledge(msg);
        } catch (Exception e) {
            consumer.negativeAcknowledge(msg);
        }
    };

    public void onStart(@Observes StartupEvent startupEvent) throws PulsarClientException {
        consumer = pulsarClient.newConsumer(Schema.JSON(CustomerEvent.class))
                .topic(TOPIC_WITH_KEY)
                .subscriptionName("my-subscription")
                .messageListener(myMessageListener)
                .subscriptionType(SubscriptionType.Key_Shared)
                .keySharedPolicy(KeySharedPolicy.autoSplitHashRange())
                .subscribe();
    }

    public void onStop(@Observes ShutdownEvent shutdownEvent) throws PulsarClientException {
        pulsarClient.close();
        consumer.close();
    }
}
