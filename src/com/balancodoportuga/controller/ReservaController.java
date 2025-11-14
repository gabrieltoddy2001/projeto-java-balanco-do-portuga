package com.balancodoportuga.controller;

import com.balancodoportuga.dao.ReservaDAO;
import com.balancodoportuga.dao.VeiculoDAO;
import com.balancodoportuga.model.Cliente;
import com.balancodoportuga.model.Reserva;
import com.balancodoportuga.model.Veiculo;
import com.balancodoportuga.ui.forms.ReservaForm;
import com.balancodoportuga.service.ReservaService;

import javax.swing.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;


public class ReservaController {

    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();

    //Carrega os veículos disponíveis no combo do formulário.
   
    public void carregarVeiculos(ReservaForm form) {
        try {
            List<Veiculo> veiculos = veiculoDAO.getAll();
            JComboBox<Veiculo> combo = form.getComboVeiculo();
            combo.removeAllItems();
            for (Veiculo v : veiculos) {
                combo.addItem(v);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(form, "Erro ao carregar veículos: " + e.getMessage());
        }
    }
public List<Reserva> listarReservasPorCliente(int clienteId) {
    try {
        ReservaService service = new ReservaService();
        return service.listarPorCliente(clienteId);
    } catch (Exception e) {
        e.printStackTrace();
        return List.of();
    }
}

    //Salva a reserva com todas as validações.
    public void salvarReserva(ReservaForm form) {
        try {
            Cliente cliente = form.getClienteLogado();
            if (cliente == null) {
                JOptionPane.showMessageDialog(form, "Cliente não identificado.");
                return;
            }

            Veiculo veiculo = (Veiculo) form.getComboVeiculo().getSelectedItem();
            if (veiculo == null) {
                JOptionPane.showMessageDialog(form, "Selecione um veículo.");
                return;
            }

            String dataInicioStr = form.getCampoDataInicio().getText().trim();
            String dataFimStr = form.getCampoDataFim().getText().trim();

            if (dataInicioStr.isEmpty() || dataFimStr.isEmpty()) {
                JOptionPane.showMessageDialog(form, "Preencha as datas de início e fim.");
                return;
            }

            LocalDate dataInicio, dataFim;
            try {
                dataInicio = LocalDate.parse(dataInicioStr);
                dataFim = LocalDate.parse(dataFimStr);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(form, "Formato de data inválido! Use o formato YYYY-MM-DD.");
                return;
            }

            if (dataFim.isBefore(dataInicio)) {
    JOptionPane.showMessageDialog(form, "A data de fim não pode ser anterior à data de início.");
    return;
}

//Bloqueia reserva antes da data atual
if (dataInicio.isBefore(LocalDate.now())) {
    JOptionPane.showMessageDialog(form,
            "A data de início da reserva não pode ser anterior à data atual.",
            "Data inválida",
            JOptionPane.WARNING_MESSAGE);
    return;
}

long dias = ChronoUnit.DAYS.between(dataInicio, dataFim);
if (dias <= 0) {
    JOptionPane.showMessageDialog(form, "A reserva deve ter pelo menos 1 dia de duração.");
    return;
}


            // Verifica se o veículo está disponível
            if (reservaDAO.verificarConflitoReserva(veiculo.getId(), dataInicio, dataFim)) {
                JOptionPane.showMessageDialog(form,
                        "🚫 Este veículo já está reservado nesse período.",
                        "Conflito de datas",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            double total = dias * veiculo.getDiaria();

            Reserva reserva = new Reserva(0, cliente, veiculo, dataInicio, dataFim, total, "Solicitada", "Pendente");
            reservaDAO.insert(reserva);

            JOptionPane.showMessageDialog(form,
                    "✅ Reserva cadastrada com sucesso!\n" +
                    "Veículo: " + veiculo.getModelo() + "\n" +
                    "Período: " + dataInicio + " até " + dataFim + "\n" +
                    "Total: R$ " + String.format("%.2f", total));

            form.dispose();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(form, "Erro ao salvar reserva: " + e.getMessage());
        }
    }
}
