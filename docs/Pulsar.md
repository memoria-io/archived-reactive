# Apache Pulsar

## Standalone mode
```
docker run -it \
  -p 6650:6650 \
  -p 8080:8080 \
  apachepulsar/pulsar-standalone:2.4.2
```
Then you can go to [http://localhost:8080/admin/v2/persistent/public/default/](http://localhost:8080/admin/v2/persistent/public/default/)

For Java client Service URL is `pulsar.serviceUrl: pulsar://localhost:6650`
## Dashboard with Standalone
```
docker run -it -p 8090:80 \
  -e SERVICE_URL=localhost:8080 \
  apachepulsar/pulsar-dashboard:2.4.2
```
Then got to [localhost:8090](localhost:8090)

