package com.example.demo.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import quickfix.FieldNotFound;
import quickfix.MessageCracker;
import quickfix.SessionID;
import quickfix.field.ClOrdID;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.NewOrderSingle;

@Component
public class MessageCrackerImpl extends MessageCracker {
    private final Logger log = LoggerFactory.getLogger(MessageCrackerImpl.class);
    private final EventProducer eventProducer;

    public MessageCrackerImpl(EventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }

    public void onMessage(NewOrderSingle order, SessionID sessionId) {
        try {
            String clOrdId = order.getString(ClOrdID.FIELD);
            log.debug("Received NewOrderSingle: {} from {}", clOrdId, sessionId);
            eventProducer.publishOrder(order);
        } catch (FieldNotFound e) {
            log.error("Missing field in NewOrderSingle", e);
        }
    }

    public void onMessage(ExecutionReport exec, SessionID sessionId) {
        try {
            String execId = exec.getExecID().getValue();
            log.debug("ExecutionReport: {}", execId);
            eventProducer.publishExecution(exec);
        } catch (Exception e) {
            log.error("Error extracting exec report", e);
        }
    }
}