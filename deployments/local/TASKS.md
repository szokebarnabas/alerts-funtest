Problems:

1. I cannot see the alerts loaded in prometheus - done

echo "some_metric 63.14" | curl --data-binary @- http://localhost:9091/metrics/job/pushgateway/instance/my-instance

