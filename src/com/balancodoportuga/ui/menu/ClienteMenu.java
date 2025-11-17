package com.balancodoportuga.ui.menu;

import com.balancodoportuga.controller.ClienteController;
import com.balancodoportuga.model.Cliente;
import com.balancodoportuga.model.Reserva;
import com.balancodoportuga.ui.forms.ReservaForm;
import com.balancodoportuga.ui.forms.PagamentoForm;

import javax.swing.*;
import java.awt.*;

public class ClienteMenu extends JFrame {

    private final Cliente cliente;
    private final ClienteController controller = new ClienteController();

    private JLabel lblStatusReserva, lblVeiculo, lblPeriodo, lblValor;

    //TEMA PORTUGAL
    private final Color fundo = new Color(25, 25, 25);
    private final Color painel = new Color(35, 35, 35);
    private final Color dourado = new Color(255, 215, 0);
    private final Color vermelho = new Color(178, 0, 0);

    public ClienteMenu(Cliente cliente) {
        this.cliente = cliente;

        // ===== Configuração da Janela =====
        setTitle("Balanço do Portuga - Área do Cliente");
        setSize(900, 550);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // ===== Painel Principal =====
        JPanel painelPrincipal = new JPanel(new BorderLayout(0, 20));
        painelPrincipal.setBackground(fundo);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(painelPrincipal);

        // ===== Cabeçalho =====
        JLabel titulo = new JLabel("Bem-vindo, " + cliente.getNome() + "!", SwingConstants.CENTER);
        titulo.setForeground(dourado);
        titulo.setFont(new Font("Segoe UI Semibold", Font.BOLD, 26));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(fundo);
        header.add(titulo, BorderLayout.CENTER);

        JPanel linhaDecorativa = new JPanel();
        linhaDecorativa.setPreferredSize(new Dimension(100, 3));
        linhaDecorativa.setBackground(dourado);
        header.add(linhaDecorativa, BorderLayout.SOUTH);

        painelPrincipal.add(header, BorderLayout.NORTH);

        // ===== Painel de Reserva Atual =====
        JPanel painelReserva = new JPanel(new GridLayout(4, 1, 8, 8));
        painelReserva.setBackground(painel);
        painelReserva.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(dourado),
                "Sua Reserva Atual",
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 14),
                dourado
        ));

        lblStatusReserva = criarLabelInfo();
        lblVeiculo = criarLabelInfo();
        lblPeriodo = criarLabelInfo();
        lblValor = criarLabelInfo();

        painelReserva.add(lblStatusReserva);
        painelReserva.add(lblVeiculo);
        painelReserva.add(lblPeriodo);
        painelReserva.add(lblValor);

        painelPrincipal.add(painelReserva, BorderLayout.CENTER);

        // ===== Painel Inferior =====
        JPanel painelCards = new JPanel(new GridLayout(1, 4, 25, 0));
        painelCards.setBackground(fundo);

        JButton btnReservas = criarCard("📅", "Minhas Reservas", new Color(0, 150, 250));
        JButton btnPagamentos = criarCard("💳", "Pagamentos", new Color(0, 180, 80));
        JButton btnContato = criarCard("📞", "Contato", new Color(200, 160, 0));
        JButton btnSair = criarCard("🚶", "Sair", vermelho);

        painelCards.add(btnReservas);
        painelCards.add(btnPagamentos);
        painelCards.add(btnContato);
        painelCards.add(btnSair);

        painelPrincipal.add(painelCards, BorderLayout.SOUTH);

        // ===== Ações =====
        carregarReservaAtual();
        atualizarPainelReservaAtual();

        btnReservas.addActionListener(e -> new ReservaForm(cliente).setVisible(true));
        btnPagamentos.addActionListener(e -> new PagamentoForm(this, cliente).setVisible(true));
        btnContato.addActionListener(e -> mostrarContato());
        btnSair.addActionListener(e -> {
            new LoginTela().setVisible(true);
            dispose();
        });
    }

    // ============================================================
    // ESTILO VISUAL
    // ============================================================

    private JLabel criarLabelInfo() {
        JLabel lbl = new JLabel("-", SwingConstants.LEFT);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    private JButton criarCard(String emoji, String texto, Color corBase) {
        JButton card = new JButton("<html><center>" + emoji + "<br><br>" + texto + "</center></html>");
        card.setFont(new Font("Segoe UI", Font.BOLD, 16));
        card.setForeground(Color.WHITE);
        card.setBackground(corBase);
        card.setFocusPainted(false);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setOpaque(true);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                BorderFactory.createEmptyBorder(25, 15, 25, 15)
        ));

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(corBase.brighter());
                card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(corBase);
                card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            }
        });

        return card;
    }

    // ============================================================
    // RESERVA ATUAL
    // ============================================================

    private void carregarReservaAtual() {
        try {
            Reserva r = controller.buscarReservaAtiva(cliente.getId());
            if (r != null) {
                lblStatusReserva.setText("Status: " + r.getStatus());
                lblVeiculo.setText("Veículo: " + r.getVehicle().getModelo());
                lblPeriodo.setText("Período: " + r.getDataInicio() + " até " + r.getDataFim());
                lblValor.setText("Valor Total: R$ " + String.format("%.2f", r.getTotal()));
            } else {
                lblStatusReserva.setText("Nenhuma reserva ativa no momento.");
                lblVeiculo.setText("");
                lblPeriodo.setText("");
                lblValor.setText("");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar reserva: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarPainelReservaAtual() {
        new Timer(10000, e -> carregarReservaAtual()).start();
    }

    // ============================================================
    // CONTATO (DIALOG)
    // ============================================================

    private void mostrarContato() {
        Color fundo = new Color(25, 25, 25);
        Color painel = new Color(35, 35, 35);
        Color amarelo = new Color(255, 215, 0);
        Color texto = Color.WHITE;

        JDialog dialog = new JDialog(this, "Contato", true);
        dialog.setSize(420, 260);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(fundo);

        JPanel painelPrincipal = new JPanel(new GridBagLayout());
        painelPrincipal.setBackground(painel);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titulo = new JLabel("Informações de Contato");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(amarelo);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        painelPrincipal.add(titulo, gbc);

        gbc.gridwidth = 1;

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setForeground(amarelo);
        JLabel txtEmail = new JLabel("atendimento@balancodoportuga.com");
        txtEmail.setForeground(texto);

        JLabel lblTel = new JLabel("Telefone:");
        lblTel.setForeground(amarelo);
        JLabel txtTel = new JLabel("(71) 99718-8255");
        txtTel.setForeground(texto);

        JLabel lblAtend = new JLabel("Atendimento:");
        lblAtend.setForeground(amarelo);
        JLabel txtAtend = new JLabel("Segunda a Sábado, 8h às 18h");
        txtAtend.setForeground(texto);

        gbc.gridy = 1;
        gbc.gridx = 0; painelPrincipal.add(lblEmail, gbc);
        gbc.gridx = 1; painelPrincipal.add(txtEmail, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0; painelPrincipal.add(lblTel, gbc);
        gbc.gridx = 1; painelPrincipal.add(txtTel, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0; painelPrincipal.add(lblAtend, gbc);
        gbc.gridx = 1; painelPrincipal.add(txtAtend, gbc);

        dialog.add(painelPrincipal, BorderLayout.CENTER);

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
    }
}