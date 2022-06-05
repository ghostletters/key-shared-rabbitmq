package xyz.ghostletters.setup;

import io.quarkus.runtime.StartupEvent;
import org.testcontainers.Testcontainers;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class TestContainerHostNetwork {

    public void onStart(@Observes StartupEvent startupEvent) {
//        Testcontainers.exposeHostPorts(5673);
    }
}
