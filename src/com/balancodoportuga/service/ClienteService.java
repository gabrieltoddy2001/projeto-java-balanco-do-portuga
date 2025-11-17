package com.balancodoportuga.service;

import com.balancodoportuga.dao.ClienteDAO;
import com.balancodoportuga.dao.ReservaDAO;
import com.balancodoportuga.dao.PagamentoDAO;
import com.balancodoportuga.dao.VeiculoDAO;
import com.balancodoportuga.model.Reserva;
import com.balancodoportuga.model.Pagamento;

import java.sql.SQLException;
import java.util.List;

public class ClienteService {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final PagamentoDAO pagamentoDAO = new PagamentoDAO();
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();

    // Retorna a reserva ativa do cliente (Solicitada ou Em andamento)
    public Reserva buscarReservaAtiva(int clienteId) throws SQLException {
        List<Reserva> reservas =
                reservaDAO.getByClientId(clienteId, clienteDAO.getAll(), veiculoDAO.getAll());

        for (Reserva r : reservas) {
            if (r.getStatus().equalsIgnoreCase("Em andamento") ||
                r.getStatus().equalsIgnoreCase("Solicitada")) {
                return r;
            }
        }
        return null;
    }

    // Lista todas as reservas do cliente
    public List<Reserva> listarReservasCliente(int clienteId) throws SQLException {
        return reservaDAO.getByClientId(clienteId, clienteDAO.getAll(), veiculoDAO.getAll());
    }

    // Cancela uma reserva existente
    public void cancelarReserva(int reservaId) throws SQLException {
        reservaDAO.cancelarReserva(reservaId);
    }

    // Lista todos os pagamentos do cliente
    public List<Pagamento> listarPagamentosCliente(int clienteId) throws SQLException {
        return pagamentoDAO.getByCliente(clienteId, reservaDAO);
    }

    // Registra um pagamento e atualiza automaticamente o status da reserva
    public void registrarPagamento(int reservaId, double valor, String metodo) throws SQLException {

        Reserva reserva = reservaDAO.getById(reservaId);

        pagamentoDAO.registrarPagamento(reserva, valor, metodo);

        double pago = pagamentoDAO.getTotalPago(reserva.getId());
        double total = reserva.getTotal();

        if (pago >= total) {
            reservaDAO.atualizarStatusPagamento(reserva.getId(), "Pago");
        } else {
            reservaDAO.atualizarStatusPagamento(reserva.getId(), "Parcial");
        }
    }
}