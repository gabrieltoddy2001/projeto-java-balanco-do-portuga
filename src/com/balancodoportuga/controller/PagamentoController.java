package com.balancodoportuga.controller;

import com.balancodoportuga.model.Reserva;
import com.balancodoportuga.model.Pagamento;
import com.balancodoportuga.service.PagamentoService;
import com.balancodoportuga.service.ReservaService;
import com.balancodoportuga.service.PagamentoService.PagamentoStatus;
import com.balancodoportuga.dao.ReservaDAO;
import com.balancodoportuga.dao.PagamentoDAO;


import javax.swing.*;
import java.util.ArrayList;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.awt.GridLayout;
import java.awt.Component;


 // Controlador responsável por intermediar as operações de pagamento entre a interface (UI) e as camadas de serviço.
 
public class PagamentoController {

    private final PagamentoService pagamentoService = new PagamentoService();
    private final ReservaService reservaService = new ReservaService();

    
    // CONSULTAS
    // Retorna todos os pagamentos de uma reserva específica.
    public List<Pagamento> listarPagamentosPorReserva(int reservaId) throws SQLException {
        return pagamentoService.listarPorReserva(reservaId);
    }

    //Retorna todos os pagamentos de um cliente.
    public List<Pagamento> listarPagamentosPorCliente(int clienteId) throws SQLException {
        return pagamentoService.listarPorCliente(clienteId);
    }

    //Retorna o status do pagamento (progresso, cor e texto para UI).
    public PagamentoStatus getStatusPagamento(Reserva reserva) throws SQLException {
        return pagamentoService.calcularStatusPagamento(reserva);
    }

    // REGISTROS E OPERAÇÕES
    // Registra um novo pagamento e atualiza o status da reserva.
    public void registrarPagamento(Component parent, Reserva reserva, double valor, String metodo) {
    try {
        if (valor <= 0) {
            JOptionPane.showMessageDialog(parent, "O valor deve ser maior que zero.");
            return;
        }

        pagamentoService.registrarPagamento(reserva, valor, metodo);
        JOptionPane.showMessageDialog(parent,
                String.format("Pagamento registrado com sucesso!\n\nValor: R$ %.2f\nMétodo: %s",
                        valor, metodo),
                "Pagamento Efetuado",
                JOptionPane.INFORMATION_MESSAGE);
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(parent, "Erro ao registrar pagamento: " + ex.getMessage());
    }
}
    public List<Pagamento> listarPagamentosFuncionario(List<Reserva> reservas) {
    try {
        List<Pagamento> todosPagamentos = new ArrayList<>();
        for (Reserva r : reservas) {
            List<Pagamento> lista = listarPagamentosPorReserva(r.getId());
            if (lista != null) {
                todosPagamentos.addAll(lista);
            }
        }
        return todosPagamentos;
    } catch (Exception e) {
        throw new RuntimeException("Erro ao listar pagamentos do funcionário: " + e.getMessage(), e);
    }
}

    //Aplica multa automática com base nos dias de atraso.
    public void aplicarMultaManual(Reserva reserva, String descricao, double valorMulta) {
    try {
        
       double novoTotal = reserva.getTotal() + valorMulta;

        //Atualiza o valor total da reserva
        ReservaDAO reservaDAO = new ReservaDAO();
        reservaDAO.atualizarValorTotal(reserva.getId(), novoTotal);

        //Registrar Pagamento de Multa
        PagamentoDAO pagDAO = new PagamentoDAO();
        pagDAO.inserirPagamentoMulta(reserva.getId(), valorMulta, descricao);

        //Pagamento pendente até gerar algum pagamento
        reservaDAO.atualizarStatusPagamento(reserva.getId(), "Pendente");

        JOptionPane.showMessageDialog(null,
                "✅ Multa aplicada com sucesso!\n" +
                "Valor: R$ " + String.format("%.2f", valorMulta) + "\n" +
                "Descrição: " + descricao,
                "Multa Registrada",
                JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(null,
                "Erro ao aplicar multa: " + ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
    }
}

    //RELATÓRIOS / RESUMOS
    //Gera um painel de resumo financeiro
        public JPanel gerarResumoFinanceiro(List<Reserva> reservas) {
        try {
            double totalPago = 0, totalPendente = 0;
            LocalDate hoje = LocalDate.now();

            for (Reserva r : reservas) {
                PagamentoStatus status = pagamentoService.calcularStatusPagamento(r);
                totalPago += (r.getTotal() - status.getRestante());
                if (status.getRestante() > 0) totalPendente += status.getRestante();
            }

            JPanel painel = new JPanel(new GridLayout(3, 1, 5, 5));
            painel.setBorder(BorderFactory.createTitledBorder("📊 Resumo Financeiro"));
            painel.add(new JLabel(String.format("💰 Total Pago: R$ %.2f", totalPago)));
            painel.add(new JLabel(String.format("💸 Total Pendente: R$ %.2f", totalPendente)));
            painel.add(new JLabel(String.format("📅 Atualizado em: %s", hoje)));
            return painel;

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao gerar resumo: " + ex.getMessage());
            return new JPanel();
        }
    }
}

