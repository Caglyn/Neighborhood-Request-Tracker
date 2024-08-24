package com.example.myapplication;

public class Muhtar {
    String name;
    String ilce;
    String mahalle;
    int muhtarMi;

    Muhtar(String name, String ilce, String mahalle, int muhtarMi) {
        this.name = name;
        this.ilce = ilce;
        this.mahalle = mahalle;
        this.muhtarMi = muhtarMi;
    }

    @Override
    public String toString() {
        return name + " (" + ilce + ", " + mahalle + ")";
    }
}