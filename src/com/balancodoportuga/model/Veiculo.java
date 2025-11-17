package com.balancodoportuga.model;

public class Veiculo {
    private int id;
    private String modelo;
    private String marca;
    private String placa;
    private int ano;
    private double diaria;

    public Veiculo(int id, String modelo, String marca, String placa, int ano, double diaria) {
        this.id = id;
        this.modelo = modelo;
        this.marca = marca;
        this.placa = placa;
        this.ano = ano;
        this.diaria = diaria;
    }
    
    public int getId() {
        return id;
    }

    public String getModelo() {
        return modelo;
    }

    public String getMarca() {
        return marca;
    }

    public String getPlaca() {
        return placa;
    }

    public int getAno() {
        return ano;
    }

    public double getDiaria() {
        return diaria;
    }

  
    public void setId(int id) {
        this.id = id;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public void setDiaria(double diaria) {
        this.diaria = diaria;
    }

    @Override
    public String toString() {
        return modelo + " - " + placa;
    }
}