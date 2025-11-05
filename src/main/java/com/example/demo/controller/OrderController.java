package com.example.demo.controller;

import com.example.demo.service.OrderRequest;
import com.example.demo.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;


    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest req) {
        orderService.sendOrder(req);
        return ResponseEntity.accepted().body("Order sent");
    }
}