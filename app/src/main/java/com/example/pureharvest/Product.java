package com.example.pureharvest;

import java.io.Serializable;

public class Product implements Serializable {
    private String id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;   // New field for Image URL
    private int quantity;      // New field for Quantity
    private String category;   // New field for Category
    private String farmerId;

    // Constructor with all fields
    public Product(String id, String name, String description, double price, String imageUrl, int quantity, String category, String farmerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.category = category;
        this.farmerId = farmerId;
    }

    // Default constructor required for Firebase (for data deserialization)
    public Product() {
        // Empty constructor required for Firebase
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCategory() {
        return category;
    }

    public String getFarmerId() {
        return farmerId;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
    }
}
