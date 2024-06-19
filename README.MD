# Spring Boot microservices story

This project aims to create useful coding stories about microservices that can be helpful for developers.
It is also used to simply demonstrate, share and play with different aspects around programming such as design patterns, best practices etc.
Please keep in mind that this story is like living organism that keep growing in sense nothing is perfect and everything counts :)   
So feel free to use it and enjoy! :)

### Useful Support & Commands & Links
```
Actuator end points
http://localhost:${PORT}/actuator
http://localhost:${PORT}/actuator/health
http://localhost:${PORT}/actuator/circuitbreakerevents
http://localhost:${PORT}/actuator/httpexchanges

Config exposure endpoints via actuator: 
#management.endpoints.web.exposure.include=health,beans,mappings,httpexchanges

Micrometer tracing metrics examples (micrometer has glue code for actuator)
http://localhost:${PORT}/actuator/metrics/hikaricp.connections.active

Command Line Passing Args, possible ways
./gradlew :order-ws:bootRun "-Dserver.port=6669" -Dport=6661 -Dgreen_arg=greenfield "-Dgreen_arg2=green world"

```

### Useful external links
```
Axon framework architecture diagram, CQRS, event sourcing 
https://docs.axoniq.io/reference-guide/architecture-overview

```

### Issues
```
- 403 forbidden with filters users API
- Zipkin trace id propagation not work within inventory  

```

