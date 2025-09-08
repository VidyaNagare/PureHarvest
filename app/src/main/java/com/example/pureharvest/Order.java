package com.example.pureharvest;

public class Order {
    private String orderId;
    private String productName;
    private int quantity;
    private double price;
    private String status; // e.g., "Pending", "Shipped", "Delivered"
    private String buyerId;
    private String farmerId; // Added farmerId field

    private String customerName; // Customer's name
    private String customerAddress; // Customer's address

    // Empty constructor required for Firebase
    public Order() {
    }

    // Constructor with customer details
    public Order(String orderId, String productName, int quantity, double price, String status,
                 String buyerId, String farmerId, String customerName, String customerAddress) {
        this.orderId = orderId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
        this.buyerId = buyerId;
        this.farmerId = farmerId; // Set the farmerId field
        this.customerName = customerName; // Set the customer name
        this.customerAddress = customerAddress; // Set the customer address
    }

    // Getters and Setters for all fields
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }
}
