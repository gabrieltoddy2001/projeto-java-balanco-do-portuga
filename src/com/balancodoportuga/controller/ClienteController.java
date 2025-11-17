package com.balancodoportuga.controller;

import com.balancodoportuga.model.Reserva;
import com.balancodoportuga.model.Pagamento;
import com.balancodoportuga.service.ReservaService;
import com.balancodoportuga.service.PagamentoService;
import com.balancodoportuga.service.PagamentoService.PagamentoStatus;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

// Controlador principal da área do CLIENTE, faz a ponte entre UI e Service.
public class ClienteController {

    private final ReservaService reservaService = new ReservaService();
    private final PagamentoService pagamentoService = new PagamentoService();

    // RESERVAS DO CLIENTE
    // Lista todas as reservas vinculadas ao cliente
    public List<Reserva> listarReservasCliente(int clienteId) throws SQLException {
        reservaService.atualizarStatusAutomaticamente();
        return reservaService.listarPorCliente(clienteId);
    }

    // Busca a reserva ativa (Solicitada ou Em andamento)
    public Reserva buscarReservaAtiva(int clienteId) throws SQLException {
        return reservaService.buscarAtiva(clienteId);
    }

    // Cancela uma reserva específica
    public void cancelarReserva(int reservaId) throws SQLException {
        reservaService.cancelar(reservaId);
    }

    // Aplica filtros personalizados nos campos
    public List<Reserva> filtrarReservas(
            List<Reserva> listaOriginal,
            String termo,
            String status,
            LocalDate inicio,
            LocalDate fim
    ) {
        return reservaService.filtrarReservas(listaOriginal, termo, status, inicio, fim);
    }

    // Atualiza automaticamente o status das reservas vencidas/em andamento
    public void atualizarStatusAutomaticamente() throws SQLException {
        reservaService.atualizarStatusAutomaticamente();
    }

    // PAGAMENTOS
    // Lista todos os pagamentos de um cliente
    public List<Pagamento> listarPagamentosCliente(int clienteId) throws SQLException {
        return pagamentoService.listarPorCliente(clienteId);
    }

    // Lista todos os pagamentos associados a uma reserva
    public List<Pagamento> listarPagamentosPorReserva(int reservaId) throws SQLException {
        return pagamentoService.listarPorReserva(reservaId);
    }

    // Registra um novo pagamento e atualiza o status da reserva
    public void registrarPagamento(int clienteId, int reservaId, double valor, String metodo) throws SQLException {
        List<Reserva> reservas = listarReservasCliente(clienteId);

        Reserva reserva = reservas.stream()
                .filter(r -> r.getId() == reservaId)
                .findFirst()
                .orElseThrow(() -> new SQLException("Reserva não encontrada para registrar pagamento."));

        pagamentoService.registrarPagamento(reserva, valor, metodo);
    }

    // Retorna o status do pagamento
    public PagamentoStatus getStatusPagamento(Reserva reserva) throws SQLException {
        return pagamentoService.calcularStatusPagamento(reserva);
    }
}

