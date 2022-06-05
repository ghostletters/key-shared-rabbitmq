package xyz.ghostletters;

import org.apache.pulsar.client.api.Schema;
import org.junit.jupiter.api.Test;
import xyz.ghostletters.event.CustomerEvent;

public class SchemaDecodeTest {

    @Test
    void decodeBytesWithSchema() {
        Schema<CustomerEvent> eventSchema = Schema.JSON(CustomerEvent.class);
        CustomerEvent customerEvent = new CustomerEvent("id", "name");

//        eventSchema.
    }
}
