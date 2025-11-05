package com.example.demo.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import quickfix.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class QuickfixApp implements Application {

    private final Logger log = LoggerFactory.getLogger(QuickfixApp.class);
    private final ExecutorService messageProcessingPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    private final MessageCrackerImpl cracker;


    public QuickfixApp(MessageCrackerImpl cracker) {
        this.cracker = cracker;
    }

    @Override
    public void onCreate(SessionID sessionId) {
        log.info("Session created: {}", sessionId);
    }

    @Override
    public void onLogon(SessionID sessionId) {
        log.info("Logon: {}", sessionId);
    }

    @Override
    public void onLogout(SessionID sessionId) {
        log.info("Logout: {}", sessionId);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionId) {
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
    }

    @Override
    public void toApp(Message message, SessionID sessionId) throws DoNotSend {
    }


    @Override
    public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        final Message copy = (Message) message.clone();
        messageProcessingPool.submit(() -> {
            try {
                cracker.crack(copy, sessionId);
            } catch (Exception e) {
                log.error("Error processing message", e);
            }
        });
    }
}
