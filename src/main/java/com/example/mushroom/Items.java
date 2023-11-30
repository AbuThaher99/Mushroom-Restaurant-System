package com.example.mushroom;

public class Items {
    private String name;
    private double price;
    private int quantity;
    private double total;
    private String note;


    public Items(String name, double price) {
        this.name = name;
        this.price = price;
    }
    public Items(String name, double price, int quantity, String note) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;

        this.note = note;
    }

    public String getName() {
        return this.name;
    }

    public double getPrice() {
        return this.price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotal() {
        return this.total = price * quantity;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote (String note) {
        this.note = note;
    }




}
