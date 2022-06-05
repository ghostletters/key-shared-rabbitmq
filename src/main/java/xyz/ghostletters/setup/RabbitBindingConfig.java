package xyz.ghostletters.setup;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import io.quarkiverse.rabbitmqclient.RabbitMQClient;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import static xyz.ghostletters.pulsar.RabbitSourceConfig.QUEUE_NAME;

@ApplicationScoped
public class RabbitBindingConfig {

    @Inject
    RabbitMQClient rabbitMQClient;
    private Channel channel;


    public void onApplicationStart(@Observes StartupEvent event) {
        // on application start prepare the queus and message listener
        setupQueues();
    }

    private void setupQueues() {
        try {
            Connection connection = rabbitMQClient.connect();
            channel = connection.createChannel();
            channel.exchangeDeclare(QUEUE_NAME, BuiltinExchangeType.TOPIC, true);
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.queueBind(QUEUE_NAME, QUEUE_NAME, "#");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
