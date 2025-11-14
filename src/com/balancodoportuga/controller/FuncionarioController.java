package com.balancodoportuga.controller;

import com.balancodoportuga.dao.*;
import com.balancodoportuga.model.Cliente;
import com.balancodoportuga.model.Reserva;
import com.balancodoportuga.model.Veiculo;
import com.balancodoportuga.service.ReservaService;
import com.balancodoportuga.service.PagamentoService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

//Controlador principal da área do FUNCIONÁRIO, eentraliza todas as operações administrativas (clientes, veículos, reservas e pagamentos).
public class FuncionarioController {

    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();
    private final PagamentoDAO pagamentoDAO = new PagamentoDAO();

    private final ReservaService reservaService = new ReservaService();
    private final PagamentoService pagamentoService = new PagamentoService();

    private List<Cliente> listaClientes;
    private List<Veiculo> listaVeiculos;

    // CLIENTES
    public List<Cliente> listarClientes() throws SQLException {
    listaClientes = clienteDAO.getAll();
    return listaClientes;
    }

    public List<Cliente> filtrarClientes(String filtro) throws SQLException {
        if (listaClientes == null) listaClientes = clienteDAO.getAll();
        String termo = filtro.trim().toLowerCase();

        if (termo.isEmpty()) return listaClientes;

        return listaClientes.stream()
                .filter(c -> c.getNome().toLowerCase().contains(termo)
                        || c.getCpf().contains(termo)
                        || c.getEmail().toLowerCase().contains(termo)
                        || c.getCidade().toLowerCase().contains(termo)
                        || c.getBairro().toLowerCase().contains(termo)
                        || c.getEstado().toLowerCase().contains(termo))
                .toList();
    }

    public void salvarCliente(Cliente c) throws SQLException {
        clienteDAO.insert(c);
    }

    public void atualizarCliente(Cliente c) throws SQLException {
        clienteDAO.update(c);
    }

    public void excluirCliente(int id) throws SQLException {
        clienteDAO.delete(id);
    }

// VEÍCULOS
// Retorna todos os veículos cadastrados
public List<Veiculo> listarVeiculos() throws SQLException {
    listaVeiculos = veiculoDAO.getAll();
    return listaVeiculos;
}

// Adiciona um novo veículo após validação
public void adicionarVeiculo(String placa, String modelo, String marca, String cor, String ano, String diaria) throws Exception {
    int anoInt = Integer.parseInt(ano);
    double valorDiaria = Double.parseDouble(diaria);
    Veiculo v = new Veiculo(0, modelo, marca, placa, anoInt, valorDiaria); // ordem corrigida
    veiculoDAO.insert(v);
}

// Atualiza um veículo existente
public void editarVeiculo(int id, String placa, String modelo, String marca, String cor, String ano, String diaria) throws Exception {
    int anoInt = Integer.parseInt(ano);
    double valorDiaria = Double.parseDouble(diaria);
    Veiculo atualizado = new Veiculo(id, modelo, marca, placa, anoInt, valorDiaria); // ordem corrigida
    veiculoDAO.update(atualizado);
}


// Remove veículo pelo ID
public void excluirVeiculo(int id) throws SQLException {
    // Verifica se o veículo possui reservas ativas ou pendentes.
    List<Reserva> reservas = reservaDAO.getAll(clienteDAO.getAll(), veiculoDAO.getAll());

    boolean possuiReservaAtiva = reservas.stream()
            .anyMatch(r -> r.getVehicle() != null
                    && r.getVehicle().getId() == id
                    && (
                        r.getStatus().equalsIgnoreCase("Solicitada")
                        || r.getStatus().equalsIgnoreCase("Em andamento")
                    ));

    if (possuiReservaAtiva) {
        JOptionPane.showMessageDialog(null,
                "Este veículo não pode ser excluído pois possui uma reserva ativa ou pendente.",
                "Ação bloqueada", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Nenhuma reserva ativa: pode excluir
    veiculoDAO.delete(id);
}

    //RESERVAS
    public List<Reserva> listarReservas() throws SQLException {
        reservaService.atualizarStatusAutomaticamente();
        return reservaDAO.getAll(clienteDAO.getAll(), veiculoDAO.getAll());
    }

    public List<Reserva> filtrarReservas(List<Reserva> listaOriginal,
                                         String termo, String status,
                                         LocalDate inicio, LocalDate fim) {
        return reservaService.filtrarReservas(listaOriginal, termo, status, inicio, fim);
    }

        //CANCELAMENTO / CONCLUSÃO DE RESERVAS
        public void cancelarReserva(JFrame frame, Reserva reserva) {
        if (!reserva.getStatus().equalsIgnoreCase("Em andamento")) {
            JOptionPane.showMessageDialog(frame, "Somente reservas em andamento podem ser canceladas.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(frame, "Deseja cancelar esta reserva?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                reservaDAO.cancelarReserva(reserva.getId());
                JOptionPane.showMessageDialog(frame, "Reserva cancelada com sucesso!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Erro ao cancelar reserva: " + ex.getMessage());
            }
        }
    }

    public void concluirReserva(JFrame frame, Reserva reserva) {
        if (!reserva.getStatus().equalsIgnoreCase("Em andamento")) {
            JOptionPane.showMessageDialog(frame, "Somente reservas em andamento podem ser concluídas.");
            return;
        }

        LocalDate hoje = LocalDate.now();
        long diasAtraso = hoje.isAfter(reserva.getDataFim()) ? ChronoUnit.DAYS.between(reserva.getDataFim(), hoje) : 0;
        double multa = diasAtraso > 0 ? reserva.getTotal() * 0.05 * diasAtraso : 0;
        double novoTotal = reserva.getTotal() + multa;

        String msg = String.format("""
                Dias de atraso: %d
                Multa: R$ %.2f
                Total final: R$ %.2f

                Confirmar conclusão da reserva?
                """, diasAtraso, multa, novoTotal);

        int confirm = JOptionPane.showConfirmDialog(frame, msg, "Confirmar Conclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                reservaDAO.concluirReserva(reserva.getId(), novoTotal);
                JOptionPane.showMessageDialog(frame, "Reserva concluída com sucesso!\nTotal final: R$ " + String.format("%.2f", novoTotal));
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Erro ao concluir reserva: " + ex.getMessage());
            }
        }
    }

        // RELATÓRIOS E PAGAMENTOS
        public JPanel gerarResumoFinanceiro(List<Reserva> reservas) throws SQLException {
        double totalPago = 0, totalPendente = 0, totalPrevisto = 0;
        LocalDate hoje = LocalDate.now();

        for (Reserva r : reservas) {
            double pago = pagamentoDAO.getTotalPago(r.getId());
            totalPago += pago;
            if (r.getDataFim().isAfter(hoje)) totalPrevisto += r.getTotal();
            if (pago < r.getTotal()) totalPendente += (r.getTotal() - pago);
        }

        JPanel painel = new JPanel(new GridLayout(3, 1, 5, 5));
        painel.setBorder(BorderFactory.createTitledBorder("📊 Resumo Financeiro"));
        painel.add(new JLabel(String.format("💰 Total Pago: R$ %.2f", totalPago)));
        painel.add(new JLabel(String.format("💸 Total Pendente: R$ %.2f", totalPendente)));
        painel.add(new JLabel(String.format("🕒 A Vencer: R$ %.2f", totalPrevisto)));

        return painel;
    }

    public void gerarRelatorioReservas(JFrame frame, List<Reserva> reservas) {
        if (reservas.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Nenhuma reserva encontrada.");
            return;
        }

        long total = reservas.size();
        long emAndamento = reservas.stream().filter(r -> r.getStatus().equalsIgnoreCase("Em andamento")).count();
        long solicitadas = reservas.stream().filter(r -> r.getStatus().equalsIgnoreCase("Solicitada")).count();
        long concluidas = reservas.stream().filter(r -> r.getStatus().equalsIgnoreCase("Concluída")).count();
        long canceladas = reservas.stream().filter(r -> r.getStatus().equalsIgnoreCase("Cancelada")).count();

        double valorTotal = reservas.stream()
                .filter(r -> r.getStatus().equalsIgnoreCase("Concluída"))
                .mapToDouble(Reserva::getTotal)
                .sum();
        double mediaValor = concluidas > 0 ? valorTotal / concluidas : 0.0;

        double duracaoMedia = reservas.stream()
                .filter(r -> r.getStatus().equalsIgnoreCase("Concluída"))
                .mapToLong(r -> ChronoUnit.DAYS.between(r.getDataInicio(), r.getDataFim()))
                .average().orElse(0.0);

        String relatorio = String.format("""
                📊 RELATÓRIO DETALHADO DE RESERVAS

                Total de Reservas: %d
                ├─ Em Andamento: %d
                ├─ Solicitadas: %d
                ├─ Concluídas: %d
                └─ Canceladas: %d

                💰 Valor Total Arrecadado: R$ %.2f
                💵 Média por Reserva Concluída: R$ %.2f
                ⏳ Duração Média das Reservas: %.1f dias
                """, total, emAndamento, solicitadas, concluidas, canceladas, valorTotal, mediaValor, duracaoMedia);

        JOptionPane.showMessageDialog(frame, relatorio, "Relatório de Reservas", JOptionPane.INFORMATION_MESSAGE);
    }

    // PAGAMENTOS
    public void registrarPagamento(Component parent, Reserva reserva) {
        try {
            double total = reserva.getTotal();
            double pago = pagamentoDAO.getTotalPago(reserva.getId());
            double restante = total - pago;

            String valorStr = JOptionPane.showInputDialog(parent,
                    "Valor restante: R$ " + String.format("%.2f", restante) +
                            "\nDigite o valor a registrar:",
                    String.format("%.2f", restante));

            if (valorStr == null || valorStr.isBlank()) return;
            double valor = Double.parseDouble(valorStr.replace(",", "."));
            if (valor <= 0) {
                JOptionPane.showMessageDialog(parent, "Valor inválido.");
                return;
            }

            String metodo = (String) JOptionPane.showInputDialog(
                    parent, "Método de pagamento:", "Selecionar método",
                    JOptionPane.QUESTION_MESSAGE, null,
                    new String[]{"Pix", "Cartão de Crédito", "Cartão de Débito", "Dinheiro"}, "Pix");

            if (metodo == null) return;

            pagamentoService.registrarPagamento(reserva, valor, metodo);
            JOptionPane.showMessageDialog(parent, "Pagamento registrado com sucesso!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, "Erro ao registrar pagamento: " + ex.getMessage());
        }
    }

    public void aplicarMulta(Component parent, Reserva reserva) {
        try {
            long diasAtraso = ChronoUnit.DAYS.between(reserva.getDataFim(), LocalDate.now());
            if (diasAtraso <= 0) {
                JOptionPane.showMessageDialog(parent, "Nenhum atraso nesta reserva.");
                return;
            }

            double multa = diasAtraso * (reserva.getVehicle().getDiaria() * 0.3);
            double novoTotal = reserva.getTotal() + multa;
            reservaDAO.concluirReserva(reserva.getId(), novoTotal);

            JOptionPane.showMessageDialog(parent, String.format(
                    "Multa aplicada: R$ %.2f (%d dias de atraso)", multa, diasAtraso
            ));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(parent, "Erro ao aplicar multa: " + ex.getMessage());
        }
    }

       //SUPORTE A RELATÓRIOS
       public List<Reserva> filtrarReservas(LocalDate inicio, LocalDate fim, String status) throws SQLException {
        List<Reserva> todas = listarReservas();
        return reservaService.filtrarReservas(todas, "", status, inicio, fim);
    }

    public double calcularTotalReservas(List<Reserva> reservas) {
        return reservas.stream()
                .filter(r -> !"Cancelada".equalsIgnoreCase(r.getStatus()))
                .mapToDouble(Reserva::getTotal)
                .sum();
    }
}






