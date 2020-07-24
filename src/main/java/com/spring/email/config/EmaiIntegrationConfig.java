package com.spring.email.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.mail.dsl.Mail;
import org.springframework.integration.mail.support.DefaultMailHeaderMapper;
import org.springframework.integration.mapping.HeaderMapper;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.scheduling.support.PeriodicTrigger;

import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Configuration
public class EmaiIntegrationConfig {

    @Value("${mail.store.protocol}")
    private String storeProtocol;

    @Value("${mail.imap.host}")
    private String imapHost;


    @Value("${mail.imap.port}")
    private String imapPort;

    @Value("${mail.imap.username}")
    private String imapUsername;

    @Value("${mail.imap.password}")
    private String imapPassword;

    @Value("${mail.imap.socketFactory.class}")
    private String socketFactoryClass;

    @Value("${mail.imap.socketFactory.fallback}")
    private String socketFactoryFallback;



    @Value("${mail.debug}")
    private String mailDebug;


    @Bean
    public HeaderMapper<MimeMessage> mailHeaderMapper() {
        return new DefaultMailHeaderMapper();
    }

    @Bean
    public IntegrationFlow integrationFlow() {
        IntegrationFlow flow =  IntegrationFlows
                                    .from(Mail.imapInboundAdapter(imapUrl())
                                .javaMailProperties(javaMailProperties())
                                .shouldMarkMessagesAsRead(false)
                                .shouldDeleteMessages(false)
                                .simpleContent(true),
                        e -> e.autoStartup(true)
                                .poller(p -> p.fixedDelay(5000))
                                    )
                .channel(MessageChannels.queue("imapChannel"))
                .get();
        return flow;
    }


    private Properties javaMailProperties(){
        Properties properties = new Properties();
        properties.setProperty("mail.imap.socketFactory.class",socketFactoryClass);
        properties.setProperty("mail.imap.socketFactory.fallback",socketFactoryFallback);
        properties.setProperty("mail.store.protocol",storeProtocol);
        properties.setProperty("mail.debug",mailDebug);
        return properties;
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata defaultPoller() {

        PollerMetadata pollerMetadata = new PollerMetadata();
        pollerMetadata.setTrigger(new PeriodicTrigger(1000));
        return pollerMetadata;
    }


    private String imapUrl() {
        return storeProtocol+"://"+imapUsername+":"+imapPassword+"@"+imapHost+":"+imapPort+"/inbox";
    }
}
