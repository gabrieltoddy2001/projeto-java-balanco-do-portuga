package com.balancodoportuga.model;

import java.time.LocalDate;

public class Pagamento {
    private int id;
    private Reserva reserva;
    private double valor;
    private LocalDate dataPagamento;
    private String metodoPagamento;

    public Pagamento(int id, Reserva reserva, double valor, LocalDate dataPagamento, String metodoPagamento) {
        this.id = id;
        this.reserva = reserva;
        this.valor = valor;
        this.dataPagamento = dataPagamento;
        this.metodoPagamento = metodoPagamento;
    }

    public int getId() { return id; }
    public Reserva getReserva() { return reserva; }
    public double getValor() { return valor; }
    public LocalDate getDataPagamento() { return dataPagamento; }
    public String getMetodoPagamento() { return metodoPagamento; }

    public void setId(int id) { this.id = id; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }
    public void setValor(double valor) { this.valor = valor; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }
    public void setMetodoPagamento(String metodoPagamento) { this.metodoPagamento = metodoPagamento; }
}