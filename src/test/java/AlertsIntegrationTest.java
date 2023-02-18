import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.IOException;

@Testcontainers
public class AlertsIntegrationTest {

    public static final String PROMETHEUS = "prometheus";
    public static final String ALERTMANAGER = "alertmanager";
    public static final String PUSH_GATEWAY = "pushgateway";
    public static final int PROMETHEUS_SERVICE_PORT = 9090;
    public static final int PUSH_GATEWAY_SERVICE_PORT = 9091;
    public static final int ALERT_MANAGER_SERVICE_PORT = 9093;
    @Container
    public static DockerComposeContainer dockerComposeContainer =
            new DockerComposeContainer(new File("deployments/local/docker-compose.yml"))
                    .withExposedService(PROMETHEUS, PROMETHEUS_SERVICE_PORT)
                    .withExposedService(PUSH_GATEWAY, PUSH_GATEWAY_SERVICE_PORT)
                    .withExposedService(ALERTMANAGER, ALERT_MANAGER_SERVICE_PORT)
                    .withLocalCompose(true);

    @Test
    @DisplayName("Should find Book by id")
    public void shouldFindBookByIdTest() throws IOException {
        DockerService prometheus = getService(PROMETHEUS, PROMETHEUS_SERVICE_PORT);
        DockerService alertManager = getService(ALERTMANAGER, ALERT_MANAGER_SERVICE_PORT);
        DockerService pushGateway = getService(PUSH_GATEWAY, PUSH_GATEWAY_SERVICE_PORT);

        CollectorRegistry registry = new CollectorRegistry();

        Gauge containerCount = Gauge.build()
                .name("k8_number_of_containers")
                .help("some help")
                .register(registry);

        containerCount.set(18);

        PushGateway pg = new PushGateway(pushGateway.host + ":" + pushGateway.port);
        pg.pushAdd(registry, "my_batch_job");

        System.out.println(prometheus);
    }

    private DockerService getService(String serviceName, int servicePort) {
        String host = dockerComposeContainer.getServiceHost(serviceName, servicePort);
        int port = dockerComposeContainer.getServicePort(serviceName, servicePort);
        return new DockerService(host, port);
    }

    private record DockerService(String host, int port) {

    }
}
