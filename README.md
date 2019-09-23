# Spring Cloud Platform

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

# Technologies to use:

- Protocol:    [RSocket](http://rsocket.io/) | [FAQ](http://rsocket.io/docs/FAQ) | [Motivation](http://rsocket.io/docs/Motivations) | [Github Repos](https://github.com/rsocket)
- Gateway:     [Spring Cloud Gateway](https://github.com/spring-cloud/spring-cloud-gateway)
- Configs:     [Spring Cloud Config Server](https://cloud.spring.io/spring-cloud-config/reference/html/#_spring_cloud_config_server) in combination with Git
- Testing:     [Spring Cloud Contract](https://spring.io/projects/spring-cloud-contract)
- Development: [Spring Cloud CLI](https://cloud.spring.io/spring-cloud-cli/reference/html/)
- Tracing:     [Spring Cloud Sleuth / Zipkin](https://spring.io/projects/spring-cloud-sleuth) | [Tutorial](https://spring.io/blog/2016/02/15/distributed-tracing-with-spring-cloud-sleuth-and-spring-cloud-zipkin) | [Baeldung Tutorial](https://www.baeldung.com/tracing-services-with-zipkin)
- Pipelines:   [Cloud Pipelines](https://github.com/CloudPipelines/) | [Why the migration](https://spring.io/blog/2018/11/13/spring-cloud-pipelines-to-cloud-pipelines-migration)
- Data Access: [Spring Data](https://spring.io/projects/spring-data)

# References

* [Beginner's guide to Spring Cloud](https://www.youtube.com/watch?v=aO3W-lYnw-o) | [Github Repo](https://github.com/ryanjbaxter/beginners-guide-to-spring-cloud/)
* [Building Micro-Services with Spring Cloud](https://www.youtube.com/watch?v=ZyK5QrKCbwM) (great video) | [GitHub Repository](https://github.com/joshlong/bootiful-microservices)
* [Reactive Databases](https://r2dbc.io/)
* [Aeron Protocol](https://github.com/real-logic/aeron/wiki) | [Blog Post](https://medium.com/@pirogov.alexey/aeron-low-latency-transport-protocol-9493f8d504e8)
* [RSocket and Spring Cloud Gateway](https://content.pivotal.io/slides/welcome-to-the-reactive-revolution-rsocket-and-spring-cloud-gateway-spencer-gibb) | [Older Presentation](https://qconsp.com/system/files/presentation-slides/rsocket_and_spring_cloud_gateway-spencer.gibb_.pdf)
* [Spring Cloud Gateway RSocket Sample](https://github.com/spencergibb/spring-cloud-gateway-rsocket-sample)
* [Living on the Edge: Spring Cloud Gateway](https://www.youtube.com/watch?v=jOawuL1Xnwo) (Interesting approach to exposing the Gateway as a Service (broker)!)
* [Spring Tips: Spring Cloud Gateway](https://www.youtube.com/watch?v=TwVtlNX-2Hs)
* [Distributed Reactive Streams with RSocket, Reactor, and Spring](https://www.youtube.com/watch?v=WVnAbv65uCU) (great video)
* [Netifi](https://www.netifi.com/) | [Docs](https://docs.netifi.com/1.6.8/) | [Netifi Broker vs. Spring Cloud Gateway](https://community.netifi.com/t/netifi-vs-future-spring-cloud-gateway/174) | [Youtube Channel](https://www.youtube.com/channel/UCgq8KGNViXB_D-EUpQBLHzA) | [Motivation](https://www.youtube.com/watch?v=V5bhLd_DPjM) | [Spring Demo Repo](https://github.com/netifi/spring-demo)
* [Understanding Reactive Types](https://spring.io/blog/2016/04/19/understanding-reactive-types)
* [Reactive Streams](https://www.reactive-streams.org)
* [Project Reactor](https://projectreactor.io/)
* [RxJava]()
* [Spring Boot CLI Installation](https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/html/getting-started.html#getting-started-manual-cli-installation)