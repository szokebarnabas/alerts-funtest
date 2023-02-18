import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PushgatewayClient {
    private static final String PUSHGATEWAY_ADDRESS = "http://localhost:9091/metrics/job/pushgateway/instance/my-instance";

    public static void main(String[] args) throws IOException {
        CollectorRegistry registry = new CollectorRegistry();
        Gauge duration = Gauge.build()
                .name("my_batch_job_duration_seconds").help("Duration of my batch job in seconds.").register(registry);
        Gauge.Timer durationTimer = duration.startTimer();
        try {
            // Your code here.

            // This is only added to the registry after success,
            // so that a previous success in the Pushgateway isn't overwritten on failure.
            Gauge lastSuccess = Gauge.build()
                    .name("my_batch_job_last_success").help("Last time my batch job succeeded, in unixtime.").register(registry);
            lastSuccess.setToCurrentTime();
        } finally {
            durationTimer.setDuration();
            PushGateway pg = new PushGateway("127.0.0.1:9091");
            pg.pushAdd(registry, "my_batch_job");
        }

    }
}