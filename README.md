# Eureka Threshold Issue

## Build Components

Execute on project root:

´´´shell
mvn clean package
´´´

## Run Components

### Service Registry

```shell
cd service-registry
mvn spring-boot:run
```

### Service Registry Client

❗️**Note:** Make sure Service Registry was started beforehand.

```shell
cd service-registry-client
mvn spring-boot:run
```

# Issue Descriptions:

## Renewal threshold in standalone mode when only Eureka is running

### Reproduction

1. Start Eureka
2. Notice the log output displaying the currently calculated renewal threshold
   ```
    Tomcat started on port(s): 8761 (http) with context path ''
    2020-03-25 11:57:57.388  INFO 43411 --- [           main] .s.c.n.e.s.EurekaAutoServiceRegistration : Updating port to 8761
    2020-03-25 11:57:57.395  INFO 43411 --- [           main] c.e.c.serviceregistry.ServiceRegistry    : Started ServiceRegistry in 3.241 seconds (JVM running for 3.566)
    2020-03-25 11:57:59.314  INFO 43411 --- [hresholdUpdater] c.n.e.r.PeerAwareInstanceRegistryImpl    : Current renewal threshold is : 15
    2020-03-25 11:57:59.365  INFO 43411 --- [a-EvictionTimer] c.n.e.registry.AbstractInstanceRegistry  : Running the evict task with compensationTime 0ms
    2020-03-25 11:58:01.315  INFO 43411 --- [hresholdUpdater] c.n.e.r.PeerAwareInstanceRegistryImpl    : Current renewal threshold is : 15
    2020-03-25 11:58:01.366  INFO 43411 --- [a-EvictionTimer] c.n.e.registry.AbstractInstanceRegistry  : Running the evict task with compensationTime 0ms
    2020-03-25 11:58:03.320  INFO 43411 --- [hresholdUpdater] c.n.e.r.PeerAwareInstanceRegistryImpl    : Current renewal threshold is : 15
   ```
3. The log output shows that the calculated renewal threshold is 15 - this seems wrong.

Eureka is configured with the following settings:

```yaml
eureka:
  # For documentation of server configs, see: https://github.com/Netflix/eureka/blob/master/eureka-core/src/main/java/com/netflix/eureka/EurekaServerConfig.java, 
  # For default values, see:                  https://github.com/Netflix/eureka/blob/f660f788e8309621186deee6ffe9425ab8243056/eureka-core/src/main/java/com/netflix/eureka/DefaultEurekaServerConfig.java 
  server:                         
    enable-self-preservation: true                 # Remove service instances that don't send heartbeats on time. Review this setting in a resilient setup. See: https://github.com/Netflix/eureka/wiki/Server-Self-Preservation-Mode
    expected-client-renewal-interval-seconds:  2   # Make sure this is is set to the same value as the lease renewal interval in clients (or slightly higher)
    eviction-interval-timer-in-ms:             2000
    response-cache-auto-expiration-in-seconds: 2
    renewal-threshold-update-interval-ms:      2000 # Default: 15 * 60 * 1000
    renewal-percent-threshold:                 0.5  # Default: 0.85
  instance:
    lease-renewal-interval-in-seconds: 2           # Only for local development. remove this setting in production and thus default to 30 seconds.
```

This means that...
1. Eureka expects client renewals every **2** seconds
2. Uses a renewal threshold of 50%
3. Updates its threshold (in case of new clients joining) every 2 seconds.

With these settings, and a standalone Eureka, that has no clients connected, the `Current renewal threshold` should be 0.
Reason: 0 clients = 0 expected renewals, 50% of 0 = 0.

However, Eureka displays a current renewal threshold of 15, which in my opinion indicates two issues:

1. Eureka sees itself as a client
2. Eureka ignores the `expected-client-renewal-interval-seconds` configuration set to two, and instead takes 30 (the default) as its value.

Reason: 1 client (Eureka itself) = 30 expected 

