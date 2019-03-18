package com.example.lukas.shoppinglist_ljaeger15;

public class Article {
    int id;
    String text;
    String quantity;
    String store;
    String shop;

    public Article(int id, String text, String quantity, String store, String shop) {
        this.id = id;
        this.text = text;
        this.quantity = quantity;
        this.store = store;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    @Override
    public String toString() {

        return id + " " + text + " " + quantity;

    }
}
