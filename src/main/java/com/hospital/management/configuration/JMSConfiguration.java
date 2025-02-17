package com.hospital.management.configuration;

import jakarta.jms.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

/**
 * Configuration class for setting up JMS (Java Message Service) in the hospital management system.
 * This class enables JMS and provides a bean definition for the JMS listener container factory.
 */
@EnableJms
@Configuration
public class JMSConfiguration {

    /**
     * Creates and configures a JMS listener container factory.
     * This factory is used to manage JMS listeners with a specified concurrency level
     * and transaction support.
     *
     * @param connectionFactory the JMS connection factory used to establish connections
     * @return a configured {@link JmsListenerContainerFactory} instance
     */
    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        // Sets the concurrency level for JMS listeners, allowing 3 to 10 concurrent consumers
        factory.setConcurrency("3-10");
        factory.setAutoStartup(false);

        // Enables session transactions to ensure message processing reliability
        factory.setSessionTransacted(true);
        return factory;
    }
}
