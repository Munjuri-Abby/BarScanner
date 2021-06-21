package com.example.barscanner;

public class Products {

    private String ProductName, BarCode, ProductPrice, Image, Category,  PID, Date, Time;

    public Products()
    {
    }

    public Products(String productName, String barCode, String productPrice, String image, String category, String PID, String date, String time) {
        this.ProductName = productName;
        this.BarCode = barCode;
        this.ProductPrice = productPrice;
        this.Image = image;
        this.Category = category;
        this.PID = PID;
        this.Date = date;
        this.Time = time;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getBarCode() {
        return BarCode;
    }

    public void setBarCode(String barCode) {
        BarCode = barCode;
    }

    public String getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(String productPrice) {
        ProductPrice = productPrice;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}