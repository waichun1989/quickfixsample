package com.example.demo.service;

public class OrderRequest {
    public String symbol;
    public char side; // '1' = buy, '2' = sell
    public double price;
    public long quantity;
    public String clOrdId;
}