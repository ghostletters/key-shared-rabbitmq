package xyz.ghostletters.pulsar;

import io.quarkus.runtime.ShutdownEvent;
import org.apache.pulsar.client.api.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import xyz.ghostletters.event.CustomerEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.Random;

@ApplicationScoped
public class RandomMessageProducer {

    public static final String TOPIC_RAW = "persistent://public/default/topic-raw";
    PulsarClient pulsarClient;
    Producer<CustomerEvent> producer;

    Random rand = new Random();

    @ConfigProperty(name = "quarkus.http.port")
    String httpPort;

//    @Scheduled(every = "1s")
    void sendMessage() throws PulsarClientException {
        if (producer == null) {
            return;
        }

        int n = rand.nextInt(5);

        CustomerEvent customerEvent = new CustomerEvent("id-" + n, "name-" + n);

        producer.newMessage()
                .key("customer-" + n)
                .value(customerEvent)
                .property("my-key", "my-value")
                .send();
    }

//    public void onStart(@Observes StartupEvent startupEvent) throws PulsarClientException {
//        pulsarClient = PulsarClient.builder()
//                .serviceUrl("pulsar://localhost:6650")
//                .build();
//
//        producer = pulsarClient.newProducer(Schema.JSON(CustomerEvent.class))
//                .topic(TOPIC_RAW)
//                .batcherBuilder(BatcherBuilder.KEY_BASED)
//                .create();
//    }

    public void onStop(@Observes ShutdownEvent shutdownEvent) throws PulsarClientException {
        pulsarClient.close();
        producer.close();
    }
}
