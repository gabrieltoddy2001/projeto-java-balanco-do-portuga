package com.balancodoportuga.ui.forms;

import com.balancodoportuga.controller.VeiculoController;
import com.balancodoportuga.model.Veiculo;
import com.balancodoportuga.ui.menu.FuncionarioMenu;
import com.balancodoportuga.ui.tablemodel.VeiculoTableModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class VeiculoForm extends JFrame {

    private final VeiculoController controller = new VeiculoController();
    private JTable tabela;
    private VeiculoTableModel tableModel;
    private JTextField campoBusca;
    private List<Veiculo> listaCompleta;

    // TEMA PORTUGAL
    private final Color verdePortugal = new Color(0, 102, 51);
    private final Color vermelhoPortugal = new Color(178, 0, 0);
    private final Color douradoSuave = new Color(255, 215, 0);
    private final Color fundoEscuro = new Color(28, 28, 28);

    public VeiculoForm() {
        setTitle("Gerenciar Veículos");
        setSize(950, 520);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout(15, 15));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        painelPrincipal.setBackground(fundoEscuro);
        add(painelPrincipal);

        // ======== FILTROS ========
        campoBusca = criarCampo();

        JLabel lblBusca = criarLabel("Pesquisar:");
        JLabel lblMarca = criarLabel("Marca:");

        JComboBox<String> comboMarca = new JComboBox<>();
        comboMarca.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        comboMarca.addItem("Todas");
        try {
            List<Veiculo> temp = controller.listarVeiculos();
            temp.stream().map(Veiculo::getMarca).distinct().sorted().forEach(comboMarca::addItem);
        } catch (Exception ignored) {}

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filtros.setBackground(fundoEscuro);
        filtros.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(douradoSuave),
                "Filtros",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 13),
                douradoSuave
        ));

        filtros.add(lblBusca);
        filtros.add(campoBusca);
        filtros.add(lblMarca);
        filtros.add(comboMarca);

        painelPrincipal.add(filtros, BorderLayout.NORTH);

        // ======== TABELA ========
        tableModel = new VeiculoTableModel();
        tabela = new JTable(tableModel);

        estilizarTabela();

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.getViewport().setBackground(new Color(45, 45, 45));
        painelPrincipal.add(scroll, BorderLayout.CENTER);

        // ======== BOTÕES ========
        JButton btnAdicionar = criarBotao("Adicionar", verdePortugal);
        JButton btnEditar = criarBotao("Editar", verdePortugal);
        JButton btnExcluir = criarBotao("Excluir", vermelhoPortugal);
        JButton btnVoltar = criarBotao("Voltar", vermelhoPortugal);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        painelBotoes.setBackground(fundoEscuro);
        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnVoltar);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        // Ações
        btnAdicionar.addActionListener(e -> adicionarVeiculo());
        btnEditar.addActionListener(e -> editarVeiculo());
        btnExcluir.addActionListener(e -> excluirVeiculo());
        btnVoltar.addActionListener(e -> {
            new FuncionarioMenu().setVisible(true);
            dispose();
        });

        campoBusca.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrar(comboMarca); }
            public void removeUpdate(DocumentEvent e) { filtrar(comboMarca); }
            public void changedUpdate(DocumentEvent e) { filtrar(comboMarca); }
        });

        comboMarca.addItemListener(e -> filtrar(comboMarca));

        carregarVeiculos();
    }

    // ======== CAMPOS ESTILIZADOS ========

    private JTextField criarCampo() {
        JTextField campo = new JTextField(20);
        campo.setBackground(new Color(40, 40, 40));
        campo.setForeground(Color.WHITE);
        campo.setCaretColor(Color.WHITE);
        campo.setBorder(BorderFactory.createLineBorder(douradoSuave));
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return campo;
    }

    private JLabel criarLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(douradoSuave);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return lbl;
    }

    private JButton criarBotao(String texto, Color corBase) {
        JButton btn = new JButton(texto);
        btn.setBackground(corBase);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        btn.setPreferredSize(new Dimension(140, 40));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(corBase.darker());
            }
            @Override public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(corBase);
            }
        });

        return btn;
    }

    // ======== TABELA ESTILIZADA ========

    private void estilizarTabela() {
        tabela.setBackground(new Color(45, 45, 45));
        tabela.setForeground(Color.WHITE);
        tabela.setRowHeight(28);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabela.setSelectionBackground(verdePortugal);
        tabela.setSelectionForeground(Color.YELLOW);

        tabela.getTableHeader().setBackground(douradoSuave);
        tabela.getTableHeader().setForeground(Color.BLACK);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        tabela.setGridColor(Color.YELLOW);
        ((DefaultTableCellRenderer) tabela.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        tabela.getColumnModel().getColumn(0).setCellRenderer(center);
        tabela.getColumnModel().getColumn(4).setCellRenderer(center);
        tabela.getColumnModel().getColumn(5).setCellRenderer(center);
    }

    // ======== LÓGICA (mantida) ========

    private void carregarVeiculos() {
        try {
            listaCompleta = controller.listarVeiculos();
            tableModel.setVeiculos(listaCompleta);
        } catch (SQLException e) {
            mostrarMensagem("Erro ao carregar veículos: " + e.getMessage(), true);
        }
    }

    private void filtrar(JComboBox<String> comboMarca) {
        String termo = campoBusca.getText().trim().toLowerCase();
        String marca = comboMarca.getSelectedItem().toString();

        if (listaCompleta == null) return;

        tableModel.setVeiculos(listaCompleta.stream()
                .filter(v ->
                        (marca.equals("Todas") || v.getMarca().equalsIgnoreCase(marca)) &&
                        (v.getModelo().toLowerCase().contains(termo)
                                || v.getMarca().toLowerCase().contains(termo)
                                || v.getPlaca().toLowerCase().contains(termo)))
                .toList()
        );
    }

   private void adicionarVeiculo() {
    // ====== CONFIGURAÇÕES DO DIALOG ======
    JDialog dialog = new JDialog(this, "Adicionar Veículo", true);
    dialog.setSize(650, 300);
    dialog.setLayout(new BorderLayout(10, 10));
    dialog.setLocationRelativeTo(this);
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    // ====== CORES ======
    Color fundo = new Color(25, 25, 25);
    Color painel = new Color(35, 35, 35);
    Color campo = new Color(55, 55, 55);
    Color amarelo = new Color(255, 215, 0);
    Color texto = Color.WHITE;

    dialog.getContentPane().setBackground(fundo);

    // ====== PAINEL PRINCIPAL ======
    JPanel painelPrincipal = new JPanel(new GridBagLayout());
    painelPrincipal.setBackground(painel);
    painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // ====== FUNÇÕES DE COMPONENTES ======
    java.util.function.Function<String, JLabel> label = (txt) -> {
        JLabel l = new JLabel(txt);
        l.setForeground(amarelo);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return l;
    };

    java.util.function.Function<Dimension, JTextField> campoTexto = (dim) -> {
        JTextField f = new JTextField();
        f.setPreferredSize(dim);
        f.setBackground(campo);
        f.setForeground(texto);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createLineBorder(amarelo));
        return f;
    };

    // ====== CAMPOS ======
    JTextField placa = campoTexto.apply(new Dimension(120, 28));
    JTextField modelo = campoTexto.apply(new Dimension(200, 28));
    JTextField marca = campoTexto.apply(new Dimension(150, 28));
    JTextField ano = campoTexto.apply(new Dimension(80, 28));
    JTextField diaria = campoTexto.apply(new Dimension(100, 28));

    // ====== LINHA 1 ======
    gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
    painelPrincipal.add(label.apply("Placa:"), gbc);
    gbc.gridx = 1; gbc.weightx = 0.3;
    painelPrincipal.add(placa, gbc);

    gbc.gridx = 2; gbc.weightx = 0.1;
    painelPrincipal.add(label.apply("Modelo:"), gbc);
    gbc.gridx = 3; gbc.weightx = 0.5;
    painelPrincipal.add(modelo, gbc);

    // ====== LINHA 2 ======
    gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1;
    painelPrincipal.add(label.apply("Marca:"), gbc);
    gbc.gridx = 1; gbc.weightx = 0.3;
    painelPrincipal.add(marca, gbc);

    gbc.gridx = 2; gbc.weightx = 0.1;
    painelPrincipal.add(label.apply("Ano:"), gbc);
    gbc.gridx = 3; gbc.weightx = 0.2;
    painelPrincipal.add(ano, gbc);

    // ====== LINHA 3 ======
    gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.1;
    painelPrincipal.add(label.apply("Valor da Diária (R$):"), gbc);
    gbc.gridx = 1; gbc.weightx = 0.3;
    painelPrincipal.add(diaria, gbc);

    dialog.add(painelPrincipal, BorderLayout.CENTER);

    // ====== BOTÕES ======
    JButton btnSalvar = new JButton("Salvar");
    btnSalvar.setBackground(new Color(0, 150, 0));
    btnSalvar.setForeground(Color.WHITE);
    btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 13));

    JButton btnCancelar = new JButton("Cancelar");
    btnCancelar.setBackground(new Color(150, 0, 0));
    btnCancelar.setForeground(Color.WHITE);
    btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 13));

    JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    painelBotoes.setBackground(fundo);
    painelBotoes.add(btnSalvar);
    painelBotoes.add(btnCancelar);

    dialog.add(painelBotoes, BorderLayout.SOUTH);

    // ====== AÇÕES ======
    btnCancelar.addActionListener(e -> dialog.dispose());

    btnSalvar.addActionListener(e -> {
        String placaStr = placa.getText().trim().toUpperCase();
        String modeloStr = modelo.getText().trim();
        String marcaStr = marca.getText().trim();
        String anoStr = ano.getText().trim();
        String diariaStr = diaria.getText().trim().replace(",", ".");

        if (!validarCampos(placaStr, modeloStr, marcaStr, anoStr, diariaStr)) return;

        try {
            controller.adicionarVeiculo(placaStr, modeloStr, marcaStr, "", anoStr, diariaStr);
            carregarVeiculos();
            mostrarMensagem("Veículo cadastrado com sucesso!", false);
            dialog.dispose();
        } catch (Exception ex) {
            mostrarMensagem("Erro ao cadastrar veículo: " + ex.getMessage(), true);
        }
    });

    dialog.setVisible(true);
}

   private void editarVeiculo() {
    int row = tabela.getSelectedRow();
    if (row == -1) {
        mostrarMensagem("Selecione um veículo para editar!", true);
        return;
    }

    Veiculo v = tableModel.getVeiculo(row);

    // ====== CONFIGURAÇÕES DO DIALOG ======
    JDialog dialog = new JDialog(this, "Editar Veículo", true);
    dialog.setSize(650, 300);
    dialog.setLayout(new BorderLayout(10, 10));
    dialog.setLocationRelativeTo(this);
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    // ====== CORES ======
    Color fundo = new Color(25, 25, 25);
    Color painel = new Color(35, 35, 35);
    Color campo = new Color(55, 55, 55);
    Color amarelo = new Color(255, 215, 0);
    Color texto = Color.WHITE;

    dialog.getContentPane().setBackground(fundo);

    // ====== PAINEL PRINCIPAL ======
    JPanel painelPrincipal = new JPanel(new GridBagLayout());
    painelPrincipal.setBackground(painel);
    painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // ====== FUNÇÕES DE COMPONENTES ======
    java.util.function.Function<String, JLabel> label = (txt) -> {
        JLabel l = new JLabel(txt);
        l.setForeground(amarelo);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return l;
    };

    java.util.function.Function<Dimension, JTextField> campoTexto = (dim) -> {
        JTextField f = new JTextField();
        f.setPreferredSize(dim);
        f.setBackground(campo);
        f.setForeground(texto);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createLineBorder(amarelo));
        return f;
    };

    // ====== CAMPOS ======
    JTextField placa = campoTexto.apply(new Dimension(120, 28));
    JTextField modelo = campoTexto.apply(new Dimension(200, 28));
    JTextField marca = campoTexto.apply(new Dimension(150, 28));
    JTextField ano = campoTexto.apply(new Dimension(80, 28));
    JTextField diaria = campoTexto.apply(new Dimension(100, 28));

    // Preenche com os dados existentes
    placa.setText(v.getPlaca());
    modelo.setText(v.getModelo());
    marca.setText(v.getMarca());
    ano.setText(String.valueOf(v.getAno()));
    diaria.setText(String.valueOf(v.getDiaria()));

    // ====== LINHA 1 ======
    gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
    painelPrincipal.add(label.apply("Placa:"), gbc);
    gbc.gridx = 1; gbc.weightx = 0.3;
    painelPrincipal.add(placa, gbc);

    gbc.gridx = 2; gbc.weightx = 0.1;
    painelPrincipal.add(label.apply("Modelo:"), gbc);
    gbc.gridx = 3; gbc.weightx = 0.5;
    painelPrincipal.add(modelo, gbc);

    // ====== LINHA 2 ======
    gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1;
    painelPrincipal.add(label.apply("Marca:"), gbc);
    gbc.gridx = 1; gbc.weightx = 0.3;
    painelPrincipal.add(marca, gbc);

    gbc.gridx = 2; gbc.weightx = 0.1;
    painelPrincipal.add(label.apply("Ano:"), gbc);
    gbc.gridx = 3; gbc.weightx = 0.2;
    painelPrincipal.add(ano, gbc);

    // ====== LINHA 3 ======
    gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.1;
    painelPrincipal.add(label.apply("Valor da Diária (R$):"), gbc);
    gbc.gridx = 1; gbc.weightx = 0.3;
    painelPrincipal.add(diaria, gbc);

    dialog.add(painelPrincipal, BorderLayout.CENTER);

    // ====== BOTÕES ======
    JButton btnSalvar = new JButton("Salvar Alterações");
    btnSalvar.setBackground(new Color(0, 150, 0));
    btnSalvar.setForeground(Color.WHITE);
    btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 13));

    JButton btnCancelar = new JButton("Cancelar");
    btnCancelar.setBackground(new Color(150, 0, 0));
    btnCancelar.setForeground(Color.WHITE);
    btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 13));

    JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    painelBotoes.setBackground(fundo);
    painelBotoes.add(btnSalvar);
    painelBotoes.add(btnCancelar);

    dialog.add(painelBotoes, BorderLayout.SOUTH);

    // ====== AÇÕES ======
    btnCancelar.addActionListener(e -> dialog.dispose());

    btnSalvar.addActionListener(e -> {
        String placaStr = placa.getText().trim().toUpperCase();
        String modeloStr = modelo.getText().trim();
        String marcaStr = marca.getText().trim();
        String anoStr = ano.getText().trim();
        String diariaStr = diaria.getText().trim().replace(",", ".");

        if (!validarCampos(placaStr, modeloStr, marcaStr, anoStr, diariaStr)) return;

        try {
            controller.editarVeiculo(v.getId(), placaStr, modeloStr, marcaStr, "", anoStr, diariaStr);
            carregarVeiculos();
            mostrarMensagem("Veículo atualizado com sucesso!", false);
            dialog.dispose();
        } catch (Exception ex) {
            mostrarMensagem("Erro ao editar veículo: " + ex.getMessage(), true);
        }
    });

    dialog.setVisible(true);
}


    private void excluirVeiculo() {
        int row = tabela.getSelectedRow();
        if (row == -1) {
            mostrarMensagem("Selecione um veículo!", true);
            return;
        }

        Veiculo v = tableModel.getVeiculo(row);

        int opt = JOptionPane.showConfirmDialog(this,
                "Excluir o veículo " + v.getModelo() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (opt == JOptionPane.YES_OPTION) {
            try {
                controller.excluirVeiculo(v.getId());
                carregarVeiculos();
                mostrarMensagem("Excluído com sucesso!", false);
            } catch (SQLException e) {
                mostrarMensagem("Erro ao excluir: " + e.getMessage(), true);
            }
        }
    }

    private boolean validarCampos(String placa, String modelo, String marca, String ano, String diaria) {
        if (placa.isEmpty() || modelo.isEmpty() || marca.isEmpty() || ano.isEmpty() || diaria.isEmpty()) {
            mostrarMensagem("Preencha todos os campos.", true);
            return false;
        }

        if (!placa.matches("^[A-Z]{3}-?\\d[A-Z\\d]\\d{2}$")) {
            mostrarMensagem("Placa inválida!", true);
            return false;
        }

        try {
            int anoInt = Integer.parseInt(ano);
            if (anoInt < 1900 || anoInt > 2100) {
                mostrarMensagem("Ano inválido.", true);
                return false;
            }
        } catch (Exception e) {
            mostrarMensagem("Ano deve ser numérico.", true);
            return false;
        }

        try {
            double valor = Double.parseDouble(diaria);
            if (valor <= 0) {
                mostrarMensagem("Diária deve ser maior que zero.", true);
                return false;
            }
        } catch (Exception e) {
            mostrarMensagem("Valor inválido.", true);
            return false;
        }

        return true;
    }

    private void mostrarMensagem(String msg, boolean erro) {
        JOptionPane.showMessageDialog(this, msg,
                erro ? "Erro" : "Informação",
                erro ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
    }
}