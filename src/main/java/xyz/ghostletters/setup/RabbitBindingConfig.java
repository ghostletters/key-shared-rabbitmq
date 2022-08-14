package xyz.ghostletters.setup;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import io.quarkiverse.rabbitmqclient.RabbitMQClient;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;
import java.io.UncheckedIOException;

import static xyz.ghostletters.pulsar.RabbitSourceConfig.EXCHANGE_NAME;
import static xyz.ghostletters.pulsar.RabbitSourceConfig.QUEUE_NAME;

@ApplicationScoped
public class RabbitBindingConfig {

    @Inject
    RabbitMQClient rabbitMQClient;
    private Channel channel;

    public void onApplicationStart(@Observes StartupEvent event) {
        // on application start prepare the queues and message listener
        setupQueues();
    }

    private void setupQueues() {
        try {
            Connection connection = rabbitMQClient.connect();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true);
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "#");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
