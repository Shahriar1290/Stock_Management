package com.example.stockmanagementsystem.models;

public class Order {
    private String id;
    private String productId;
    private String productName;
    private int quantity;
    private long timestamp;

    public Order() {}

    public Order(String id, String productId, String productName, int quantity, long timestamp) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public long getTimestamp() { return timestamp; }
}
