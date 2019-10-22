# Spring Cloud Platform

## Pre-Requisites

# Installing Spring Cloud CLI

There are a variety of ways to install Spring Cloud CLI, and you can find all of them [here](https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/html/getting-started.html#getting-started-manual-cli-installation).  
The easiest way, however, is to use [Homebrew](https://brew.sh/) (requires a Mac environment).

Using Homebrew simply execute these few commands:

```bash
$ brew tap pivotal/tap
$ brew install springboot
```
This installs the Spring **Boot** CLI. After that, you need to install the Spring Cloud CLI plugin like this:

```bash
$ spring install org.springframework.cloud:spring-cloud-cli:2.1.0.RELEASE
```

To check the installed Spring Boot CLI version and the available Spring Cloud services you can start up locally, run the following commands:

```bash
$ spring version      # print the version of the Spring Boot CLI
$ spring cloud --list # print the available services you can start with Spring Cloud CLI
```

To bring up a service via the CLI, you can type:

```bash
$ spring cloud eureka # bring up a Eureka service registry locally.
```

For more information see the [Spring Cloud CLI documentation](https://cloud.spring.io/spring-cloud-cli/reference/html/).

# Starting or Deploying Zipkin Server

Many of the services of this project use Spring Cloud Sleuth](https://spring.io/projects/spring-cloud-sleuth) with an integration with [Zipkin](https://zipkin.io/) to write trace logs in order to trace requests analyce latencies.

Trace logs generated by a service are sent to a Zipkin server instance expected to be running (by default) on http://localhost:9411/zipkin.
The easiest way to run a Zipkin server is to use Docker:

```bash
docker run -p 9411:9411 --name zipkinServer openzipkin/zipkin
```

... or ...

```bash
./scripts/startZipkin.sh
```

This will pull and start a Zipkin server container and once it is started you can access its UI using the link http://localhost:9411/zipkin.

As services will be making requests and logging traces, you will see them in the Zipkin UI. This allows you not only to see which requests are being sent and received but also how long they took and if they were successful or not.

Note, that Spring Cloud Sleuth and Zipkin also work for message-based communication, e.g. using RabbitMQ. See the Spring Cloud Sleuth](https://cloud.spring.io/spring-cloud-static/spring-cloud-sleuth/2.1.3.RELEASE/single/spring-cloud-sleuth.html#_sleuth_with_zipkin_over_rabbitmq_or_kafka) documentation for details.

# Starting or Deploying Message Broker

```bash
docker run -d --hostname my-rabbit --name rabbitBroker -p 15672:15672 -p 5672:5672 rabbitmq:3-management
```

... or ...

```bash
./scripts/startRabbit.sh
```

Once started you can access its UI using http://localhost:15672/#/.  
**Username:** guest
**Password:** guest

# Service Startup Order

Required startup order (ideally):
1. Zipkin server
2. RabbitMQ
3. service-registry 
4. config-server instances 
5. services (i.e. service-registry clients)

For 1. - 3. the order does not really matter. They can be started in any order and will find one another.
Services need to be started last, as they will require configuration for startup coming from the config-server.

# Config Server

[Access in Browser](http://localhost:1111/service/master_or_profile)

This project was created with [Spring Starter](https://start.spring.io/) using the following dependencies:

* Config Server
* Zipkin Client
* Eureka Discovery Client
* Spring Boot Actuator
* Cloud Bus (Adds `spring-cloud-bus`)
* Spring for RabbitMQ (adds `spring-boot-starter-amqp`, and `spring-cloud-stream-binder-rabbit` for Spring Cloud Bus)

Project `config-server` is a Spring Boot application that acts as a central configuration server.
It uses [Spring Cloud Config Server](https://cloud.spring.io/spring-cloud-config/reference/html/#_spring_cloud_config_server) to serve configurations that are stored in a [GitHub repository](https://github.com/FWinkler79/SpringCloudPlatform-Configs).

- Bootstraps its own configurations by loading them from GitHub.
- Uses Zipkin / Sleuth to report tracing information.
- Is a Eureka client, i.e. registers itself with service registry.
- Services find / lookup config-server via Eureka.
- Allows more than one config-server instances to run and be addressed by one logical service name.
- config-server (and all its potential instances) need to start before any service that reads configuration is started.
  (See [this Spring Cloud Config issue](https://github.com/spring-cloud/spring-cloud-config/issues/514).)
- Configs are loaded using the following URL pattern `/{name}/{profile}/{label}` where
  - name: service ID
  - profile: active spring profile (optional)
  - label: usually `master` but can be any git tag, commit ID or branch name.
  - see [Spring Cloud Config](https://cloud.spring.io/spring-cloud-config/reference/html/) and [Locating Remote Configuration Resources](https://cloud.spring.io/spring-cloud-config/reference/html/#_locating_remote_configuration_resources)
- Uses Spring Cloud Bus, RabbitMQ and Spring Cloud Config Monitor to expose an `/monitor` endpoint which can be registered as a GitHub WebHook. Every time the configuration changes in GitHub, GitHub sends an event to the `/monitor` endpoint.
  The controller behind the `/monitor` endpoint fires a refresh event via the Spring Cloud Bus (using RabbitMQ as the signalling layer), BUT: only to the service instances that are affected by the change in GitHub (based on Eureka service registry).
  Services refresh automatically with changes in the configurations being pushed to GitHub!
- Exposes `http://localhost:1111/actuator/bus-refresh` endpoint, to refresh all services on the bus at once. See also: `http://localhost:1111/actuator/bus-env` where you could post key-value pairs to add to every service's environment.
  See also [addressing of individual service instance](https://cloud.spring.io/spring-cloud-bus/reference/html/index.html#addressing-an-instance).
- Use http://smee.io to forward WebHook events to localhost.

You can see the configurations the server provides by opening its endpoint in a browser. 
For example, `http://localhost:1111/reservation-service/master` shows the configurations the `reservation-service` sees. With the URL `http://localhost:1111/application/master` you can see the configurations **every** application sees (the basic configurations).
Note  that the reservation-service in this example sees an layered view of its own configurations mixed with the basic configurations!
Using `http://localhost:1111/reservation-service/cloud/master` will show the configurations `reservation-service` will see, if the `cloud` profile is active in the service (e.g. by setting `spring.profiles.active=cloud` in the `reservation-service`'s environment).

## Updating Configs

Update files, push to GitHub, then refresh services that need updated configs.

Actuator endpoints:
- For debugging: `/actuator/env` and `/actuator/health`
- For Refreshing: `/actuator/refresh` - POST only. Send empty POST request. Receive list of changed properties (delta) after refresh.

**Note:** Needs Rabbit MQ running!

# Service Registry

[Access in Browser](http://localhost:8761/)

This project was created with [Spring Starter](https://start.spring.io/) using the following dependencies:

* Eureka Server
* Config Client
* Zipkin Client
* Spring Boot Actuator

- Currently does not use configs from config-server but brings its own.
- Startup is independent from config-server

# Greetings Service

[Access in Browser](http://localhost:5555/greeting)

This project was created with [Spring Starter](https://start.spring.io/) using the following dependencies:

* Spring Web
* Spring for RabbitMQ (adds `spring-boot-starter-amqp`, and `spring-cloud-stream-binder-rabbit` for Spring Cloud Bus)
* Cloud Bus (Adds `spring-cloud-bus`)
* Config Client
* Zipkin Client
* Eureka Discovery Client
* Spring Boot Actuator

There is a [greetings endpoint](http://localhost:5555/greeting), that shows a greetings message that comes from the `greetings-service.yml` configuration in the [Configs Repository](https://github.com/FWinkler79/SpringCloudPlatform-Configs).
With Rabbit MQ running (`./scripts/startRabbit.sh`) and a connection to Smee.io established (`./scripts/connectToSmee.sh`), you can change the configs in the repo, and see how the message gets updated automatically (you need to refresh greetings endpoint).

# Reservation Service

[Access in Browser](http://localhost:2222/reservations?page=1&size=2&sort=reservationName,asc)

This project was created with [Spring Starter](https://start.spring.io/) using the following dependencies:

* Spring Reactive Web
* Rest Repositories
* Cloud Bus (Adds `spring-cloud-bus`)
* Spring for RabbitMQ (adds `spring-boot-starter-amqp`, and `spring-cloud-stream-binder-rabbit` for Spring Cloud Bus)
* Spring Data JPA
* H2 Database
* Config Client
* Zipkin Client
* Eureka Discovery Client
* Spring Boot Actuator
* RSocket
* Lombok

Note that the service URL (`http://localhost:2222/reservations?page=1&size=2&sort=reservationName,asc`) contains parameters for **sorting and paging** - all out of the box with [Spring Data](https://spring.io/projects/spring-data).
There is also a "search by name" endpoint available. E.g. `http://localhost:2222/reservations/search/by-name?reservationName=Carl` will return only the reservations whose name is "Carl".

# Reservation Service Client

[Access in Browser](http://localhost:9999/healthStatus})
[Access via Postman (HTTP POST)](http://localhost:9999/reservation/create/{reservationName})

This project was created with [Spring Starter](https://start.spring.io/) using the following dependencies:

* Spring Reactive Web
* Cloud Bus (Adds `spring-cloud-bus`)
* Spring for RabbitMQ (adds `spring-boot-starter-amqp`, and `spring-cloud-stream-binder-rabbit` for Spring Cloud Bus)
* Config Client
* Zipkin Client
* Eureka Discovery Client
* Spring Boot Actuator
* RSocket
* Lombok

# Service Gateway

[Access in Browser](http://localhost:7777/)

This project was created with [Spring Starter](https://start.spring.io/) using the following dependencies:

* Gateway
* Spring Boot Actuator
* Eureka Discovery Client
* Config Client
* Cloud Bus (Adds `spring-cloud-bus`)
* Spring for RabbitMQ (adds `spring-boot-starter-amqp`, and `spring-cloud-stream-binder-rabbit` for Spring Cloud Bus)
* Zipkin Client
* Lombok

And we manually added the RSocket gateway broker, which at the time of writing was in technical preview stage and not yet available via Spring starter.io:

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-gateway-rsocket-broker</artifactId>
</dependency>
```

# Diagnostics Service

[Access in Browser](http://localhost:8777/)

This project was created with [Spring Starter](https://start.spring.io/) using the following dependencies:

* Eureka Discovery Client
* Zipkin Client
* Spring Boot Starter Web
* Spring Boot Actuator

Additionally references WebJars to provide static UI content.

Usage: `http://localhost:8777/lookup/<serviceName>` e.g. `http://localhost:8777/lookup/config-server`. The output will be the information that is returned by Eureka.

# Technologies to use:

- Protocol:    [RSocket](http://rsocket.io/) | [FAQ](http://rsocket.io/docs/FAQ) | [Motivation](http://rsocket.io/docs/Motivations) | [Github Repos](https://github.com/rsocket)
- Gateway:     [Spring Cloud Gateway](https://github.com/spring-cloud/spring-cloud-gateway)
- Configs:     [Spring Cloud Config Server](https://cloud.spring.io/spring-cloud-config/reference/html/#_spring_cloud_config_server) in combination with Git | [Spring Cloud Bus](https://spring.io/projects/spring-cloud-bus)
- Testing:     [Spring Cloud Contract](https://spring.io/projects/spring-cloud-contract)
- Development: [Spring Cloud CLI](https://cloud.spring.io/spring-cloud-cli/reference/html/)
- Tracing:     [Spring Cloud Sleuth / Zipkin](https://spring.io/projects/spring-cloud-sleuth) | [Tutorial](https://spring.io/blog/2016/02/15/distributed-tracing-with-spring-cloud-sleuth-and-spring-cloud-zipkin) | [Baeldung Tutorial](https://www.baeldung.com/tracing-services-with-zipkin) | [Video](https://content.pivotal.io/springone-platform-2017/distributed-tracing-latency-analysis-for-your-microservices-grzejszczak-krishna) 
- Pipelines:   [Cloud Pipelines](https://github.com/CloudPipelines/) | [Why the migration](https://spring.io/blog/2018/11/13/spring-cloud-pipelines-to-cloud-pipelines-migration)
- Data Access: [Spring Data](https://spring.io/projects/spring-data)
- REST Docs:   [Spring REST Docs](https://spring.io/projects/spring-restdocs)
- Secure Password Storage: [Spring Vault](https://spring.io/projects/spring-vault) | [Vault](https://www.vaultproject.io/) | [Vault Docker Image](https://hub.docker.com/_/vault)

# Appendix

## Understanding Spring / Spring Boot / Spring Cloud Versioning

When you are developing with Spring, you will usually want to use the released versions. However, there may be times in which you will need to switch to a newer milestone, e.g. in case of errors in a release or if you are asked to try out a new fix the Spring teams have introduced.

Generally, the following version patterns are usually encountered with Spring / Spring Boot:

1. major.minor.patch.RELEASE
2. major.minor.patch.SRx (with x=[1..N))
3. major.minor.patch.Mx (with x=[1..N))
4. major.minor.patch.RCx (with x=[1..N))
5. major.minor.patch.BUILD-SNAPSHOT

The version suffixes mean the following:
* RELEASE - the (first) release and production-ready version of an artifact, e.g. `2.1.18.RELEASE`
* SRx - one of potentially multiple _service releases_, i.e. newer versions of a released artifact, e.g. containing critical patches. Example: `2.1.18.SR2`
* Mx - one of potentially many _milestone_ releases, usually relevant if you already want to take a peek at the next version the Spring team is working on. E.g. `2.2.0.M6`
* RCx - one of potentially multiple _release candidates_. These are usually newer than any milestone, and constitute one of potentially multiple milestones that are considered for final release.
* BUILD-SNAPSHOT - the (usually) latest snapshot versions of an artifact. Note, that we have experienced some release candidate versions being more up to date than the BUILD-SNAPSHOTs!

The general rule of thumb is:
1. If you intend to stay on the safe release path, prefer service release (`SRx`) versions over release (`RELEASE`) versions. And pick the one with the highest number, e.g. `SR4` rather than `SR3`.
1. If you intend to peek into the next version coming, prefer release candidate (`RCx`) versions over milestone (`Mx`) versions. And pick the one with the highest number, e.g. `RC2` over `RC1` over `M6`.

Spring and Spring Boot usually use numbers for versions. For Spring Cloud, usually, no major.minor.patch versions are used, but names - e.g. those of British towns - in alphabetically increasing order.
For example, at the time of writing, the latest release of Spring Cloud is the `Greenwhich` release, with `Hoxton` milestones available as the next release is being prepared.
Predecessors of `Greenwhich` are `Angel`, `Brighton`, `Camden`, `Dalston`, `Edgware`, and `Finchley` - you get the idea.
For Spring cloud the same version suffixes apply as stated above.

If you intend to use milestone or snapshot versions, you will have to add the following repository configurations to your Maven `pom.xml` for the dependencies to be resolved properly:

For Milestones:
```xml 
<repositories>
  <repository>
    <id>spring-milestones</id>
    <name>Spring Milestones</name>
    <url>https://repo.spring.io/milestone</url>
  </repository>
</repositories>
<pluginRepositories>
  <pluginRepository>
    <id>spring-milestones</id>
    <name>Spring Milestones</name>
    <url>https://repo.spring.io/milestone</url>
  </pluginRepository>
</pluginRepositories>
```

For SNAPSHOTs:

```xml
<repositories>
  <repository>
    <id>spring-milestones</id>
    <name>Spring Snapshots</name>
    <url>https://repo.spring.io/snapshot</url>
  </repository>
</repositories>
<pluginRepositories>
  <pluginRepository>
    <id>spring-milestones</id>
    <name>Spring Snapshots</name>
    <url>https://repo.spring.io/snapshot</url>
  </pluginRepository>
</pluginRepositories>
```

The milestone repository links given above will contain also releases and service releases. Thus, you will also be able to resolve `.RELEASE` versions while pointing to the milestones repository.
In case you only want to see the milestones and release candidates you can use the following URLs instead:

* For Milestones: https://repo.spring.io/libs-milestone-local
* For Snapshots: https://repo.spring.io/libs-snapshot-local

❗️Note: In case you are working from behind a Maven mirror (e.g. Sonatype Nexus) that does not contain Spring milestones and snapshots, you will need to make sure (in you Maven `settings.xml`) that Maven by-passes the mirror when it tries to resolve the milestone or snapshot Spring dependencies.

# References

**Spring Cloud**
* [Beginner's guide to Spring Cloud](https://www.youtube.com/watch?v=aO3W-lYnw-o) | [Github Repo](https://github.com/ryanjbaxter/beginners-guide-to-spring-cloud/)
* [Building Micro-Services with Spring Cloud](https://www.youtube.com/watch?v=ZyK5QrKCbwM) (great video) | [GitHub Repository](https://github.com/joshlong/bootiful-microservices)
* [Spring Tips: Spring Cloud Gateway](https://www.youtube.com/watch?v=TwVtlNX-2Hs) | [Living on the Edge: Spring Cloud Gateway](https://www.youtube.com/watch?v=jOawuL1Xnwo) (Interesting approach to exposing the Gateway as a Service (broker)!)
* [Spring Boot CLI Installation](https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/html/getting-started.html#getting-started-manual-cli-installation)
* [Spring Cloud Bus](https://spring.io/projects/spring-cloud-bus) | [Baeldung Tutorial](https://www.baeldung.com/spring-cloud-bus)

**RSocket**
* [Distributed Reactive Streams with RSocket, Reactor, and Spring](https://www.youtube.com/watch?v=WVnAbv65uCU) (great video)
* [RSocket and Spring Cloud Gateway Video](https://www.youtube.com/watch?v=PfbycN_eqhg) | [Presentation](https://content.pivotal.io/slides/welcome-to-the-reactive-revolution-rsocket-and-spring-cloud-gateway-spencer-gibb) | [Older Presentation](https://qconsp.com/system/files/presentation-slides/rsocket_and_spring_cloud_gateway-spencer.gibb_.pdf) | [Sample](https://github.com/ciberkleid/spring-flights) | [Older Sample](https://github.com/spencergibb/spring-cloud-gateway-rsocket-sample)
* [RSocket Support in Spring 5.2 Video](https://www.youtube.com/watch?v=iSSrZoGtoSE) | [Documentation](https://docs.spring.io/spring/docs/5.2.0.RELEASE/spring-framework-reference/web-reactive.html#rsocket) (the basis for Spring Boot RSocket support)
* [RSocket Support in Spring Boot 2.2](https://docs.spring.io/spring-boot/docs/2.2.0.M6/reference/html/spring-boot-features.html#boot-features-rsocket)
* [RSocket Support in Spring Security Video](https://youtu.be/iSSrZoGtoSE?t=2844) | [Documentation](https://docs.spring.io/spring-security/site/docs/5.2.0.RELEASE/reference/htmlsingle/#rsocket) | [Sample](https://github.com/bclozel/spring-flights/tree/security)
* [Netifi](https://www.netifi.com/) | [Docs](https://docs.netifi.com/1.6.8/) | [Netifi Broker vs. Spring Cloud Gateway](https://community.netifi.com/t/netifi-vs-future-spring-cloud-gateway/174) | [Youtube Channel](https://www.youtube.com/channel/UCgq8KGNViXB_D-EUpQBLHzA) | [Motivation](https://www.youtube.com/watch?v=V5bhLd_DPjM) | [Spring Demo Repo](https://github.com/netifi/spring-demo)
* [Aeron Protocol](https://github.com/real-logic/aeron/wiki) | [Blog Post](https://medium.com/@pirogov.alexey/aeron-low-latency-transport-protocol-9493f8d504e8)
* [RSocket CLI](https://github.com/rsocket/rsocket-cli) (great for debugging and running RSocket servers easily. See also [RSocket Support in Spring 5.2 Video](https://www.youtube.com/watch?v=iSSrZoGtoSE) for usage)

**Reactive Programming**
* [Understanding Reactive Types](https://spring.io/blog/2016/04/19/understanding-reactive-types)
* [Reactive Streams](https://www.reactive-streams.org)
* [Project Reactor](https://projectreactor.io/)
* [RxJava](https://github.com/ReactiveX/RxJava)
* [101 Reactive Gems](https://github.com/reactor/reactive-streams-commons/issues/21)
* [Notes on Reactive Programming (Part 1) - The Reactive Landscape](https://spring.io/blog/2016/06/07/notes-on-reactive-programming-part-i-the-reactive-landscape) | [Notes on Reactive Programming (Part 2) - Writing Some Code](https://spring.io/blog/2016/06/13/notes-on-reactive-programming-part-ii-writing-some-code)
* [Reactive Databases](https://r2dbc.io/)

**Dristributed Tracing**
* [Distributed Tracing with Zipkin](https://www.youtube.com/watch?v=f9J1Av8rwCE)
* [Open Tracing Initiative](https://github.com/opentracing)

