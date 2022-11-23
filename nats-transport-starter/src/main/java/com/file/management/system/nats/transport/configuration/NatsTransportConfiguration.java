package com.file.management.system.nats.transport.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import java.io.IOException;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringValueResolver;

@Configuration
@PropertySource("classpath:nats-default.properties")
@ComponentScan(basePackages = "com.file.management.system.nats.transport")
public class NatsTransportConfiguration {

    @Value("${spring.nats.server}")
    private String server;

    @Value("${spring.nats.max-reconnects}")
    private Integer maxReconnects;

    @Value("${spring.nats.reconnect-wait-millis}")
    private Integer reconnectWaitMillis;

    @Bean
    public Connection connection() throws InterruptedException, IOException {
        return Nats.connect(new Options.Builder()
          .server(server)
          .maxReconnects(maxReconnects)
          .reconnectWait(Duration.ofMillis(reconnectWaitMillis))
          .build());
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Bean
    public StringValueResolver stringValueResolver(ConfigurableApplicationContext applicationContext) {
        return new EmbeddedValueResolver(applicationContext.getBeanFactory());
    }

}