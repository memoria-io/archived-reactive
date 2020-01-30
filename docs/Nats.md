# Nats Integration


## Nats Docker
* `4222` is for clients.
* `8222` is an HTTP management port for information reporting.
* `6222` is a routing port for clustering.

```bash

docker run -d -p 4222:4222 -p 6222:6222 -p 8222:8222 nats 
```

