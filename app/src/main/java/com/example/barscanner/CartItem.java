package com.example.barscanner;

public class CartItem {
    private String productName, quantity, productPrice;

    public CartItem(){

    }

    public CartItem(String productName, String quantity, String productPrice) {
        this.productName = productName;
        this.quantity = quantity;
        this.productPrice = productPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }
}
