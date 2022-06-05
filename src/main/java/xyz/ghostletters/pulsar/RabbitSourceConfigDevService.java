package xyz.ghostletters.pulsar;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.common.io.SourceConfig;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;

import static xyz.ghostletters.pulsar.PulsarFunctionConfig.NAMESPACE;
import static xyz.ghostletters.pulsar.PulsarFunctionConfig.TENANT;
import static xyz.ghostletters.pulsar.RandomMessageProducer.TOPIC_RAW;

@ApplicationScoped
public class RabbitSourceConfigDevService {

    public static final String QUEUE_NAME = "quote-requests";
    private static final String RABBIT_SOURCE = "rabbit-source";
    private static final String PULSAR_VERSION = "2.10.0";

    PulsarAdmin pulsarAdmin = PulsarAdmin.builder()
            .serviceHttpUrl("http://localhost:8080")
            .build();

    public RabbitSourceConfigDevService() throws PulsarClientException {
    }

    public void onStart() {
        try {
            if (isSourceRunning()) {
                return; // do nothing
            }
            createSource();
        } catch (PulsarAdminException e) {
            throw new RuntimeException(e);
        }

    }

    private void createSource() throws PulsarAdminException {
        pulsarAdmin.sources().createSource(
                SourceConfig.builder()
                        .tenant(TENANT)
                        .namespace(NAMESPACE)
                        .name(RABBIT_SOURCE)
                        .topicName(TOPIC_RAW)
                        .archive("builtin://rabbitmq")
//                        .schemaType(SchemaType.JSON.name()) DO NOT SET SCHEMA HERE. WILL FAIL
                        // but TOPIC_RAW must have a schema. See PulsarFunctionConfig
                        .parallelism(1)
                        .configs(buildRabbitConfig())
                        .build(),
                "builtin://connectors/pulsar-io-rabbitmq-" + PULSAR_VERSION + ".nar"
        );
    }

    private Map<String, Object> buildRabbitConfig() {
        return Map.ofEntries(
                Map.entry("host", "rabbitmq"),
                Map.entry("port", "5672"),
                Map.entry("virtualHost", "/"),
                Map.entry("username", "guest"),
                Map.entry("password", "guest"),
                Map.entry("queueName", QUEUE_NAME),
                Map.entry("connectionName", "test-connection"),
                Map.entry("requestedChannelMax", "0"),
                Map.entry("requestedFrameMax", "0"),
                Map.entry("connectionTimeout", "60000"),
                Map.entry("handshakeTimeout", "10000"),
                Map.entry("requestedHeartbeat", "60"),
                Map.entry("prefetchCount", "0"),
                Map.entry("prefetchGlobal", "false"),
                // set to passive so Pulsar does not try to create a new queue
                Map.entry("passive", "true")
        );
    }


    private boolean isSourceRunning() throws PulsarAdminException {
        return pulsarAdmin.sources().listSources(TENANT, NAMESPACE)
                .contains(RABBIT_SOURCE);
    }
}
