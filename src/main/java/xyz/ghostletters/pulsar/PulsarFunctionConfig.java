package xyz.ghostletters.pulsar;

import io.quarkus.runtime.StartupEvent;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.common.functions.ConsumerConfig;
import org.apache.pulsar.common.functions.FunctionConfig;
import org.apache.pulsar.common.schema.SchemaType;
import xyz.ghostletters.CustomerEventFunction;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static xyz.ghostletters.pulsar.KeySharedConsumer.TOPIC_WITH_KEY;
import static xyz.ghostletters.pulsar.RandomMessageProducer.TOPIC_RAW;

@ApplicationScoped
public class PulsarFunctionConfig {

    public static final String TENANT = "public";
    public static final String NAMESPACE = "default";
    public static final String FUNCTION_NAME = "CustomerEventFunction";

    PulsarAdmin pulsarAdmin = PulsarAdmin.builder()
            .serviceHttpUrl("http://localhost:8080")
            .build();

    public PulsarFunctionConfig() throws PulsarClientException {
    }

    public void onStart() throws PulsarClientException {
        try {
            if (isFunctionRunning()) {
                return; // do nothing
            }
            createFunction();
        } catch (PulsarAdminException e) {
            throw new RuntimeException(e);
        }

    }

    private void createFunction() throws PulsarAdminException {
        Map<String, ConsumerConfig> inputSpecs = new HashMap<String, ConsumerConfig>();
        inputSpecs.put(TOPIC_RAW,
                ConsumerConfig.builder().schemaType(SchemaType.JSON.name()).build());

        pulsarAdmin.functions().createFunction(
                FunctionConfig.builder()
                        .tenant(TENANT)
                        .namespace(NAMESPACE)
                        .name(FUNCTION_NAME)
                        .inputSpecs(inputSpecs)
                        .outputSchemaType(SchemaType.JSON.name())
                        .jar("pulsar-function-customer-event-1.0-SNAPSHOT.jar")
                        .inputs(List.of(TOPIC_RAW))
                        .output(TOPIC_WITH_KEY)
                        .className(CustomerEventFunction.class.getName())
                        .build(),
                "/home/newur/dev/pulsar-function-customer-event/target/pulsar-function-customer-event-1.0-SNAPSHOT.jar"
        );
    }

    private boolean isFunctionRunning() throws PulsarAdminException {
        return pulsarAdmin.functions().getFunctions(TENANT, NAMESPACE)
                .contains(FUNCTION_NAME);
    }

}
