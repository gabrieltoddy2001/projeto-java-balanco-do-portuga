package com.balancodoportuga.ui.forms;

import com.balancodoportuga.controller.FuncionarioController;
import com.balancodoportuga.model.Cliente;
import com.balancodoportuga.ui.menu.FuncionarioMenu;
import com.balancodoportuga.ui.tablemodel.ClienteTableModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

public class ClienteForm extends JFrame {

    private final FuncionarioController controller = new FuncionarioController();
    private JTable table;
    private ClienteTableModel tableModel;
    private JTextField campoBusca;

    // Tema Portugal
    private final Color verdePortugal = new Color(0, 102, 51);
    private final Color vermelhoPortugal = new Color(178, 0, 0);
    private final Color douradoSuave = new Color(255, 215, 0);
    private final Color fundoEscuro = new Color(28, 28, 28);

    public ClienteForm() {
        setTitle("Gerenciar Clientes");
        setSize(950, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout(15, 15));
        painelPrincipal.setBackground(fundoEscuro);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(painelPrincipal);

        // Painel de busca + filtros
        campoBusca = estilizarCampoBusca(new JTextField(25));

        JLabel lblBusca = new JLabel("Pesquisar:");
        lblBusca.setForeground(douradoSuave);
        lblBusca.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filtros.setBackground(fundoEscuro);
        filtros.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(douradoSuave),
                "Filtros",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                douradoSuave
        ));

        filtros.add(lblBusca);
        filtros.add(campoBusca);
        painelPrincipal.add(filtros, BorderLayout.NORTH);

        // Tabela
        tableModel = new ClienteTableModel();
        table = new JTable(tableModel);
        estilizarTabela(table);

        // larguras das colunas
        table.getColumnModel().getColumn(0).setPreferredWidth(40);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(220);  // Nome
        table.getColumnModel().getColumn(2).setPreferredWidth(130);  // CPF
        table.getColumnModel().getColumn(3).setPreferredWidth(150);  // Telefone
        table.getColumnModel().getColumn(4).setPreferredWidth(380);  // Endereço Completo

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(40, 40, 40));
        painelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Botões
        JButton btnAdicionar = criarBotao("Adicionar", verdePortugal);
        JButton btnEditar = criarBotao("Editar", verdePortugal);
        JButton btnExcluir = criarBotao("Excluir", vermelhoPortugal);
        JButton btnVoltar = criarBotao("Voltar", vermelhoPortugal);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        painelBotoes.setBackground(fundoEscuro);
        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnVoltar);

        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        // Ações
        btnAdicionar.addActionListener(e -> adicionarCliente());
        btnEditar.addActionListener(e -> editarCliente());
        btnExcluir.addActionListener(e -> excluirCliente());
        btnVoltar.addActionListener(e -> {
            new FuncionarioMenu().setVisible(true);
            dispose();
        });

        // Filtro automático
        campoBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrarClientes(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrarClientes(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrarClientes(); }
        });

        carregarClientes();
    }

    // ============================================================
    // Estilo personalizado
    // ============================================================
    private JTextField estilizarCampoBusca(JTextField campo) {
        campo.setBackground(new Color(40, 40, 40));
        campo.setForeground(Color.WHITE);
        campo.setCaretColor(Color.WHITE);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBorder(BorderFactory.createLineBorder(douradoSuave));
        return campo;
    }

    private JButton criarBotao(String texto, Color corBase) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(corBase);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(130, 40));
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(corBase.darker()); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(corBase); }
        });

        return btn;
    }

    private void estilizarTabela(JTable tabela) {
        tabela.setBackground(new Color(45, 45, 45));
        tabela.setForeground(Color.WHITE);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabela.setRowHeight(28);

        tabela.getTableHeader().setBackground(douradoSuave);
        tabela.getTableHeader().setForeground(Color.BLACK);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabela.setGridColor(Color.YELLOW);
    }

    // ============================================================
    // Métodos principais
    // ============================================================
    private void carregarClientes() {
        try {
            atualizarTabela(controller.listarClientes());
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar clientes: " + e.getMessage());
        }
    }

    private void filtrarClientes() {
        try {
            atualizarTabela(controller.filtrarClientes(campoBusca.getText()));
        } catch (SQLException e) {
            mostrarErro("Erro ao filtrar clientes: " + e.getMessage());
        }
    }

    // ============================================================
    // ADICIONAR CLIENTE
    // ============================================================
    private void adicionarCliente() {

        JDialog dialog = new JDialog(this, "Adicionar Cliente", true);
        dialog.setSize(750, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(25, 25, 25));

        Color fundo = new Color(25, 25, 25);
        Color painel = new Color(35, 35, 35);
        Color campo = new Color(55, 55, 55);
        Color amarelo = new Color(255, 215, 0);
        Color texto = Color.WHITE;

        JPanel painelPrincipal = new JPanel(new GridBagLayout());
        painelPrincipal.setBackground(painel);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ==== FACTORIES ====
        Function<String, JLabel> label = (txt) -> {
            JLabel l = new JLabel(txt);
            l.setForeground(amarelo);
            l.setFont(new Font("Segoe UI", Font.BOLD, 14));
            return l;
        };

        Function<Integer, JTextField> campoTexto = (width) -> {
            JTextField f = new JTextField();
            f.setPreferredSize(new Dimension(width, 30));
            f.setBackground(campo);
            f.setForeground(texto);
            f.setCaretColor(Color.WHITE);
            f.setBorder(BorderFactory.createLineBorder(amarelo));
            return f;
        };

        // ==== CAMPOS ====
        JTextField nome = campoTexto.apply(250);
        JTextField email = campoTexto.apply(250);
        JTextField endereco = campoTexto.apply(250);
        JTextField bairro = campoTexto.apply(250);
        JTextField cidade = campoTexto.apply(150);
        JTextField numero = campoTexto.apply(80);
        JTextField estado = campoTexto.apply(60);
        JTextField cnh = campoTexto.apply(150);

        estado.setDocument(new javax.swing.text.PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                if (str == null) return;
                if ((getLength() + str.length()) <= 2)
                    super.insertString(offs, str.toUpperCase(), a);
            }
        });

        JFormattedTextField cpf, telefone;
        try {
            cpf = new JFormattedTextField(new MaskFormatter("###.###.###-##"));
            telefone = new JFormattedTextField(new MaskFormatter("(##) #####-####"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (JFormattedTextField f : new JFormattedTextField[]{cpf, telefone}) {
            f.setPreferredSize(new Dimension(150, 30));
            f.setBackground(campo);
            f.setForeground(texto);
            f.setCaretColor(Color.WHITE);
            f.setBorder(BorderFactory.createLineBorder(amarelo));
        }

        int y = 0;

        // ===== LINHAS =====
        // Nome | CPF
        gbc.gridy = y++;
        gbc.gridx = 0; painelPrincipal.add(label.apply("Nome:"), gbc);
        gbc.gridx = 1; painelPrincipal.add(nome, gbc);
        gbc.gridx = 2; painelPrincipal.add(label.apply("CPF:"), gbc);
        gbc.gridx = 3; painelPrincipal.add(cpf, gbc);

        // Email | Telefone
        gbc.gridy = y++;
        gbc.gridx = 0; painelPrincipal.add(label.apply("E-mail:"), gbc);
        gbc.gridx = 1; painelPrincipal.add(email, gbc);
        gbc.gridx = 2; painelPrincipal.add(label.apply("Telefone:"), gbc);
        gbc.gridx = 3; painelPrincipal.add(telefone, gbc);

        // Endereço | Número
        gbc.gridy = y++;
        gbc.gridx = 0; painelPrincipal.add(label.apply("Endereço:"), gbc);
        gbc.gridx = 1; painelPrincipal.add(endereco, gbc);
        gbc.gridx = 2; painelPrincipal.add(label.apply("Número:"), gbc);
        gbc.gridx = 3; painelPrincipal.add(numero, gbc);

        // Bairro | Cidade
        gbc.gridy = y++;
        gbc.gridx = 0; painelPrincipal.add(label.apply("Bairro:"), gbc);
        gbc.gridx = 1; painelPrincipal.add(bairro, gbc);
        gbc.gridx = 2; painelPrincipal.add(label.apply("Cidade:"), gbc);
        gbc.gridx = 3; painelPrincipal.add(cidade, gbc);

        // Estado | CNH
        gbc.gridy = y++;
        gbc.gridx = 0; painelPrincipal.add(label.apply("Estado:"), gbc);
        gbc.gridx = 1; painelPrincipal.add(estado, gbc);
        gbc.gridx = 2; painelPrincipal.add(label.apply("CNH:"), gbc);
        gbc.gridx = 3; painelPrincipal.add(cnh, gbc);

        dialog.add(painelPrincipal, BorderLayout.CENTER);

        // ==== BOTÕES ====
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setBackground(new Color(0, 150, 0));
        btnSalvar.setForeground(Color.WHITE);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(150, 0, 0));
        btnCancelar.setForeground(Color.WHITE);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.setBackground(fundo);
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);

        dialog.add(painelBotoes, BorderLayout.SOUTH);

        // ==== AÇÕES ====
        btnCancelar.addActionListener(e -> dialog.dispose());

        btnSalvar.addActionListener(e -> {
            try {
                // VIEW só lê campos e chama o controller
                controller.salvarCliente(
                        nome.getText().trim(),
                        cpf.getText().replaceAll("[^0-9]", ""),
                        email.getText().trim(),
                        telefone.getText().replaceAll("[^0-9]", ""),
                        endereco.getText().trim(),
                        numero.getText().trim(),
                        bairro.getText().trim(),
                        cidade.getText().trim(),
                        estado.getText().trim(),
                        cnh.getText().trim()
                );

                carregarClientes();
                mostrarInfo("Cliente cadastrado com sucesso!");
                dialog.dispose();

            } catch (Exception ex) {
                mostrarErro("Erro ao cadastrar cliente: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }

    // ============================================================
    // EDITAR CLIENTE
    // ============================================================
    private void editarCliente() {

        int row = table.getSelectedRow();
        if (row == -1) {
            mostrarInfo("Selecione um cliente para editar!");
            return;
        }

        Cliente c = tableModel.getClienteAt(row);

        // ==== CORES DO TEMA ====
        Color fundo = new Color(25, 25, 25);
        Color painel = new Color(35, 35, 35);
        Color campo = new Color(55, 55, 55);
        Color amarelo = new Color(255, 215, 0);
        Color texto = Color.WHITE;

        JDialog dialog = new JDialog(this, "Editar Cliente", true);
        dialog.setSize(750, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(fundo);

        JPanel painelPrincipal = new JPanel(new GridBagLayout());
        painelPrincipal.setBackground(painel);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ==== FACTORIES ====
        Function<String, JLabel> label = (txt) -> {
            JLabel l = new JLabel(txt);
            l.setForeground(amarelo);
            l.setFont(new Font("Segoe UI", Font.BOLD, 14));
            return l;
        };

        Function<Integer, JTextField> campoTexto = (width) -> {
            JTextField f = new JTextField();
            f.setPreferredSize(new Dimension(width, 30));
            f.setBackground(campo);
            f.setForeground(texto);
            f.setCaretColor(Color.WHITE);
            f.setBorder(BorderFactory.createLineBorder(amarelo));
            return f;
        };

        // ==== CAMPOS ====
        JTextField nome = campoTexto.apply(250);
        nome.setText(c.getNome());

        JTextField email = campoTexto.apply(250);
        email.setText(c.getEmail());

        JTextField endereco = campoTexto.apply(250);
        endereco.setText(c.getEndereco());

        JTextField bairro = campoTexto.apply(250);
        bairro.setText(c.getBairro());

        JTextField cidade = campoTexto.apply(150);
        cidade.setText(c.getCidade());

        JTextField numero = campoTexto.apply(80);
        numero.setText(c.getNumero());

        JTextField estado = campoTexto.apply(60);
        estado.setText(c.getEstado());

        JTextField cnh = campoTexto.apply(150);
        cnh.setText(c.getCnh());

        estado.setDocument(new javax.swing.text.PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (str == null) return;
                if ((getLength() + str.length()) <= 2)
                    super.insertString(offs, str.toUpperCase(), a);
            }
        });

        JFormattedTextField cpf, telefone;
        try {
            cpf = new JFormattedTextField(new MaskFormatter("###.###.###-##"));
            telefone = new JFormattedTextField(new MaskFormatter("(##) #####-####"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        cpf.setText(c.getCpf());
        telefone.setText(c.getTelefone());

        for (JFormattedTextField f : new JFormattedTextField[]{cpf, telefone}) {
            f.setPreferredSize(new Dimension(150, 30));
            f.setBackground(campo);
            f.setForeground(texto);
            f.setCaretColor(Color.WHITE);
            f.setBorder(BorderFactory.createLineBorder(amarelo));
        }

        int y = 0;

        // ===== LINHAS =====
        // Nome | CPF
        gbc.gridy = y++;
        gbc.gridx = 0; painelPrincipal.add(label.apply("Nome:"), gbc);
        gbc.gridx = 1; painelPrincipal.add(nome, gbc);
        gbc.gridx = 2; painelPrincipal.add(label.apply("CPF:"), gbc);
        gbc.gridx = 3; painelPrincipal.add(cpf, gbc);

        // Email | Telefone
        gbc.gridy = y++;
        gbc.gridx = 0; painelPrincipal.add(label.apply("E-mail:"), gbc);
        gbc.gridx = 1; painelPrincipal.add(email, gbc);
        gbc.gridx = 2; painelPrincipal.add(label.apply("Telefone:"), gbc);
        gbc.gridx = 3; painelPrincipal.add(telefone, gbc);

        // Endereço | Número
        gbc.gridy = y++;
        gbc.gridx = 0; painelPrincipal.add(label.apply("Endereço:"), gbc);
        gbc.gridx = 1; painelPrincipal.add(endereco, gbc);
        gbc.gridx = 2; painelPrincipal.add(label.apply("Número:"), gbc);
        gbc.gridx = 3; painelPrincipal.add(numero, gbc);

        // Bairro | Cidade
        gbc.gridy = y++;
        gbc.gridx = 0; painelPrincipal.add(label.apply("Bairro:"), gbc);
        gbc.gridx = 1; painelPrincipal.add(bairro, gbc);
        gbc.gridx = 2; painelPrincipal.add(label.apply("Cidade:"), gbc);
        gbc.gridx = 3; painelPrincipal.add(cidade, gbc);

        // Estado | CNH
        gbc.gridy = y++;
        gbc.gridx = 0; painelPrincipal.add(label.apply("Estado:"), gbc);
        gbc.gridx = 1; painelPrincipal.add(estado, gbc);
        gbc.gridx = 2; painelPrincipal.add(label.apply("CNH:"), gbc);
        gbc.gridx = 3; painelPrincipal.add(cnh, gbc);

        dialog.add(painelPrincipal, BorderLayout.CENTER);

        // ==== BOTÕES ====
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setBackground(new Color(0, 150, 0));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(150, 0, 0));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.setBackground(fundo);
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);

        dialog.add(painelBotoes, BorderLayout.SOUTH);

        // ==== AÇÕES ====
        btnCancelar.addActionListener(e -> dialog.dispose());

        btnSalvar.addActionListener(e -> {
            try {
                controller.atualizarCliente(
                        c.getId(),
                        nome.getText().trim(),
                        cpf.getText().replaceAll("[^0-9]", ""),
                        email.getText().trim(),
                        telefone.getText().replaceAll("[^0-9]", ""),
                        endereco.getText().trim(),
                        numero.getText().trim(),
                        bairro.getText().trim(),
                        cidade.getText().trim(),
                        estado.getText().trim(),
                        cnh.getText().trim()
                );

                carregarClientes();
                mostrarInfo("Cliente atualizado com sucesso!");
                dialog.dispose();

            } catch (Exception ex) {
                mostrarErro("Erro ao editar cliente: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }

    // ============================================================
    // EXCLUIR CLIENTE
    // ============================================================
    private void excluirCliente() {
        int row = table.getSelectedRow();
        if (row == -1) {
            mostrarInfo("Selecione um cliente para excluir!");
            return;
        }

        Cliente c = tableModel.getClienteAt(row);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir o cliente " + c.getNome() + "?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            controller.excluirCliente(c.getId());
            carregarClientes();
            mostrarInfo("Cliente excluído com sucesso!");
        } catch (Exception e) {
            mostrarErro("Erro ao excluir cliente: " + e.getMessage());
        }
    }

    public void atualizarTabela(List<Cliente> lista) {
        tableModel.setClients(lista);
    }

    private void mostrarInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Informação", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarErro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}