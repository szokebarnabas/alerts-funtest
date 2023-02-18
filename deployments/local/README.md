Api calls

1. Set up your Prometheus and Alertmanager instances and configure your alerting rules as needed.
2. Create a test case that triggers the condition that should cause the alert to fire.
3. Use a testing framework such as pytest or Selenium to automate the test case.
4. Use the Alertmanager API to query the current status of alerts and check if the expected alert has been fired.

Assert that the alert status returned by the API matches the expected status.

# Delete time series in prometheus:
https://prometheus.io/docs/prometheus/latest/querying/api/#delete-series

# Verify if rules are deployed to an environment:

Get deployed rules in prometheus: GET http://localhost:9090/api/v1/rules

# Check if an alert is being raised in alert manager
Get raised alerts in alertmanager: GET http://localhost:9093/api/v2/alerts

# Delete the time series of a specific metric:
http://localhost:9090/api/v1/admin/tsdb/delete_series?match[]=up

Python example:

Test if alert is firing

```
import requests

def test_alert_firing():
    # Trigger the condition that should cause the alert to fire

    # Wait for the alert to be fired and processed by Alertmanager
    # You can wait for a few seconds to allow enough time for the alert to fire and propagate.

    # Query the Alertmanager API to get the list of active alerts
    response = requests.get('http://localhost:9093/api/v2/alerts')

    # Check if the expected alert is in the list and its firing status is "firing"
    alerts = response.json()['data']
    alert_names = [alert['labels']['alertname'] for alert in alerts]
    assert 'my_alert_name' in alert_names

    my_alert = alerts[alert_names.index('my_alert_name')]
    assert my_alert['status'] == 'firing'
```

Delete time series (for cleanup between test scenarios)
```
import requests

prometheus_url = 'http://localhost:9090'

# Example PromQL query to delete a specific time series
query = 'del_series(my_metric{instance="example.com:9100"})'

# Construct the DELETE request
url = f"{prometheus_url}/api/v1/admin/tsdb/delete_series?match[]={query}"
response = requests.delete(url)

# Check the response status code for success or error
if response.status_code == 200:
    print(f"Time series matching query '{query}' were deleted successfully.")
else:
    print(f"Error deleting time series: {response.text}")

```

Push gateway example:

```
from prometheus_client import CollectorRegistry, Gauge, push_to_gateway

registry = CollectorRegistry()
g = Gauge('my_metric', 'This is my metric', registry=registry)
g.set(10)

push_to_gateway('pushgateway.example.com:9091', job='my_job', registry=registry)


```