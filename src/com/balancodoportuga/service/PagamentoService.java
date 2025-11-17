package com.balancodoportuga.service;

import com.balancodoportuga.dao.PagamentoDAO;
import com.balancodoportuga.dao.ReservaDAO;
import com.balancodoportuga.model.Pagamento;
import com.balancodoportuga.model.Reserva;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PagamentoService {

    private final PagamentoDAO pagamentoDAO = new PagamentoDAO();
    private final ReservaDAO reservaDAO = new ReservaDAO();

    //Retorna todos os pagamentos de um cliente
    public List<Pagamento> listarPorCliente(int clienteId) throws SQLException {
        return pagamentoDAO.getByCliente(clienteId, reservaDAO);
    }

    //Retorna todos os pagamentos de uma reserva específica
    public List<Pagamento> listarPorReserva(int reservaId) throws SQLException {
        return pagamentoDAO.getByReservaId(reservaId, reservaDAO);
    }

    // Registra um pagamento e atualiza status da reserva
    public void registrarPagamento(Reserva reserva, double valor, String metodo) throws SQLException {
        pagamentoDAO.registrarPagamento(reserva, valor, metodo);

        double pago = pagamentoDAO.getTotalPago(reserva.getId());
        double total = reserva.getTotal();

        if (pago >= total) {
            reservaDAO.atualizarStatusPagamento(reserva.getId(), "Pago");
        } else {
            reservaDAO.atualizarStatusPagamento(reserva.getId(), "Parcial");
        }
    }

    // Calcula o progresso e a cor da barra de pagamento
    public PagamentoStatus calcularStatusPagamento(Reserva reserva) throws SQLException {
        double total = reserva.getTotal();
        double pago = pagamentoDAO.getTotalPago(reserva.getId());
        double percentual = (pago / total) * 100;
        double restante = Math.max(total - pago, 0);

        Color cor;
        if (percentual >= 100) cor = new Color(46, 204, 113);       // verde
        else if (percentual >= 50) cor = new Color(241, 196, 15);  // amarelo
        else cor = new Color(231, 76, 60);                         // vermelho

        return new PagamentoStatus((int) percentual, restante, cor,
                String.format("Pago: %.0f%% | Restante: R$ %.2f", percentual, restante));
    }

    /// Classe auxiliar que representa o estado visual do pagamento
    public static class PagamentoStatus {
        private final int progresso;
        private final double restante;
        private final Color cor;
        private final String texto;

        public PagamentoStatus(int progresso, double restante, Color cor, String texto) {
            this.progresso = progresso;
            this.restante = restante;
            this.cor = cor;
            this.texto = texto;
        }

        public int getProgresso() { return progresso; }
        public double getRestante() { return restante; }
        public Color getCor() { return cor; }
        public String getTexto() { return texto; }
    }

    public double calcularTotalPagoGeral(List<Reserva> reservas) throws SQLException {
        double total = 0;
        for (Reserva r : reservas) {
            total += pagamentoDAO.getTotalPago(r.getId());
        }
        return total;
    }

    // Calcula o total pendente de pagamento em todas as reservas
    public double calcularTotalPendente(List<Reserva> reservas) throws SQLException {
        double total = 0;
        for (Reserva r : reservas) {
            double pago = pagamentoDAO.getTotalPago(r.getId());
            total += Math.max(r.getTotal() - pago, 0);
        }
        return total;
    }

    // Calcula o total previsto (reservas futuras ainda em andamento)
    public double calcularTotalPrevisto(List<Reserva> reservas) {
        double total = 0;
        java.time.LocalDate hoje = java.time.LocalDate.now();
        for (Reserva r : reservas) {
            if (r.getDataFim().isAfter(hoje)) {
                total += r.getTotal();
            }
        }
        return total;
    }
}