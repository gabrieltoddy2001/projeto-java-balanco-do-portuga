package com.balancodoportuga.ui.forms;

import com.balancodoportuga.controller.ReservaController;
import com.balancodoportuga.controller.ClienteController;
import com.balancodoportuga.controller.FuncionarioController;
import com.balancodoportuga.model.Cliente;
import com.balancodoportuga.model.Veiculo;
import com.balancodoportuga.model.Reserva;
import com.balancodoportuga.ui.tablemodel.ReservaTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservaForm extends JFrame {
    // TEMA PORTUGAL
    private final Color verdePortugal = new Color(0, 102, 51);
    private final Color vermelhoPortugal = new Color(178, 0, 0);
    private final Color douradoSuave = new Color(255, 215, 0);
    private final Color fundoEscuro = new Color(28, 28, 28);
    private final Font fontePadrao = new Font("Segoe UI", Font.PLAIN, 14);

    private final boolean isFuncionario;
    private final Cliente clienteLogado;
   
    private final ReservaController reservaController = new ReservaController();
    private final ClienteController clienteController = new ClienteController();
    private final FuncionarioController funcionarioController = new FuncionarioController();

    private JTable tabela;
    private ReservaTableModel modeloTabela;

    private JTextField txtDataInicio;
    private JTextField txtDataFim;
    private JComboBox<String> cbStatus;
    private JTextField campoBuscaCliente;
    private JLabel lblTotal;
    private JLabel lblResumo;

    // Campos modo cliente
    private JComboBox<Veiculo> comboVeiculo;
    private JTextField campoDataInicio;
    private JTextField campoDataFim;
    private JButton btnSalvar;

    public ReservaForm() {
        this.isFuncionario = true;
        this.clienteLogado = null;
        inicializar();
    }

    public ReservaForm(Cliente cliente) {
        this.isFuncionario = false;
        this.clienteLogado = cliente;
        inicializar();
    }

    private void inicializar() {
        setTitle(isFuncionario ? "Gerenciar Reservas" : "Minhas Reservas");
        setSize(950, 560);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout(12, 12));
        raiz.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        raiz.setBackground(fundoEscuro);
        add(raiz);

        if (isFuncionario) {
            montarUIFuncionario(raiz);
            carregarReservasFuncionario();
        } else {
            montarUICliente(raiz);
        }
    }

    // ============================================================
    // MODO FUNCIONÁRIO
    // ============================================================
    private void montarUIFuncionario(JPanel raiz) {
        // --------------------------
        // FILTROS
        // --------------------------
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filtros.setBackground(fundoEscuro);
        filtros.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(douradoSuave),
                "Filtros",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 13),
                douradoSuave
        ));

        filtros.add(criarLabel("Status:"));
        cbStatus = new JComboBox<>(new String[]{
                "Todos", "Solicitada", "Em andamento", "Concluída", "Cancelada"
        });
        estilizarCampo(cbStatus);
        filtros.add(cbStatus);

        filtros.add(criarLabel("Cliente:"));
        campoBuscaCliente = criarCampo();
        filtros.add(campoBuscaCliente);

        filtros.add(criarLabel("Data Início:"));
        txtDataInicio = criarCampo();
        filtros.add(txtDataInicio);

        filtros.add(criarLabel("Data Fim:"));
        txtDataFim = criarCampo();
        filtros.add(txtDataFim);

        raiz.add(filtros, BorderLayout.NORTH);

        // --------------------------
        // TABELA
        // --------------------------
        modeloTabela = new ReservaTableModel(new ArrayList<>(), false);
        tabela = new JTable(modeloTabela);
        estilizarTabela(tabela);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.getViewport().setBackground(new Color(45, 45, 45));
        scroll.setBackground(new Color(45, 45, 45));
        scroll.getViewport().setOpaque(true);
        scroll.setBorder(BorderFactory.createLineBorder(douradoSuave));

        raiz.add(scroll, BorderLayout.CENTER);

// --------------------------
// RODAPÉ
// --------------------------
JPanel rodape = new JPanel(new BorderLayout());
rodape.setBackground(fundoEscuro);

// ========== LINHA SUPERIOR: RESUMO ==========
lblResumo = criarLabel("Resumo:");
lblResumo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

lblTotal = new JLabel("Total Faturado: R$ 0,00");
lblTotal.setForeground(douradoSuave);
lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));

JPanel linhaResumo = new JPanel(new BorderLayout());
linhaResumo.setBackground(fundoEscuro);
linhaResumo.add(lblResumo, BorderLayout.WEST);
linhaResumo.add(lblTotal, BorderLayout.EAST);

// adiciona o resumo na parte superior do rodapé
rodape.add(linhaResumo, BorderLayout.NORTH);

// ========== LINHA INFERIOR: BOTÕES ==========
JButton btnConcluir = criarBotao("Concluir Reserva", verdePortugal);
JButton btnCancelar = criarBotao("Cancelar Reserva", vermelhoPortugal);
JButton btnVoltar = criarBotao("Voltar", vermelhoPortugal);

// ---- AÇÃO CONCLUIR ----
btnConcluir.addActionListener(e -> {
    int row = tabela.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Selecione uma reserva para concluir!");
        return;
    }

    Reserva reserva = modeloTabela.getReservaAt(row);
    funcionarioController.concluirReserva(this, reserva);
    carregarReservasFuncionario();
});

// ---- AÇÃO CANCELAR ----
btnCancelar.addActionListener(e -> {
    int row = tabela.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Selecione uma reserva para cancelar!");
        return;
    }

    Reserva reserva = modeloTabela.getReservaAt(row);

    if (!reserva.getStatus().equalsIgnoreCase("Em andamento")
            && !reserva.getStatus().equalsIgnoreCase("Solicitada")) {
        JOptionPane.showMessageDialog(this,
                "Somente reservas 'Solicitadas' ou 'Em andamento' podem ser canceladas!",
                "Ação inválida", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Cancelar esta reserva e calcular valor proporcional?",
            "Confirmar Cancelamento",
            JOptionPane.YES_NO_OPTION
    );

    if (confirm != JOptionPane.YES_OPTION) return;

    try {
        LocalDate hoje = LocalDate.now();
        LocalDate inicio = reserva.getDataInicio();

        long diasUsados = java.time.temporal.ChronoUnit.DAYS.between(inicio, hoje);
        if (diasUsados < 1) diasUsados = 1;

        double diaria = reserva.getVehicle().getDiaria();
        double totalProporcional = diasUsados * diaria;

        new com.balancodoportuga.dao.ReservaDAO()
                .cancelarReservaComValor(reserva.getId(), totalProporcional);

        JOptionPane.showMessageDialog(this,
                String.format("""
                        Reserva cancelada!
                        Dias utilizados: %d
                        Total proporcional: R$ %.2f
                        """, diasUsados, totalProporcional));

        carregarReservasFuncionario();

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Erro ao cancelar: " + ex.getMessage());
    }
});

// ---- VOLTAR ----
btnVoltar.addActionListener(e -> {
    new com.balancodoportuga.ui.menu.FuncionarioMenu().setVisible(true);
    dispose();
});

// linha dos botões (embaixo)
JPanel linhaBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
linhaBotoes.setBackground(fundoEscuro);

linhaBotoes.add(btnConcluir); // primeiro
linhaBotoes.add(btnCancelar); // segundo
linhaBotoes.add(btnVoltar);   // terceiro

rodape.add(linhaBotoes, BorderLayout.SOUTH);

// adiciona no painel principal
raiz.add(rodape, BorderLayout.SOUTH);

        // Filtros automáticos
        javax.swing.event.DocumentListener listener = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { atualizarReservasAuto(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { atualizarReservasAuto(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { atualizarReservasAuto(); }
        };
        txtDataInicio.getDocument().addDocumentListener(listener);
        txtDataFim.getDocument().addDocumentListener(listener);
        campoBuscaCliente.getDocument().addDocumentListener(listener);
        cbStatus.addItemListener(e -> atualizarReservasAuto());
    }

    private void carregarReservasFuncionario() {
        try {
            List<Reserva> reservas = funcionarioController.listarReservas();
            atualizarTabelaFuncionario(reservas);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar reservas: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarReservasAuto() {
        try {
            LocalDate ini = txtDataInicio.getText().isBlank() ? null : LocalDate.parse(txtDataInicio.getText());
            LocalDate fim = txtDataFim.getText().isBlank() ? null : LocalDate.parse(txtDataFim.getText());
            String status = cbStatus.getSelectedItem().toString();
            String termo = campoBuscaCliente.getText().trim().toLowerCase();

            List<Reserva> filtradas = funcionarioController.filtrarReservas(ini, fim, status);

            if (!termo.isEmpty()) {
                filtradas = filtradas.stream()
                        .filter(r -> r.getClient().getNome().toLowerCase().contains(termo))
                        .toList();
            }

            atualizarTabelaFuncionario(filtradas);

        } catch (Exception ignored) {}
    }

    private void atualizarTabelaFuncionario(List<Reserva> lista) {
        modeloTabela.setReservas(lista);
        double total = funcionarioController.calcularTotalReservas(lista);
        lblTotal.setText(String.format("Total Faturado: R$ %.2f", total));
        lblResumo.setText(gerarResumoStatus(lista));
    }

    private String gerarResumoStatus(List<Reserva> lista) {
        long solicitadas = lista.stream().filter(r -> r.getStatus().equalsIgnoreCase("Solicitada")).count();
        long andamento = lista.stream().filter(r -> r.getStatus().equalsIgnoreCase("Em andamento")).count();
        long concluidas = lista.stream().filter(r -> r.getStatus().equalsIgnoreCase("Concluída")).count();
        long canceladas = lista.stream().filter(r -> r.getStatus().equalsIgnoreCase("Cancelada")).count();

        return String.format("Solicitadas: %d | Em andamento: %d | Concluídas: %d | Canceladas: %d",
                solicitadas, andamento, concluidas, canceladas);
    }

    // ============================================================
    // MODO CLIENTE
    // ============================================================
    private void montarUICliente(JPanel raiz) {
        // --------------------------
        // CABEÇALHO
        // --------------------------
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(fundoEscuro);
        JLabel titulo = new JLabel("Minhas Reservas");
        titulo.setForeground(douradoSuave);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(titulo, BorderLayout.WEST);
        raiz.add(header, BorderLayout.NORTH);

        // --------------------------
        // ABAS
        // --------------------------
        JTabbedPane abas = new JTabbedPane();
        abas.setBackground(fundoEscuro);
        abas.setForeground(Color.WHITE);

        // --------------------------
        // ABA 1 - NOVA RESERVA
        // --------------------------
        JPanel nova = new JPanel(new GridBagLayout());
        nova.setBackground(fundoEscuro);
        nova.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(douradoSuave),
                "Nova Reserva",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                douradoSuave
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblVeiculo = criarLabel("Veículo:");
        comboVeiculo = new JComboBox<>();
        comboVeiculo.setFont(fontePadrao);

        JLabel lblInicio = criarLabel("Data Início (AAAA-MM-DD):");
        campoDataInicio = criarCampo();

        JLabel lblFim = criarLabel("Data Fim (AAAA-MM-DD):");
        campoDataFim = criarCampo();

        JLabel lblEstimativa = criarLabel("Estimativa: —");
        lblEstimativa.setForeground(douradoSuave);

        JButton btnVerificar = criarBotao("Verificar Disponibilidade", verdePortugal);
        btnSalvar = criarBotao("Salvar Reserva", vermelhoPortugal);
        JButton btnVoltar = criarBotao("Voltar", vermelhoPortugal);
        btnVoltar.addActionListener(e -> dispose());

        gbc.gridx = 0; gbc.gridy = 0; nova.add(lblVeiculo, gbc);
        gbc.gridx = 1; nova.add(comboVeiculo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; nova.add(lblInicio, gbc);
        gbc.gridx = 1; nova.add(campoDataInicio, gbc);

        gbc.gridx = 0; gbc.gridy = 2; nova.add(lblFim, gbc);
        gbc.gridx = 1; nova.add(campoDataFim, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; nova.add(lblEstimativa, gbc);

        JPanel botoesNova = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoesNova.setBackground(fundoEscuro);
        botoesNova.add(btnVerificar);
        botoesNova.add(btnSalvar);
        botoesNova.add(btnVoltar);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; nova.add(botoesNova, gbc);

        // listeners de estimativa
        Runnable atualizar = () -> atualizarEstimativa(lblEstimativa);
        comboVeiculo.addActionListener(e -> atualizar.run());
        addDoc(campoDataInicio, atualizar);
        addDoc(campoDataFim, atualizar);

        btnVerificar.addActionListener(e -> verificarDisponibilidade());
        btnSalvar.addActionListener(e -> salvarReservaCliente());

        // --------------------------
        // ABA 2 - HISTÓRICO
        // --------------------------
        JPanel hist = new JPanel(new BorderLayout());
        hist.setBackground(fundoEscuro);

        modeloTabela = new ReservaTableModel(new ArrayList<>(), true);
        tabela = new JTable(modeloTabela);
        estilizarTabela(tabela);

        JScrollPane scrollHist = new JScrollPane(tabela);
        scrollHist.getViewport().setBackground(new Color(45, 45, 45));
        scrollHist.setBackground(new Color(45, 45, 45));
        scrollHist.getViewport().setOpaque(true);
        scrollHist.setBorder(BorderFactory.createLineBorder(douradoSuave));

        hist.add(scrollHist, BorderLayout.CENTER);

        JButton btnVoltar2 = criarBotao("Voltar", vermelhoPortugal);
        btnVoltar2.addActionListener(e -> dispose());

        JPanel barraHist = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        barraHist.setBackground(fundoEscuro);
        barraHist.add(btnVoltar2);

        hist.add(barraHist, BorderLayout.SOUTH);

        abas.add("Nova Reserva", nova);
        abas.add("Histórico", hist);

        raiz.add(abas, BorderLayout.CENTER);

        // Carregar dados iniciais
        reservaController.carregarVeiculosNoCombo(comboVeiculo, this);
        carregarHistoricoClienteTabela(tabela);
    }

    // ============================================================
    // AÇÕES MODO CLIENTE
    // ============================================================
    private void verificarDisponibilidade() {
        try {
            Veiculo v = (Veiculo) comboVeiculo.getSelectedItem();
            if (v == null) {
                JOptionPane.showMessageDialog(this, "Selecione um veículo.");
                return;
            }

            LocalDate ini = parseData(campoDataInicio.getText());
            LocalDate fim = parseData(campoDataFim.getText());
            if (ini == null || fim == null) {
                JOptionPane.showMessageDialog(this, "Datas inválidas.");
                return;
            }

            boolean conflito = reservaController.verificarDisponibilidade(v.getId(), ini, fim);

            JOptionPane.showMessageDialog(this,
                    conflito ? "Veículo indisponível no período." : "Veículo disponível!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }

    private void salvarReservaCliente() {
        try {
            if (clienteLogado == null) {
                JOptionPane.showMessageDialog(this, "Nenhum cliente logado encontrado.");
                return;
            }

            Veiculo v = (Veiculo) comboVeiculo.getSelectedItem();
            if (v == null) {
                JOptionPane.showMessageDialog(this, "Selecione um veículo.");
                return;
            }

            LocalDate ini = parseData(campoDataInicio.getText());
            LocalDate fim = parseData(campoDataFim.getText());
            if (ini == null || fim == null) {
                JOptionPane.showMessageDialog(this, "Datas inválidas.");
                return;
            }

            reservaController.salvarReserva(clienteLogado.getId(), v.getId(), ini, fim);

            JOptionPane.showMessageDialog(this, "Reserva registrada com sucesso!");
            carregarHistoricoClienteTabela(tabela);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar reserva: " + ex.getMessage());
        }
    }

    private void carregarHistoricoClienteTabela(JTable tabelaHist) {
        try {
            clienteController.atualizarStatusAutomaticamente();
            List<Reserva> reservas = (clienteLogado == null)
                    ? List.of()
                    : clienteController.listarReservasCliente(clienteLogado.getId());
            tabelaHist.setModel(new ReservaTableModel(reservas, true));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar histórico: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============================================================
    // ESTILO — MÉTODOS AUXILIARES
    // ============================================================
    private JLabel criarLabel(String txt) {
        JLabel lbl = new JLabel(txt);
        lbl.setForeground(douradoSuave);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return lbl;
    }

    private JTextField criarCampo() {
        JTextField campo = new JTextField();
        campo.setBackground(new Color(40, 40, 40));
        campo.setForeground(Color.WHITE);
        campo.setCaretColor(Color.WHITE);
        campo.setBorder(BorderFactory.createLineBorder(douradoSuave));
        campo.setFont(fontePadrao);
        campo.setPreferredSize(new Dimension(140, 28));
        return campo;
    }

    private void estilizarCampo(JComponent c) {
        c.setBackground(new Color(40, 40, 40));
        c.setForeground(Color.WHITE);
        c.setFont(fontePadrao);
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        btn.setPreferredSize(new Dimension(160, 38));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(cor.darker()); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(cor); }
        });

        return btn;
    }

    private void estilizarTabela(JTable tabela) {
        tabela.setBackground(new Color(45, 45, 45));
        tabela.setForeground(Color.WHITE);
        tabela.setRowHeight(24);
        tabela.setSelectionBackground(verdePortugal);
        tabela.setSelectionForeground(Color.WHITE);

        tabela.getTableHeader().setBackground(douradoSuave);
        tabela.getTableHeader().setForeground(Color.BLACK);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        ((DefaultTableCellRenderer)tabela.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    // ============================================================
    // LÓGICA AUXILIAR
    // ============================================================
    private static void addDoc(JTextField campo, Runnable action) {
        campo.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { action.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { action.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { action.run(); }
        });
    }

    private LocalDate parseData(String s) {
        try {
            if (s == null || s.trim().isEmpty() || s.contains("_")) return null;
            return LocalDate.parse(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private void atualizarEstimativa(JLabel lbl) {
        Veiculo v = (Veiculo) comboVeiculo.getSelectedItem();
        LocalDate ini = parseData(campoDataInicio.getText());
        LocalDate fim = parseData(campoDataFim.getText());
        if (v == null || ini == null || fim == null || fim.isBefore(ini)) {
            lbl.setText("Estimativa: —");
            return;
        }
        long dias = java.time.temporal.ChronoUnit.DAYS.between(ini, fim);
        if (dias <= 0) { lbl.setText("Estimativa: —"); return; }
        double total = dias * v.getDiaria();
        lbl.setText(String.format("Estimativa: %d dia(s) • Total: R$ %.2f", dias, total));
    }

    // GETTERS PARA O CONTROLLER
    public Cliente getClienteLogado() { return clienteLogado; }
    public JComboBox<Veiculo> getComboVeiculo() { return comboVeiculo; }
    public JTextField getCampoDataInicio() { return campoDataInicio; }
    public JTextField getCampoDataFim() { return campoDataFim; }
}