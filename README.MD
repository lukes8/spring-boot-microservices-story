# Spring Boot microservices story

This project aims to create useful coding stories about microservices that can be helpful for developers.
It is also used to simply demonstrate, share and play with different aspects around programming such as design patterns, best practices etc. 
So feel free to use it and enjoy! :)

### Commands
```
todo

#management.endpoints.web.exposure.include=health,beans,mappings,httpexchanges

Actuator end points
http://localhost:${PORT}/actuator
http://localhost:${PORT}/actuator/health
http://localhost:${PORT}/actuator/circuitbreakerevents
http://localhost:${PORT}/actuator/httpexchanges

Micrometer tracing metrics examples (micrometer has glue code for actuator)
http://localhost:${PORT}/actuator/metrics/hikaricp.connections.active
```

### Useful links
```
Axon framework architecture diagram, CQRS, event sourcing 
https://docs.axoniq.io/reference-guide/architecture-overview

```

### Issues
```
- 403 forbidden with filters users API
- Zipkin trace id propagation not work within inventory  

```

