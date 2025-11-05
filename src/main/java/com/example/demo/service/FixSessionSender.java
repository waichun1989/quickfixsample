package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.MsgType;
import quickfix.Message;


@Component
public class FixSessionSender {
    private final Logger log = LoggerFactory.getLogger(FixSessionSender.class);
    private final SessionID sessionID = new SessionID("FIX.4.4","MY_FIRM","COUNTERPARTY");


    public boolean send(Message message) {
        try {
            Session.sendToTarget(message, sessionID);
            return true;
        } catch (Exception e) {
            log.error("Failed to send FIX message", e);
            return false;
        }
    }
}