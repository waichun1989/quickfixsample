package com.example.demo.config;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.quickfixj.jmx.JmxExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import quickfix.*;

import javax.sql.DataSource;
import java.io.InputStream;

@Configuration
public class FixEngineConfig {
    private final Logger log = LoggerFactory.getLogger(FixEngineConfig.class);

    @Value("${fix.config-path}")
    private String configPath;

    private Acceptor acceptor;
    private JmxExporter jmxExporter;

    private final Application quickfixApp;
    private final DataSource dataSource;

    public FixEngineConfig(Application quickfixApp, DataSource dataSource) {
        this.quickfixApp = quickfixApp;
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void start() throws Exception {
        log.info("Starting QuickFIX/J Acceptor from {}", configPath);
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("quickfixj.cfg");
        if (stream == null) throw new IllegalStateException("quickfixj.cfg not found on classpath");

        SessionSettings settings = new SessionSettings(stream);

// Choose JDBC store for production reliability
        MessageStoreFactory storeFactory = new FileStoreFactory(settings);
        MessageFactory messageFactory = new DefaultMessageFactory();
        LogFactory logFactory = new FileLogFactory(settings);

        acceptor = new SocketAcceptor(quickfixApp, storeFactory, settings, logFactory, messageFactory);
        jmxExporter = new JmxExporter();
        jmxExporter.register(acceptor);
        acceptor.start();
        log.info("QuickFIX/J Acceptor started");
    }

    @PreDestroy
    public void stop() {
        try {
            log.info("Stopping QuickFIX/J Acceptor");
            if (acceptor != null) acceptor.stop();
        } catch (Exception e) {
            log.error("Error stopping acceptor", e);
        }
    }
}
