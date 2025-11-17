package com.balancodoportuga.ui.forms;

import com.balancodoportuga.controller.PagamentoController;
import com.balancodoportuga.controller.FuncionarioController;
import com.balancodoportuga.controller.ReservaController;
import com.balancodoportuga.dao.ReservaDAO;
import com.balancodoportuga.model.Cliente;
import com.balancodoportuga.model.Reserva;
import com.balancodoportuga.model.Pagamento;
import com.balancodoportuga.service.PagamentoService.PagamentoStatus;
import com.balancodoportuga.ui.tablemodel.PagamentoTableModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

//não mexe aqui pelo amor de deus, deu trabalho pra setar cor por cor
public class PagamentoForm extends JDialog {
    // TEMA (Portugal)
    private static final Color BG_WINDOW = new Color(18, 18, 18);
    private static final Color BG_PANEL  = new Color(28, 28, 28);
    private static final Color FG_TEXT   = new Color(235, 235, 235);
    private static final Color FG_MUTED  = new Color(200, 200, 200);
    private static final Color ACCENT_GREEN  = new Color(0, 102, 0);
    private static final Color ACCENT_RED    = new Color(200, 16, 46);
    private static final Color ACCENT_YELLOW = new Color(255, 204, 0);

    private static final Color BORDER_GRAY   = new Color(60, 60, 60);
    private static final Color BORDER_YELLOW = new Color(255, 204, 0);
    private static final Color GRID_GRAY     = new Color(70, 70, 70);
    private static final Font  FONT_BASE     = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  FONT_BOLD     = new Font("Segoe UI", Font.BOLD, 14);

    private final PagamentoController pagamentoController = new PagamentoController();

    // =========================
    // Construtor para CLIENTE
    // =========================
    public PagamentoForm(JFrame parent, Cliente cliente) {
        super(parent, "Pagamentos - " + cliente.getNome(), true);
        setSize(900, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BG_WINDOW);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        estilizarPainel(painelPrincipal);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(painelPrincipal, BorderLayout.CENTER);

        JLabel titulo = new JLabel("Pagamentos de " + cliente.getNome(), SwingConstants.CENTER);
        titulo.setFont(FONT_BOLD.deriveFont(16f));
        titulo.setForeground(FG_TEXT);
        painelPrincipal.add(titulo, BorderLayout.NORTH);

        try {
            ReservaController reservaController = new ReservaController();
            List<Reserva> reservas = reservaController.listarReservasPorCliente(cliente.getId());

            JComboBox<Reserva> cbReservas = new JComboBox<>(reservas.toArray(new Reserva[0]));
            estilizarCombo(cbReservas);

            JTable tabelaPagamentos = new JTable(new PagamentoTableModel(List.of()));
            estilizarTabela(tabelaPagamentos);

            JScrollPane scroll = new JScrollPane(tabelaPagamentos);
            estilizarScroll(scroll);

            // Barra de progresso
            JProgressBar progressBar = new JProgressBar(0, 100);
            estilizarProgress(progressBar);

            JPanel painelCentro = new JPanel(new BorderLayout(10, 10));
            estilizarPainel(painelCentro);

            JPanel painelTopo = new JPanel(new GridLayout(3, 1, 6, 6));
            estilizarPainel(painelTopo);
            JLabel lblSel = new JLabel("Selecione uma reserva:");
            lblSel.setForeground(FG_TEXT);
            lblSel.setFont(FONT_BASE);
            painelTopo.add(lblSel);
            painelTopo.add(cbReservas);
            painelTopo.add(progressBar);

            painelCentro.add(painelTopo, BorderLayout.NORTH);
            painelCentro.add(scroll, BorderLayout.CENTER);
            painelPrincipal.add(painelCentro, BorderLayout.CENTER);

            // Botões
            JButton btnPagar     = themedButton("Efetuar Pagamento", ACCENT_GREEN);
            JButton btnHistorico = themedButton("Histórico", ACCENT_GREEN);
            JButton btnVoltar    = themedButton("Voltar", ACCENT_RED);

            JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            estilizarPainel(painelBotoes);
            painelBotoes.add(btnPagar);
            painelBotoes.add(btnHistorico);
            painelBotoes.add(btnVoltar);
            painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

            // Ações
            cbReservas.addActionListener(e -> atualizarTabelaCliente(cbReservas, tabelaPagamentos, progressBar));
            btnPagar.addActionListener(e -> realizarPagamentoCliente(cbReservas, cliente));
            btnHistorico.addActionListener(e -> mostrarHistorico(cliente));
            btnVoltar.addActionListener(e -> dispose());

            if (cbReservas.getItemCount() > 0) {
                // inicializa visual
                atualizarTabelaCliente(cbReservas, tabelaPagamentos, progressBar);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar pagamentos: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============================
    // Construtor para FUNCIONÁRIO
    // ============================
    public PagamentoForm(JFrame parent) {
        super(parent, "Gerenciar Pagamentos", true);
        setSize(900, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BG_WINDOW);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        estilizarPainel(painelPrincipal);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(painelPrincipal, BorderLayout.CENTER);

        JLabel titulo = new JLabel("Gerenciamento de Pagamentos", SwingConstants.CENTER);
        titulo.setFont(FONT_BOLD.deriveFont(16f));
        titulo.setForeground(FG_TEXT);
        painelPrincipal.add(titulo, BorderLayout.NORTH);

        try {
            FuncionarioController funcController = new FuncionarioController();
            List<Reserva> reservas = funcController.listarReservas();

            // Filtros
            JTextField campoBusca = new JTextField(20);
            estilizarTextField(campoBusca);

            JComboBox<String> cbStatusPagamento = new JComboBox<>(new String[]{
                    "Todos", "Pendente", "Parcial", "Pago"
            });
            estilizarCombo(cbStatusPagamento);

            JPanel painelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            estilizarPainel(painelFiltros);
            painelFiltros.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(BORDER_YELLOW, 1, true),
                    "Filtros",
                    0, 0,
                    FONT_BOLD,
                    FG_TEXT
            ));

            JLabel lbStatus = new JLabel("Status:");
            lbStatus.setForeground(ACCENT_YELLOW);
            lbStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
            painelFiltros.add(lbStatus);
            painelFiltros.add(cbStatusPagamento);

            painelFiltros.add(Box.createHorizontalStrut(15));

            JLabel lbBusca = new JLabel("Pesquisar:");
            lbBusca.setForeground(ACCENT_YELLOW);
            lbBusca.setFont(new Font("Segoe UI", Font.BOLD, 14));
            painelFiltros.add(lbBusca);
            painelFiltros.add(campoBusca);

            painelPrincipal.add(painelFiltros, BorderLayout.NORTH);

            String[] colunas = {
                    "Reserva", "Cliente", "Placa", "Modelo",
                    "Data Início", "Data Fim", "Valor Total", "Status Pagamento"
            };

            DefaultTableModel modeloPagamento = new DefaultTableModel(colunas, 0) {
                @Override
                public boolean isCellEditable(int r, int c) { return false; }
            };

            JTable tabela = new JTable(modeloPagamento);
            estilizarTabela(tabela);

            JScrollPane scroll = new JScrollPane(tabela);
            estilizarScroll(scroll);
            painelPrincipal.add(scroll, BorderLayout.CENTER);

            preencherTabelaPagamentos(modeloPagamento, reservas);

            JButton btnRegistrar = themedButton("Registrar Pagamento", ACCENT_GREEN);
                        JButton btnDetalhes  = themedButton("Detalhes", ACCENT_GREEN);
                        JButton btnMulta     = themedButton("Aplicar Multa", ACCENT_RED);
            JButton btnVoltar    = themedButton("Voltar", ACCENT_RED);
            
                      JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            estilizarPainel(painelBotoes);
            painelBotoes.add(btnRegistrar);
                        painelBotoes.add(btnDetalhes);
                        painelBotoes.add(btnMulta);
            painelBotoes.add(btnVoltar);
            painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

            // Registrar pagamento
            btnRegistrar.addActionListener(e -> {
                int row = tabela.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Selecione uma reserva!", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Reserva r = reservas.get(row);

                if (reservaEstaQuitada(r)) {
                    JOptionPane.showMessageDialog(this,
                            "Esta reserva já está totalmente paga.\nNenhum pagamento adicional é permitido.",
                            "Pagamento Concluído",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                String valorStr = JOptionPane.showInputDialog(this, "Digite o valor do pagamento:");
                if (valorStr == null || valorStr.isBlank()) return;

                try {
                    double valor = Double.parseDouble(valorStr.replace(",", "."));
                    if (valor <= 0) {
                        JOptionPane.showMessageDialog(this,
                                "O valor deve ser maior que zero!", "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String metodo = (String) JOptionPane.showInputDialog(
                            this,
                            "Selecione o método de pagamento:",
                            "Método",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            new String[]{"Pix", "Cartão de Crédito", "Cartão de Débito", "Dinheiro"},
                            "Pix"
                    );
                    if (metodo == null) return;

                    pagamentoController.registrarPagamento(this, r, valor, metodo);

                    try {
                        preencherTabelaPagamentos(modeloPagamento, funcController.listarReservas());
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this,
                                "Erro ao carregar reservas: " + ex.getMessage(),
                                "Erro", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Valor inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            });

            btnMulta.addActionListener(e -> {
                int row = tabela.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Selecione uma reserva!", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Reserva r = reservas.get(row);
                abrirDialogMulta(r);
            });

            btnDetalhes.addActionListener(e -> {
                int row = tabela.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Selecione uma reserva!", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Reserva r = reservas.get(row);
                abrirDialogDetalhes(r);
            });

            btnVoltar.addActionListener(e -> {
    parent.setVisible(true);
    dispose();
});


            // Filtro status + pesquisa
            DocumentListener doc = new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { aplicar(); }
                public void removeUpdate(DocumentEvent e) { aplicar(); }
                public void changedUpdate(DocumentEvent e) { aplicar(); }
                private void aplicar() {
                    String termo = campoBusca.getText().trim().toLowerCase();
                    String statusFiltro = cbStatusPagamento.getSelectedItem().toString();

                    List<Reserva> filtradas = reservas.stream()
                            .filter(r -> r.getClient().getNome().toLowerCase().contains(termo)
                                    || r.getVehicle().getModelo().toLowerCase().contains(termo)
                                    || r.getVehicle().getPlaca().toLowerCase().contains(termo)
                                    || String.valueOf(r.getId()).contains(termo)
                            )
                            .filter(r -> statusFiltro.equals("Todos") ||
                                    r.getStatusPagamento().equalsIgnoreCase(statusFiltro))
                            .toList();

                    preencherTabelaPagamentos(modeloPagamento, filtradas);
                }
            };
            campoBusca.getDocument().addDocumentListener(doc);
            cbStatusPagamento.addItemListener(e -> docChanged(campoBusca));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao abrir pagamentos: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void docChanged(JTextField tf) {
        tf.setText(tf.getText());
    }

    // =========================
    // Diálogo de MULTA (tema)
    // =========================
    private void abrirDialogMulta(Reserva reserva) {
        JDialog dialog = new JDialog(this, "Aplicar Multa - Reserva " + reserva.getId(), true);
        dialog.setSize(500, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(BG_WINDOW);

        JPanel painel = new JPanel(new BorderLayout(10, 10));
        estilizarPainel(painel);
        painel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        dialog.add(painel, BorderLayout.CENTER);

        JLabel titulo = new JLabel("Descreva o ocorrido e o valor da multa", SwingConstants.CENTER);
        titulo.setFont(FONT_BOLD);
        titulo.setForeground(FG_TEXT);
        painel.add(titulo, BorderLayout.NORTH);

        JTextArea campoDescricao = new JTextArea();
        campoDescricao.setLineWrap(true);
        campoDescricao.setWrapStyleWord(true);
        campoDescricao.setFont(FONT_BASE);
        campoDescricao.setForeground(FG_TEXT);
        campoDescricao.setBackground(BG_PANEL);
        campoDescricao.setCaretColor(FG_TEXT);

        JScrollPane scroll = new JScrollPane(campoDescricao);
        estilizarScroll(scroll);
        scroll.setPreferredSize(new Dimension(420, 140));

        JPanel painelValor = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        estilizarPainel(painelValor);
        JLabel lblValor = new JLabel("Valor da Multa (R$):");
        lblValor.setForeground(ACCENT_YELLOW);
        lblValor.setFont(FONT_BOLD);
        JTextField campoValorMulta = new JTextField(10);
        estilizarTextField(campoValorMulta);

        painelValor.add(lblValor);
        painelValor.add(campoValorMulta);

        JPanel painelCentro = new JPanel(new BorderLayout(10, 10));
        estilizarPainel(painelCentro);
        painelCentro.add(scroll, BorderLayout.CENTER);
        painelCentro.add(painelValor, BorderLayout.SOUTH);
        painel.add(painelCentro, BorderLayout.CENTER);

        JButton btnAplicar = themedButton("Confirmar", ACCENT_GREEN);
        JButton btnCancelar = themedButton("Cancelar", ACCENT_RED);
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        estilizarPainel(botoes);
        botoes.add(btnAplicar);
        botoes.add(btnCancelar);
        painel.add(botoes, BorderLayout.SOUTH);

        btnCancelar.addActionListener(e -> dialog.dispose());
        btnAplicar.addActionListener(e -> {
            String desc = campoDescricao.getText().trim();
            String valorStr = campoValorMulta.getText().trim().replace(",", ".");

            if (desc.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Descreva o ocorrido.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (valorStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Informe o valor da multa.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                double valor = Double.parseDouble(valorStr);
                if (valor <= 0) {
                    JOptionPane.showMessageDialog(dialog, "O valor deve ser maior que zero.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                pagamentoController.aplicarMultaManual(reserva, desc, valor);
                JOptionPane.showMessageDialog(dialog, "Multa aplicada com sucesso!");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Valor inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    // ===========================
    // Diálogo de DETALHES (tema)
    // ===========================
    private void abrirDialogDetalhes(Reserva reserva) {
        JDialog dialog = new JDialog(this, "Detalhes da Reserva " + reserva.getId(), true);
        dialog.setSize(520, 620);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(BG_WINDOW);

        JPanel painel = new JPanel(new BorderLayout(10, 10));
        estilizarPainel(painel);
        painel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        dialog.add(painel, BorderLayout.CENTER);

        JLabel titulo = new JLabel("Extrato da Reserva", SwingConstants.CENTER);
        titulo.setFont(FONT_BOLD.deriveFont(16f));
        titulo.setForeground(FG_TEXT);
        painel.add(titulo, BorderLayout.NORTH);

        List<Pagamento> listaPag;
        try {
            listaPag = pagamentoController.listarPagamentosPorReserva(reserva.getId());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pagamentos: " + e.getMessage());
            return;
        }

        double totalPago = listaPag.stream().mapToDouble(Pagamento::getValor).sum();
        double totalReserva = reserva.getTotal();
        double pendente = Math.max(0, totalReserva - totalPago);

        JProgressBar barra = new JProgressBar(0, (int) Math.max(1, Math.round(totalReserva)));
        estilizarProgress(barra);
        barra.setValue((int) Math.round(totalPago));
        barra.setStringPainted(true);
        barra.setString(String.format("Pago: R$ %.2f / R$ %.2f", totalPago, totalReserva));
        atualizarCorBarra(barra, totalReserva <= 0 ? 0 : (int) Math.round((totalPago / totalReserva) * 100));

        JPanel painelInfo = new JPanel(new GridLayout(4, 1, 5, 5));
        estilizarPainel(painelInfo);
        painelInfo.add(labelMuted("Cliente: " + reserva.getClient().getNome()));
        painelInfo.add(labelMuted("Veículo: " + reserva.getVehicle().getModelo() +
                " (" + reserva.getVehicle().getPlaca() + ")"));
        painelInfo.add(labelMuted("Status Pagamento: " + reserva.getStatusPagamento()));
        painelInfo.add(barra);

        painel.add(painelInfo, BorderLayout.CENTER);

        JTextArea detalhesPag = new JTextArea();
        detalhesPag.setEditable(false);
        detalhesPag.setFont(FONT_BASE);
        detalhesPag.setForeground(FG_TEXT);
        detalhesPag.setBackground(BG_PANEL);

        detalhesPag.append("PAGAMENTOS REALIZADOS:\n\n");
        if (listaPag.isEmpty()) {
            detalhesPag.append("Nenhum pagamento registrado.\n");
        } else {
            for (Pagamento p : listaPag) {
                String metodo = p.getMetodoPagamento();
                if (metodo != null && metodo.startsWith("Multa:")) {
                    String descricao = metodo.substring(6).trim();
                    detalhesPag.append(String.format("• R$ %.2f  -  %s  (Multa)\n", p.getValor(), p.getDataPagamento()));
                    detalhesPag.append("    ⇒ " + descricao + "\n\n");
                } else {
                    detalhesPag.append(String.format("• R$ %.2f  -  %s  (%s)\n",
                            p.getValor(), p.getDataPagamento(), metodo));
                }
            }
        }

        detalhesPag.append("\nRESUMO:\n");
        detalhesPag.append(String.format("• Total Pago: R$ %.2f\n", totalPago));
        detalhesPag.append(String.format("• Total Devido: R$ %.2f\n", totalReserva));
        detalhesPag.append(String.format("• Pendente: R$ %.2f\n", pendente));

        JScrollPane scroll = new JScrollPane(detalhesPag);
        estilizarScroll(scroll);
        scroll.setPreferredSize(new Dimension(440, 260));
        painel.add(scroll, BorderLayout.SOUTH);

        JButton btnFechar = themedButton("Fechar", ACCENT_RED);
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        estilizarPainel(rodape);
        rodape.add(btnFechar);
        dialog.add(rodape, BorderLayout.SOUTH);

        btnFechar.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    // ====================================
    // Tabela/Progress/Filtros
    // ====================================
    private void estilizarTabela(JTable tabela) {
        tabela.setFont(FONT_BASE);
        tabela.setForeground(FG_TEXT);
        tabela.setBackground(new Color(45, 45, 45));
        tabela.setRowHeight(24);
        tabela.setGridColor(GRID_GRAY);
        tabela.setSelectionBackground(ACCENT_GREEN.darker());
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setFillsViewportHeight(true);

        JTableHeader header = tabela.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setForeground(Color.BLACK);
        header.setBackground(ACCENT_YELLOW);
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        center.setForeground(FG_TEXT);
        center.setBackground(new Color(45, 45, 45));
        tabela.setDefaultRenderer(Object.class, center);
    }

    private void estilizarScroll(JScrollPane scroll) {
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_YELLOW));
        scroll.getViewport().setBackground(BG_PANEL);
        scroll.setBackground(BG_PANEL);
    }

    private void estilizarProgress(JProgressBar barra) {
        barra.setBackground(BG_PANEL);
        barra.setForeground(ACCENT_GREEN);
        barra.setFont(FONT_BASE);
        barra.setStringPainted(true);
    }

    private void estilizarTextField(JTextField tf) {
        tf.setFont(FONT_BASE);
        tf.setForeground(FG_TEXT);
        tf.setCaretColor(FG_TEXT);
        tf.setBackground(new Color(35, 35, 35));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_YELLOW),
                BorderFactory.createEmptyBorder(4, 5, 4, 5)
        ));
    }

    private void estilizarCombo(JComboBox<?> combo) {
        combo.setFont(FONT_BASE);
        combo.setForeground(FG_TEXT);
        combo.setBackground(new Color(35, 35, 35));
        combo.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));
    }

    private void estilizarPainel(JComponent c) {
        c.setBackground(BG_PANEL);
        c.setForeground(FG_TEXT);
    }

    private JButton themedButton(String texto, Color corFundo) {
        JButton b = new JButton(texto);
        b.setFont(FONT_BOLD);
        b.setForeground(Color.WHITE);
        b.setBackground(corFundo);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corFundo.darker(), 1, true),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        return b;
    }

    private JLabel labelMuted(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(FONT_BASE);
        l.setForeground(FG_MUTED);
        return l;
    }

    // ======================
    // Lógica
    // ======================
    private void preencherTabelaPagamentos(DefaultTableModel modelo, List<Reserva> reservas) {
        modelo.setRowCount(0);
        for (Reserva r : reservas) {
            modelo.addRow(new Object[]{
                    r.getId(),
                    r.getClient().getNome(),
                    r.getVehicle().getPlaca(),
                    r.getVehicle().getModelo(),
                    r.getDataInicio(),
                    r.getDataFim(),
                    String.format("R$ %.2f", r.getTotal()),
                    r.getStatusPagamento()
            });
        }
    }

    private void atualizarTabelaCliente(JComboBox<Reserva> cb, JTable tabela, JProgressBar barra) {
        Reserva r = (Reserva) cb.getSelectedItem();
        if (r == null) return;
        try {
            PagamentoStatus status = pagamentoController.getStatusPagamento(r);
            int prog = status.getProgresso();
            barra.setValue(prog);
            barra.setString(status.getTexto());
            atualizarCorBarra(barra, prog);

            List<Pagamento> lista = pagamentoController.listarPagamentosPorReserva(r.getId());
            tabela.setModel(new PagamentoTableModel(lista));
            estilizarTabela(tabela);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao atualizar dados: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void realizarPagamentoCliente(JComboBox<Reserva> cb, Cliente cliente) {
        Reserva r = (Reserva) cb.getSelectedItem();
        if (r == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva primeiro!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (reservaEstaQuitada(r)) {
            JOptionPane.showMessageDialog(this,
                    "Esta reserva já está completamente paga!",
                    "Reserva Quitada",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String valorStr = JOptionPane.showInputDialog(this, "Digite o valor a pagar:");
        if (valorStr == null || valorStr.isBlank()) return;

        try {
            double valor = Double.parseDouble(valorStr.replace(",", "."));
            String metodo = (String) JOptionPane.showInputDialog(
                    this,
                    "Método de pagamento:",
                    "Escolha",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"Pix", "Cartão de Crédito", "Cartão de Débito", "Dinheiro"},
                    "Pix"
            );
            if (metodo == null) return;

            pagamentoController.registrarPagamento(this, r, valor, metodo);
            JOptionPane.showMessageDialog(this, "Pagamento registrado com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            atualizarTabelaCliente(cb, (JTable) ((JScrollPane) ((JPanel) ((JPanel) getContentPane()
                    .getComponent(0)).getComponent(1)).getComponent(1)).getViewport().getView(), // seguro o suficiente aqui
                    (JProgressBar) ((JPanel) ((JPanel) ((JPanel) getContentPane()
                            .getComponent(0)).getComponent(1)).getComponent(0)).getComponent(2));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao registrar pagamento: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
private void mostrarHistorico(Cliente cliente) {

    // Tema padrão
    Color fundo = new Color(25, 25, 25);
    Color painel = new Color(35, 35, 35);
    Color amarelo = new Color(255, 215, 0);
    Color texto = Color.WHITE;

    JDialog dialog = new JDialog(this, "Histórico de Pagamentos", true);
    dialog.setSize(460, 330);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout(10, 10));
    dialog.getContentPane().setBackground(fundo);

    try {
        List<Pagamento> listaPag = pagamentoController.listarPagamentosPorCliente(cliente.getId());
        double total = listaPag.stream().mapToDouble(Pagamento::getValor).sum();
        long qtd = listaPag.size();
        double media = qtd > 0 ? total / qtd : 0;

        // Painel central
        JPanel painelCentral = new JPanel(new GridBagLayout());
        painelCentral.setBackground(painel);
        painelCentral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Título
        JLabel titulo = new JLabel("Histórico de Pagamentos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(amarelo);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        painelCentral.add(titulo, gbc);

        gbc.gridwidth = 1;

        // Função para labels
        java.util.function.Function<String, JLabel> lbl = txt -> {
            JLabel l = new JLabel(txt);
            l.setForeground(amarelo);
            l.setFont(new Font("Segoe UI", Font.BOLD, 14));
            return l;
        };

        java.util.function.Function<String, JLabel> valor = txt -> {
            JLabel l = new JLabel(txt);
            l.setForeground(texto);
            l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            return l;
        };

        // Informações
        gbc.gridy = 2; gbc.gridx = 0; painelCentral.add(lbl.apply("Total de Pagamentos:"), gbc);
        gbc.gridx = 1; painelCentral.add(valor.apply(String.valueOf(qtd)), gbc);

        gbc.gridy = 3; gbc.gridx = 0; painelCentral.add(lbl.apply("Valor Total Pago:"), gbc);
        gbc.gridx = 1; painelCentral.add(valor.apply(String.format("R$ %.2f", total)), gbc);

        gbc.gridy = 4; gbc.gridx = 0; painelCentral.add(lbl.apply("Média por Pagamento:"), gbc);
        gbc.gridx = 1; painelCentral.add(valor.apply(String.format("R$ %.2f", media)), gbc);

        dialog.add(painelCentral, BorderLayout.CENTER);

        // Botão fechar
        JButton btnFechar = new JButton("Fechar");
        btnFechar.setBackground(Color.RED);
        btnFechar.setForeground(Color.WHITE);
        btnFechar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnFechar.addActionListener(e -> dialog.dispose());

        JPanel painelBotao = new JPanel();
        painelBotao.setBackground(fundo);
        painelBotao.add(btnFechar);

        dialog.add(painelBotao, BorderLayout.SOUTH);

        dialog.setVisible(true);

    } catch (Exception ex) {

        JOptionPane.showMessageDialog(
                this,
                "Erro ao gerar histórico: " + ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }
}

    private boolean reservaEstaQuitada(Reserva r) {
        try {
            ReservaDAO dao = new ReservaDAO();
            Reserva reservaAtualizada = dao.getById(r.getId());
            if (reservaAtualizada == null) return true;

            double totalPago = pagamentoController
                    .listarPagamentosPorReserva(r.getId())
                    .stream()
                    .mapToDouble(Pagamento::getValor)
                    .sum();

            return totalPago >= reservaAtualizada.getTotal();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao verificar pagamento: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return true;
        }
    }

    private void atualizarCorBarra(JProgressBar barra, int progresso) {
        if (progresso >= 100) {
            barra.setForeground(new Color(46, 204, 113)); // verde
        } else if (progresso >= 70) {
            barra.setForeground(new Color(52, 152, 219)); // azul
        } else if (progresso >= 30) {
            barra.setForeground(new Color(241, 196, 15)); // amarelo
        } else {
            barra.setForeground(new Color(231, 76, 60)); // vermelho
        }
    }
}