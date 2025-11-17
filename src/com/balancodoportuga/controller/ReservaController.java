package com.balancodoportuga.controller;

import com.balancodoportuga.dao.ReservaDAO;
import com.balancodoportuga.dao.VeiculoDAO;
import com.balancodoportuga.model.Reserva;
import com.balancodoportuga.model.Veiculo;
import com.balancodoportuga.service.ReservaService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import java.awt.Component;


public class ReservaController {

    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();
    private final ReservaService reservaService = new ReservaService();

    // ============================================================
    // VEÍCULOS
    // ============================================================
    public List<Veiculo> listarVeiculos() throws SQLException {
        return veiculoDAO.getAll();
    }
    public void carregarVeiculosNoCombo(JComboBox<Veiculo> combo, Component parent) {
    try {
        combo.removeAllItems();
        List<Veiculo> veiculos = veiculoDAO.getAll();
        for (Veiculo v : veiculos) combo.addItem(v);
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(parent,
                "Erro ao carregar veículos: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
    // ============================================================
    // DISPONIBILIDADE
    // ============================================================
    public boolean verificarDisponibilidade(int veiculoId, LocalDate inicio, LocalDate fim) throws SQLException {
        return reservaDAO.verificarConflitoReserva(veiculoId, inicio, fim);
    }

    // ============================================================
    // LISTAGEM POR CLIENTE
    // ============================================================
    public List<Reserva> listarReservasPorCliente(int clienteId) throws Exception {
        return reservaService.listarPorCliente(clienteId);
    }

    // ============================================================
    // SALVAR RESERVA (MODO CLIENTE)
    // ============================================================
    public void salvarReserva(int clienteId, int veiculoId, LocalDate dataInicio, LocalDate dataFim) throws Exception {

        // === VALIDAÇÕES ===
        if (dataInicio == null || dataFim == null)
            throw new Exception("Datas não podem ser vazias.");

        if (dataFim.isBefore(dataInicio))
            throw new Exception("A data de fim não pode ser antes da data de início.");

        if (dataInicio.isBefore(LocalDate.now()))
            throw new Exception("A data inicial não pode ser anterior ao dia atual.");

        long dias = ChronoUnit.DAYS.between(dataInicio, dataFim);
        if (dias <= 0)
            throw new Exception("A reserva deve ter pelo menos 1 dia.");

        // === Veículo ===
        Veiculo veiculo = veiculoDAO.getById(veiculoId);
        if (veiculo == null)
            throw new Exception("Veículo não encontrado.");

        // === Conflito ===
        boolean conflito = reservaDAO.verificarConflitoReserva(veiculoId, dataInicio, dataFim);
        if (conflito)
            throw new Exception("O veículo selecionado está indisponível neste período.");

        // === Cálculo ===
        double total = dias * veiculo.getDiaria();

        // === Criação da reserva ===
        Reserva reserva = new Reserva(
                0,
                new com.balancodoportuga.model.Cliente(clienteId), // cliente minimal constructor
                veiculo,
                dataInicio,
                dataFim,
                total,
                "Solicitada",
                "Pendente"
        );

        reservaDAO.insert(reserva);
    }
}