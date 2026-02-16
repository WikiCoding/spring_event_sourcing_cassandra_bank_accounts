# Summary
Small project to get familiar modeling for Cassandra and have a hands on with it. 
Used Event Sourcing with Snapshots to practice, without any concurrency checks in place.
Used the Outbox Pattern to dispatch Kafka Messages to a Queries Service which will consume those messages and Store the current state of an Account in a PostgreSQL database, creating the Projection, and exposes a GET API to check the accounts.

# Run the project

1. in the root of the project run `docker-compose up -d`
2. in the root directory run `./mvnw clean install -DskipTests`
3. if you get a problem with the `AccountOuterClass.Account` go to `target/generated-sources/protobuf/java` and mark that directory as Generated Sources Root
4. go to `queries` directory and also run `./mvnw clean install -DskipTests` and if the proto isn't compiling mark the directory in target as Generated Sources Root
5. start the projects with `./mvnw clean install -DskipTests`
6. visit `localhost:80` to check Cassandra's key spaces and tables
7. visit `localhost:8083` to check Adminer for PostgresSQL database and tables
8. visit `localhost:8080` to check the Kafka UI
9. visit `localhost:16686` to check Distributed Tracing in Jaeger
10. visit `http://localhost:3000` to explore the logs and metrics from both apps in Grafana

# APIs
## Bank Accounts Lifecycle Engine
1. Create account -> POST to http://localhost:8081/accounts with body
```json
{
    "accountName": "First Name",
    "startBalance": 100.0
}
```
2. Deposit money to bank account -> POST to http://localhost:8081/accounts/:accountNumber/deposit with body
```json
{
    "amount": 100.0
}
```
3. Withdraw money to bank account -> POST to http://localhost:8081/accounts/:accountNumber/withdraw with body
```json
{
  "amount": 25.0
}
```
4. Rebuild the state of an account without starting the queries service -> GET to http://localhost:8081/accounts/:accountNumber

## Bank Accounts Queries
1. Current state of an Account -> GET to http://localhost:8082/accounts/:accountNumber

# Protobuf Model (Account.proto)
```protobuf
syntax = "proto3";

package account;

option java_package = "com.wikicoding.bank_account_lifecycle_engine";

message Account {
  string account_number = 1;
  string account_name = 2;
  double balance = 3;
  int64 created_at = 4;
  int32 version = 5;
}
```
## pom.xml changes to run the protoc automatically and protobuf dependency
```xml
<dependencies>
    <dependency>
        <groupId>com.google.protobuf</groupId>
        <artifactId>protobuf-java</artifactId>
        <version>4.32.0</version>
    </dependency>
</dependencies>

<build>
    <extensions>
        <!--            extension to detect OS at use-->
        <extension>
            <groupId>kr.motd.maven</groupId>
            <artifactId>os-maven-plugin</artifactId>
            <version>1.7.1</version>
        </extension>
    </extensions>
    <plugins>
        <plugin>
            <groupId>org.xolstice.maven.plugins</groupId>
            <artifactId>protobuf-maven-plugin</artifactId>
            <version>0.6.1</version>

            <configuration>
                <protoSourceRoot>
                    ${project.basedir}/src/main/java
                </protoSourceRoot>
                <!-- protoc compiler -->
                <protocArtifact>
                    com.google.protobuf:protoc:3.25.3:exe:${os.detected.classifier}
                </protocArtifact>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>compile</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

# Considerations and findings
1. Spring Data Cassandra dependency changed between Springboot 3.X and 4.X. For Springboot 4.X using
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-cassandra</artifactId>
    <version>4.1.0-M1</version>
    <scope>compile</scope>
</dependency> 
```
2. Spring Kafka dependency changed between Springboot 3.X and 4.X. For Springboot 4.X using
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-kafka</artifactId>
</dependency> 
```
3. Distributed Tracing with OTLP Exporter and Jaeger is now incompatible with Springboot 4.X. Springboot 4.X now comes with built-in first class support for OpenTelemetry
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-opentelemetry</artifactId>
</dependency>
<dependency>
    <groupId>com.github.loki4j</groupId>
    <artifactId>loki-logback-appender</artifactId>
    <version>1.6.0</version>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency> 
```
```bash
spring.kafka.template.observation-enabled=true
spring.kafka.listener.observation-enabled=true

management.endpoints.web.exposure.include=*
management.prometheus.metrics.export.enabled=true
management.prometheus.metrics.export.pushgateway.enabled=true
management.opentelemetry.tracing.export.otlp.endpoint=http://localhost:4318/v1/traces
management.tracing.sampling.probability=1.0
management.otlp.metrics.export.enabled=false
```
4. Cassandra rule number 1 might be that you should create your data models based on how you need to query the data
5. Cassandra shows very fast writes, and it feels slower when querying data