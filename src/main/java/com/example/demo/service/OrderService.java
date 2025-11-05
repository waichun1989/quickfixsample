package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import quickfix.field.*;
import quickfix.fix44.NewOrderSingle;

import java.time.Instant;


@Service
public class OrderService {
    private final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final FixSessionSender fixSessionSender;


    public OrderService(FixSessionSender fixSessionSender) {
        this.fixSessionSender = fixSessionSender;
    }


    public void sendOrder(OrderRequest req) {
        try {
            NewOrderSingle nos = new NewOrderSingle(
                    new ClOrdID(req.clOrdId != null ? req.clOrdId : String.valueOf(Instant.now().toEpochMilli())),
                    new Side(req.side),
                    new TransactTime(),
                    new OrdType(OrdType.LIMIT)
            );
            nos.set(new Symbol(req.symbol));
            nos.set(new Price(req.price));
            nos.set(new quickfix.field.OrderQty(req.quantity));
            fixSessionSender.send(nos);
            log.info("Sent NewOrderSingle {}", req.clOrdId);
        } catch (Exception e) {
            log.error("Failed to build/send order", e);
        }
    }
}