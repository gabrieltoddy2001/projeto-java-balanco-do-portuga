    package com.balancodoportuga.model;

    import java.time.LocalDate;

    public class Reserva {
        private int id;
        private Cliente client;
        private Veiculo vehicle;
        private LocalDate dataInicio;
        private LocalDate dataFim;
        private double total;
        private String status;
        private String statusPagamento;


        public Reserva(int id, Cliente client, Veiculo vehicle, LocalDate inicio,
               LocalDate fim, double total, String status, String statusPagamento) {
    this.id = id;
    this.client = client;
    this.vehicle = vehicle;
    this.dataInicio = inicio;
    this.dataFim = fim;
    this.total = total;
    this.status = status;
    this.statusPagamento = statusPagamento;
}

        public int getId() { return id; }
        public Cliente getClient() { return client; }
        public Veiculo getVehicle() { return vehicle; }
        public LocalDate getDataInicio() { return dataInicio; }
        public LocalDate getDataFim() { return dataFim; }
        public double getTotal() { return total; }
        public String getStatus() { return status; }

        public void setStatus(String status) { this.status = status; }
       
        public String getStatusPagamento() {
    return statusPagamento;
}

public void setStatusPagamento(String statusPagamento) {
    this.statusPagamento = statusPagamento;
}

        @Override
        
public String toString() {
    return String.format("Reserva #%d - %s (%s)", 
        getId(), 
        getVehicle().getModelo(), 
        getVehicle().getPlaca());
}

    }