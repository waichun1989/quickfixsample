package com.example.demo.server;

import com.example.demo.app.MessageCrackerImpl;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.NewOrderSingle;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class SimpleInitiator implements Application {

    private final ExecutorService messageProcessingPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    private final MessageCrackerImpl cracker;

    public SimpleInitiator(MessageCrackerImpl cracker) {
        this.cracker = cracker;
    }

    @PostConstruct
    public void init() throws Exception {
        initiator();
    }

    public void initiator() throws Exception {
        SessionSettings settings = new SessionSettings("quickfixj-initiator.cfg");
        MessageStoreFactory storeFactory = new MemoryStoreFactory();
        LogFactory logFactory = new ScreenLogFactory(true, true, true);
        MessageFactory messageFactory = new DefaultMessageFactory();
        Initiator initiator = new SocketInitiator(this, storeFactory, settings, logFactory, messageFactory);
        initiator.start();
        System.out.println("Initiator started. Press Ctrl+C to stop.");
    }

    @Override
    public void onCreate(SessionID sessionId) {
    }

    @Override
    public void onLogon(SessionID sessionId) {
        System.out.println("Logged on: " + sessionId);
        try {
            sendTestOrder(sessionId);
        } catch (SessionNotFound e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onLogout(SessionID sessionId) {
        System.out.println("Logged out: " + sessionId);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionId) {
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId) {
    }

    @Override
    public void toApp(Message message, SessionID sessionId) throws DoNotSend {
    }

    @Override
    public void fromApp(Message message, SessionID sessionId) {
        System.out.println("Received: " + message);

        final Message copy = (Message) message.clone();
        messageProcessingPool.submit(() -> {
            try {
                cracker.crack(copy, sessionId);
            } catch (Exception e) {
                System.out.println("Error processing message");
                e.printStackTrace();
            }
        });
    }

    // Convenience: send a test NewOrderSingle after logon (you can add this in onLogon)
    private void sendTestOrder(SessionID sessionID) throws SessionNotFound {
        System.out.println("Sending test order");
        NewOrderSingle nos = new NewOrderSingle(
                new ClOrdID("test-" + Instant.now().toEpochMilli()),
                new Side(Side.BUY),
                new TransactTime(),
                new OrdType(OrdType.LIMIT)
        );
        nos.set(new Symbol("AAPL"));
        nos.set(new Price(150.00));
        nos.set(new quickfix.field.OrderQty(10));
        Session.sendToTarget(nos, sessionID);
        System.out.println("Sent test order");
    }
}
