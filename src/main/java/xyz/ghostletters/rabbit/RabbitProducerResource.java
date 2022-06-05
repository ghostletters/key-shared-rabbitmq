package xyz.ghostletters.rabbit;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import xyz.ghostletters.event.CustomerEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Random;

import static xyz.ghostletters.pulsar.RabbitSourceConfig.QUEUE_NAME;

@ApplicationScoped
@Path("/request")
public class RabbitProducerResource {

    @Channel(QUEUE_NAME)
    Emitter<CustomerEvent> quoteRequestEmitter;

    Random rand = new Random();

//        @Scheduled(every = "2s")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String createRabbitRequest() {
        int n = rand.nextInt(5);

        CustomerEvent customerEvent = new CustomerEvent("id-" + n, "name-" + n);
        quoteRequestEmitter.send(customerEvent);
        return "here comes the boom";
    }
}
